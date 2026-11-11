package lach_01298.qmd.datagen.recipe;

import com.nred.nuclearcraft.recipe.BasicRecipeBuilder;
import com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient;
import com.nred.nuclearcraft.recipe.fission.FissionHeatingRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import static lach_01298.qmd.datagen.recipe.NeohaulRecipeIngredients.*;

/** Native Neohaul fission heating recipes. */
final class NeohaulFissionHeatingRecipeRecipes {
    static void add(RecipeOutput output) {
        new BasicRecipeBuilder<>(new FissionHeatingRecipe(
                SizedChanceFluidIngredient.of(fluidTag("c:hot_mercury"), 1),
                SizedChanceFluidIngredient.of(fluidTag("c:high_pressure_mercury"), 2), FissionHeatingRecipe.RecipeHeatingType.OTHER,
                256))
                .save(RecipeOutputs.named(output, "qmd:fission_heating/hot_mercury"), ResourceLocation.parse("qmd:fission_heating/hot_mercury"));

        new BasicRecipeBuilder<>(new FissionHeatingRecipe(
                SizedChanceFluidIngredient.of(fluidTag("c:mercury"), 1),
                SizedChanceFluidIngredient.of(fluidTag("c:high_pressure_mercury"), 2), FissionHeatingRecipe.RecipeHeatingType.OTHER,
                512))
                .save(RecipeOutputs.named(output, "qmd:fission_heating/mercury"), ResourceLocation.parse("qmd:fission_heating/mercury"));
    }

    private NeohaulFissionHeatingRecipeRecipes() {}
}
