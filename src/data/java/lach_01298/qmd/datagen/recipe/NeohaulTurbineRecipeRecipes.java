package lach_01298.qmd.datagen.recipe;

import com.nred.nuclearcraft.recipe.BasicRecipeBuilder;
import com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient;
import com.nred.nuclearcraft.recipe.turbine.TurbineRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import static lach_01298.qmd.datagen.recipe.NeohaulRecipeIngredients.*;

/** Native Neohaul turbine recipes. */
final class NeohaulTurbineRecipeRecipes {
    static void add(RecipeOutput output) {
        new BasicRecipeBuilder<>(new TurbineRecipe(
                SizedChanceFluidIngredient.of(fluidTag("c:high_pressure_mercury"), 1),
                SizedChanceFluidIngredient.of(fluidTag("c:exhaust_mercury"), 3), 192.0, 3.0, 1.0, "cloud", 0.04310344827586207))
                .save(RecipeOutputs.named(output, "qmd:turbine/high_pressure_mercury"), ResourceLocation.parse("qmd:turbine/high_pressure_mercury"));
    }

    private NeohaulTurbineRecipeRecipes() {}
}
