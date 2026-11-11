package lach_01298.qmd.registry;

import com.mojang.serialization.MapCodec;
import com.nred.nuclearcraft.recipe.NCRecipes;
import lach_01298.qmd.QuantumMinecraftDynamics;
import lach_01298.qmd.item.equipment.EquipmentEnabledCondition;
import lach_01298.qmd.item.equipment.HevArmorDyeRecipe;
import lach_01298.qmd.recipe.accelerator.AbstractAcceleratorRecipe;
import lach_01298.qmd.recipe.accelerator.AcceleratorSourceRecipe;
import lach_01298.qmd.recipe.accelerator.AcceleratorCoolingRecipe;
import lach_01298.qmd.recipe.accelerator.MassSpectrometerRecipe;

import lach_01298.qmd.recipe.chamber.AbstractChamberRecipe;
import lach_01298.qmd.recipe.chamber.ChamberKind;
import lach_01298.qmd.recipe.chamber.TargetChamberRecipe;
import lach_01298.qmd.recipe.chamber.DecayChamberRecipe;
import lach_01298.qmd.recipe.chamber.CollisionChamberRecipe;
import lach_01298.qmd.recipe.chamber.BeamDumpRecipe;
import lach_01298.qmd.recipe.chamber.NeutralContainmentRecipe;
import lach_01298.qmd.recipe.chamber.CellFillingRecipe;
import lach_01298.qmd.recipe.chamber.NucleosynthesisChamberRecipe;
import lach_01298.qmd.recipe.chamber.VacuumChamberHeatingRecipe;
import lach_01298.qmd.recipe.CollectorRecipe;
import lach_01298.qmd.recipe.IrradiatorRecipe;
import lach_01298.qmd.recipe.LiquefierRecipe;
import lach_01298.qmd.recipe.OreLeacherRecipe;
import lach_01298.qmd.recipe.OverrideNCRecipesCondition;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/** Recipe types, serializers, conditions and Neohaul processor handlers. */
public final class QMDRecipes {

