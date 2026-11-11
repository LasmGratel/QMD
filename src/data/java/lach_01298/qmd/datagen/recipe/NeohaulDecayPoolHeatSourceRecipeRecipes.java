package lach_01298.qmd.datagen.recipe;

import com.nred.nuclearcraft.recipe.BasicRecipeBuilder;
import com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient;
import com.nred.nuclearcraft.recipe.SizedChanceItemIngredient;
import com.nred.nuclearcraft.recipe.machine.DecayPoolHeatSourceRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import static lach_01298.qmd.datagen.recipe.NeohaulRecipeIngredients.*;

/** Native Neohaul decay pool heat source recipes. */
final class NeohaulDecayPoolHeatSourceRecipeRecipes {
    static void add(RecipeOutput output) {
        new BasicRecipeBuilder<>(new DecayPoolHeatSourceRecipe(
                SizedChanceItemIngredient.of(itemTag("c:ingots/beryllium_7"), 1),
                SizedChanceFluidIngredient.EMPTY,
                SizedChanceItemIngredient.of(itemTag("c:ingots/lithium_7"), 1),
                SizedChanceFluidIngredient.EMPTY, 12280, 390))
                .save(RecipeOutputs.named(output, "qmd:decay_pool_heat_source/beryllium_7"), ResourceLocation.parse("qmd:decay_pool_heat_source/beryllium_7"));

        new BasicRecipeBuilder<>(new DecayPoolHeatSourceRecipe(
                SizedChanceItemIngredient.of(itemTag("c:ingots/cobalt_60"), 1),
                SizedChanceFluidIngredient.EMPTY,
                SizedChanceItemIngredient.of(itemTag("c:ingots/nickel"), 1),
                SizedChanceFluidIngredient.EMPTY, 16700, 285))
                .save(RecipeOutputs.named(output, "qmd:decay_pool_heat_source/cobalt_60"), ResourceLocation.parse("qmd:decay_pool_heat_source/cobalt_60"));

        new BasicRecipeBuilder<>(new DecayPoolHeatSourceRecipe(
                SizedChanceItemIngredient.of(itemTag("c:ingots/iridium_192"), 1),
                SizedChanceFluidIngredient.EMPTY,
                SizedChanceItemIngredient.of(itemTag("c:ingots/platinum"), 1),
                SizedChanceFluidIngredient.EMPTY, 12580, 380))
                .save(RecipeOutputs.named(output, "qmd:decay_pool_heat_source/iridium_192"), ResourceLocation.parse("qmd:decay_pool_heat_source/iridium_192"));

        new BasicRecipeBuilder<>(new DecayPoolHeatSourceRecipe(
                SizedChanceItemIngredient.of(itemTag("c:ingots/sodium_22"), 1),
                SizedChanceFluidIngredient.EMPTY,
                SizedChanceItemIngredient.EMPTY,
                SizedChanceFluidIngredient.of(fluid("qmd:neon"), 1000), 15600, 310))
                .save(RecipeOutputs.named(output, "qmd:decay_pool_heat_source/sodium_22"), ResourceLocation.parse("qmd:decay_pool_heat_source/sodium_22"));
    }

    private NeohaulDecayPoolHeatSourceRecipeRecipes() {}
}
