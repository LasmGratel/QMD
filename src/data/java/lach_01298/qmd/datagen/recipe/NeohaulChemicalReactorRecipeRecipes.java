package lach_01298.qmd.datagen.recipe;

import com.nred.nuclearcraft.recipe.ProcessorRecipeBuilder;
import com.nred.nuclearcraft.recipe.processor.ChemicalReactorRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import static lach_01298.qmd.datagen.recipe.NeohaulRecipeIngredients.*;

/** Native Neohaul chemical reactor recipes. */
final class NeohaulChemicalReactorRecipeRecipes {
    static void add(RecipeOutput output) {
        new ProcessorRecipeBuilder(ChemicalReactorRecipe.class, 1, 1, 0)
                .addFluidInput(fluid("nuclearcraftneohaul:hydrogen"), 1000)
                .addFluidInput(fluid("qmd:chlorine"), 1000)
                .addFluidResult(fluid("qmd:hydrochloric_acid"), 2000)
                .save(RecipeOutputs.named(output, "qmd:chemical_reactor/hydrochloric_acid"), ResourceLocation.parse("qmd:chemical_reactor/hydrochloric_acid"));

        new ProcessorRecipeBuilder(ChemicalReactorRecipe.class, 1, 1, 0)
                .addFluidInput(fluid("qmd:nitrogen_dioxide"), 3000)
                .addFluidInput(fluid("minecraft:water"), 1000)
                .addFluidResult(fluid("qmd:nitric_acid"), 2000)
                .addFluidResult(fluid("qmd:nitric_oxide"), 1000)
                .save(RecipeOutputs.named(output, "qmd:chemical_reactor/nitric_acid"), ResourceLocation.parse("qmd:chemical_reactor/nitric_acid"));

        new ProcessorRecipeBuilder(ChemicalReactorRecipe.class, 1, 1, 0)
                .addFluidInput(fluid("nuclearcraftneohaul:nitrogen"), 1000)
                .addFluidInput(fluid("nuclearcraftneohaul:oxygen"), 1000)
                .addFluidResult(fluid("qmd:nitric_oxide"), 2000)
                .save(RecipeOutputs.named(output, "qmd:chemical_reactor/nitric_oxide"), ResourceLocation.parse("qmd:chemical_reactor/nitric_oxide"));

        new ProcessorRecipeBuilder(ChemicalReactorRecipe.class, 1, 1, 0)
                .addFluidInput(fluid("qmd:nitric_oxide"), 2000)
                .addFluidInput(fluid("nuclearcraftneohaul:oxygen"), 1000)
                .addFluidResult(fluid("qmd:nitrogen_dioxide"), 2000)
                .save(RecipeOutputs.named(output, "qmd:chemical_reactor/nitrogen_dioxide"), ResourceLocation.parse("qmd:chemical_reactor/nitrogen_dioxide"));
    }

    private NeohaulChemicalReactorRecipeRecipes() {}
}
