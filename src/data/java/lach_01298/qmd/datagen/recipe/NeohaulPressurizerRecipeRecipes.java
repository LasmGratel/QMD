package lach_01298.qmd.datagen.recipe;

import com.nred.nuclearcraft.recipe.ProcessorRecipeBuilder;
import com.nred.nuclearcraft.recipe.processor.PressurizerRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import static lach_01298.qmd.datagen.recipe.NeohaulRecipeIngredients.*;

/** Native Neohaul pressurizer recipes. */
final class NeohaulPressurizerRecipeRecipes {
    static void add(RecipeOutput output) {
        new ProcessorRecipeBuilder(PressurizerRecipe.class, 1.0, 2.0, 0.0)
                .addItemInput(item("nuclearcraftneohaul:strontium_90_dust"), 9)
                .addItemResult(item("qmd:strontium_90_block"), 1)
                .save(RecipeOutputs.named(output, "qmd:pressurizer/strontium_90_block"), ResourceLocation.parse("qmd:pressurizer/strontium_90_block"));
    }

    private NeohaulPressurizerRecipeRecipes() {}
}
