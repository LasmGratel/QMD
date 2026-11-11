package lach_01298.qmd.datagen.recipe;

import lach_01298.qmd.recipe.IrradiatorRecipe;
import net.minecraft.data.recipes.RecipeOutput;

import static lach_01298.qmd.datagen.recipe.QmdRecipeBuilders.*;
import static lach_01298.qmd.datagen.recipe.QmdRecipeIngredients.*;

/** Typed Irradiator recipe declarations. */
final class QmdIrradiatorRecipes {
    static void add(RecipeOutput output) {
        irradiator(IrradiatorRecipe.Kind.PROCESS)
                .input(item("minecraft:brown_mushroom"))
                .output(itemStack("nuclearcraftneohaul:glowing_mushroom", 1))
                .multiplier(4.0D)
                .radiation(0.0D)
                .save(output, id("qmd:irradiator/brown_mushroom"));

        irradiator(IrradiatorRecipe.Kind.PROCESS)
                .input(item("minecraft:short_grass"))
                .output(itemStack("minecraft:dead_bush", 1))
                .multiplier(1.0D)
                .radiation(0.0D)
                .save(output, id("qmd:irradiator/grass"));

        irradiator(IrradiatorRecipe.Kind.PROCESS)
                .input(item("minecraft:mossy_cobblestone"))
                .output(itemStack("minecraft:cobblestone", 1))
                .multiplier(1.0D)
                .radiation(0.0D)
                .save(output, id("qmd:irradiator/mossy_cobblestone"));

        irradiator(IrradiatorRecipe.Kind.PROCESS)
                .input(item("minecraft:mossy_cobblestone_wall"))
                .output(itemStack("minecraft:cobblestone_wall", 1))
                .multiplier(1.0D)
                .radiation(0.0D)
                .save(output, id("qmd:irradiator/mossy_cobblestone_wall"));

        irradiator(IrradiatorRecipe.Kind.PROCESS)
                .input(item("minecraft:mossy_stone_bricks"))
                .output(itemStack("minecraft:stone_bricks", 1))
                .multiplier(1.0D)
                .radiation(0.0D)
                .save(output, id("qmd:irradiator/mossy_stone_bricks"));

        irradiator(IrradiatorRecipe.Kind.PROCESS)
                .input(item("minecraft:red_mushroom"))
                .output(itemStack("nuclearcraftneohaul:glowing_mushroom", 1))
                .multiplier(4.0D)
                .radiation(0.0D)
                .save(output, id("qmd:irradiator/red_mushroom"));

        irradiator(IrradiatorRecipe.Kind.PROCESS)
                .input(item("minecraft:rotten_flesh"))
                .output(itemStack("qmd:flesh", 1))
                .multiplier(1.0D)
                .radiation(0.0D)
                .save(output, id("qmd:irradiator/rotten_flesh"));
    }

    private QmdIrradiatorRecipes() {}
}
