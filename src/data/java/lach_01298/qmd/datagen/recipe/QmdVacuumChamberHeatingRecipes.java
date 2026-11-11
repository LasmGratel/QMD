package lach_01298.qmd.datagen.recipe;

import lach_01298.qmd.recipe.chamber.VacuumChamberHeatingRecipe;
import net.minecraft.data.recipes.RecipeOutput;

import static lach_01298.qmd.datagen.recipe.QmdRecipeBuilders.*;
import static lach_01298.qmd.datagen.recipe.QmdRecipeIngredients.*;

/** Typed VacuumChamberHeating recipe declarations. */
final class QmdVacuumChamberHeatingRecipes {
    static void add(RecipeOutput output) {
        chamber(VacuumChamberHeatingRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:hot_mercury"), 1))
                .fluidOutput(sizedFluid(tagFluid("c:high_pressure_mercury"), 2))
                .heat(128D)
                .save(output, id("qmd:vacuum_chamber_heating/hot_mercury"));

        chamber(VacuumChamberHeatingRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:mercury"), 1))
                .fluidOutput(sizedFluid(tagFluid("c:high_pressure_mercury"), 2))
                .heat(256D)
                .save(output, id("qmd:vacuum_chamber_heating/mercury"));
    }

    private QmdVacuumChamberHeatingRecipes() {}
}
