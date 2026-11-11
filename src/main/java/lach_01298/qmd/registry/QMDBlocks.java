package lach_01298.qmd.registry;

import com.nred.nuclearcraft.block.processor.ProcessorBlock;
import com.nred.nuclearcraft.multiblock.fisson.FissionNeutronShieldType;
import com.nred.nuclearcraft.multiblock.hx.HeatExchanger;
import com.nred.nuclearcraft.multiblock.hx.IHeatExchangerPartType;
import com.nred.nuclearcraft.multiblock.rtg.RTGType;
import com.nred.nuclearcraft.multiblock.turbine.TurbineRotorBladeType;
import com.nred.nuclearcraft.radiation.RadSources;
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartBlock;
import lach_01298.qmd.QuantumMinecraftDynamics;
import lach_01298.qmd.block.BeamlineBlock;
import lach_01298.qmd.block.chamber.AbstractChamberBlock;
import lach_01298.qmd.block.CollectorBlock;
import lach_01298.qmd.block.CreativeParticleSourceBlock;
import lach_01298.qmd.block.DischargeLampBlock;
import lach_01298.qmd.block.IrradiatorBlock;
import lach_01298.qmd.block.entity.OreLeacherBlockEntity;
import lach_01298.qmd.block.entity.OreLeacherMachineInfo;
import lach_01298.qmd.config.FissionConfig;
import lach_01298.qmd.config.TurbineConfig;
import lach_01298.qmd.item.player.LuminousPaintBlock;
import lach_01298.qmd.multiblock.ChamberPart;
import lach_01298.qmd.multiblock.QMDFissionPartType;
import lach_01298.qmd.multiblock.QMDRtgPartType;
import lach_01298.qmd.multiblock.QMDTurbinePartType;
import lach_01298.qmd.multiblock.accelerator.AcceleratorCoolers;
import lach_01298.qmd.multiblock.accelerator.AcceleratorKind;
import lach_01298.qmd.multiblock.accelerator.AcceleratorMaterial;
import lach_01298.qmd.multiblock.accelerator.AcceleratorPartType;
import lach_01298.qmd.multiblock.liquefier.LiquefierPartType;
import lach_01298.qmd.recipe.CollectorRecipe;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static lach_01298.qmd.multiblock.accelerator.AcceleratorPartType.Role.*;

