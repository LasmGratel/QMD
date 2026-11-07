package lach_01298.qmd.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.Unpooled;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.network.connection.ConnectionType;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/** Real registry/tag coverage for collector world recipes, data validation and network serialization. */
@GameTestHolder("qmd")
@PrefixGameTestTemplate(false)
public final class CollectorRecipeGameTests {
    private CollectorRecipeGameTests() {
    }

    @GameTest(template = "empty")
    public static void defaultRecipesRetainWorldAndOutputConditions(GameTestHelper helper) {
        List<RecipeHolder<CollectorRecipe>> liquid = helper.getLevel().getRecipeManager()
                .getAllRecipesFor(QMDMachineRecipes.LIQUID_COLLECTOR_TYPE.get());
        List<RecipeHolder<CollectorRecipe>> atmosphere = helper.getLevel().getRecipeManager()
                .getAllRecipesFor(QMDMachineRecipes.ATMOSPHERE_COLLECTOR_TYPE.get());
        Set<ResourceLocation> ids = liquid.stream().map(RecipeHolder::id).collect(Collectors.toSet());
        helper.assertTrue(ids.containsAll(Set.of(id("qmd:liquid_collector/water"), id("qmd:liquid_collector/salt_water"),
                        id("qmd:liquid_collector/lava")))
                        && atmosphere.stream().anyMatch(holder -> holder.id().equals(id("qmd:atmosphere_collector/compressed_air"))),
                "The real recipe manager must contain all four default collector recipes");
        CollectorRecipe water = find(liquid, "qmd:liquid_collector/water");
        CollectorRecipe salt = find(liquid, "qmd:liquid_collector/salt_water");
        CollectorRecipe lava = find(liquid, "qmd:liquid_collector/lava");
        CollectorRecipe air = find(atmosphere, "qmd:atmosphere_collector/compressed_air");
        assertOutput(helper, water, "minecraft:water", 5000);
        assertOutput(helper, salt, "qmd:salt_water", 1000);
        assertOutput(helper, lava, "minecraft:lava", 100);
        assertOutput(helper, air, "qmd:compressed_air", 1000);

        for (ResourceKey<Biome> freshBiome : List.of(Biomes.RIVER, Biomes.FROZEN_RIVER, Biomes.SWAMP, Biomes.MANGROVE_SWAMP)) {
            helper.assertTrue(matches(helper, water, Blocks.WATER.defaultBlockState(), freshBiome, Level.OVERWORLD),
                    "Fresh-water recipe must accept its modern river and swamp biomes: " + freshBiome.location());
            helper.assertTrue(!matches(helper, salt, Blocks.WATER.defaultBlockState(), freshBiome, Level.OVERWORLD),
                    "Salt-water recipe must not accept fresh-water biomes");
        }
        for (ResourceKey<Biome> saltBiome : List.of(Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.FROZEN_OCEAN,
                Biomes.DEEP_FROZEN_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.LUKEWARM_OCEAN,
                Biomes.DEEP_LUKEWARM_OCEAN, Biomes.WARM_OCEAN, Biomes.BEACH, Biomes.SNOWY_BEACH, Biomes.STONY_SHORE)) {
            helper.assertTrue(matches(helper, salt, Blocks.WATER.defaultBlockState(), saltBiome, Level.OVERWORLD),
                    "Salt-water recipe must accept modern ocean and beach variants: " + saltBiome.location());
            helper.assertTrue(!matches(helper, water, Blocks.WATER.defaultBlockState(), saltBiome, Level.OVERWORLD),
                    "Fresh-water recipe must not accept saline biomes");
        }
        BlockState flowingWater = Blocks.WATER.defaultBlockState().setValue(LiquidBlock.LEVEL, 1);
        BlockState flowingLava = Blocks.LAVA.defaultBlockState().setValue(LiquidBlock.LEVEL, 1);
        helper.assertTrue(!matches(helper, water, flowingWater, Biomes.RIVER, Level.OVERWORLD)
                        && !matches(helper, salt, flowingWater, Biomes.OCEAN, Level.OVERWORLD)
                        && !matches(helper, lava, flowingLava, Biomes.NETHER_WASTES, Level.NETHER),
                "Default liquid recipes must require the source-state property of the sampled block");
        helper.assertTrue(!matches(helper, water, Blocks.WATER.defaultBlockState(), Biomes.PLAINS, Level.OVERWORLD)
                        && !matches(helper, water, Blocks.WATER.defaultBlockState(), Biomes.RIVER, Level.NETHER)
                        && !matches(helper, salt, Blocks.LAVA.defaultBlockState(), Biomes.OCEAN, Level.OVERWORLD),
                "Block, biome and dimension conditions must all hold");
        helper.assertTrue(matches(helper, lava, Blocks.LAVA.defaultBlockState(), Biomes.NETHER_WASTES, Level.NETHER)
                        && matches(helper, lava, Blocks.LAVA.defaultBlockState(), Biomes.CRIMSON_FOREST, Level.NETHER)
                        && !matches(helper, lava, Blocks.LAVA.defaultBlockState(), Biomes.PLAINS, Level.OVERWORLD),
                "Lava collection must retain unrestricted Nether biome matching and its dimension restriction");
        helper.assertTrue(matches(helper, air, Blocks.AIR.defaultBlockState(), Biomes.PLAINS, Level.OVERWORLD)
                        && matches(helper, air, Blocks.STONE.defaultBlockState(), Biomes.OCEAN, Level.OVERWORLD)
                        && !matches(helper, air, Blocks.AIR.defaultBlockState(), Biomes.NETHER_WASTES, Level.NETHER),
                "Atmosphere collection must accept any overworld biome and leave block sampling unrestricted");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void worldWildcardsAlternativesAndBiomeTagsMatch(GameTestHelper helper) {
        Holder<Biome> river = biome(helper, Biomes.RIVER);
        Holder<Biome> nether = biome(helper, Biomes.NETHER_WASTES);
        helper.assertTrue(river.is(BiomeTags.IS_OVERWORLD) && !nether.is(BiomeTags.IS_OVERWORLD),
                "The test must use the actual loaded overworld biome tag");
        helper.assertTrue(WorldCondition.ANY.matches(Blocks.AIR.defaultBlockState(), river, Level.OVERWORLD)
                        && WorldCondition.ANY.matches(Blocks.LAVA.defaultBlockState(), nether, Level.NETHER),
                "Empty block, biome, biome-tag and dimension lists must remain wildcards");
        WorldCondition alternatives = new WorldCondition(List.of(
                new WorldCondition.BlockMatch(Blocks.WATER, Map.of("level", "0")),
                new WorldCondition.BlockMatch(Blocks.LAVA, Map.of())),
                List.of(Biomes.NETHER_WASTES.location()), List.of(BiomeTags.IS_OVERWORLD.location()),
                List.of(Level.OVERWORLD.location(), Level.NETHER.location()));
        helper.assertTrue(alternatives.matches(Blocks.WATER.defaultBlockState(), river, Level.OVERWORLD)
                        && alternatives.matches(Blocks.LAVA.defaultBlockState().setValue(LiquidBlock.LEVEL, 7), nether, Level.NETHER),
                "Block alternatives and biome-ID/tag alternatives must be ORed, and omitted properties must match all values");
        helper.assertTrue(!alternatives.matches(Blocks.WATER.defaultBlockState().setValue(LiquidBlock.LEVEL, 7), river, Level.OVERWORLD)
                        && !alternatives.matches(Blocks.STONE.defaultBlockState(), river, Level.OVERWORLD)
                        && !alternatives.matches(Blocks.LAVA.defaultBlockState(), biome(helper, Biomes.CRIMSON_FOREST), Level.NETHER)
                        && !alternatives.matches(Blocks.LAVA.defaultBlockState(), river, Level.END),
                "A failed block, biome alternative set or dimension must reject the complete condition");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void collectorDataAndNetworkCodecsPreserveEveryField(GameTestHelper helper) {
        WorldCondition world = new WorldCondition(List.of(
                new WorldCondition.BlockMatch(Blocks.WATER, Map.of("level", "0")),
                new WorldCondition.BlockMatch(Blocks.LAVA, Map.of("level", "1"))),
                List.of(Biomes.RIVER.location(), Biomes.FROZEN_RIVER.location()),
                List.of(BiomeTags.IS_OVERWORLD.location()),
                List.of(Level.OVERWORLD.location(), Level.NETHER.location()));
        FluidStack output = new FluidStack(Fluids.WATER, Integer.MAX_VALUE);
        output.set(DataComponents.CUSTOM_NAME, Component.literal("Collector codec test"));
        for (CollectorRecipe.Kind kind : CollectorRecipe.Kind.values()) {
            CollectorRecipe original = new CollectorRecipe(kind, world, output, -73);
            var serializer = kind == CollectorRecipe.Kind.LIQUID ? QMDMachineRecipes.LIQUID_COLLECTOR_SERIALIZER.get()
                    : QMDMachineRecipes.ATMOSPHERE_COLLECTOR_SERIALIZER.get();
            var registryOps = helper.getLevel().registryAccess().createSerializationContext(JsonOps.INSTANCE);
            JsonElement encoded = serializer.codec().codec().encodeStart(registryOps, original).getOrThrow();
            assertEquivalent(helper, original, serializer.codec().codec().parse(registryOps, encoded).getOrThrow(), "data");
            RegistryFriendlyByteBuf buffer = newBuffer(helper);
            try {
                serializer.streamCodec().encode(buffer, original);
                assertEquivalent(helper, original, serializer.streamCodec().decode(buffer), "network");
                helper.assertTrue(buffer.readableBytes() == 0, "Collector network decoder must consume its entire payload");
            } finally {
                buffer.release();
            }
            FluidStack detachedOutput = original.output();
            detachedOutput.setAmount(1);
            helper.assertTrue(original.output().getAmount() == Integer.MAX_VALUE,
                    "Callers must not be able to mutate a loaded recipe through its output accessor");
        }
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void invalidCollectorDataAndNetworkPayloadsAreRejected(GameTestHelper helper) {
        String validOutput = "\"output\":{\"id\":\"minecraft:water\",\"amount\":1}";
        List<String> invalid = List.of(
                "{\"world\":{\"blocks\":[{\"block\":\"qmd:missing_collector_block\"}]}," + validOutput + "}",
                "{\"world\":{\"blocks\":[{\"block\":\"minecraft:water\",\"properties\":{\"missing\":\"0\"}}]}," + validOutput + "}",
                "{\"world\":{\"blocks\":[{\"block\":\"minecraft:water\",\"properties\":{\"level\":\"16\"}}]}," + validOutput + "}",
                "{\"output\":{}}",
                "{\"output\":{\"id\":\"minecraft:empty\",\"amount\":1}}",
                "{\"output\":{\"id\":\"qmd:missing_collector_fluid\",\"amount\":1}}",
                "{\"output\":{\"id\":\"minecraft:water\",\"amount\":0}}",
                "{\"output\":{\"id\":\"minecraft:water\",\"amount\":-1}}");
        var registryOps = helper.getLevel().registryAccess().createSerializationContext(JsonOps.INSTANCE);
        for (CollectorRecipe.Kind kind : CollectorRecipe.Kind.values()) {
            CollectorRecipe.Serializer serializer = new CollectorRecipe.Serializer(kind);
            for (String json : invalid) {
                JsonElement input = JsonParser.parseString(json);
                helper.assertTrue(serializer.codec().codec().parse(registryOps, input).result().isEmpty(),
                        "Collector data codec must reject invalid content: " + json);
                RegistryFriendlyByteBuf buffer = newBuffer(helper);
                boolean rejected = false;
                try {
                    ByteBufCodecs.TAG.encode(buffer, JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, input));
                    try {
                        serializer.streamCodec().decode(buffer);
                    } catch (RuntimeException expected) {
                        rejected = true;
                    }
                } finally {
                    buffer.release();
                }
                helper.assertTrue(rejected, "Collector network codec must apply the same validation: " + json);
            }
            CollectorRecipe maximum = serializer.codec().codec().parse(registryOps, JsonParser.parseString(
                    "{\"output\":{\"id\":\"minecraft:water\",\"amount\":2147483647}}")).getOrThrow();
            helper.assertTrue(maximum.output().getAmount() == Integer.MAX_VALUE,
                    "Positive rates up to the normal integer maximum must remain valid for custom low-efficiency recipes");
        }
        helper.succeed();
    }

    private static CollectorRecipe find(List<RecipeHolder<CollectorRecipe>> recipes, String recipeId) {
        return recipes.stream().filter(holder -> holder.id().equals(id(recipeId))).findFirst().orElseThrow().value();
    }

    private static boolean matches(GameTestHelper helper, CollectorRecipe recipe, BlockState block,
                                   ResourceKey<Biome> biome, ResourceKey<Level> dimension) {
        return recipe.matches(new CollectorRecipe.Input(block, biome(helper, biome), dimension), helper.getLevel());
    }

    private static Holder<Biome> biome(GameTestHelper helper, ResourceKey<Biome> key) {
        return helper.getLevel().registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(key);
    }

    private static void assertOutput(GameTestHelper helper, CollectorRecipe recipe, String fluid, int amount) {
        helper.assertTrue(BuiltInRegistries.FLUID.getKey(recipe.output().getFluid()).equals(id(fluid))
                        && recipe.output().getAmount() == amount,
                "Default collector output must be " + amount + " mB/t of " + fluid);
    }

    private static void assertEquivalent(GameTestHelper helper, CollectorRecipe original, CollectorRecipe decoded, String codec) {
        helper.assertTrue(decoded.kind() == original.kind() && decoded.priority() == original.priority()
                        && decoded.world().equals(original.world()) && FluidStack.matches(decoded.output(), original.output()),
                "Collector " + codec + " codec must preserve kind, every world predicate, priority, fluid, amount and components");
    }

    private static RegistryFriendlyByteBuf newBuffer(GameTestHelper helper) {
        return new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess(), ConnectionType.NEOFORGE);
    }

    private static ResourceLocation id(String value) {
        return ResourceLocation.parse(value);
    }
}
