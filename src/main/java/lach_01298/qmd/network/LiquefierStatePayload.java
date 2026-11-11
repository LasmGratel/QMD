package lach_01298.qmd.network;

import lach_01298.qmd.block.menu.LiquefierMenu;
import lach_01298.qmd.multiblock.liquefier.LiquefierLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import java.util.List;

/** Complete menu snapshot, scoped to the currently open menu and its controller position. */
public record LiquefierStatePayload(int containerId, BlockPos pos, State state) implements CustomPacketPayload {

    public static final Type<LiquefierStatePayload> TYPE = new Type<>(ResourceLocation.parse("qmd:liquefier_state"));
    public static final StreamCodec<RegistryFriendlyByteBuf, LiquefierStatePayload> STREAM_CODEC = new StreamCodec<>() {

        @Override
        public LiquefierStatePayload decode(RegistryFriendlyByteBuf buf) {
            int id = buf.readVarInt();
            BlockPos pos = buf.readBlockPos();
            int x = buf.readVarInt(), y = buf.readVarInt(), z = buf.readVarInt(), compressors = buf.readVarInt(),
                nozzles = buf.readVarInt();
            boolean active = buf.readBoolean();
            long energy = buf.readVarLong(), capacity = buf.readVarLong();
            double[] stats = new double[11];
            for (int i = 0; i < stats.length; i++) stats[i] = buf.readDouble();
            List<FluidStack> tanks = List.of(
                FluidStack.OPTIONAL_STREAM_CODEC.decode(buf),
                FluidStack.OPTIONAL_STREAM_CODEC.decode(buf),
                FluidStack.OPTIONAL_STREAM_CODEC.decode(buf),
                FluidStack.OPTIONAL_STREAM_CODEC.decode(buf));
            return new LiquefierStatePayload(
                id,
                pos,
                new State(
                    x,
                    y,
                    z,
                    compressors,
                    nozzles,
                    active,
                    energy,
                    capacity,
                    stats[0],
                    stats[1],
                    stats[2],
                    stats[3],
                    stats[4],
                    stats[5],
                    stats[6],
                    stats[7],
                    stats[8],
                    stats[9],
                    stats[10],
                    tanks));
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, LiquefierStatePayload packet) {
            State s = packet.state;
            buf.writeVarInt(packet.containerId);
            buf.writeBlockPos(packet.pos);
            buf.writeVarInt(s.x);
            buf.writeVarInt(s.y);
            buf.writeVarInt(s.z);
            buf.writeVarInt(s.compressors);
            buf.writeVarInt(s.nozzles);
            buf.writeBoolean(s.active);
            buf.writeVarLong(s.energy);
            buf.writeVarLong(s.capacity);
            double[] stats = { s.gasIn, s.liquidOut, s.coolantIn, s.coolantOut, s.power, s.heat, s.tempDiff,
                s.energyEfficiency, s.heatEfficiency, s.pressureEfficiency, s.pressure };
            for (double stat : stats) buf.writeDouble(stat);
            for (FluidStack tank : s.tanks) FluidStack.OPTIONAL_STREAM_CODEC.encode(buf, tank);
        }
    };

    public LiquefierStatePayload {
        pos = pos.immutable();
    }

    @Override
    public Type<LiquefierStatePayload> type() {
        return TYPE;
    }

    public static void apply(Player player, LiquefierStatePayload payload) {
        if (player.containerMenu instanceof LiquefierMenu menu && menu.containerId == payload.containerId
            && menu.getBlockPos()
                .equals(payload.pos))
            menu.receiveState(payload.state);
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1")
            .playToClient(TYPE, STREAM_CODEC, (packet, context) -> apply(context.player(), packet));
        event.registrar("1")
            .playToClient(
                LiquefierRenderPayload.TYPE,
                LiquefierRenderPayload.STREAM_CODEC,
                (packet, context) -> LiquefierRenderPayload.apply(context.player(), packet));
    }

    public record State(int x, int y, int z, int compressors, int nozzles, boolean active, long energy, long capacity,
        double gasIn, double liquidOut, double coolantIn, double coolantOut, double power, double heat, double tempDiff,
        double energyEfficiency, double heatEfficiency, double pressureEfficiency, double pressure,
        List<FluidStack> tanks) {

        public State {
            if (tanks.size() != 4) throw new IllegalArgumentException("Liquefier menu state requires four tanks");
            tanks = tanks.stream()
                .map(FluidStack::copy)
                .toList();
        }

        @Override
        public List<FluidStack> tanks() {
            return tanks.stream()
                .map(FluidStack::copy)
                .toList();
        }

        public static State empty() {
            return new State(
                0,
                0,
                0,
                0,
                0,
                false,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                List.of(FluidStack.EMPTY, FluidStack.EMPTY, FluidStack.EMPTY, FluidStack.EMPTY));
        }

        public static State from(LiquefierLogic logic) {
            var mb = logic.multiblock;
            return new State(
                mb.getInteriorLengthX(),
                mb.getInteriorLengthY(),
                mb.getInteriorLengthZ(),
                logic.getPartMap(lach_01298.qmd.block.entity.liquefier.LiquefierCompressorBlockEntity.class)
                    .size(),
                logic.getPartMap(lach_01298.qmd.block.entity.liquefier.LiquefierNozzleBlockEntity.class)
                    .size(),
                mb.isExchangerOn,
                logic.energyStorage.getEnergyStoredLong(),
                logic.energyStorage.getMaxEnergyStoredLong(),
                mb.tubeInputRateFP,
                logic.liquidOutFP,
                mb.shellInputRateFP,
                logic.coolantOutFP,
                logic.powerUseFP,
                mb.heatTransferRateFP,
                mb.totalTempDiff,
                logic.energyEfficiency,
                logic.heatEfficiency,
                logic.pressureEfficiency,
                logic.pressure,
                List.of(
                    logic.gasTanks.get(0)
                        .getFluid(),
                    logic.gasTanks.get(1)
                        .getFluid(),
                    mb.shellTanks.get(0)
                        .getFluid(),
                    mb.shellTanks.get(1)
                        .getFluid()));
        }
    }
}
