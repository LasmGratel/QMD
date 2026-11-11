package lach_01298.qmd.datagen.recipe;

import com.nred.nuclearcraft.recipe.ProcessorRecipeBuilder;
import com.nred.nuclearcraft.recipe.processor.DecayHastenerRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import static lach_01298.qmd.datagen.recipe.NeohaulRecipeIngredients.*;

/** Native Neohaul decay hastener recipes. */
final class NeohaulDecayHastenerRecipeRecipes {
    static void add(RecipeOutput output) {
        new ProcessorRecipeBuilder(DecayHastenerRecipe.class, 0.05, 1, 6.89)
                .addItemInput(itemTag("c:ingots/beryllium_7"), 1)
                .addItemResult(itemTag("c:ingots/lithium_7"), 1)
                .save(RecipeOutputs.named(output, "qmd:decay_hastener/beryllium_7"), ResourceLocation.parse("qmd:decay_hastener/beryllium_7"));

        new ProcessorRecipeBuilder(DecayHastenerRecipe.class, 0.33125, 1, 0.190)
                .addItemInput(itemTag("c:ingots/cobalt_60"), 1)
                .addItemResult(itemTag("c:dusts/nickel"), 1)
                .save(RecipeOutputs.named(output, "qmd:decay_hastener/cobalt_60"), ResourceLocation.parse("qmd:decay_hastener/cobalt_60"));

        new ProcessorRecipeBuilder(DecayHastenerRecipe.class, 0.0875, 1, 4.94)
                .addItemInput(itemTag("c:ingots/iridium_192"), 1)
                .addItemResult(itemTag("c:dusts/platinum"), 1)
                .save(RecipeOutputs.named(output, "qmd:decay_hastener/iridium_192"), ResourceLocation.parse("qmd:decay_hastener/iridium_192"));
    }

    private NeohaulDecayHastenerRecipeRecipes() {}
}
