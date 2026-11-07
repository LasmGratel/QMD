package lach_01298.qmd.recipe;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.List;
import java.util.Map;

/** Alternatives within each category are ORed; block, biome and dimension categories are ANDed. */
public record WorldCondition(List<BlockMatch> blocks, List<ResourceLocation> biomes,
                             List<ResourceLocation> biomeTags, List<ResourceLocation> dimensions) {
    public static final int MAX_ALTERNATIVES = 4096;
    public static final WorldCondition ANY = new WorldCondition(List.of(), List.of(), List.of(), List.of());
    private static final Codec<List<ResourceLocation>> IDS = ResourceLocation.CODEC.listOf(0, MAX_ALTERNATIVES);
    public static final Codec<WorldCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockMatch.CODEC.listOf(0, MAX_ALTERNATIVES).optionalFieldOf("blocks", List.of()).forGetter(WorldCondition::blocks),
            IDS.optionalFieldOf("biomes", List.of()).forGetter(WorldCondition::biomes),
            IDS.optionalFieldOf("biome_tags", List.of()).forGetter(WorldCondition::biomeTags),
            IDS.optionalFieldOf("dimensions", List.of()).forGetter(WorldCondition::dimensions)
    ).apply(instance, WorldCondition::new));

    public WorldCondition {
        blocks = checkedCopy(blocks);
        biomes = checkedCopy(biomes);
        biomeTags = checkedCopy(biomeTags);
        dimensions = checkedCopy(dimensions);
    }

    private static <T> List<T> checkedCopy(List<T> values) {
        if (values.size() > MAX_ALTERNATIVES) {
            throw new IllegalArgumentException("Too many collector world-condition alternatives");
        }
        return List.copyOf(values);
    }

    public boolean matches(BlockState state, Holder<Biome> biome, ResourceKey<Level> dimension) {
        return (blocks.isEmpty() || blocks.stream().anyMatch(block -> block.matches(state)))
                && (biomes.isEmpty() && biomeTags.isEmpty()
                || biomes.stream().anyMatch(id -> biome.is(ResourceKey.create(Registries.BIOME, id)))
                || biomeTags.stream().anyMatch(id -> biome.is(TagKey.create(Registries.BIOME, id))))
                && (dimensions.isEmpty() || dimensions.contains(dimension.location()));
    }

    /** Unspecified properties are unrestricted; specified properties must be valid for the named block. */
    public record BlockMatch(Block block, Map<String, String> properties) {
        private static final Codec<Map<String, String>> PROPERTIES =
                Codec.unboundedMap(Codec.string(1, 128), Codec.string(1, 128)).validate(properties ->
                        properties.size() <= 32 ? DataResult.success(properties)
                                : DataResult.error(() -> "Too many collector block-state properties"));
        public static final Codec<BlockMatch> CODEC = RecordCodecBuilder.<Pair<Block, Map<String, String>>>create(instance -> instance.group(
                BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(Pair::getFirst),
                PROPERTIES.optionalFieldOf("properties", Map.of()).forGetter(Pair::getSecond)
        ).apply(instance, Pair::of)).flatXmap(value -> {
            try {
                return DataResult.success(new BlockMatch(value.getFirst(), value.getSecond()));
            } catch (IllegalArgumentException exception) {
                return DataResult.error(exception::getMessage);
            }
        }, value -> DataResult.success(Pair.of(value.block(), value.properties())));

        public BlockMatch {
            properties = Map.copyOf(properties);
            if (properties.size() > 32) {
                throw new IllegalArgumentException("Too many collector block-state properties");
            }
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                Property<?> property = block.getStateDefinition().getProperty(entry.getKey());
                if (property == null || property.getValue(entry.getValue()).isEmpty()) {
                    throw new IllegalArgumentException("Invalid collector block property " + entry.getKey() + "="
                            + entry.getValue() + " for " + BuiltInRegistries.BLOCK.getKey(block));
                }
            }
        }

        public boolean matches(BlockState state) {
            if (!state.is(block)) {
                return false;
            }
            return properties.entrySet().stream().allMatch(entry -> {
                Property<?> property = block.getStateDefinition().getProperty(entry.getKey());
                return state.getValue(property).equals(property.getValue(entry.getValue()).orElseThrow());
            });
        }
    }
}
