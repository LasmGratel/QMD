package lach_01298.qmd.datagen.recipe;

import com.nred.nuclearcraft.recipe.ProcessorRecipeBuilder;
import com.nred.nuclearcraft.recipe.processor.FluidEnricherRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import static lach_01298.qmd.datagen.recipe.NeohaulRecipeIngredients.*;

/** Native Neohaul enricher recipes. */
final class NeohaulEnricherRecipeRecipes {
    static void add(RecipeOutput output) {
        new ProcessorRecipeBuilder(FluidEnricherRecipe.class, 2, 2, 0)
                .addItemInput(itemTag("c:ingots/neodymium"), 1)
                .addFluidInput(fluidTag("c:yag"), 1296)
                .addFluidResult(fluid("qmd:nd_yag"), 1296)
                .save(RecipeOutputs.named(output, "qmd:enricher/nd_yag"), ResourceLocation.parse("qmd:enricher/nd_yag"));

        new ProcessorRecipeBuilder(FluidEnricherRecipe.class, 2, 2, 0)
                .addItemInput(itemTag("c:ingots/yttrium"), 1)
                .addFluidInput(fluidTag("c:alumina"), 120)
                .addFluidResult(fluid("qmd:yag"), 48)
                .save(RecipeOutputs.named(output, "qmd:enricher/yag"), ResourceLocation.parse("qmd:enricher/yag"));

        new ProcessorRecipeBuilder(FluidEnricherRecipe.class, 1, 1, 0)
                .addItemInput(itemTag("c:dusts/salt"), 1)
                .addFluidInput(fluidTag("c:water"), 1000)
                .addFluidResult(fluid("qmd:sodium_chloride_solution"), 666)
                .save(RecipeOutputs.named(output.withConditions(hasTag("c:dusts/salt")), "qmd:fluid_enricher/sodium_chloride_solution"), ResourceLocation.parse("qmd:fluid_enricher/sodium_chloride_solution"));
    }

    private NeohaulEnricherRecipeRecipes() {}
}
