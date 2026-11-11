package lach_01298.qmd.registry;

import com.nred.nuclearcraft.registration.CapabilityRegistration;
import lach_01298.qmd.QuantumMinecraftDynamics;
import lach_01298.qmd.block.chamber.AbstractChamberBlock;
import lach_01298.qmd.block.entity.accelerator.AbstractAcceleratorBlockEntity;
import lach_01298.qmd.block.entity.AtmosphereCollectorBlockEntity;
import lach_01298.qmd.block.entity.BeamlineBlockEntity;
import lach_01298.qmd.block.entity.chamber.AbstractChamberBlockEntity;
import lach_01298.qmd.block.entity.CreativeParticleSourceBlockEntity;
import lach_01298.qmd.block.entity.HafniumShieldBlockEntity;
import lach_01298.qmd.block.entity.IrradiatorBlockEntity;
import lach_01298.qmd.block.entity.LiquidCollectorBlockEntity;
import lach_01298.qmd.block.entity.OreLeacherBlockEntity;
import lach_01298.qmd.block.entity.OreLeacherMachineInfo;
import lach_01298.qmd.block.entity.StrontiumRtgBlockEntity;
import lach_01298.qmd.block.entity.SuperAlloyBladeBlockEntity;
import lach_01298.qmd.block.entity.liquefier.LiquefierCompressorBlockEntity;
import lach_01298.qmd.block.entity.liquefier.LiquefierControllerBlockEntity;
import lach_01298.qmd.block.entity.liquefier.LiquefierEnergyPortBlockEntity;
import lach_01298.qmd.block.entity.liquefier.LiquefierFluidPortBlockEntity;
import lach_01298.qmd.block.entity.liquefier.LiquefierNozzleBlockEntity;
import lach_01298.qmd.capability.QMDParticleCapabilities;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static lach_01298.qmd.multiblock.accelerator.AcceleratorPartType.Role.*;

