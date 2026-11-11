package lach_01298.qmd.datagen.recipe;

import com.nred.nuclearcraft.recipe.ProcessorRecipeBuilder;
import com.nred.nuclearcraft.recipe.processor.CrystallizerRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import static lach_01298.qmd.datagen.recipe.NeohaulRecipeIngredients.*;

/** Native Neohaul crystallizer recipes. */
final class NeohaulCrystallizerRecipeRecipes {
    static void add(RecipeOutput output) {
        new ProcessorRecipeBuilder(CrystallizerRecipe.class, 2, 2, 0)
                .addFluidInput(fluid("qmd:nd_yag"), 432)
                .addItemResult(item("qmd:part_rod_nd_yag"), 1)
                .save(RecipeOutputs.named(output, "qmd:crystallizer/nd_yag_rod"), ResourceLocation.parse("qmd:crystallizer/nd_yag_rod"));

        new ProcessorRecipeBuilder(CrystallizerRecipe.class, 2, 4, 0)
                .addFluidInput(fluid("qmd:salt_water"), 10000)
                .addItemResult(itemTag("c:dusts/salt"), 1)
                .save(RecipeOutputs.named(output, "qmd:crystallizer/salt_from_salt_water"), ResourceLocation.parse("qmd:crystallizer/salt_from_salt_water"));

        new ProcessorRecipeBuilder(CrystallizerRecipe.class, 1, 1, 0)
                .addFluidInput(fluid("qmd:sodium_chloride_solution"), 666)
                .addItemResult(itemTag("c:dusts/salt"), 1)
                .save(RecipeOutputs.named(output, "qmd:crystallizer/salt_from_sodium_chloride_solution"), ResourceLocation.parse("qmd:crystallizer/salt_from_sodium_chloride_solution"));

        new ProcessorRecipeBuilder(CrystallizerRecipe.class, 2, 2, 0)
                .addFluidInput(fluid("qmd:silicon"), 1296)
                .addItemResult(item("qmd:semiconductor_silicon_boule"), 1)
                .save(RecipeOutputs.named(output, "qmd:crystallizer/silicon_boule"), ResourceLocation.parse("qmd:crystallizer/silicon_boule"));
    }

    private NeohaulCrystallizerRecipeRecipes() {}
}
