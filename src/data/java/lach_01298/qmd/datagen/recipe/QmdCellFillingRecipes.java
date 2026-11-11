package lach_01298.qmd.datagen.recipe;

import com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient;
import lach_01298.qmd.recipe.chamber.CellFillingRecipe;
import net.minecraft.data.recipes.RecipeOutput;

import static lach_01298.qmd.datagen.recipe.QmdRecipeBuilders.*;
import static lach_01298.qmd.datagen.recipe.QmdRecipeIngredients.*;

/** Typed CellFilling recipe declarations. */
final class QmdCellFillingRecipes {
    static void add(RecipeOutput output) {
        part0(output);
        part1(output);
    }

    private static void part0(RecipeOutput output) {
        chamber(CellFillingRecipe::new)
                .itemInput(sizedItem(item("qmd:cell_antideuterium"), 1))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .itemOutput(sizedItem(item("qmd:cell_empty"), 1))
                .fluidOutput(sizedFluid(tagFluid("c:antideuterium"), 100))
                .save(output, id("qmd:cell_filling/empty_antideuterium"));

        chamber(CellFillingRecipe::new)
                .itemInput(sizedItem(item("qmd:cell_antihelium"), 1))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .itemOutput(sizedItem(item("qmd:cell_empty"), 1))
                .fluidOutput(sizedFluid(tagFluid("c:antihelium"), 100))
                .save(output, id("qmd:cell_filling/empty_antihelium"));

        chamber(CellFillingRecipe::new)
                .itemInput(sizedItem(item("qmd:cell_antihelium3"), 1))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .itemOutput(sizedItem(item("qmd:cell_empty"), 1))
                .fluidOutput(sizedFluid(tagFluid("c:antihelium3"), 100))
                .save(output, id("qmd:cell_filling/empty_antihelium3"));

        chamber(CellFillingRecipe::new)
                .itemInput(sizedItem(item("qmd:cell_antihydrogen"), 1))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .itemOutput(sizedItem(item("qmd:cell_empty"), 1))
                .fluidOutput(sizedFluid(tagFluid("c:antihydrogen"), 100))
                .save(output, id("qmd:cell_filling/empty_antihydrogen"));

        chamber(CellFillingRecipe::new)
                .itemInput(sizedItem(item("qmd:cell_antitritium"), 1))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .itemOutput(sizedItem(item("qmd:cell_empty"), 1))
                .fluidOutput(sizedFluid(tagFluid("c:antitritium"), 100))
                .save(output, id("qmd:cell_filling/empty_antitritium"));

        chamber(CellFillingRecipe::new)
                .itemInput(sizedItem(item("qmd:cell_glueballs"), 1))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .itemOutput(sizedItem(item("qmd:cell_empty"), 1))
                .fluidOutput(sizedFluid(tagFluid("c:glueballs"), 100))
                .save(output, id("qmd:cell_filling/empty_glueballs"));

        chamber(CellFillingRecipe::new)
                .itemInput(sizedItem(item("qmd:cell_muonium"), 1))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .itemOutput(sizedItem(item("qmd:cell_empty"), 1))
                .fluidOutput(sizedFluid(tagFluid("c:muonium"), 100))
                .save(output, id("qmd:cell_filling/empty_muonium"));

        chamber(CellFillingRecipe::new)
                .itemInput(sizedItem(item("qmd:cell_positronium"), 1))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .itemOutput(sizedItem(item("qmd:cell_empty"), 1))
                .fluidOutput(sizedFluid(tagFluid("c:positronium"), 100))
                .save(output, id("qmd:cell_filling/empty_positronium"));

        chamber(CellFillingRecipe::new)
                .itemInput(sizedItem(item("qmd:cell_tauonium"), 1))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .itemOutput(sizedItem(item("qmd:cell_empty"), 1))
                .fluidOutput(sizedFluid(tagFluid("c:tauonium"), 100))
                .save(output, id("qmd:cell_filling/empty_tauonium"));

        chamber(CellFillingRecipe::new)
                .itemInput(sizedItem(item("qmd:cell_empty"), 1))
                .fluidInput(sizedFluid(tagFluid("c:antideuterium"), 100))
                .itemOutput(sizedItem(item("qmd:cell_antideuterium"), 1))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .save(output, id("qmd:cell_filling/fill_antideuterium"));

        chamber(CellFillingRecipe::new)
                .itemInput(sizedItem(item("qmd:cell_empty"), 1))
                .fluidInput(sizedFluid(tagFluid("c:antihelium"), 100))
                .itemOutput(sizedItem(item("qmd:cell_antihelium"), 1))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .save(output, id("qmd:cell_filling/fill_antihelium"));

        chamber(CellFillingRecipe::new)
                .itemInput(sizedItem(item("qmd:cell_empty"), 1))
                .fluidInput(sizedFluid(tagFluid("c:antihelium3"), 100))
                .itemOutput(sizedItem(item("qmd:cell_antihelium3"), 1))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .save(output, id("qmd:cell_filling/fill_antihelium3"));

        chamber(CellFillingRecipe::new)
                .itemInput(sizedItem(item("qmd:cell_empty"), 1))
                .fluidInput(sizedFluid(tagFluid("c:antihydrogen"), 100))
                .itemOutput(sizedItem(item("qmd:cell_antihydrogen"), 1))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .save(output, id("qmd:cell_filling/fill_antihydrogen"));

        chamber(CellFillingRecipe::new)
                .itemInput(sizedItem(item("qmd:cell_empty"), 1))
                .fluidInput(sizedFluid(tagFluid("c:antitritium"), 100))
                .itemOutput(sizedItem(item("qmd:cell_antitritium"), 1))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .save(output, id("qmd:cell_filling/fill_antitritium"));

        chamber(CellFillingRecipe::new)
                .itemInput(sizedItem(item("qmd:cell_empty"), 1))
                .fluidInput(sizedFluid(tagFluid("c:glueballs"), 100))
                .itemOutput(sizedItem(item("qmd:cell_glueballs"), 1))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .save(output, id("qmd:cell_filling/fill_glueballs"));

        chamber(CellFillingRecipe::new)
                .itemInput(sizedItem(item("qmd:cell_empty"), 1))
                .fluidInput(sizedFluid(tagFluid("c:muonium"), 100))
                .itemOutput(sizedItem(item("qmd:cell_muonium"), 1))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .save(output, id("qmd:cell_filling/fill_muonium"));

        chamber(CellFillingRecipe::new)
                .itemInput(sizedItem(item("qmd:cell_empty"), 1))
                .fluidInput(sizedFluid(tagFluid("c:positronium"), 100))
                .itemOutput(sizedItem(item("qmd:cell_positronium"), 1))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .save(output, id("qmd:cell_filling/fill_positronium"));

        chamber(CellFillingRecipe::new)
                .itemInput(sizedItem(item("qmd:cell_empty"), 1))
                .fluidInput(sizedFluid(tagFluid("c:tauonium"), 100))
                .itemOutput(sizedItem(item("qmd:cell_tauonium"), 1))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .save(output, id("qmd:cell_filling/fill_tauonium"));

        chamber(CellFillingRecipe::new)
                .itemInput(sizedItem(item("qmd:cell_antideuterium"), 1))
                .fluidInput(sizedFluid(tagFluid("c:antideuterium"), 100))
                .itemOutput(sizedItem(item("qmd:cell_antideuterium"), 1))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .save(output, id("qmd:cell_filling/refill_antideuterium"));

        chamber(CellFillingRecipe::new)
                .itemInput(sizedItem(item("qmd:cell_antihelium"), 1))
                .fluidInput(sizedFluid(tagFluid("c:antihelium"), 100))
                .itemOutput(sizedItem(item("qmd:cell_antihelium"), 1))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .save(output, id("qmd:cell_filling/refill_antihelium"));

        chamber(CellFillingRecipe::new)
                .itemInput(sizedItem(item("qmd:cell_antihelium3"), 1))
                .fluidInput(sizedFluid(tagFluid("c:antihelium3"), 100))
                .itemOutput(sizedItem(item("qmd:cell_antihelium3"), 1))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .save(output, id("qmd:cell_filling/refill_antihelium3"));

        chamber(CellFillingRecipe::new)
                .itemInput(sizedItem(item("qmd:cell_antihydrogen"), 1))
                .fluidInput(sizedFluid(tagFluid("c:antihydrogen"), 100))
                .itemOutput(sizedItem(item("qmd:cell_antihydrogen"), 1))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .save(output, id("qmd:cell_filling/refill_antihydrogen"));

        chamber(CellFillingRecipe::new)
                .itemInput(sizedItem(item("qmd:cell_antitritium"), 1))
                .fluidInput(sizedFluid(tagFluid("c:antitritium"), 100))
                .itemOutput(sizedItem(item("qmd:cell_antitritium"), 1))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .save(output, id("qmd:cell_filling/refill_antitritium"));

        chamber(CellFillingRecipe::new)
                .itemInput(sizedItem(item("qmd:cell_glueballs"), 1))
                .fluidInput(sizedFluid(tagFluid("c:glueballs"), 100))
                .itemOutput(sizedItem(item("qmd:cell_glueballs"), 1))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .save(output, id("qmd:cell_filling/refill_glueballs"));
    }

    private static void part1(RecipeOutput output) {
        chamber(CellFillingRecipe::new)
                .itemInput(sizedItem(item("qmd:cell_muonium"), 1))
                .fluidInput(sizedFluid(tagFluid("c:muonium"), 100))
                .itemOutput(sizedItem(item("qmd:cell_muonium"), 1))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .save(output, id("qmd:cell_filling/refill_muonium"));

        chamber(CellFillingRecipe::new)
                .itemInput(sizedItem(item("qmd:cell_positronium"), 1))
                .fluidInput(sizedFluid(tagFluid("c:positronium"), 100))
                .itemOutput(sizedItem(item("qmd:cell_positronium"), 1))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .save(output, id("qmd:cell_filling/refill_positronium"));

        chamber(CellFillingRecipe::new)
                .itemInput(sizedItem(item("qmd:cell_tauonium"), 1))
                .fluidInput(sizedFluid(tagFluid("c:tauonium"), 100))
                .itemOutput(sizedItem(item("qmd:cell_tauonium"), 1))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .save(output, id("qmd:cell_filling/refill_tauonium"));
    }

    private QmdCellFillingRecipes() {}
}
