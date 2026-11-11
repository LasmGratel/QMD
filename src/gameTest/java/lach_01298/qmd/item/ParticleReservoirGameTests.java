package lach_01298.qmd.item;

import io.netty.buffer.Unpooled;
import lach_01298.qmd.capability.ParticleCellFluidHandler;
import lach_01298.qmd.particle.CellFluid;
import lach_01298.qmd.registry.QMDFluids;
import lach_01298.qmd.registry.QMDItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.network.connection.ConnectionType;

@GameTestHolder("qmd")
@PrefixGameTestTemplate(false)
public final class ParticleReservoirGameTests {
    @GameTest(template = "empty", batch = "qmd.items")
    public static void legacyReservoirImportsWithoutLosingCustomData(GameTestHelper helper) {
        var item = QMDItems.PARTICLE_SOURCES.get("sodium_22").get();
        ItemStack stack = new ItemStack(item, 4);
        CompoundTag old = new CompoundTag();
        CompoundTag reservoir = new CompoundTag();
        reservoir.putInt("particle_amount", 12345);
        reservoir.putInt("particle_capacity", 5000000);
        old.put("particle_storage", reservoir);
        old.putString("owner", "kept");
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(old));
        helper.assertTrue(ParticleReservoirItem.stored(stack) == 12345, "Legacy partial source must override the full implicit default");
        ItemStack consumed = item.consume(stack, 345);
        helper.assertTrue(consumed.getCount() == 1 && ParticleReservoirItem.stored(consumed) == 12000,
                "Consumption returns only one independently depleted reservoir");
        helper.assertTrue(stack.getCount() == 4 && ParticleReservoirItem.stored(stack) == 12345,
                "Source inventory is untouched until the machine splits it");
        helper.assertTrue(consumed.get(DataComponents.CUSTOM_DATA).copyTag().getString("owner").equals("kept"), "Unrelated data survives import");
        ItemStack restored = ItemStack.parseOptional(helper.getLevel().registryAccess(), (CompoundTag) consumed.save(helper.getLevel().registryAccess()));
        helper.assertTrue(ParticleReservoirItem.stored(restored) == 12000, "Modern particle component survives registry-aware persistence");
        helper.assertTrue(item.consume(stack, 12345).isEmpty(), "An exhausted source leaves its empty remainder");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.items")
    public static void cellFluidSimulationAndRemaindersAreConserved(GameTestHelper helper) {
        ItemStack cell = QMDItems.PARTICLE_CELLS.get("antihydrogen").get().getDefaultInstance();
        ParticleReservoirItem.setStored(cell, 1501);
        var handler = cell.getCapability(Capabilities.FluidHandler.ITEM);
        helper.assertTrue(handler != null, "Registered cell item exposes the native fluid capability");
        helper.assertTrue(handler.drain(100, IFluidHandler.FluidAction.SIMULATE).getAmount() == 1
                && ParticleReservoirItem.stored(cell) == 1501, "Simulation leaves exact particle count unchanged");
        helper.assertTrue(handler.drain(100, IFluidHandler.FluidAction.EXECUTE).getAmount() == 1
                && ParticleReservoirItem.stored(handler.getContainer()) == 501, "Draining mB retains the sub-mB particle remainder");
        int fill = handler.fill(new FluidStack(QMDFluids.EXOTIC.get("antihydrogen").still.get(), 100), IFluidHandler.FluidAction.EXECUTE);
        helper.assertTrue(fill == 99 && ParticleReservoirItem.stored(handler.getContainer()) == 99501, "Filling never rounds away stored particles or overfills");
        helper.assertTrue(handler.fill(new FluidStack(QMDFluids.EXOTIC.get("antihelium").still.get(), 1), IFluidHandler.FluidAction.EXECUTE) == 0,
                "Different species cannot mix");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.items")
    public static void emptyAndFullCellConversionsPreserveFluidComponents(GameTestHelper helper) {
        var handler = new ParticleCellFluidHandler(new ItemStack(QMDItems.EMPTY_CELL.get()));
        FluidStack input = new FluidStack(QMDFluids.EXOTIC.get("muonium").still.get(), 100);
        CompoundTag marker = new CompoundTag(); marker.putInt("batch", 42);
        input.set(DataComponents.CUSTOM_DATA, CustomData.of(marker));
        helper.assertTrue(handler.fill(input, IFluidHandler.FluidAction.SIMULATE) == 100
                && handler.getContainer().is(QMDItems.EMPTY_CELL.get()), "Simulated item conversion must not replace its container");
        helper.assertTrue(handler.fill(input, IFluidHandler.FluidAction.EXECUTE) == 100
                && handler.getContainer().is(QMDItems.PARTICLE_CELLS.get("muonium").get()), "Filling chooses the correct cell item");
        ItemStack saved = ItemStack.parseOptional(helper.getLevel().registryAccess(),
                (CompoundTag) handler.getContainer().save(helper.getLevel().registryAccess()));
        helper.assertTrue(ItemStack.isSameItemSameComponents(handler.getContainer(), saved),
                "The immutable fluid identity and particle amount survive cell persistence");
        var buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess(), ConnectionType.NEOFORGE);
        try {
            CellFluid.STREAM_CODEC.encode(buffer, CellFluid.of(input));
            helper.assertTrue(CellFluid.of(input).equals(CellFluid.STREAM_CODEC.decode(buffer)), "Cell fluid identity is network-safe and value-comparable");
        } finally { buffer.release(); }
        FluidStack output = handler.drain(100, IFluidHandler.FluidAction.EXECUTE);
        helper.assertTrue(FluidStack.matches(input, output) && handler.getContainer().is(QMDItems.EMPTY_CELL.get()),
                "A full fill/drain cycle returns every mB and component and the empty shell");
        ItemStack legacyCell = new ItemStack(QMDItems.PARTICLE_CELLS.get("muonium").get());
        CompoundTag legacy = new CompoundTag();
        CompoundTag reservoir = new CompoundTag(); reservoir.putInt("particle_amount", 1000);
        legacy.put("particle_storage", reservoir); legacy.putString("owner", "kept");
        legacyCell.set(DataComponents.CUSTOM_DATA, CustomData.of(legacy));
        var legacyHandler = new ParticleCellFluidHandler(legacyCell);
        helper.assertTrue(legacyHandler.drain(100, IFluidHandler.FluidAction.EXECUTE).getAmount() == 1,
                "Legacy particle counts drain through the native fluid capability");
        CompoundTag retained = legacyHandler.getContainer().get(DataComponents.CUSTOM_DATA).copyTag();
        helper.assertTrue(legacyHandler.getContainer().is(QMDItems.EMPTY_CELL.get())
                && !retained.contains("particle_storage") && retained.getString("owner").equals("kept"),
                "Complete fluid depletion removes reserved legacy contents and preserves unrelated shell data");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.items")
    public static void extremeGammaRadiationRemainsFiniteAfterShielding(GameTestHelper helper) {
        var pos = helper.absolutePos(new net.minecraft.core.BlockPos(2, 2, 2)).getCenter();
        var pig = net.minecraft.world.entity.EntityType.PIG.create(helper.getLevel());
        helper.assertTrue(pig != null, "Create a real living target");
        pig.setPos(pos); helper.getLevel().addFreshEntity(pig);
        var radiation = com.nred.nuclearcraft.radiation.RadiationHelper.getEntityRadiation(pig);
        helper.assertTrue(radiation != null, "Living entities expose the native radiation attachment");
        radiation.setExternalRadiationResistance(1);
        lach_01298.qmd.entity.GammaFlashEntity.create(helper.getLevel(), pos, 0.000001, 0, Double.POSITIVE_INFINITY);
        helper.assertTrue(Double.isFinite(radiation.getRadiationLevel()) && radiation.getRadiationLevel() > 0
                        && Double.isFinite(radiation.getTotalRads()),
                "An overflowing configured cell dose must remain finite through Neohaul's squared shielding calculation");
        pig.discard();
        helper.getLevel().getEntitiesOfClass(lach_01298.qmd.entity.GammaFlashEntity.class,
                new net.minecraft.world.phys.AABB(pos, pos).inflate(0.01)).forEach(net.minecraft.world.entity.Entity::discard);
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.items")
    public static void depletedCellsAndGammaFlashKeepTheirLifecycle(GameTestHelper helper) {
        var cell = (ParticleCellItem) QMDItems.PARTICLE_CELLS.get("positronium").get();
        ItemStack contents = cell.getDefaultInstance();
        CompoundTag marker = new CompoundTag(); marker.putString("batch", "preserved");
        contents.set(DataComponents.CUSTOM_DATA, CustomData.of(marker));
        ItemStack empty = cell.consume(contents, cell.capacity());
        helper.assertTrue(empty.is(QMDItems.EMPTY_CELL.get())
                && empty.get(DataComponents.CUSTOM_DATA).copyTag().getString("batch").equals("preserved"),
                "Exhausted cells preserve the physical shell and unrelated components");
        var pos = helper.absolutePos(new net.minecraft.core.BlockPos(2, 2, 2)).getCenter();
        lach_01298.qmd.entity.GammaFlashEntity.create(helper.getLevel(), pos, 0.5, 0, 0);
        var flashes = helper.getLevel().getEntitiesOfClass(lach_01298.qmd.entity.GammaFlashEntity.class,
                new net.minecraft.world.phys.AABB(pos, pos).inflate(0.1));
        helper.assertTrue(flashes.size() == 1 && flashes.getFirst().size() == 0.5F, "Server gamma flash has a synchronized physical size");
        var flash = flashes.getFirst();
        for (int i = 0; i < 100; i++) flash.tick();
        CompoundTag saved = new CompoundTag(); flash.saveWithoutId(saved);
        var restored = lach_01298.qmd.registry.QMDEntities.GAMMA_FLASH.get().create(helper.getLevel());
        restored.load(saved);
        helper.assertTrue(restored.size() == 0.5F, "Gamma size survives world persistence");
        for (int i = 0; i < 399; i++) restored.tick();
        helper.assertTrue(!restored.isRemoved(), "Reloaded gamma flash retains its remaining lifetime");
        restored.tick();
        helper.assertTrue(restored.isRemoved(), "Gamma flash expires at its original 500-tick lifetime");
        flash.discard();
        helper.succeed();
    }
}
