package lach_01298.qmd.datagen.recipe;

import com.nred.nuclearcraft.recipe.ProcessorRecipeBuilder;
import com.nred.nuclearcraft.recipe.processor.MelterRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import static lach_01298.qmd.datagen.recipe.NeohaulRecipeIngredients.*;

/** Native Neohaul melter recipes. */
final class NeohaulMelterRecipeRecipes {
    static void add(RecipeOutput output) {
        new ProcessorRecipeBuilder(MelterRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:ingot_calcium"), 1)
                .addFluidResult(fluid("qmd:calcium"), 144)
                .save(RecipeOutputs.named(output, "qmd:melter/qmd_calcium"), ResourceLocation.parse("qmd:melter/qmd_calcium"));

        new ProcessorRecipeBuilder(MelterRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:ingot_chromium"), 1)
                .addFluidResult(fluid("qmd:chromium"), 144)
                .save(RecipeOutputs.named(output, "qmd:melter/qmd_chromium"), ResourceLocation.parse("qmd:melter/qmd_chromium"));

        new ProcessorRecipeBuilder(MelterRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:ingot_cobalt"), 1)
                .addFluidResult(fluid("qmd:cobalt"), 144)
                .save(RecipeOutputs.named(output, "qmd:melter/qmd_cobalt"), ResourceLocation.parse("qmd:melter/qmd_cobalt"));

        new ProcessorRecipeBuilder(MelterRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:ingot_hafnium"), 1)
                .addFluidResult(fluid("qmd:hafnium"), 144)
                .save(RecipeOutputs.named(output, "qmd:melter/qmd_hafnium"), ResourceLocation.parse("qmd:melter/qmd_hafnium"));

        new ProcessorRecipeBuilder(MelterRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:ingot_iridium"), 1)
                .addFluidResult(fluid("qmd:iridium"), 144)
                .save(RecipeOutputs.named(output, "qmd:melter/qmd_iridium"), ResourceLocation.parse("qmd:melter/qmd_iridium"));

        new ProcessorRecipeBuilder(MelterRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:ingot_mercury"), 1)
                .addFluidResult(fluid("qmd:mercury"), 144)
                .save(RecipeOutputs.named(output, "qmd:melter/qmd_mercury"), ResourceLocation.parse("qmd:melter/qmd_mercury"));

        new ProcessorRecipeBuilder(MelterRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:ingot_neodymium"), 1)
                .addFluidResult(fluid("qmd:neodymium"), 144)
                .save(RecipeOutputs.named(output, "qmd:melter/qmd_neodymium"), ResourceLocation.parse("qmd:melter/qmd_neodymium"));

        new ProcessorRecipeBuilder(MelterRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:ingot_nickel"), 1)
                .addFluidResult(fluid("qmd:nickel"), 144)
                .save(RecipeOutputs.named(output, "qmd:melter/qmd_nickel"), ResourceLocation.parse("qmd:melter/qmd_nickel"));

        new ProcessorRecipeBuilder(MelterRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:ingot_niobium"), 1)
                .addFluidResult(fluid("qmd:niobium"), 144)
                .save(RecipeOutputs.named(output, "qmd:melter/qmd_niobium"), ResourceLocation.parse("qmd:melter/qmd_niobium"));

        new ProcessorRecipeBuilder(MelterRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:ingot_osmium"), 1)
                .addFluidResult(fluid("qmd:osmium"), 144)
                .save(RecipeOutputs.named(output, "qmd:melter/qmd_osmium"), ResourceLocation.parse("qmd:melter/qmd_osmium"));

        new ProcessorRecipeBuilder(MelterRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:ingot_platinum"), 1)
                .addFluidResult(fluid("qmd:platinum"), 144)
                .save(RecipeOutputs.named(output, "qmd:melter/qmd_platinum"), ResourceLocation.parse("qmd:melter/qmd_platinum"));

        new ProcessorRecipeBuilder(MelterRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:ingot_strontium"), 1)
                .addFluidResult(fluid("qmd:strontium"), 144)
                .save(RecipeOutputs.named(output, "qmd:melter/qmd_strontium"), ResourceLocation.parse("qmd:melter/qmd_strontium"));

        new ProcessorRecipeBuilder(MelterRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:ingot_titanium"), 1)
                .addFluidResult(fluid("qmd:titanium"), 144)
                .save(RecipeOutputs.named(output, "qmd:melter/qmd_titanium"), ResourceLocation.parse("qmd:melter/qmd_titanium"));

        new ProcessorRecipeBuilder(MelterRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:ingot_tungsten"), 1)
                .addFluidResult(fluid("qmd:tungsten"), 144)
                .save(RecipeOutputs.named(output, "qmd:melter/qmd_tungsten"), ResourceLocation.parse("qmd:melter/qmd_tungsten"));

        new ProcessorRecipeBuilder(MelterRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:ingot_yttrium"), 1)
                .addFluidResult(fluid("qmd:yttrium"), 144)
                .save(RecipeOutputs.named(output, "qmd:melter/qmd_yttrium"), ResourceLocation.parse("qmd:melter/qmd_yttrium"));

        new ProcessorRecipeBuilder(MelterRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:ingot_zinc"), 1)
                .addFluidResult(fluid("qmd:zinc"), 144)
                .save(RecipeOutputs.named(output, "qmd:melter/qmd_zinc"), ResourceLocation.parse("qmd:melter/qmd_zinc"));
    }

    private NeohaulMelterRecipeRecipes() {}
}
