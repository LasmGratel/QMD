package lach_01298.qmd.datagen.recipe;

import com.nred.nuclearcraft.recipe.BasicRecipeBuilder;
import com.nred.nuclearcraft.recipe.SizedChanceItemIngredient;
import com.nred.nuclearcraft.recipe.fission.SolidFissionRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import static lach_01298.qmd.datagen.recipe.NeohaulRecipeIngredients.*;

/** Native Neohaul solid fission recipes. */
final class NeohaulSolidFissionRecipeRecipes {
    static void add(RecipeOutput output) {
        new BasicRecipeBuilder<>(new SolidFissionRecipe(
                SizedChanceItemIngredient.of(item("qmd:mix_291_ni"), 1),
                SizedChanceItemIngredient.of(item("qmd:depleted_mix_291_ni"), 1), 12004, 1666, 5.0, 35, 0, 0.11, true, 0.000833))
                .save(RecipeOutputs.named(output, "qmd:solid_fission/depleted_mix_291_ni"), ResourceLocation.parse("qmd:solid_fission/depleted_mix_291_ni"));

        new BasicRecipeBuilder<>(new SolidFissionRecipe(
                SizedChanceItemIngredient.of(item("qmd:mix_291_ox"), 1),
                SizedChanceItemIngredient.of(item("qmd:depleted_mix_291_ox"), 1), 10000, 2000, 5.0, 25, 0, 0.11, true, 0.000833))
                .save(RecipeOutputs.named(output, "qmd:solid_fission/depleted_mix_291_ox"), ResourceLocation.parse("qmd:solid_fission/depleted_mix_291_ox"));

        new BasicRecipeBuilder<>(new SolidFissionRecipe(
                SizedChanceItemIngredient.of(item("qmd:mix_291_za"), 1),
                SizedChanceItemIngredient.of(item("qmd:depleted_mix_291_za"), 1), 9001, 2222, 5.0, 20, 0, 0.11, true, 0.000833))
                .save(RecipeOutputs.named(output, "qmd:solid_fission/depleted_mix_291_za"), ResourceLocation.parse("qmd:solid_fission/depleted_mix_291_za"));
    }

    private NeohaulSolidFissionRecipeRecipes() {}
}
