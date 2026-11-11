package lach_01298.qmd.datagen.recipe;

import lach_01298.qmd.recipe.IrradiatorRecipe;
import net.minecraft.data.recipes.RecipeOutput;

import static lach_01298.qmd.datagen.recipe.QmdRecipeBuilders.*;
import static lach_01298.qmd.datagen.recipe.QmdRecipeIngredients.*;

/** Typed IrradiatorFuel recipe declarations. */
final class QmdIrradiatorFuelRecipes {
    static void add(RecipeOutput output) {
        irradiator(IrradiatorRecipe.Kind.FUEL)
                .input(item("qmd:source_cobalt_60"))
                .multiplier(1.0D)
                .save(output, id("qmd:irradiator_fuel/cobalt_60"));

        irradiator(IrradiatorRecipe.Kind.FUEL)
                .input(item("qmd:source_iridium_192"))
                .multiplier(10.0D)
                .save(output, id("qmd:irradiator_fuel/iridium_192"));
    }

    private QmdIrradiatorFuelRecipes() {}
}
