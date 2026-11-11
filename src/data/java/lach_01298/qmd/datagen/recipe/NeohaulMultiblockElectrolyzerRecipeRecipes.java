package lach_01298.qmd.datagen.recipe;

import com.nred.nuclearcraft.recipe.BasicRecipeBuilder;
import com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient;
import com.nred.nuclearcraft.recipe.machine.MultiblockElectrolyzerRecipe;
import java.util.List;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import static lach_01298.qmd.datagen.recipe.NeohaulRecipeIngredients.*;

/** Native Neohaul multiblock electrolyzer recipes. */
final class NeohaulMultiblockElectrolyzerRecipeRecipes {
    static void add(RecipeOutput output) {
        new BasicRecipeBuilder<>(new MultiblockElectrolyzerRecipe(
                List.of(),
                List.of(SizedChanceFluidIngredient.of(fluid("qmd:sodium_chloride_solution"), 1332)),
                List.of(),
                List.of(SizedChanceFluidIngredient.of(fluid("nuclearcraftneohaul:hydrogen"), 1000), SizedChanceFluidIngredient.of(fluid("qmd:chlorine"), 1000), SizedChanceFluidIngredient.of(fluid("nuclearcraftneohaul:sodium_hydroxide_solution"), 1332)),
                0.5, 1, "chloride_solution"))
                .save(RecipeOutputs.named(output, "qmd:multiblock_electrolyzer/sodium_chloride_solution"), ResourceLocation.parse("qmd:multiblock_electrolyzer/sodium_chloride_solution"));
    }

    private NeohaulMultiblockElectrolyzerRecipeRecipes() {}
}
