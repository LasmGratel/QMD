package lach_01298.qmd.datagen.recipe;

import lach_01298.qmd.recipe.accelerator.AcceleratorSourceRecipe;
import net.minecraft.data.recipes.RecipeOutput;

import static lach_01298.qmd.datagen.recipe.QmdRecipeBuilders.*;
import static lach_01298.qmd.datagen.recipe.QmdRecipeIngredients.*;

/** Typed AcceleratorSource recipe declarations. */
final class QmdAcceleratorSourceRecipes {
    static void add(RecipeOutput output) {
        accelerator(AcceleratorSourceRecipe::new)
                .item(item("qmd:cell_antideuterium"), 1)
                .particle(beam("antideuteron", 10000, 0L, 0D))
                .save(output, id("qmd:accelerator_source/cell_antideuterium"));

        accelerator(AcceleratorSourceRecipe::new)
                .item(item("qmd:cell_antihelium"), 1)
                .particle(beam("antialpha", 10000, 0L, 0D))
                .save(output, id("qmd:accelerator_source/cell_antihelium"));

        accelerator(AcceleratorSourceRecipe::new)
                .item(item("qmd:cell_antihelium3"), 1)
                .particle(beam("antihelion", 10000, 0L, 0D))
                .save(output, id("qmd:accelerator_source/cell_antihelium3"));

        accelerator(AcceleratorSourceRecipe::new)
                .item(item("qmd:cell_antihydrogen"), 1)
                .particle(beam("antiproton", 10000, 0L, 0D))
                .save(output, id("qmd:accelerator_source/cell_antihydrogen"));

        accelerator(AcceleratorSourceRecipe::new)
                .item(item("qmd:cell_antitritium"), 1)
                .particle(beam("antitriton", 10000, 0L, 0D))
                .save(output, id("qmd:accelerator_source/cell_antitritium"));

        accelerator(AcceleratorSourceRecipe::new)
                .fluid(fluidStack("nuclearcraftneohaul:deuterium", 5))
                .particle(beam("deuteron", 10000, 0L, 0D))
                .save(output, id("qmd:accelerator_source/deuterium"));

        accelerator(AcceleratorSourceRecipe::new)
                .fluid(fluidStack("nuclearcraftneohaul:diborane", 5))
                .particle(beam("boron_ion", 10000, 0L, 0D))
                .save(output, id("qmd:accelerator_source/diborane"));

        accelerator(AcceleratorSourceRecipe::new)
                .fluid(fluidStack("nuclearcraftneohaul:helium", 10))
                .particle(beam("alpha", 10000, 0L, 0D))
                .save(output, id("qmd:accelerator_source/helium"));

        accelerator(AcceleratorSourceRecipe::new)
                .fluid(fluidStack("nuclearcraftneohaul:helium_3", 10))
                .particle(beam("helion", 10000, 0L, 0D))
                .save(output, id("qmd:accelerator_source/helium_3"));

        accelerator(AcceleratorSourceRecipe::new)
                .fluid(fluidStack("nuclearcraftneohaul:hydrogen", 5))
                .particle(beam("proton", 10000, 0L, 0D))
                .save(output, id("qmd:accelerator_source/hydrogen"));

        accelerator(AcceleratorSourceRecipe::new)
                .item(item("qmd:source_calcium_48"), 1)
                .particle(beam("calcium_48_ion", 10000, 0L, 0D))
                .save(output, id("qmd:accelerator_source/source_calcium_48"));

        accelerator(AcceleratorSourceRecipe::new)
                .item(item("qmd:source_sodium_22"), 1)
                .particle(beam("positron", 10000, 0L, 0D))
                .save(output, id("qmd:accelerator_source/source_sodium_22"));

        accelerator(AcceleratorSourceRecipe::new)
                .item(item("qmd:source_tungsten_filament"), 1)
                .particle(beam("electron", 10000, 0L, 0D))
                .save(output, id("qmd:accelerator_source/source_tungsten_filament"));

        accelerator(AcceleratorSourceRecipe::new)
                .fluid(fluidStack("nuclearcraftneohaul:tritium", 5))
                .particle(beam("triton", 10000, 0L, 0D))
                .save(output, id("qmd:accelerator_source/tritium"));
    }

    private QmdAcceleratorSourceRecipes() {}
}
