package lach_01298.qmd.datagen.recipe;

import com.nred.nuclearcraft.recipe.ProcessorRecipeBuilder;
import com.nred.nuclearcraft.recipe.processor.CentrifugeRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import static lach_01298.qmd.datagen.recipe.NeohaulRecipeIngredients.*;

/** Native Neohaul centrifuge recipes. */
final class NeohaulCentrifugeRecipeRecipes {
    static void add(RecipeOutput output) {
        new ProcessorRecipeBuilder(CentrifugeRecipe.class, 1, 1, 0)
                .addFluidInput(fluidTag("c:coal"), 100)
                .addFluidResult(fluid("qmd:carbon"), 100)
                .addFluidResult(fluid("nuclearcraftneohaul:sulfur"), 111)
                .save(RecipeOutputs.named(output, "qmd:centrifuge/coal"), ResourceLocation.parse("qmd:centrifuge/coal"));

        new ProcessorRecipeBuilder(CentrifugeRecipe.class, 1, 1, 0)
                .addFluidInput(fluidTag("c:redstone"), 100)
                .addFluidResult(fluid("qmd:mercury"), 144)
                .addFluidResult(fluid("nuclearcraftneohaul:sulfur"), 666)
                .save(RecipeOutputs.named(output, "qmd:centrifuge/redstone"), ResourceLocation.parse("qmd:centrifuge/redstone"));
    }

    private NeohaulCentrifugeRecipeRecipes() {}
}
