package lach_01298.qmd.datagen.recipe;

import com.nred.nuclearcraft.recipe.BasicRecipeBuilder;
import com.nred.nuclearcraft.recipe.SizedChanceItemIngredient;
import com.nred.nuclearcraft.recipe.fission.FissionIrradiatorRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import static lach_01298.qmd.datagen.recipe.NeohaulRecipeIngredients.*;

/** Native Neohaul fission irradiator recipes. */
final class NeohaulFissionIrradiatorRecipeRecipes {
    static void add(RecipeOutput output) {
        new BasicRecipeBuilder<>(new FissionIrradiatorRecipe(
                SizedChanceItemIngredient.of(item("qmd:ingot_cobalt"), 1),
                SizedChanceItemIngredient.of(item("qmd:isotope_cobalt_60"), 1), 1920000L, 0.0, 0.0, 0L, 9223372036854775807L, 0.0))
                .save(RecipeOutputs.named(output, "qmd:fission_irradiator/cobalt_60"), ResourceLocation.parse("qmd:fission_irradiator/cobalt_60"));

        new BasicRecipeBuilder<>(new FissionIrradiatorRecipe(
                SizedChanceItemIngredient.of(item("qmd:ingot_iridium"), 1),
                SizedChanceItemIngredient.of(item("qmd:isotope_iridium_192"), 1), 1920000L, 0.0, 0.0, 0L, 9223372036854775807L, 0.0))
                .save(RecipeOutputs.named(output, "qmd:fission_irradiator/iridium_192"), ResourceLocation.parse("qmd:fission_irradiator/iridium_192"));

        new BasicRecipeBuilder<>(new FissionIrradiatorRecipe(
                SizedChanceItemIngredient.of(itemTag("c:wafers/silicon"), 1),
                SizedChanceItemIngredient.of(item("qmd:semiconductor_silicon_n_doped"), 1), 120000L, 0, 0, 0L, 9223372036854775807L,
                0))
                .save(RecipeOutputs.named(output, "qmd:fission_irradiator/silicon_n_doped"), ResourceLocation.parse("qmd:fission_irradiator/silicon_n_doped"));
    }

    private NeohaulFissionIrradiatorRecipeRecipes() {}
}
