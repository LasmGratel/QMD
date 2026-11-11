package lach_01298.qmd.compat.jei.recipe;

import lach_01298.qmd.recipe.CollectorRecipe;
import lach_01298.qmd.recipe.WorldCondition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** The complete, stable predicate description, kept independent of client and JEI runtime classes. */
public final class CollectorWorldInfo {

    private CollectorWorldInfo() {}

    public static List<Component> describe(RecipeHolder<CollectorRecipe> holder) {
        WorldCondition world = holder.value()
            .world();
        List<Component> result = new ArrayList<>();
        result.add(Component.translatable("gui.qmd.jei.collector.logic"));
        addSection(
            result,
            "blocks",
            world.blocks()
                .stream()
                .map(CollectorWorldInfo::blockDescription)
                .toList());
        List<String> biomes = new ArrayList<>();
        world.biomes()
            .forEach(biome -> biomes.add(biome.toString()));
        world.biomeTags()
            .forEach(tag -> biomes.add("#" + tag));
        addSection(result, "biomes", biomes);
        addSection(
            result,
            "dimensions",
            world.dimensions()
                .stream()
                .map(Object::toString)
                .toList());
        result.add(
            Component.translatable(
                "gui.qmd.jei.collector.priority",
                holder.value()
                    .priority()));
        result.add(
            Component.translatable(
                "gui.qmd.jei.collector.recipe",
                holder.id()
                    .toString()));
        return List.copyOf(result);
    }

    private static void addSection(List<Component> result, String key, List<String> alternatives) {
        if (alternatives.isEmpty()) {
            result.add(
                Component
                    .translatable("gui.qmd.jei.collector." + key, Component.translatable("gui.qmd.jei.collector.any")));
        } else {
            result.add(Component.translatable("gui.qmd.jei.collector." + key, ""));
            alternatives.forEach(alternative -> result.add(Component.literal("  " + alternative)));
        }
    }

    private static String blockDescription(WorldCondition.BlockMatch block) {
        String id = BuiltInRegistries.BLOCK.getKey(block.block())
            .toString();
        if (block.properties()
            .isEmpty()) return id;
        return id + block.properties()
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .map(property -> property.getKey() + "=" + property.getValue())
            .collect(Collectors.joining(",", "[", "]"));
    }
}
