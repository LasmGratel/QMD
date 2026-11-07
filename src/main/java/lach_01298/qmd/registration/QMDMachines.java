package lach_01298.qmd.registration;

import com.nred.nuclearcraft.block.processor.ProcessorBlock;
import lach_01298.qmd.QuantumMinecraftDynamics;
import lach_01298.qmd.machine.OreLeacherBlockEntity;
import lach_01298.qmd.machine.OreLeacherMachineInfo;
import lach_01298.qmd.machine.OreLeacherMenu;
import lach_01298.qmd.collector.CollectorBlock;
import lach_01298.qmd.collector.LiquidCollectorBlockEntity;
import lach_01298.qmd.collector.AtmosphereCollectorBlockEntity;
import lach_01298.qmd.recipe.CollectorRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class QMDMachines {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(QuantumMinecraftDynamics.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(QuantumMinecraftDynamics.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, QuantumMinecraftDynamics.MODID);
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, QuantumMinecraftDynamics.MODID);

    public static final DeferredBlock<ProcessorBlock<OreLeacherBlockEntity>> ORE_LEACHER =
            BLOCKS.register("ore_leacher", () -> new ProcessorBlock<>(OreLeacherMachineInfo.KEY));
    public static final DeferredItem<BlockItem> ORE_LEACHER_ITEM = ITEMS.registerSimpleBlockItem(ORE_LEACHER);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OreLeacherBlockEntity>> ORE_LEACHER_ENTITY =
            BLOCK_ENTITIES.register("ore_leacher", () -> BlockEntityType.Builder.of(
                    OreLeacherBlockEntity::new, ORE_LEACHER.get()).build(null));
    public static final DeferredHolder<MenuType<?>, MenuType<OreLeacherMenu>> ORE_LEACHER_MENU =
            MENUS.register("ore_leacher", () -> IMenuTypeExtension.create(OreLeacherMenu::new));
    public static final DeferredBlock<CollectorBlock> LIQUID_COLLECTOR =
            BLOCKS.register("liquid_collector", () -> new CollectorBlock(CollectorRecipe.Kind.LIQUID));
    public static final DeferredBlock<CollectorBlock> ATMOSPHERE_COLLECTOR =
            BLOCKS.register("atmosphere_collector", () -> new CollectorBlock(CollectorRecipe.Kind.ATMOSPHERE));
    public static final DeferredItem<BlockItem> LIQUID_COLLECTOR_ITEM = ITEMS.registerSimpleBlockItem(LIQUID_COLLECTOR);
    public static final DeferredItem<BlockItem> ATMOSPHERE_COLLECTOR_ITEM = ITEMS.registerSimpleBlockItem(ATMOSPHERE_COLLECTOR);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LiquidCollectorBlockEntity>> LIQUID_COLLECTOR_ENTITY =
            BLOCK_ENTITIES.register("liquid_collector", () -> BlockEntityType.Builder.of(
                    LiquidCollectorBlockEntity::new, LIQUID_COLLECTOR.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AtmosphereCollectorBlockEntity>> ATMOSPHERE_COLLECTOR_ENTITY =
            BLOCK_ENTITIES.register("atmosphere_collector", () -> BlockEntityType.Builder.of(
                    AtmosphereCollectorBlockEntity::new, ATMOSPHERE_COLLECTOR.get()).build(null));

    public static void register(IEventBus bus) {
        OreLeacherMachineInfo.initialize();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        MENUS.register(bus);
        bus.addListener(QMDMachines::registerCapabilities);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ORE_LEACHER_ENTITY.get(),
                (tile, side) -> tile.getItemSideCapability(side));
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ORE_LEACHER_ENTITY.get(),
                (tile, side) -> tile.getFluidSideCapability(side));
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ORE_LEACHER_ENTITY.get(),
                (tile, side) -> tile.getEnergySideCapability(side));
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, LIQUID_COLLECTOR_ENTITY.get(),
                (tile, side) -> tile.getFluidSideCapability(side));
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, LIQUID_COLLECTOR_ENTITY.get(),
                (tile, side) -> tile.getEnergySideCapability(side));
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ATMOSPHERE_COLLECTOR_ENTITY.get(),
                (tile, side) -> tile.getFluidSideCapability(side));
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ATMOSPHERE_COLLECTOR_ENTITY.get(),
                (tile, side) -> tile.getEnergySideCapability(side));
    }

    private QMDMachines() {}
}
