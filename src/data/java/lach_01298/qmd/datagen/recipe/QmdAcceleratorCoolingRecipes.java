package lach_01298.qmd.datagen.recipe;

import lach_01298.qmd.recipe.accelerator.AcceleratorCoolingRecipe;
import net.minecraft.data.recipes.RecipeOutput;

import static lach_01298.qmd.datagen.recipe.QmdRecipeBuilders.*;
import static lach_01298.qmd.datagen.recipe.QmdRecipeIngredients.*;

/** Typed AcceleratorCooling recipe declarations. */
final class QmdAcceleratorCoolingRecipes {
    static void add(RecipeOutput output) {
        accelerator(AcceleratorCoolingRecipe::new)
                .fluid(fluidStack("qmd:liquid_argon", 1))
                .fluidOutput(sizedFluid(fluid("qmd:argon"), 64))
                .heat(1000)
                .temperature(87)
                .save(output, id("qmd:accelerator_cooling/argon"));

        accelerator(AcceleratorCoolingRecipe::new)
                .fluid(fluidStack("nuclearcraftneohaul:liquid_helium", 1))
                .fluidOutput(sizedFluid(fluid("nuclearcraftneohaul:helium"), 64))
                .heat(1000)
                .temperature(4)
                .save(output, id("qmd:accelerator_cooling/helium"));

        accelerator(AcceleratorCoolingRecipe::new)
                .fluid(fluidStack("qmd:liquid_neon", 1))
                .fluidOutput(sizedFluid(fluid("qmd:neon"), 64))
                .heat(1000)
                .temperature(27)
                .save(output, id("qmd:accelerator_cooling/neon"));

        accelerator(AcceleratorCoolingRecipe::new)
                .fluid(fluidStack("nuclearcraftneohaul:liquid_nitrogen", 1))
                .fluidOutput(sizedFluid(fluid("nuclearcraftneohaul:nitrogen"), 64))
                .heat(1000)
                .temperature(77)
                .save(output, id("qmd:accelerator_cooling/nitrogen"));

        accelerator(AcceleratorCoolingRecipe::new)
                .fluid(fluidStack("minecraft:water", 1))
                .fluidOutput(sizedFluid(fluid("nuclearcraftneohaul:condensate_water"), 1))
                .heat(32)
                .temperature(300)
                .save(output, id("qmd:accelerator_cooling/water"));
    }

    private QmdAcceleratorCoolingRecipes() {}
}
