package lach_01298.qmd.datagen.recipe;

import com.nred.nuclearcraft.recipe.ProcessorRecipeBuilder;
import com.nred.nuclearcraft.recipe.processor.FluidMixerRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import static lach_01298.qmd.datagen.recipe.NeohaulRecipeIngredients.*;

/** Native Neohaul salt mixer recipes. */
final class NeohaulSaltMixerRecipeRecipes {
    static void add(RecipeOutput output) {
        new ProcessorRecipeBuilder(FluidMixerRecipe.class, 1, 1, 0)
                .addFluidInput(fluidTag("c:mercury"), 144)
                .addFluidInput(fluidTag("c:sulfur"), 666)
                .addFluidResult(fluid("nuclearcraftneohaul:redstone"), 100)
                .save(RecipeOutputs.named(output, "qmd:fluid_mixer/redstone"), ResourceLocation.parse("qmd:fluid_mixer/redstone"));
    }

    private NeohaulSaltMixerRecipeRecipes() {}
}