/** Block entity types and their block capability bindings. */
public final class QMDBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister
        .create(Registries.BLOCK_ENTITY_TYPE, QuantumMinecraftDynamics.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OreLeacherBlockEntity>> ORE_LEACHER = BLOCK_ENTITIES
        .register(
            "ore_leacher",
            () -> BlockEntityType.Builder.of(OreLeacherBlockEntity::new, QMDBlocks.ORE_LEACHER.get())
                .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LiquidCollectorBlockEntity>> LIQUID_COLLECTOR = BLOCK_ENTITIES
        .register(
            "liquid_collector",
            () -> BlockEntityType.Builder.of(LiquidCollectorBlockEntity::new, QMDBlocks.LIQUID_COLLECTOR.get())
                .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AtmosphereCollectorBlockEntity>> ATMOSPHERE_COLLECTOR = BLOCK_ENTITIES
        .register(
            "atmosphere_collector",
            () -> BlockEntityType.Builder.of(AtmosphereCollectorBlockEntity::new, QMDBlocks.ATMOSPHERE_COLLECTOR.get())
                .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CreativeParticleSourceBlockEntity>> CREATIVE_PARTICLE_SOURCE = BLOCK_ENTITIES
        .register(
            "creative_particle_source",
            () -> BlockEntityType.Builder
                .of(CreativeParticleSourceBlockEntity::new, QMDBlocks.CREATIVE_PARTICLE_SOURCE.get())
                .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LiquefierControllerBlockEntity>> LIQUEFIER_CONTROLLER = BLOCK_ENTITIES
        .register(
            "liquefier_controller",
            () -> BlockEntityType.Builder.of(LiquefierControllerBlockEntity::new, QMDBlocks.LIQUEFIER_CONTROLLER.get())
                .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LiquefierCompressorBlockEntity>> LIQUEFIER_COMPRESSOR = BLOCK_ENTITIES
        .register(
            "liquefier_compressor",
            () -> BlockEntityType.Builder
                .of(
                    LiquefierCompressorBlockEntity::new,
                    QMDBlocks.LIQUEFIER_COPPER_COMPRESSOR.get(),
                    QMDBlocks.LIQUEFIER_NEODYMIUM_COMPRESSOR.get(),
                    QMDBlocks.LIQUEFIER_SAMARIUM_COBALT_COMPRESSOR.get())
                .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LiquefierNozzleBlockEntity>> LIQUEFIER_NOZZLE = BLOCK_ENTITIES
        .register(
            "liquefier_nozzle",
            () -> BlockEntityType.Builder.of(LiquefierNozzleBlockEntity::new, QMDBlocks.LIQUEFIER_NOZZLE.get())
                .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LiquefierFluidPortBlockEntity>> LIQUEFIER_FLUID_PORT = BLOCK_ENTITIES
        .register(
            "liquefier_fluid_port",
            () -> BlockEntityType.Builder.of(LiquefierFluidPortBlockEntity::new, QMDBlocks.LIQUEFIER_FLUID_PORT.get())
                .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LiquefierEnergyPortBlockEntity>> LIQUEFIER_ENERGY_PORT = BLOCK_ENTITIES
        .register(
            "liquefier_energy_port",
            () -> BlockEntityType.Builder.of(LiquefierEnergyPortBlockEntity::new, QMDBlocks.LIQUEFIER_ENERGY_PORT.get())
                .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AbstractAcceleratorBlockEntity>> ACCELERATOR_PART = BLOCK_ENTITIES
        .register(
            "accelerator_part",
            () -> BlockEntityType.Builder
                .of(
                    AbstractAcceleratorBlockEntity::create,
                    QMDBlocks.ACCELERATOR_BLOCKS.values()
                        .stream()
                        .map(DeferredBlock::get)
                        .toArray(Block[]::new))
                .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AbstractChamberBlockEntity>> PARTICLE_CHAMBER = BLOCK_ENTITIES
        .register(
            "particle_chamber",
            () -> BlockEntityType.Builder.of(
                AbstractChamberBlockEntity::create,
                QMDBlocks.CHAMBER_PARTS.values()
                    .stream()
                    .map(DeferredBlock::get)
                    .toArray(AbstractChamberBlock[]::new))
                .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HafniumShieldBlockEntity>> HAFNIUM_SHIELD = BLOCK_ENTITIES
        .register(
            "neutron_shield_hafnium",
            () -> BlockEntityType.Builder.of(HafniumShieldBlockEntity::new, QMDBlocks.HAFNIUM_SHIELD.get())
                .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StrontiumRtgBlockEntity>> STRONTIUM_RTG = BLOCK_ENTITIES
        .register(
            "rtg_strontium",
            () -> BlockEntityType.Builder.of(StrontiumRtgBlockEntity::new, QMDBlocks.STRONTIUM_RTG.get())
                .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IrradiatorBlockEntity>> IRRADIATOR = BLOCK_ENTITIES
        .register(
            "irradiator",
            () -> BlockEntityType.Builder.of(IrradiatorBlockEntity::new, QMDBlocks.IRRADIATOR.get())
                .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BeamlineBlockEntity>> BEAMLINE = BLOCK_ENTITIES
        .register(
            "beamline",
            () -> BlockEntityType.Builder.of(BeamlineBlockEntity::new, QMDBlocks.BEAMLINE.get())
                .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SuperAlloyBladeBlockEntity>> SUPER_ALLOY_TURBINE_BLADE = BLOCK_ENTITIES
        .register(
            "turbine_blade_super_alloy",
            () -> BlockEntityType.Builder.of(SuperAlloyBladeBlockEntity::new, QMDBlocks.SUPER_ALLOY_TURBINE_BLADE.get())
                .build(null));

    public static void register(IEventBus bus) {
        OreLeacherMachineInfo.initialize();
        BLOCK_ENTITIES.register(bus);
        bus.addListener(QMDBlockEntities::registerCapabilities);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            QMDParticleCapabilities.BLOCK,
            CREATIVE_PARTICLE_SOURCE.get(),
            (tile, side) -> tile.getParticleBeams()
                .getFirst());
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            ORE_LEACHER.get(),
            (tile, side) -> tile.getItemSideCapability(side));
        event.registerBlockEntity(
            Capabilities.FluidHandler.BLOCK,
            ORE_LEACHER.get(),
            (tile, side) -> tile.getFluidSideCapability(side));
        event.registerBlockEntity(
            Capabilities.EnergyStorage.BLOCK,
            ORE_LEACHER.get(),
            (tile, side) -> tile.getEnergySideCapability(side));
        event.registerBlockEntity(
            Capabilities.FluidHandler.BLOCK,
            LIQUID_COLLECTOR.get(),
            (tile, side) -> tile.getFluidSideCapability(side));
        event.registerBlockEntity(
            Capabilities.EnergyStorage.BLOCK,
            LIQUID_COLLECTOR.get(),
            (tile, side) -> tile.getEnergySideCapability(side));
        event.registerBlockEntity(
            Capabilities.FluidHandler.BLOCK,
            ATMOSPHERE_COLLECTOR.get(),
            (tile, side) -> tile.getFluidSideCapability(side));
        event.registerBlockEntity(
            Capabilities.EnergyStorage.BLOCK,
            ATMOSPHERE_COLLECTOR.get(),
            (tile, side) -> tile.getEnergySideCapability(side));

        event.registerBlockEntity(
            Capabilities.FluidHandler.BLOCK,
            LIQUEFIER_FLUID_PORT.get(),
            (entity, side) -> entity.fluidHandler());
        event.registerBlockEntity(
            Capabilities.EnergyStorage.BLOCK,
            LIQUEFIER_ENERGY_PORT.get(),
            (entity, side) -> entity.energyInput());

        event.registerBlockEntity(
            Capabilities.EnergyStorage.BLOCK,
            ACCELERATOR_PART.get(),
            (tile, side) -> tile.partType().role == ENERGY_PORT ? tile.energyCapability() : null);
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            ACCELERATOR_PART.get(),
            (tile, side) -> tile.itemCapability());
        event.registerBlockEntity(
            Capabilities.FluidHandler.BLOCK,
            ACCELERATOR_PART.get(),
            (tile, side) -> tile.fluidCapability());
        event.registerBlockEntity(
            QMDParticleCapabilities.BLOCK,
            ACCELERATOR_PART.get(),
            (tile, side) -> tile.partType().role == BEAM_PORT || tile.partType().role == SYNCHROTRON_PORT
                ? tile.particleCapability(side)
                : null);

        event.registerBlockEntity(
            Capabilities.EnergyStorage.BLOCK,
            PARTICLE_CHAMBER.get(),
            (part, side) -> part.energyCapability());
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            PARTICLE_CHAMBER.get(),
            (part, side) -> part.itemCapability());
        event.registerBlockEntity(
            Capabilities.FluidHandler.BLOCK,
            PARTICLE_CHAMBER.get(),
            (part, side) -> part.fluidCapability());
        event.registerBlockEntity(
            QMDParticleCapabilities.BLOCK,
            PARTICLE_CHAMBER.get(),
            (part, side) -> part.particleCapability(side));

        event.registerBlockEntity(
            Capabilities.EnergyStorage.BLOCK,
            STRONTIUM_RTG.get(),
            (entity, side) -> entity.getEnergySideCapability(side));

        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            IRRADIATOR.get(),
            IrradiatorBlockEntity::itemCapability);
        event.registerBlockEntity(
            CapabilityRegistration.CAPABILITY_RADIATION_RESISTANCE,
            IRRADIATOR.get(),
            (tile, ignored) -> tile.resistance());
        event.registerBlockEntity(
            CapabilityRegistration.CAPABILITY_RADIATION_SOURCE,
            IRRADIATOR.get(),
            (tile, ignored) -> tile.radiationSource());

        event.registerBlockEntity(QMDParticleCapabilities.BLOCK, BEAMLINE.get(), (tile, side) -> tile.capability(side));
    }

    private QMDBlockEntities() {}
}
