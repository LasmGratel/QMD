package lach_01298.qmd.datagen.recipe;

import lach_01298.qmd.recipe.LiquefierRecipe;
import net.minecraft.data.recipes.RecipeOutput;

import static lach_01298.qmd.datagen.recipe.QmdRecipeBuilders.*;
import static lach_01298.qmd.datagen.recipe.QmdRecipeIngredients.*;

/** Typed LiquefierCoolant recipe declarations. */
final class QmdLiquefierCoolantRecipes {
    static void add(RecipeOutput output) {
        liquefier(LiquefierRecipe.Kind.COOLANT)
                .input(fluid("qmd:liquid_argon"), 1)
                .output(fluidStack("qmd:argon", 64))
                .heat(1000D)
                .temperatures(87, 300)
                .save(output, id("qmd:liquefier_coolant/argon"));

        liquefier(LiquefierRecipe.Kind.COOLANT)
                .input(fluid("nuclearcraftneohaul:liquid_helium"), 1)
                .output(fluidStack("nuclearcraftneohaul:helium", 64))
                .heat(1000D)
                .temperatures(4, 300)
                .save(output, id("qmd:liquefier_coolant/helium"));

        liquefier(LiquefierRecipe.Kind.COOLANT)
                .input(fluid("qmd:liquid_neon"), 1)
                .output(fluidStack("qmd:neon", 64))
                .heat(1000D)
                .temperatures(27, 300)
                .save(output, id("qmd:liquefier_coolant/neon"));

        liquefier(LiquefierRecipe.Kind.COOLANT)
                .input(fluid("nuclearcraftneohaul:liquid_nitrogen"), 1)
                .output(fluidStack("nuclearcraftneohaul:nitrogen", 64))
                .heat(1000D)
                .temperatures(77, 300)
                .save(output, id("qmd:liquefier_coolant/nitrogen"));

        liquefier(LiquefierRecipe.Kind.COOLANT)
                .input(fluid("minecraft:water"), 1)
                .output(fluidStack("nuclearcraftneohaul:condensate_water", 1))
                .heat(32D)
                .temperatures(300, 350)
                .save(output, id("qmd:liquefier_coolant/water"));
    }

    private QmdLiquefierCoolantRecipes() {}
}
