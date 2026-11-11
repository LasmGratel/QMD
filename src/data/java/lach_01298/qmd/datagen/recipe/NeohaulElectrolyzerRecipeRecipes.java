package lach_01298.qmd.datagen.recipe;

import com.nred.nuclearcraft.recipe.ProcessorRecipeBuilder;
import com.nred.nuclearcraft.recipe.processor.ElectrolyzerRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import static lach_01298.qmd.datagen.recipe.NeohaulRecipeIngredients.*;

/** Native Neohaul electrolyzer recipes. */
final class NeohaulElectrolyzerRecipeRecipes {
    static void add(RecipeOutput output) {
        new ProcessorRecipeBuilder(ElectrolyzerRecipe.class, 0.5, 1, 0)
                .addFluidInput(fluidTag("c:sodium_chloride_solution"), 1332)
                .addFluidResult(fluid("nuclearcraftneohaul:hydrogen"), 1000)
                .addFluidResult(fluid("qmd:chlorine"), 1000)
                .addFluidResult(fluid("nuclearcraftneohaul:sodium_hydroxide_solution"), 1332)
                .save(RecipeOutputs.named(output, "qmd:electrolyzer/sodium_chloride_solution"), ResourceLocation.parse("qmd:electrolyzer/sodium_chloride_solution"));
    }

    private NeohaulElectrolyzerRecipeRecipes() {}
}
