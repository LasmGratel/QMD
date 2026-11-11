package lach_01298.qmd.datagen.recipe;

import com.nred.nuclearcraft.recipe.ProcessorRecipeBuilder;
import com.nred.nuclearcraft.recipe.processor.SupercoolerRecipe;
import lach_01298.qmd.recipe.OverrideNCRecipesCondition;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import static lach_01298.qmd.datagen.recipe.NeohaulRecipeIngredients.*;

/** Native Neohaul supercooler recipes. */
final class NeohaulSupercoolerRecipeRecipes {
    static void add(RecipeOutput output) {
        new ProcessorRecipeBuilder(SupercoolerRecipe.class, 0.013333333333333334, 1, 0)
                .addFluidInput(fluidTag("c:compressed_air"), 64)
                .addFluidResult(fluid("qmd:liquid_air"), 1)
                .save(RecipeOutputs.named(output.withConditions(new OverrideNCRecipesCondition(true)), "qmd:supercooler/liquid_air_from_compressed_air_override"), ResourceLocation.parse("qmd:supercooler/liquid_air_from_compressed_air_override"));

        new ProcessorRecipeBuilder(SupercoolerRecipe.class, 0.5, 0.5, 0)
                .addFluidInput(fluidTag("c:compressed_air"), 8000)
                .addFluidResult(fluid("qmd:liquid_air"), 25)
                .save(RecipeOutputs.named(output.withConditions(new OverrideNCRecipesCondition(false)), "qmd:supercooler/liquid_air_from_compressed_air_standard"), ResourceLocation.parse("qmd:supercooler/liquid_air_from_compressed_air_standard"));

        new ProcessorRecipeBuilder(SupercoolerRecipe.class, 0.013333333333333334, 5.0, 0.0)
                .addFluidInput(fluidTag("c:helium"), 64)
                .addFluidResult(fluid("nuclearcraftneohaul:liquid_helium"), 1)
                .save(RecipeOutputs.named(output.withConditions(new OverrideNCRecipesCondition(true)), "qmd:supercooler/liquid_helium_from_helium_override"), ResourceLocation.parse("qmd:supercooler/liquid_helium_from_helium_override"));

        new ProcessorRecipeBuilder(SupercoolerRecipe.class, 1.0, 1.0, 0.0)
                .addFluidInput(fluidTag("c:helium"), 8000)
                .addFluidResult(fluid("nuclearcraftneohaul:liquid_helium"), 25)
                .save(RecipeOutputs.named(output.withConditions(new OverrideNCRecipesCondition(false)), "qmd:supercooler/liquid_helium_from_helium_standard"), ResourceLocation.parse("qmd:supercooler/liquid_helium_from_helium_standard"));

        new ProcessorRecipeBuilder(SupercoolerRecipe.class, 0.013333333333333334, 2.5, 0.0)
                .addFluidInput(fluidTag("c:nitrogen"), 64)
                .addFluidResult(fluid("nuclearcraftneohaul:liquid_nitrogen"), 1)
                .save(RecipeOutputs.named(output.withConditions(new OverrideNCRecipesCondition(true)), "qmd:supercooler/liquid_nitrogen_from_nitrogen_override"), ResourceLocation.parse("qmd:supercooler/liquid_nitrogen_from_nitrogen_override"));

        new ProcessorRecipeBuilder(SupercoolerRecipe.class, 0.5, 0.5, 0.0)
                .addFluidInput(fluidTag("c:nitrogen"), 8000)
                .addFluidResult(fluid("nuclearcraftneohaul:liquid_nitrogen"), 25)
                .save(RecipeOutputs.named(output.withConditions(new OverrideNCRecipesCondition(false)), "qmd:supercooler/liquid_nitrogen_from_nitrogen_standard"), ResourceLocation.parse("qmd:supercooler/liquid_nitrogen_from_nitrogen_standard"));
    }

    private NeohaulSupercoolerRecipeRecipes() {}
}