/** All QMD blocks and their multiblock part definitions. */
public final class QMDBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(QuantumMinecraftDynamics.MODID);

    public static final DeferredBlock<ProcessorBlock<OreLeacherBlockEntity>> ORE_LEACHER = BLOCKS
        .register("ore_leacher", () -> new ProcessorBlock<>(OreLeacherMachineInfo.KEY));

    public static final DeferredBlock<CollectorBlock> LIQUID_COLLECTOR = BLOCKS
        .register("liquid_collector", () -> new CollectorBlock(CollectorRecipe.Kind.LIQUID));

    public static final DeferredBlock<CollectorBlock> ATMOSPHERE_COLLECTOR = BLOCKS
        .register("atmosphere_collector", () -> new CollectorBlock(CollectorRecipe.Kind.ATMOSPHERE));

    public static final DeferredBlock<CreativeParticleSourceBlock> CREATIVE_PARTICLE_SOURCE = BLOCKS
        .register("creative_particle_source", () -> new CreativeParticleSourceBlock());

    public static final DeferredBlock<IrradiatorBlock> IRRADIATOR = BLOCKS
        .register("irradiator", () -> new IrradiatorBlock());

    public static final DeferredBlock<MultiblockPartBlock<HeatExchanger, IHeatExchangerPartType>> LIQUEFIER_CONTROLLER = registerLiquefierPart(
        "liquefier_controller",
        LiquefierPartType.CONTROLLER);

    public static final DeferredBlock<MultiblockPartBlock<HeatExchanger, IHeatExchangerPartType>> LIQUEFIER_COPPER_COMPRESSOR = registerLiquefierPart(
        "copper_liquefier_compressor",
        LiquefierPartType.COMPRESSOR);

    public static final DeferredBlock<MultiblockPartBlock<HeatExchanger, IHeatExchangerPartType>> LIQUEFIER_NEODYMIUM_COMPRESSOR = registerLiquefierPart(
        "neodymium_liquefier_compressor",
        LiquefierPartType.COMPRESSOR);

    public static final DeferredBlock<MultiblockPartBlock<HeatExchanger, IHeatExchangerPartType>> LIQUEFIER_SAMARIUM_COBALT_COMPRESSOR = registerLiquefierPart(
        "samarium_cobalt_liquefier_compressor",
        LiquefierPartType.COMPRESSOR);

    public static final DeferredBlock<MultiblockPartBlock<HeatExchanger, IHeatExchangerPartType>> LIQUEFIER_NOZZLE = registerLiquefierPart(
        "liquefier_nozzle",
        LiquefierPartType.NOZZLE);

    public static final DeferredBlock<MultiblockPartBlock<HeatExchanger, IHeatExchangerPartType>> LIQUEFIER_FLUID_PORT = registerLiquefierPart(
        "liquefier_fluid_port",
        LiquefierPartType.FLUID_PORT);

    public static final DeferredBlock<MultiblockPartBlock<HeatExchanger, IHeatExchangerPartType>> LIQUEFIER_ENERGY_PORT = registerLiquefierPart(
        "liquefier_energy_port",
        LiquefierPartType.ENERGY_PORT);

    public static final Map<String, DeferredBlock<Block>> ACCELERATOR_BLOCKS = new LinkedHashMap<>();

    public static final Map<String, AcceleratorPartType> ACCELERATOR_PARTS = new LinkedHashMap<>();

    static {
        for (var kind : AcceleratorKind.values())
            registerAcceleratorPart(kind.id + "_controller", CONTROLLER, kind, null, 0);
        registerAcceleratorPart("accelerator_casing", CASING);
        registerAcceleratorPart("accelerator_glass", GLASS);
        registerAcceleratorPart("accelerator_beam", BEAM);
        registerAcceleratorPart("accelerator_yoke", YOKE);
        registerAcceleratorPart("accelerator_beam_port", BEAM_PORT);
        registerAcceleratorPart("accelerator_synchrotron_port", SYNCHROTRON_PORT);
        registerAcceleratorPart("accelerator_energy_port", ENERGY_PORT);
        registerAcceleratorPart("accelerator_coolant_inlet", COOLANT_INLET);
        registerAcceleratorPart("accelerator_coolant_outlet", COOLANT_OUTLET);
        registerAcceleratorPart("accelerator_redstone_port", REDSTONE_PORT);
        registerAcceleratorPart("accelerator_ion_collector", COLLECTOR);
        registerAcceleratorPart("accelerator_port", PROCESS_PORT);
        registerAcceleratorPart("accelerator_computer_port", COMPUTER_PORT);
        registerAcceleratorPart("accelerator_ion_source_basic", SOURCE, null, null, 0);
        registerAcceleratorPart("accelerator_ion_source_laser", SOURCE, null, null, 1);
        for (var material : AcceleratorMaterial.values()) {
            registerAcceleratorPart("accelerator_rf_cavity_" + material.id, CAVITY, null, material, 0);
            registerAcceleratorPart("accelerator_magnet_" + material.id, MAGNET, null, material, 0);
        }
        for (int n = 0; n < AcceleratorCoolers.NAMES.length; n++)
            registerAcceleratorPart("accelerator_cooler_" + AcceleratorCoolers.NAMES[n], COOLER, null, null, n);
    }

    public static final Map<ChamberPart, DeferredBlock<AbstractChamberBlock>> CHAMBER_PARTS;

    static {
        var parts = new EnumMap<ChamberPart, DeferredBlock<AbstractChamberBlock>>(ChamberPart.class);
        for (ChamberPart role : ChamberPart.values()) {
            var block = BLOCKS.register(role.id, () -> AbstractChamberBlock.create(role));

            parts.put(role, block);
        }
        CHAMBER_PARTS = Map.copyOf(parts);
    }

    public static final FissionNeutronShieldType HAFNIUM_SHIELD_TYPE = new FissionNeutronShieldType(
        "hafnium",
        FissionConfig.SHIELD_HEAT::get,
        FissionConfig.SHIELD_EFFICIENCY::get);

    public static final RTGType STRONTIUM_RTG_TYPE = new RTGType(
        "strontium",
        FissionConfig.RTG_POWER::get,
        RadSources.STRONTIUM_90);

    public static final DeferredBlock<Block> TUNGSTEN_CARBIDE_REFLECTOR = BLOCKS.registerSimpleBlock(
        "reflector_tungsten_carbide",
        Block.Properties.of()
            .strength(2F, 15F)
            .requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> HAFNIUM_SHIELD = BLOCKS
        .register("neutron_shield_hafnium", () -> QMDFissionPartType.SHIELD.createBlock(HAFNIUM_SHIELD_TYPE));

    public static final DeferredBlock<Block> STRONTIUM_RTG = BLOCKS
        .register("rtg_strontium", () -> QMDRtgPartType.STRONTIUM.createBlock(STRONTIUM_RTG_TYPE));

    public static final DeferredBlock<Block> STRONTIUM_90_BLOCK = BLOCKS.registerSimpleBlock(
        "strontium_90_block",
        Block.Properties.of()
            .strength(3F, 15F)
            .requiresCorrectToolForDrops());

    public static final TurbineRotorBladeType SUPER_ALLOY_BLADE_TYPE = new TurbineRotorBladeType(
        "super_alloy",
        TurbineConfig.BLADE_EFFICIENCY::get,
        TurbineConfig.BLADE_EXPANSION::get);

    public static final DeferredBlock<Block> SUPER_ALLOY_TURBINE_BLADE = BLOCKS.register(
        "turbine_blade_super_alloy",
        () -> QMDTurbinePartType.ROTOR_BLADE.createBlock(SUPER_ALLOY_BLADE_TYPE));

    public static final DeferredBlock<BeamlineBlock> BEAMLINE = BLOCKS.register("beamline", () -> new BeamlineBlock());

    public static final Map<String, DeferredBlock<DischargeLampBlock>> DISCHARGE_LAMPS;

    static {
        Map<String, DeferredBlock<DischargeLampBlock>> lamps = new LinkedHashMap<>();
        for (String gas : new String[] { "empty", "hydrogen", "helium", "nitrogen", "oxygen", "neon", "argon", "sodium",
            "mercury" }) {
            var block = BLOCKS.register(
                "discharge_lamp_" + gas,
                () -> new DischargeLampBlock(
                    Block.Properties.of()
                        .strength(2F, 15F)
                        .requiresCorrectToolForDrops()));

            lamps.put(gas, block);
        }
        DISCHARGE_LAMPS = Collections.unmodifiableMap(lamps);
    }

    public static final DeferredBlock<LuminousPaintBlock> GREEN_LUMINOUS_PAINT_BLOCK = BLOCKS
        .register("block_green_luminous_paint", LuminousPaintBlock::new);

    public static final DeferredBlock<LuminousPaintBlock> BLUE_LUMINOUS_PAINT_BLOCK = BLOCKS
        .register("block_blue_luminous_paint", LuminousPaintBlock::new);

    public static final DeferredBlock<LuminousPaintBlock> ORANGE_LUMINOUS_PAINT_BLOCK = BLOCKS
        .register("block_orange_luminous_paint", LuminousPaintBlock::new);

    public static final List<DeferredBlock<MultiblockPartBlock<HeatExchanger, IHeatExchangerPartType>>> LIQUEFIER_PARTS = List
        .of(
            LIQUEFIER_CONTROLLER,
            LIQUEFIER_COPPER_COMPRESSOR,
            LIQUEFIER_NEODYMIUM_COMPRESSOR,
            LIQUEFIER_SAMARIUM_COBALT_COMPRESSOR,
            LIQUEFIER_NOZZLE,
            LIQUEFIER_FLUID_PORT,
            LIQUEFIER_ENERGY_PORT);

    public static final List<DeferredBlock<Block>> FISSION_BLOCKS = List
        .of(TUNGSTEN_CARBIDE_REFLECTOR, HAFNIUM_SHIELD, STRONTIUM_RTG, STRONTIUM_90_BLOCK);

    private static void registerAcceleratorPart(String id, AcceleratorPartType.Role role) {
        registerAcceleratorPart(id, role, null, null, 0);
    }

    private static void registerAcceleratorPart(String id, AcceleratorPartType.Role role, AcceleratorKind kind,
        AcceleratorMaterial material, int variant) {
        AcceleratorPartType type = new AcceleratorPartType(id, role, kind, material, variant);
        ACCELERATOR_PARTS.put(id, type);
        DeferredBlock<Block> block = BLOCKS.register(id, () -> type.createBlock());
        ACCELERATOR_BLOCKS.put(id, block);
    }

    public static AcceleratorPartType acceleratorTypeOf(Block block) {
        for (var entry : ACCELERATOR_BLOCKS.entrySet()) if (entry.getValue()
            .get() == block) return ACCELERATOR_PARTS.get(entry.getKey());
        throw new IllegalArgumentException("Unknown accelerator block " + block);
    }

    private static DeferredBlock<MultiblockPartBlock<HeatExchanger, IHeatExchangerPartType>> registerLiquefierPart(
        String name, LiquefierPartType type) {
        return BLOCKS.register(name, () -> type.createBlock());
    }

    public static List<Block> liquefierBlocks() {
        return LIQUEFIER_PARTS.stream()
            .map(holder -> (Block) holder.get())
            .toList();
    }

    private QMDBlocks() {}
}
