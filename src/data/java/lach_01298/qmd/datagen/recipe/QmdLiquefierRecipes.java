package lach_01298.qmd.datagen.recipe;

import lach_01298.qmd.recipe.LiquefierRecipe;
import net.minecraft.data.recipes.RecipeOutput;

import static lach_01298.qmd.datagen.recipe.QmdRecipeBuilders.*;
import static lach_01298.qmd.datagen.recipe.QmdRecipeIngredients.*;

/** Typed Liquefier recipe declarations. */
final class QmdLiquefierRecipes {
    static void add(RecipeOutput output) {
        liquefier(LiquefierRecipe.Kind.GAS)
                .input(tagFluid("c:argon"), 64)
                .output(fluidStack("qmd:liquid_argon", 1))
                .heat(1000D)
                .temperatures(600, 450)
                .energy(50D)
                .pressure(0.25D)
                .save(output, id("qmd:liquefier/argon"));

        liquefier(LiquefierRecipe.Kind.GAS)
                .input(tagFluid("c:compressed_air"), 64)
                .output(fluidStack("qmd:liquid_air", 1))
                .heat(1000D)
                .temperatures(600, 450)
                .energy(25D)
                .pressure(0.25D)
                .save(output, id("qmd:liquefier/compressed_air"));

        liquefier(LiquefierRecipe.Kind.GAS)
                .input(tagFluid("c:helium"), 64)
                .output(fluidStack("nuclearcraftneohaul:liquid_helium", 1))
                .heat(1000D)
                .temperatures(40, 400)
                .energy(50D)
                .pressure(0.1D)
                .save(output, id("qmd:liquefier/helium"));

        liquefier(LiquefierRecipe.Kind.GAS)
                .input(tagFluid("c:hydrogen"), 64)
                .output(fluidStack("qmd:liquid_hydrogen", 1))
                .heat(1000D)
                .temperatures(200, 400)
                .energy(50D)
                .pressure(0.1D)
                .save(output, id("qmd:liquefier/hydrogen"));

        liquefier(LiquefierRecipe.Kind.GAS)
                .input(tagFluid("c:neon"), 64)
                .output(fluidStack("qmd:liquid_neon", 1))
                .heat(1000D)
                .temperatures(230, 400)
                .energy(50D)
                .pressure(0.2D)
                .save(output, id("qmd:liquefier/neon"));

        liquefier(LiquefierRecipe.Kind.GAS)
                .input(tagFluid("c:nitrogen"), 64)
                .output(fluidStack("nuclearcraftneohaul:liquid_nitrogen", 1))
                .heat(1000D)
                .temperatures(600, 450)
                .energy(50D)
                .pressure(0.25D)
                .save(output, id("qmd:liquefier/nitrogen"));

        liquefier(LiquefierRecipe.Kind.GAS)
                .input(tagFluid("c:oxygen"), 64)
                .output(fluidStack("qmd:liquid_oxygen", 1))
                .heat(1000D)
                .temperatures(700, 450)
                .energy(50D)
                .pressure(0.25D)
                .save(output, id("qmd:liquefier/oxygen"));
    }

    private QmdLiquefierRecipes() {}
}
