package lach_01298.qmd.datagen.recipe;

import com.nred.nuclearcraft.recipe.ProcessorRecipeBuilder;
import com.nred.nuclearcraft.recipe.processor.SeparatorRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import static lach_01298.qmd.datagen.recipe.NeohaulRecipeIngredients.*;

/** Native Neohaul separator recipes. */
final class NeohaulSeparatorRecipeRecipes {
    static void add(RecipeOutput output) {
        new ProcessorRecipeBuilder(SeparatorRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:copernicium_291_c"), 1)
                .addItemResult(item("qmd:copernicium_291"), 1)
                .addItemResult(itemTag("c:dusts/graphite"), 1)
                .save(RecipeOutputs.named(output, "qmd:separator/copernicium_291_from_c"), ResourceLocation.parse("qmd:separator/copernicium_291_from_c"));

        new ProcessorRecipeBuilder(SeparatorRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:copernicium_291_za"), 1)
                .addItemResult(item("qmd:copernicium_291"), 1)
                .addItemResult(item("nuclearcraftneohaul:zirconium_dust"), 1)
                .save(RecipeOutputs.named(output, "qmd:separator/copernicium_291_from_za"), ResourceLocation.parse("qmd:separator/copernicium_291_from_za"));

        new ProcessorRecipeBuilder(SeparatorRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:mix_291"), 1)
                .addItemResult(item("qmd:copernicium_291"), 1)
                .addItemResult(item("nuclearcraftneohaul:uranium_238"), 8)
                .save(RecipeOutputs.named(output, "qmd:separator/mix_291"), ResourceLocation.parse("qmd:separator/mix_291"));

        new ProcessorRecipeBuilder(SeparatorRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:mix_291_c"), 1)
                .addItemResult(item("qmd:mix_291"), 1)
                .addItemResult(itemTag("c:dusts/graphite"), 1)
                .save(RecipeOutputs.named(output, "qmd:separator/mix_291_from_c"), ResourceLocation.parse("qmd:separator/mix_291_from_c"));

        new ProcessorRecipeBuilder(SeparatorRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:mix_291_za"), 1)
                .addItemResult(item("qmd:mix_291"), 1)
                .addItemResult(item("nuclearcraftneohaul:zirconium_dust"), 1)
                .save(RecipeOutputs.named(output, "qmd:separator/mix_291_from_za"), ResourceLocation.parse("qmd:separator/mix_291_from_za"));
    }

    private NeohaulSeparatorRecipeRecipes() {}
}
