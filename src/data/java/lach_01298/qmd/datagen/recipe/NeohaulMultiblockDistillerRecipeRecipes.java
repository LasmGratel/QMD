package lach_01298.qmd.datagen.recipe;

import com.nred.nuclearcraft.recipe.BasicRecipeBuilder;
import com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient;
import com.nred.nuclearcraft.recipe.machine.MultiblockDistillerRecipe;
import java.util.List;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import static lach_01298.qmd.datagen.recipe.NeohaulRecipeIngredients.*;

/** Native Neohaul multiblock distiller recipes. */
final class NeohaulMultiblockDistillerRecipeRecipes {
    static void add(RecipeOutput output) {
        new BasicRecipeBuilder<>(new MultiblockDistillerRecipe(
                List.of(SizedChanceFluidIngredient.of(fluidTag("c:liquid_air"), 1000)),
                List.of(SizedChanceFluidIngredient.of(fluid("nuclearcraftneohaul:nitrogen"), 38400), SizedChanceFluidIngredient.of(fluid("nuclearcraftneohaul:oxygen"), 12800), SizedChanceFluidIngredient.of(fluid("qmd:argon"), 6400), SizedChanceFluidIngredient.of(fluid("qmd:neon"), 5120), SizedChanceFluidIngredient.of(fluid("nuclearcraftneohaul:helium"), 1280)),
                1, 1))
                .save(RecipeOutputs.named(output, "qmd:multiblock_distiller/gases_from_liquid_air"), ResourceLocation.parse("qmd:multiblock_distiller/gases_from_liquid_air"));

        new BasicRecipeBuilder<>(new MultiblockDistillerRecipe(
                List.of(SizedChanceFluidIngredient.of(fluidTag("c:salt_water"), 10000)),
                List.of(SizedChanceFluidIngredient.of(fluid("qmd:sodium_chloride_solution"), 666), SizedChanceFluidIngredient.of(fluid("minecraft:water"), 9000)),
                1, 1))
                .save(RecipeOutputs.named(output, "qmd:multiblock_distiller/sodium_chloride_solution_from_salt_water"), ResourceLocation.parse("qmd:multiblock_distiller/sodium_chloride_solution_from_salt_water"));
    }

    private NeohaulMultiblockDistillerRecipeRecipes() {}
}
