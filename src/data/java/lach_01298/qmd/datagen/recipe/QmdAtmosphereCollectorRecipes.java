package lach_01298.qmd.datagen.recipe;

import java.util.List;
import lach_01298.qmd.recipe.CollectorRecipe;
import lach_01298.qmd.recipe.WorldCondition;
import net.minecraft.data.recipes.RecipeOutput;

import static lach_01298.qmd.datagen.recipe.QmdRecipeBuilders.*;
import static lach_01298.qmd.datagen.recipe.QmdRecipeIngredients.*;

/** Typed AtmosphereCollector recipe declarations. */
final class QmdAtmosphereCollectorRecipes {
    static void add(RecipeOutput output) {
        collector(CollectorRecipe.Kind.ATMOSPHERE)
                .output(fluidStack("qmd:compressed_air", 1000))
                .world(new WorldCondition(List.of(), List.of(), List.of(), List.of(id("minecraft:overworld"))))
                .save(output, id("qmd:atmosphere_collector/compressed_air"));
    }

    private QmdAtmosphereCollectorRecipes() {}
}