    public static final String HANDLER_NAME = "qmd:ore_leacher";

    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister
        .create(Registries.RECIPE_TYPE, QuantumMinecraftDynamics.MODID);

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister
        .create(Registries.RECIPE_SERIALIZER, QuantumMinecraftDynamics.MODID);

    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITIONS = DeferredRegister
        .create(NeoForgeRegistries.Keys.CONDITION_CODECS, QuantumMinecraftDynamics.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<OreLeacherRecipe>> ORE_LEACHER_TYPE = TYPES
        .register("ore_leacher", () -> RecipeType.simple(ResourceLocation.parse(HANDLER_NAME)));

    public static final DeferredHolder<RecipeSerializer<?>, OreLeacherRecipe.Serializer> ORE_LEACHER_SERIALIZER = SERIALIZERS
        .register("ore_leacher", OreLeacherRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<CollectorRecipe>> LIQUID_COLLECTOR_TYPE = TYPES
        .register("liquid_collector", () -> RecipeType.simple(ResourceLocation.parse("qmd:liquid_collector")));

    public static final DeferredHolder<RecipeType<?>, RecipeType<CollectorRecipe>> ATMOSPHERE_COLLECTOR_TYPE = TYPES
        .register("atmosphere_collector", () -> RecipeType.simple(ResourceLocation.parse("qmd:atmosphere_collector")));

    public static final DeferredHolder<RecipeSerializer<?>, CollectorRecipe.Serializer> LIQUID_COLLECTOR_SERIALIZER = SERIALIZERS
        .register("liquid_collector", () -> new CollectorRecipe.Serializer(CollectorRecipe.Kind.LIQUID));

    public static final DeferredHolder<RecipeSerializer<?>, CollectorRecipe.Serializer> ATMOSPHERE_COLLECTOR_SERIALIZER = SERIALIZERS
        .register("atmosphere_collector", () -> new CollectorRecipe.Serializer(CollectorRecipe.Kind.ATMOSPHERE));

    public static final NCRecipes.BasicProcessorRecipeHandler ORE_LEACHER_HANDLER = new NCRecipes.BasicProcessorRecipeHandler(
        HANDLER_NAME,
        1,
        3,
        3,
        0) {};

    private static boolean initialized;

    public static final DeferredHolder<RecipeType<?>, RecipeType<AcceleratorSourceRecipe>> ACCELERATOR_SOURCE_TYPE = TYPES
        .register(
            "accelerator_source",
            () -> RecipeType
                .simple(ResourceLocation.fromNamespaceAndPath(QuantumMinecraftDynamics.MODID, "accelerator_source")));

    public static final DeferredHolder<RecipeSerializer<?>, AbstractAcceleratorRecipe.Serializer<AcceleratorSourceRecipe>> ACCELERATOR_SOURCE_SERIALIZER = SERIALIZERS
        .register("accelerator_source", AcceleratorSourceRecipe::serializer);

    public static final DeferredHolder<RecipeType<?>, RecipeType<AcceleratorCoolingRecipe>> ACCELERATOR_COOLING_TYPE = TYPES
        .register(
            "accelerator_cooling",
            () -> RecipeType
                .simple(ResourceLocation.fromNamespaceAndPath(QuantumMinecraftDynamics.MODID, "accelerator_cooling")));

    public static final DeferredHolder<RecipeSerializer<?>, AbstractAcceleratorRecipe.Serializer<AcceleratorCoolingRecipe>> ACCELERATOR_COOLING_SERIALIZER = SERIALIZERS
        .register("accelerator_cooling", AcceleratorCoolingRecipe::serializer);

    public static final DeferredHolder<RecipeType<?>, RecipeType<MassSpectrometerRecipe>> MASS_SPECTROMETER_TYPE = TYPES
        .register(
            "mass_spectrometer",
            () -> RecipeType
                .simple(ResourceLocation.fromNamespaceAndPath(QuantumMinecraftDynamics.MODID, "mass_spectrometer")));

    public static final DeferredHolder<RecipeSerializer<?>, AbstractAcceleratorRecipe.Serializer<MassSpectrometerRecipe>> MASS_SPECTROMETER_SERIALIZER = SERIALIZERS
        .register("mass_spectrometer", MassSpectrometerRecipe::serializer);

    public static final DeferredHolder<RecipeType<?>, RecipeType<TargetChamberRecipe>> TARGET_CHAMBER_TYPE = chamberType(
        ChamberKind.TARGET_CHAMBER);

    public static final DeferredHolder<RecipeSerializer<?>, AbstractChamberRecipe.Serializer<TargetChamberRecipe>> TARGET_CHAMBER_SERIALIZER = SERIALIZERS
        .register(ChamberKind.TARGET_CHAMBER.id(), TargetChamberRecipe::serializer);

    public static final DeferredHolder<RecipeType<?>, RecipeType<DecayChamberRecipe>> DECAY_CHAMBER_TYPE = chamberType(
        ChamberKind.DECAY_CHAMBER);

    public static final DeferredHolder<RecipeSerializer<?>, AbstractChamberRecipe.Serializer<DecayChamberRecipe>> DECAY_CHAMBER_SERIALIZER = SERIALIZERS
        .register(ChamberKind.DECAY_CHAMBER.id(), DecayChamberRecipe::serializer);

    public static final DeferredHolder<RecipeType<?>, RecipeType<CollisionChamberRecipe>> COLLISION_CHAMBER_TYPE = chamberType(
        ChamberKind.COLLISION_CHAMBER);

    public static final DeferredHolder<RecipeSerializer<?>, AbstractChamberRecipe.Serializer<CollisionChamberRecipe>> COLLISION_CHAMBER_SERIALIZER = SERIALIZERS
        .register(ChamberKind.COLLISION_CHAMBER.id(), CollisionChamberRecipe::serializer);

    public static final DeferredHolder<RecipeType<?>, RecipeType<BeamDumpRecipe>> BEAM_DUMP_TYPE = chamberType(
        ChamberKind.BEAM_DUMP);

    public static final DeferredHolder<RecipeSerializer<?>, AbstractChamberRecipe.Serializer<BeamDumpRecipe>> BEAM_DUMP_SERIALIZER = SERIALIZERS
        .register(ChamberKind.BEAM_DUMP.id(), BeamDumpRecipe::serializer);

    public static final DeferredHolder<RecipeType<?>, RecipeType<NeutralContainmentRecipe>> NEUTRAL_CONTAINMENT_TYPE = chamberType(
        ChamberKind.NEUTRAL_CONTAINMENT);

    public static final DeferredHolder<RecipeSerializer<?>, AbstractChamberRecipe.Serializer<NeutralContainmentRecipe>> NEUTRAL_CONTAINMENT_SERIALIZER = SERIALIZERS
        .register(ChamberKind.NEUTRAL_CONTAINMENT.id(), NeutralContainmentRecipe::serializer);

    public static final DeferredHolder<RecipeType<?>, RecipeType<CellFillingRecipe>> CELL_FILLING_TYPE = chamberType(
        ChamberKind.CELL_FILLING);

    public static final DeferredHolder<RecipeSerializer<?>, AbstractChamberRecipe.Serializer<CellFillingRecipe>> CELL_FILLING_SERIALIZER = SERIALIZERS
        .register(ChamberKind.CELL_FILLING.id(), CellFillingRecipe::serializer);

    public static final DeferredHolder<RecipeType<?>, RecipeType<NucleosynthesisChamberRecipe>> NUCLEOSYNTHESIS_CHAMBER_TYPE = chamberType(
        ChamberKind.NUCLEOSYNTHESIS_CHAMBER);

    public static final DeferredHolder<RecipeSerializer<?>, AbstractChamberRecipe.Serializer<NucleosynthesisChamberRecipe>> NUCLEOSYNTHESIS_CHAMBER_SERIALIZER = SERIALIZERS
        .register(ChamberKind.NUCLEOSYNTHESIS_CHAMBER.id(), NucleosynthesisChamberRecipe::serializer);

    public static final DeferredHolder<RecipeType<?>, RecipeType<VacuumChamberHeatingRecipe>> VACUUM_CHAMBER_HEATING_TYPE = chamberType(
        ChamberKind.VACUUM_CHAMBER_HEATING);

    public static final DeferredHolder<RecipeSerializer<?>, AbstractChamberRecipe.Serializer<VacuumChamberHeatingRecipe>> VACUUM_CHAMBER_HEATING_SERIALIZER = SERIALIZERS
        .register(ChamberKind.VACUUM_CHAMBER_HEATING.id(), VacuumChamberHeatingRecipe::serializer);

    private static <R extends AbstractChamberRecipe> DeferredHolder<RecipeType<?>, RecipeType<R>> chamberType(
        ChamberKind kind) {
        return TYPES.register(
            kind.id(),
            () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(QuantumMinecraftDynamics.MODID, kind.id())));
    }

    public static final DeferredHolder<RecipeType<?>, RecipeType<LiquefierRecipe>> LIQUEFIER_GAS_TYPE = TYPES
        .register("liquefier", () -> RecipeType.simple(ResourceLocation.parse("qmd:liquefier")));

    public static final DeferredHolder<RecipeType<?>, RecipeType<LiquefierRecipe>> LIQUEFIER_COOLANT_TYPE = TYPES
        .register("liquefier_coolant", () -> RecipeType.simple(ResourceLocation.parse("qmd:liquefier_coolant")));

    public static final DeferredHolder<RecipeSerializer<?>, LiquefierRecipe.Serializer> LIQUEFIER_GAS_SERIALIZER = SERIALIZERS
        .register("liquefier", () -> new LiquefierRecipe.Serializer(LiquefierRecipe.Kind.GAS));

    public static final DeferredHolder<RecipeSerializer<?>, LiquefierRecipe.Serializer> LIQUEFIER_COOLANT_SERIALIZER = SERIALIZERS
        .register("liquefier_coolant", () -> new LiquefierRecipe.Serializer(LiquefierRecipe.Kind.COOLANT));

    public static final DeferredHolder<RecipeType<?>, RecipeType<IrradiatorRecipe>> IRRADIATOR_TYPE = TYPES
        .register("irradiator", () -> RecipeType.simple(ResourceLocation.parse("qmd:irradiator")));

    public static final DeferredHolder<RecipeType<?>, RecipeType<IrradiatorRecipe>> IRRADIATOR_FUEL_TYPE = TYPES
        .register("irradiator_fuel", () -> RecipeType.simple(ResourceLocation.parse("qmd:irradiator_fuel")));

    public static final DeferredHolder<RecipeSerializer<?>, IrradiatorRecipe.Serializer> IRRADIATOR_SERIALIZER = SERIALIZERS
        .register("irradiator", () -> new IrradiatorRecipe.Serializer(IrradiatorRecipe.Kind.PROCESS));

    public static final DeferredHolder<RecipeSerializer<?>, IrradiatorRecipe.Serializer> IRRADIATOR_FUEL_SERIALIZER = SERIALIZERS
        .register("irradiator_fuel", () -> new IrradiatorRecipe.Serializer(IrradiatorRecipe.Kind.FUEL));

    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<HevArmorDyeRecipe>> ARMOUR_DYE_SERIALIZER = SERIALIZERS
        .register("armour_dye", () -> new SimpleCraftingRecipeSerializer<>(HevArmorDyeRecipe::new));

    static {
        CONDITIONS.register("override_nc_recipes", () -> OverrideNCRecipesCondition.CODEC);
        CONDITIONS.register("equipment_enabled", () -> EquipmentEnabledCondition.CODEC);
    }

    public static void register(IEventBus bus) {
        TYPES.register(bus);
        SERIALIZERS.register(bus);
        CONDITIONS.register(bus);
    }

    public static synchronized void initialize() {
        if (!initialized) {
            NCRecipes.putHandler(ORE_LEACHER_HANDLER);
            initialized = true;
        }
    }

    private QMDRecipes() {}
}
