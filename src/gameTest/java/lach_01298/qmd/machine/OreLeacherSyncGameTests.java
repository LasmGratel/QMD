package lach_01298.qmd.machine;

import com.nred.nuclearcraft.payload.processor.EnergyProcessorUpdatePacket;
import io.netty.buffer.Unpooled;
import lach_01298.qmd.registration.QMDMachines;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.network.connection.ConnectionType;

/** Server-side checks of the data used by the processor screen, without loading client classes. */
@GameTestHolder("qmd")
@PrefixGameTestTemplate(false)
public final class OreLeacherSyncGameTests {
    private static final ResourceLocation[] ACIDS = {
            ResourceLocation.parse("qmd:nitric_acid"),
            ResourceLocation.parse("qmd:hydrochloric_acid"),
            ResourceLocation.parse("nuclearcraftneohaul:sulfuric_acid")
    };
    private static final long LARGE_ENERGY = (long) Integer.MAX_VALUE + 4096;

    private OreLeacherSyncGameTests() {
    }

    @GameTest(template = "empty")
    public static void updatePacketCapturesIndependentProcessingSnapshot(GameTestHelper helper) {
        OreLeacherBlockEntity machine = machine(helper, new BlockPos(1, 1, 1));
        int[] amounts = {16000, 257, 1};
        setState(machine, true, 37.25, 515.5, 62.75, LARGE_ENERGY, amounts);

        EnergyProcessorUpdatePacket snapshot = machine.getTileUpdatePacket();
        assertPacket(helper, snapshot, machine.getBlockPos(), true,
                37.25, 515.5, 62.75, LARGE_ENERGY, amounts);

        // GUI packets must describe the state when created, even if the server advances before encoding.
        machine.getTanks().getFirst().changeFluidAmount(-123);
        machine.getTanks().get(1).setFluidStored(FluidStack.EMPTY);
        machine.getTanks().get(2).setFluidStored(acid(0, 19));
        machine.setCurrentTime(38.5);
        machine.setBaseProcessTime(600.25);
        machine.setBaseProcessPower(45.5);
        machine.setIsProcessing(false);
        machine.getEnergyStorage().setEnergyStored(999);

        assertPacket(helper, snapshot, machine.getBlockPos(), true,
                37.25, 515.5, 62.75, LARGE_ENERGY, amounts);
        EnergyProcessorUpdatePacket next = machine.getTileUpdatePacket();
        helper.assertTrue(!next.isProcessing && next.time == 38.5 && next.baseProcessTime == 600.25
                        && next.baseProcessPower == 45.5 && next.energyStored == 999,
                "A later packet must capture current processing and energy values");
        helper.assertTrue(next.tankInfos.getFirst().amount() == 15877
                        && next.tankInfos.get(1).amount() == 0
                        && next.tankInfos.get(2).location().equals(ACIDS[0])
                        && next.tankInfos.get(2).amount() == 19,
                "A later packet must capture changed quantities, emptied tanks and replaced fluid identities");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void networkPacketsApplyProcessingStateAndClearStaleDisplays(GameTestHelper helper) {
        OreLeacherBlockEntity source = machine(helper, new BlockPos(1, 1, 1));
        OreLeacherBlockEntity replica = machine(helper, new BlockPos(3, 1, 1));
        int[] activeAmounts = {15999, 0, 321};
        setState(source, true, 123.75, 601.5, 87.25, LARGE_ENERGY, activeAmounts);
        EnergyProcessorUpdatePacket active = source.getTileUpdatePacket();

        setState(source, false, 0, 400, 50, 0, new int[]{0, 0, 0});
        EnergyProcessorUpdatePacket idle = source.getTileUpdatePacket();
        setState(replica, false, 9, 200, 5, 31, new int[]{1, 2, 3});

        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(),
                helper.getLevel().registryAccess(), ConnectionType.NEOFORGE);
        try {
            // Consecutive payloads also expose a codec that consumes too many or too few bytes.
            EnergyProcessorUpdatePacket.STREAM_CODEC.encode(buffer, active);
            EnergyProcessorUpdatePacket.STREAM_CODEC.encode(buffer, idle);
            EnergyProcessorUpdatePacket decodedActive = EnergyProcessorUpdatePacket.STREAM_CODEC.decode(buffer);
            assertPacket(helper, decodedActive, source.getBlockPos(), true,
                    123.75, 601.5, 87.25, LARGE_ENERGY, activeAmounts);
            helper.assertTrue(buffer.readableBytes() > 0, "The first decoder must leave the second payload intact");
            replica.onTileUpdatePacket(decodedActive);
            assertMachineMatchesPacket(helper, replica, decodedActive);

            EnergyProcessorUpdatePacket decodedIdle = EnergyProcessorUpdatePacket.STREAM_CODEC.decode(buffer);
            assertPacket(helper, decodedIdle, source.getBlockPos(), false,
                    0, 400, 50, 0, new int[]{0, 0, 0});
            helper.assertTrue(buffer.readableBytes() == 0, "Both decoders must consume exactly the two payloads");
            replica.onTileUpdatePacket(decodedIdle);
            assertMachineMatchesPacket(helper, replica, decodedIdle);
            helper.assertTrue(!replica.getIsProcessing() && replica.getCurrentTime() == 0
                            && replica.getEnergyStoredLong() == 0
                            && replica.getTanks().stream().allMatch(tank -> tank.isEmpty()),
                    "An idle update must clear stale active, progress, energy and fluid displays");
        } finally {
            buffer.release();
        }
        helper.succeed();
    }

