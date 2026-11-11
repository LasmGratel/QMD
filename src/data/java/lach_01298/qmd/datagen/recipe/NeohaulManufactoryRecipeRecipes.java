package lach_01298.qmd.datagen.recipe;

import com.nred.nuclearcraft.recipe.ProcessorRecipeBuilder;
import com.nred.nuclearcraft.recipe.processor.ManufactoryRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import static lach_01298.qmd.datagen.recipe.NeohaulRecipeIngredients.*;

/** Native Neohaul manufactory recipes. */
final class NeohaulManufactoryRecipeRecipes {
    static void add(RecipeOutput output) {
        new ProcessorRecipeBuilder(ManufactoryRecipe.class, 1, 1, 0)
                .addItemInput(itemTag("c:boules/silicon"), 1)
                .addItemResult(item("qmd:semiconductor_silicon_wafer"), 4)
                .save(RecipeOutputs.named(output, "qmd:manufactory/silicon_wafer"), ResourceLocation.parse("qmd:manufactory/silicon_wafer"));
    }

    private NeohaulManufactoryRecipeRecipes() {}
}
