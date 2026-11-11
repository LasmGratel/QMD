package lach_01298.qmd.datagen.recipe;

import com.nred.nuclearcraft.recipe.BasicRecipeBuilder;
import com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient;
import com.nred.nuclearcraft.recipe.exchanger.HeatExchangerRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import static lach_01298.qmd.datagen.recipe.NeohaulRecipeIngredients.*;

/** Native Neohaul heat exchanger recipes. */
final class NeohaulHeatExchangerRecipeRecipes {
    static void add(RecipeOutput output) {
        new BasicRecipeBuilder<>(new HeatExchangerRecipe(
                SizedChanceFluidIngredient.of(fluidTag("c:exhaust_mercury"), 6),
                SizedChanceFluidIngredient.of(fluidTag("c:hot_mercury"), 1), 128.0, HeatExchangerRecipe.RecipeHeatingType.OTHER, 700,
                700, false, -1, 0.0))
                .save(RecipeOutputs.named(output, "qmd:heat_exchanger/exhaust_mercury"), ResourceLocation.parse("qmd:heat_exchanger/exhaust_mercury"));
    }

    private NeohaulHeatExchangerRecipeRecipes() {}
}
