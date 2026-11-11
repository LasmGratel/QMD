package lach_01298.qmd.datagen.recipe;

import com.nred.nuclearcraft.recipe.BasicRecipeBuilder;
import com.nred.nuclearcraft.recipe.SizedChanceItemIngredient;
import com.nred.nuclearcraft.recipe.fission.PebbleFissionRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import static lach_01298.qmd.datagen.recipe.NeohaulRecipeIngredients.*;

/** Native Neohaul pebble fission recipes. */
final class NeohaulPebbleFissionRecipeRecipes {
    static void add(RecipeOutput output) {
        new BasicRecipeBuilder<>(new PebbleFissionRecipe(
                SizedChanceItemIngredient.of(item("qmd:mix_291_tr"), 1),
                SizedChanceItemIngredient.of(item("qmd:depleted_mix_291_tr"), 1), 10000, 2000, 5.0, 20, 10, 0.11, true, 0.000833))
                .save(RecipeOutputs.named(output, "qmd:pebble_fission/depleted_mix_291_tr"), ResourceLocation.parse("qmd:pebble_fission/depleted_mix_291_tr"));
    }

    private NeohaulPebbleFissionRecipeRecipes() {}
}
