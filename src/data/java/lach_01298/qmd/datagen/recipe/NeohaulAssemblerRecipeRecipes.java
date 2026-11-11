package lach_01298.qmd.datagen.recipe;

import com.nred.nuclearcraft.recipe.ProcessorRecipeBuilder;
import com.nred.nuclearcraft.recipe.processor.AssemblerRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import static lach_01298.qmd.datagen.recipe.NeohaulRecipeIngredients.*;

/** Native Neohaul assembler recipes. */
final class NeohaulAssemblerRecipeRecipes {
    static void add(RecipeOutput output) {
        new ProcessorRecipeBuilder(AssemblerRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(itemTag("c:processors/basic"), 1)
                .addItemInput(itemTag("c:dusts/redstone"), 4)
                .addItemInput(itemTag("c:dusts/hafnium_oxide"), 1)
                .addItemInput(itemTag("c:silicon/p_doped"), 1)
                .addItemResult(item("qmd:semiconductor_advanced_processor"), 1)
                .save(RecipeOutputs.named(output.withConditions(hasTag("c:dusts/redstone")), "qmd:assembler/advanced_processor"), ResourceLocation.parse("qmd:assembler/advanced_processor"));

        new ProcessorRecipeBuilder(AssemblerRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(itemTag("c:silicon/n_doped"), 1)
                .addItemInput(itemTag("c:dusts/redstone"), 4)
                .addItemInput(itemTag("c:ingots/gold"), 1)
                .addItemInput(itemTag("c:ingots/silver"), 1)
                .addItemResult(item("qmd:semiconductor_basic_processor"), 1)
                .save(RecipeOutputs.named(output.withConditions(hasTag("c:dusts/redstone"), hasTag("c:ingots/gold")), "qmd:assembler/basic_processor"), ResourceLocation.parse("qmd:assembler/basic_processor"));

        new ProcessorRecipeBuilder(AssemblerRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(itemTag("c:dusts/bismuth"), 2)
                .addItemInput(itemTag("c:dusts/strontium"), 2)
                .addItemInput(itemTag("c:dusts/calcium"), 2)
                .addItemInput(itemTag("c:dusts/copper_oxide"), 3)
                .addItemResult(item("qmd:chemical_dust_bscco"), 3)
                .save(RecipeOutputs.named(output, "qmd:assembler/compound_bscco"), ResourceLocation.parse("qmd:assembler/compound_bscco"));

        new ProcessorRecipeBuilder(AssemblerRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(itemTag("c:ingots/mercury"), 1)
                .addItemInput(itemTag("c:dusts/barium"), 2)
                .addItemInput(itemTag("c:dusts/calcium"), 2)
                .addItemInput(itemTag("c:dusts/copper_oxide"), 3)
                .addItemResult(item("qmd:chemical_dust_hbcco"), 3)
                .save(RecipeOutputs.named(output, "qmd:assembler/compound_hbcco"), ResourceLocation.parse("qmd:assembler/compound_hbcco"));

        new ProcessorRecipeBuilder(AssemblerRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(itemTag("c:dusts/samarium"), 1)
                .addItemInput(itemTag("c:dusts/strontium"), 1)
                .addItemInput(itemTag("c:dusts/iron_fluoride"), 2)
                .addItemInput(itemTag("c:dusts/arsenic"), 2)
                .addItemResult(item("qmd:chemical_dust_ssfaf"), 3)
                .save(RecipeOutputs.named(output, "qmd:assembler/compound_ssfaf"), ResourceLocation.parse("qmd:assembler/compound_ssfaf"));

        new ProcessorRecipeBuilder(AssemblerRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(itemTag("c:dusts/yttrium"), 1)
                .addItemInput(itemTag("c:dusts/barium"), 2)
                .addItemInput(itemTag("c:dusts/copper_oxide"), 3)
                .addItemResult(item("qmd:chemical_dust_ybco"), 3)
                .save(RecipeOutputs.named(output, "qmd:assembler/compound_ybco"), ResourceLocation.parse("qmd:assembler/compound_ybco"));

        new ProcessorRecipeBuilder(AssemblerRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(itemTag("c:processors/advanced"), 1)
                .addItemInput(itemTag("c:wires/bscco"), 4)
                .addItemInput(itemTag("c:dusts/hafnium_oxide"), 1)
                .addItemInput(itemTag("c:ingots/platinum"), 1)
                .addItemResult(item("qmd:semiconductor_elite_processor"), 1)
                .save(RecipeOutputs.named(output, "qmd:assembler/elite_processor"), ResourceLocation.parse("qmd:assembler/elite_processor"));

        new ProcessorRecipeBuilder(AssemblerRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:copernicium_291"), 1)
                .addItemInput(item("nuclearcraftneohaul:uranium_238"), 8)
                .addItemResult(item("qmd:mix_291"), 9)
                .save(RecipeOutputs.named(output, "qmd:assembler/mix_291"), ResourceLocation.parse("qmd:assembler/mix_291"));

        new ProcessorRecipeBuilder(AssemblerRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:mix_291_c"), 9)
                .addItemInput(itemTag("c:dusts/graphite"), 1)
                .addItemInput(item("nuclearcraftneohaul:pyrolytic_carbon"), 1)
                .addItemInput(item("nuclearcraftneohaul:silicon_carbide_alloy"), 1)
                .addItemResult(item("qmd:mix_291_tr"), 9)
                .save(RecipeOutputs.named(output, "qmd:assembler/mix_291_tr"), ResourceLocation.parse("qmd:assembler/mix_291_tr"));

        new ProcessorRecipeBuilder(AssemblerRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:ingot_tungsten"), 2)
                .addItemResult(item("qmd:source_tungsten_filament"), 1)
                .save(RecipeOutputs.named(output, "qmd:assembler/tungsten_filament"), ResourceLocation.parse("qmd:assembler/tungsten_filament"));

        new ProcessorRecipeBuilder(AssemblerRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(itemTag("c:dusts/bscco"), 3)
                .addItemInput(itemTag("c:ingots/silver"), 6)
                .addItemResult(item("qmd:part_wire_bscco"), 6)
                .save(RecipeOutputs.named(output, "qmd:assembler/wire_bscco"), ResourceLocation.parse("qmd:assembler/wire_bscco"));

        new ProcessorRecipeBuilder(AssemblerRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(itemTag("c:ingots/tungsten"), 4)
                .addItemInput(itemTag("c:ingots/gold"), 2)
                .addItemResult(item("qmd:part_wire_gold_tungsten"), 6)
                .save(RecipeOutputs.named(output.withConditions(hasTag("c:ingots/gold")), "qmd:assembler/wire_gold_tungsten"), ResourceLocation.parse("qmd:assembler/wire_gold_tungsten"));

        new ProcessorRecipeBuilder(AssemblerRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(itemTag("c:dusts/hbcco"), 3)
                .addItemInput(itemTag("c:ingots/silver"), 6)
                .addItemResult(item("qmd:part_wire_hbcco"), 6)
                .save(RecipeOutputs.named(output, "qmd:assembler/wire_hbcco"), ResourceLocation.parse("qmd:assembler/wire_hbcco"));

        new ProcessorRecipeBuilder(AssemblerRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(itemTag("c:dusts/ssfaf"), 3)
                .addItemInput(itemTag("c:ingots/silver"), 6)
                .addItemResult(item("qmd:part_wire_ssfaf"), 6)
                .save(RecipeOutputs.named(output, "qmd:assembler/wire_ssfaf"), ResourceLocation.parse("qmd:assembler/wire_ssfaf"));

        new ProcessorRecipeBuilder(AssemblerRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(itemTag("c:dusts/ybco"), 3)
                .addItemInput(itemTag("c:ingots/silver"), 6)
                .addItemResult(item("qmd:part_wire_ybco"), 6)
                .save(RecipeOutputs.named(output, "qmd:assembler/wire_ybco"), ResourceLocation.parse("qmd:assembler/wire_ybco"));
    }

    private NeohaulAssemblerRecipeRecipes() {}
}
