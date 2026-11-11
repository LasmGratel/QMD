package lach_01298.qmd.datagen.recipe;

import lach_01298.qmd.recipe.chamber.NeutralContainmentRecipe;
import net.minecraft.data.recipes.RecipeOutput;

import static lach_01298.qmd.datagen.recipe.QmdRecipeBuilders.*;
import static lach_01298.qmd.datagen.recipe.QmdRecipeIngredients.*;

/** Typed NeutralContainment recipe declarations. */
final class QmdNeutralContainmentRecipes {
    static void add(RecipeOutput output) {
        chamber(NeutralContainmentRecipe::new)
                .beamInput(beamInput(1000, 0L, 1L, 2D, id("qmd:antideuteron")))
                .beamInput(beamInput(1000, 0L, 1L, 2D, id("qmd:positron")))
                .fluidOutput(sizedFluid(tagFluid("c:antideuterium"), 1))
                .save(output, id("qmd:neutral_containment/antideuterium"));

        chamber(NeutralContainmentRecipe::new)
                .beamInput(beamInput(1000, 0L, 1L, 2D, id("qmd:antialpha")))
                .beamInput(beamInput(2000, 0L, 1L, 2D, id("qmd:positron")))
                .fluidOutput(sizedFluid(tagFluid("c:antihelium"), 1))
                .save(output, id("qmd:neutral_containment/antihelium"));

        chamber(NeutralContainmentRecipe::new)
                .beamInput(beamInput(1000, 0L, 1L, 2D, id("qmd:antihelion")))
                .beamInput(beamInput(2000, 0L, 1L, 2D, id("qmd:positron")))
                .fluidOutput(sizedFluid(tagFluid("c:antihelium3"), 1))
                .save(output, id("qmd:neutral_containment/antihelium3"));

        chamber(NeutralContainmentRecipe::new)
                .beamInput(beamInput(1000, 0L, 1L, 2D, id("qmd:antiproton")))
                .beamInput(beamInput(1000, 0L, 1L, 2D, id("qmd:positron")))
                .fluidOutput(sizedFluid(tagFluid("c:antihydrogen"), 1))
                .save(output, id("qmd:neutral_containment/antihydrogen"));

        chamber(NeutralContainmentRecipe::new)
                .beamInput(beamInput(1000, 0L, 1L, 2D, id("qmd:antitriton")))
                .beamInput(beamInput(1000, 0L, 1L, 2D, id("qmd:positron")))
                .fluidOutput(sizedFluid(tagFluid("c:antitritium"), 1))
                .save(output, id("qmd:neutral_containment/antitritium"));

        chamber(NeutralContainmentRecipe::new)
                .beamInput(beamInput(1000, 0L, Long.MAX_VALUE, 1D, id("qmd:glueball")))
                .beamInput(beamInput(0, 0L, Long.MAX_VALUE, 0D))
                .fluidOutput(sizedFluid(tagFluid("c:glueballs"), 1))
                .save(output, id("qmd:neutral_containment/glueballs"));

        chamber(NeutralContainmentRecipe::new)
                .beamInput(beamInput(1000, 0L, 1L, 2D, id("qmd:muon")))
                .beamInput(beamInput(1000, 0L, 1L, 2D, id("qmd:antimuon")))
                .fluidOutput(sizedFluid(tagFluid("c:muonium"), 1))
                .save(output, id("qmd:neutral_containment/muonium"));

        chamber(NeutralContainmentRecipe::new)
                .beamInput(beamInput(1000, 0L, 1L, 2D, id("qmd:electron")))
                .beamInput(beamInput(1000, 0L, 1L, 2D, id("qmd:positron")))
                .fluidOutput(sizedFluid(tagFluid("c:positronium"), 1))
                .save(output, id("qmd:neutral_containment/positronium"));

        chamber(NeutralContainmentRecipe::new)
                .beamInput(beamInput(1000, 0L, 1L, 2D, id("qmd:tau")))
                .beamInput(beamInput(1000, 0L, 1L, 2D, id("qmd:antitau")))
                .fluidOutput(sizedFluid(tagFluid("c:tauonium"), 1))
                .save(output, id("qmd:neutral_containment/tauonium"));
    }

    private QmdNeutralContainmentRecipes() {}
}
