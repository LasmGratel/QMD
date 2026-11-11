package lach_01298.qmd.datagen.recipe;

import com.nred.nuclearcraft.recipe.ProcessorRecipeBuilder;
import com.nred.nuclearcraft.recipe.processor.IngotFormerRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import static lach_01298.qmd.datagen.recipe.NeohaulRecipeIngredients.*;

/** Native Neohaul ingot former recipes. */
final class NeohaulIngotFormerRecipeRecipes {
    static void add(RecipeOutput output) {
        new ProcessorRecipeBuilder(IngotFormerRecipe.class, 1, 1, 0.0)
                .addFluidInput(fluidTag("c:calcium"), 144)
                .addItemResult(item("qmd:ingot_calcium"), 1)
                .save(RecipeOutputs.named(output, "qmd:ingot_former/qmd_calcium"), ResourceLocation.parse("qmd:ingot_former/qmd_calcium"));

        new ProcessorRecipeBuilder(IngotFormerRecipe.class, 1, 1, 0.0)
                .addFluidInput(fluidTag("c:chromium"), 144)
                .addItemResult(item("qmd:ingot_chromium"), 1)
                .save(RecipeOutputs.named(output, "qmd:ingot_former/qmd_chromium"), ResourceLocation.parse("qmd:ingot_former/qmd_chromium"));

        new ProcessorRecipeBuilder(IngotFormerRecipe.class, 1, 1, 0.0)
                .addFluidInput(fluidTag("c:cobalt"), 144)
                .addItemResult(item("qmd:ingot_cobalt"), 1)
                .save(RecipeOutputs.named(output, "qmd:ingot_former/qmd_cobalt"), ResourceLocation.parse("qmd:ingot_former/qmd_cobalt"));

        new ProcessorRecipeBuilder(IngotFormerRecipe.class, 1, 1, 0.0)
                .addFluidInput(fluidTag("c:hafnium"), 144)
                .addItemResult(item("qmd:ingot_hafnium"), 1)
                .save(RecipeOutputs.named(output, "qmd:ingot_former/qmd_hafnium"), ResourceLocation.parse("qmd:ingot_former/qmd_hafnium"));

        new ProcessorRecipeBuilder(IngotFormerRecipe.class, 1, 1, 0.0)
                .addFluidInput(fluidTag("c:iridium"), 144)
                .addItemResult(item("qmd:ingot_iridium"), 1)
                .save(RecipeOutputs.named(output, "qmd:ingot_former/qmd_iridium"), ResourceLocation.parse("qmd:ingot_former/qmd_iridium"));

        new ProcessorRecipeBuilder(IngotFormerRecipe.class, 1, 1, 0.0)
                .addFluidInput(fluidTag("c:mercury"), 144)
                .addItemResult(item("qmd:ingot_mercury"), 1)
                .save(RecipeOutputs.named(output, "qmd:ingot_former/qmd_mercury"), ResourceLocation.parse("qmd:ingot_former/qmd_mercury"));

        new ProcessorRecipeBuilder(IngotFormerRecipe.class, 1, 1, 0.0)
                .addFluidInput(fluidTag("c:neodymium"), 144)
                .addItemResult(item("qmd:ingot_neodymium"), 1)
                .save(RecipeOutputs.named(output, "qmd:ingot_former/qmd_neodymium"), ResourceLocation.parse("qmd:ingot_former/qmd_neodymium"));

        new ProcessorRecipeBuilder(IngotFormerRecipe.class, 1, 1, 0.0)
                .addFluidInput(fluidTag("c:nickel"), 144)
                .addItemResult(item("qmd:ingot_nickel"), 1)
                .save(RecipeOutputs.named(output, "qmd:ingot_former/qmd_nickel"), ResourceLocation.parse("qmd:ingot_former/qmd_nickel"));

        new ProcessorRecipeBuilder(IngotFormerRecipe.class, 1, 1, 0.0)
                .addFluidInput(fluidTag("c:niobium"), 144)
                .addItemResult(item("qmd:ingot_niobium"), 1)
                .save(RecipeOutputs.named(output, "qmd:ingot_former/qmd_niobium"), ResourceLocation.parse("qmd:ingot_former/qmd_niobium"));

        new ProcessorRecipeBuilder(IngotFormerRecipe.class, 1, 1, 0.0)
                .addFluidInput(fluidTag("c:osmium"), 144)
                .addItemResult(item("qmd:ingot_osmium"), 1)
                .save(RecipeOutputs.named(output, "qmd:ingot_former/qmd_osmium"), ResourceLocation.parse("qmd:ingot_former/qmd_osmium"));

        new ProcessorRecipeBuilder(IngotFormerRecipe.class, 1, 1, 0.0)
                .addFluidInput(fluidTag("c:platinum"), 144)
                .addItemResult(item("qmd:ingot_platinum"), 1)
                .save(RecipeOutputs.named(output, "qmd:ingot_former/qmd_platinum"), ResourceLocation.parse("qmd:ingot_former/qmd_platinum"));

        new ProcessorRecipeBuilder(IngotFormerRecipe.class, 1, 1, 0.0)
                .addFluidInput(fluidTag("c:strontium"), 144)
                .addItemResult(item("qmd:ingot_strontium"), 1)
                .save(RecipeOutputs.named(output, "qmd:ingot_former/qmd_strontium"), ResourceLocation.parse("qmd:ingot_former/qmd_strontium"));

        new ProcessorRecipeBuilder(IngotFormerRecipe.class, 1, 1, 0.0)
                .addFluidInput(fluidTag("c:titanium"), 144)
                .addItemResult(item("qmd:ingot_titanium"), 1)
                .save(RecipeOutputs.named(output, "qmd:ingot_former/qmd_titanium"), ResourceLocation.parse("qmd:ingot_former/qmd_titanium"));

        new ProcessorRecipeBuilder(IngotFormerRecipe.class, 1, 1, 0.0)
                .addFluidInput(fluidTag("c:tungsten"), 144)
                .addItemResult(item("qmd:ingot_tungsten"), 1)
                .save(RecipeOutputs.named(output, "qmd:ingot_former/qmd_tungsten"), ResourceLocation.parse("qmd:ingot_former/qmd_tungsten"));

        new ProcessorRecipeBuilder(IngotFormerRecipe.class, 1, 1, 0.0)
                .addFluidInput(fluidTag("c:yttrium"), 144)
                .addItemResult(item("qmd:ingot_yttrium"), 1)
                .save(RecipeOutputs.named(output, "qmd:ingot_former/qmd_yttrium"), ResourceLocation.parse("qmd:ingot_former/qmd_yttrium"));

        new ProcessorRecipeBuilder(IngotFormerRecipe.class, 1, 1, 0.0)
                .addFluidInput(fluidTag("c:zinc"), 144)
                .addItemResult(item("qmd:ingot_zinc"), 1)
                .save(RecipeOutputs.named(output, "qmd:ingot_former/qmd_zinc"), ResourceLocation.parse("qmd:ingot_former/qmd_zinc"));
    }

    private NeohaulIngotFormerRecipeRecipes() {}
}