    private static OreLeacherBlockEntity machine(GameTestHelper helper, BlockPos relativePos) {
        helper.setBlock(relativePos, QMDMachines.ORE_LEACHER.get());
        OreLeacherBlockEntity machine = helper.getBlockEntity(relativePos);
        helper.assertTrue(machine != null && machine.getTanks().size() == 3,
                "The placed ore leacher must provide its real block entity and three input tanks");
        // Exercise the payload's long field independently of the default 20,000 FE recipe capacity.
        machine.getEnergyStorage().setStorageCapacity(LARGE_ENERGY + 1);
        return machine;
    }

    private static void setState(OreLeacherBlockEntity machine, boolean processing, double time,
                                 double baseTime, double basePower, long energy, int[] amounts) {
        for (int tank = 0; tank < 3; ++tank) {
            machine.getTanks().get(tank).setFluidStored(acid(tank, amounts[tank]));
        }
        machine.setIsProcessing(processing);
        machine.setCurrentTime(time);
        machine.setBaseProcessTime(baseTime);
        machine.setBaseProcessPower(basePower);
        machine.getEnergyStorage().setEnergyStored(energy);
    }

    private static FluidStack acid(int tank, int amount) {
        return amount == 0 ? FluidStack.EMPTY
                : new FluidStack(BuiltInRegistries.FLUID.get(ACIDS[tank]), amount);
    }

    private static void assertPacket(GameTestHelper helper, EnergyProcessorUpdatePacket packet, BlockPos pos,
                                     boolean processing, double time, double baseTime, double basePower,
                                     long energy, int[] amounts) {
        helper.assertTrue(packet.type().equals(EnergyProcessorUpdatePacket.TYPE) && packet.pos.equals(pos),
                "The GUI update must retain its registered payload type and absolute machine position");
        helper.assertTrue(packet.isProcessing == processing && packet.time == time
                        && packet.baseProcessTime == baseTime && packet.baseProcessPower == basePower,
                "The GUI update must retain active state and fractional progress, duration and power");
        helper.assertTrue(packet.energyStored == energy, "The GUI update must preserve energy without integer truncation");
        helper.assertTrue(packet.tankInfos.size() == 3, "The GUI update must preserve all three tank positions");
        for (int tank = 0; tank < 3; ++tank) {
            ResourceLocation fluid = amounts[tank] == 0 ? ResourceLocation.parse("minecraft:empty") : ACIDS[tank];
            helper.assertTrue(packet.tankInfos.get(tank).location().equals(fluid)
                            && packet.tankInfos.get(tank).amount() == amounts[tank],
                    "The GUI update must retain fluid identity and exact amount in tank " + tank);
        }
    }

    private static void assertMachineMatchesPacket(GameTestHelper helper, OreLeacherBlockEntity machine,
                                                   EnergyProcessorUpdatePacket packet) {
        helper.assertTrue(machine.getIsProcessing() == packet.isProcessing && machine.getCurrentTime() == packet.time
                        && machine.getBaseProcessTime() == packet.baseProcessTime
                        && machine.getBaseProcessPower() == packet.baseProcessPower
                        && machine.getEnergyStoredLong() == packet.energyStored,
                "Applying a decoded GUI packet must update processing, progress, power and long energy values");
        for (int tank = 0; tank < 3; ++tank) {
            var expected = packet.tankInfos.get(tank);
            helper.assertTrue(machine.getTanks().get(tank).getFluidId().equals(expected.location())
                            && machine.getTanks().get(tank).getFluidAmount() == expected.amount(),
                    "Applying a decoded GUI packet must update tank " + tank + ", including empty tanks");
        }
    }
}
