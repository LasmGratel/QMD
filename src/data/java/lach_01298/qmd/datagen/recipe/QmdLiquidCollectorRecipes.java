package lach_01298.qmd.datagen.recipe;

import java.util.List;
import java.util.Map;
import lach_01298.qmd.recipe.CollectorRecipe;
import lach_01298.qmd.recipe.WorldCondition;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.data.recipes.RecipeOutput;

import static lach_01298.qmd.datagen.recipe.QmdRecipeBuilders.*;
import static lach_01298.qmd.datagen.recipe.QmdRecipeIngredients.*;

/** Typed LiquidCollector recipe declarations. */
final class QmdLiquidCollectorRecipes {
    static void add(RecipeOutput output) {
        collector(CollectorRecipe.Kind.LIQUID)
                .output(fluidStack("minecraft:lava", 100))
                .world(new WorldCondition(List.of(new WorldCondition.BlockMatch(Blocks.LAVA, Map.of("level", "0"))), List.of(), List.of(), List.of(id("minecraft:the_nether"))))
                .save(output, id("qmd:liquid_collector/lava"));

        collector(CollectorRecipe.Kind.LIQUID)
                .output(fluidStack("qmd:salt_water", 1000))
                .world(new WorldCondition(List.of(new WorldCondition.BlockMatch(Blocks.WATER, Map.of("level", "0"))), List.of(id("minecraft:ocean"), id("minecraft:deep_ocean"), id("minecraft:frozen_ocean"), id("minecraft:deep_frozen_ocean"), id("minecraft:cold_ocean"), id("minecraft:deep_cold_ocean"), id("minecraft:lukewarm_ocean"), id("minecraft:deep_lukewarm_ocean"), id("minecraft:warm_ocean"), id("minecraft:beach"), id("minecraft:snowy_beach"), id("minecraft:stony_shore")), List.of(), List.of(id("minecraft:overworld"))))
                .save(output, id("qmd:liquid_collector/salt_water"));

        collector(CollectorRecipe.Kind.LIQUID)
                .output(fluidStack("minecraft:water", 5000))
                .world(new WorldCondition(List.of(new WorldCondition.BlockMatch(Blocks.WATER, Map.of("level", "0"))), List.of(id("minecraft:river"), id("minecraft:frozen_river"), id("minecraft:swamp"), id("minecraft:mangrove_swamp")), List.of(), List.of(id("minecraft:overworld"))))
                .save(output, id("qmd:liquid_collector/water"));
    }

    private QmdLiquidCollectorRecipes() {}
}
