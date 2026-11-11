package lach_01298.qmd.datagen.recipe;

import com.nred.nuclearcraft.recipe.BasicRecipeBuilder;
import com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient;
import com.nred.nuclearcraft.recipe.exchanger.CondenserRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import static lach_01298.qmd.datagen.recipe.NeohaulRecipeIngredients.*;

/** Native Neohaul condenser recipes. */
final class NeohaulCondenserRecipeRecipes {
    static void add(RecipeOutput output) {
        new BasicRecipeBuilder<>(new CondenserRecipe(
                SizedChanceFluidIngredient.of(fluidTag("c:exhaust_mercury"), 6),
                SizedChanceFluidIngredient.of(fluidTag("c:hot_mercury"), 1), 128.0, 700, 700, -1, 0.0))
                .save(RecipeOutputs.named(output, "qmd:condenser/exhaust_mercury"), ResourceLocation.parse("qmd:condenser/exhaust_mercury"));

        new BasicRecipeBuilder<>(new CondenserRecipe(
                SizedChanceFluidIngredient.of(fluidTag("c:hot_mercury"), 1),
                SizedChanceFluidIngredient.of(fluidTag("c:mercury"), 1), 256.0, 700, 300, -1, 0.0))
                .save(RecipeOutputs.named(output, "qmd:condenser/hot_mercury"), ResourceLocation.parse("qmd:condenser/hot_mercury"));
    }

    private NeohaulCondenserRecipeRecipes() {}
}
