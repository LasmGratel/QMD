package lach_01298.qmd.datagen.recipe;

import lach_01298.qmd.recipe.chamber.BeamDumpRecipe;
import net.minecraft.data.recipes.RecipeOutput;

import static lach_01298.qmd.datagen.recipe.QmdRecipeBuilders.*;
import static lach_01298.qmd.datagen.recipe.QmdRecipeIngredients.*;

/** Typed BeamDump recipe declarations. */
final class QmdBeamDumpRecipes {
    static void add(RecipeOutput output) {
        chamber(BeamDumpRecipe::new)
                .beamInput(beamInput(2000, 0L, Long.MAX_VALUE, 0D, id("qmd:deuteron")))
                .fluidOutput(sizedFluid(tagFluid("c:deuterium"), 1))
                .save(output, id("qmd:beam_dump/deuterium"));

        chamber(BeamDumpRecipe::new)
                .beamInput(beamInput(1000, 0L, Long.MAX_VALUE, 0D, id("qmd:alpha")))
                .fluidOutput(sizedFluid(tagFluid("c:helium"), 1))
                .save(output, id("qmd:beam_dump/helium"));

        chamber(BeamDumpRecipe::new)
                .beamInput(beamInput(1000, 0L, Long.MAX_VALUE, 0D, id("qmd:helion")))
                .fluidOutput(sizedFluid(tagFluid("c:helium_3"), 1))
                .save(output, id("qmd:beam_dump/helium_3"));

        chamber(BeamDumpRecipe::new)
                .beamInput(beamInput(2000, 0L, Long.MAX_VALUE, 0D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:hydrogen"), 1))
                .save(output, id("qmd:beam_dump/hydrogen"));

        chamber(BeamDumpRecipe::new)
                .beamInput(beamInput(2000, 0L, Long.MAX_VALUE, 0D, id("qmd:triton")))
                .fluidOutput(sizedFluid(tagFluid("c:tritium"), 1))
                .save(output, id("qmd:beam_dump/tritium"));
    }

    private QmdBeamDumpRecipes() {}
}
