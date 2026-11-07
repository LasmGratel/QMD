package lach_01298.qmd.recipe;

import com.nred.nuclearcraft.recipe.NCRecipes;
import com.mojang.serialization.MapCodec;
import lach_01298.qmd.QuantumMinecraftDynamics;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.common.conditions.ICondition;

/** QMD recipe registries and their Nuclearcraft processor handlers. */
public final class QMDMachineRecipes {
    public static final String HANDLER_NAME = "qmd:ore_leacher";
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, QuantumMinecraftDynamics.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, QuantumMinecraftDynamics.MODID);
    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITIONS =
            DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, QuantumMinecraftDynamics.MODID);

    static {
        CONDITIONS.register("override_nc_recipes", () -> OverrideNCRecipesCondition.CODEC);
    }

    public static final DeferredHolder<RecipeType<?>, RecipeType<OreLeacherRecipe>> ORE_LEACHER_TYPE =
            TYPES.register("ore_leacher", () -> RecipeType.simple(ResourceLocation.parse(HANDLER_NAME)));
    public static final DeferredHolder<RecipeSerializer<?>, OreLeacherRecipe.Serializer> ORE_LEACHER_SERIALIZER =
            SERIALIZERS.register("ore_leacher", OreLeacherRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<CollectorRecipe>> LIQUID_COLLECTOR_TYPE =
            TYPES.register("liquid_collector", () -> RecipeType.simple(ResourceLocation.parse("qmd:liquid_collector")));
    public static final DeferredHolder<RecipeType<?>, RecipeType<CollectorRecipe>> ATMOSPHERE_COLLECTOR_TYPE =
            TYPES.register("atmosphere_collector", () -> RecipeType.simple(ResourceLocation.parse("qmd:atmosphere_collector")));
    public static final DeferredHolder<RecipeSerializer<?>, CollectorRecipe.Serializer> LIQUID_COLLECTOR_SERIALIZER =
            SERIALIZERS.register("liquid_collector", () -> new CollectorRecipe.Serializer(CollectorRecipe.Kind.LIQUID));
    public static final DeferredHolder<RecipeSerializer<?>, CollectorRecipe.Serializer> ATMOSPHERE_COLLECTOR_SERIALIZER =
            SERIALIZERS.register("atmosphere_collector", () -> new CollectorRecipe.Serializer(CollectorRecipe.Kind.ATMOSPHERE));

    public static final NCRecipes.BasicProcessorRecipeHandler ORE_LEACHER_HANDLER =
            new NCRecipes.BasicProcessorRecipeHandler(HANDLER_NAME, 1, 3, 3, 0) {
            };

    private static boolean initialized;

    private QMDMachineRecipes() {
    }

    public static void register(IEventBus modBus) {
        TYPES.register(modBus);
        SERIALIZERS.register(modBus);
        CONDITIONS.register(modBus);
    }

    public static synchronized void initialize() {
        if (!initialized) {
            NCRecipes.putHandler(ORE_LEACHER_HANDLER);
            initialized = true;
        }
    }
}
