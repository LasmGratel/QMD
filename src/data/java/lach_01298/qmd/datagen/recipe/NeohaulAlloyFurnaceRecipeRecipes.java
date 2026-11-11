package lach_01298.qmd.datagen.recipe;

import com.nred.nuclearcraft.recipe.ProcessorRecipeBuilder;
import com.nred.nuclearcraft.recipe.processor.AlloyFurnaceRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import static lach_01298.qmd.datagen.recipe.NeohaulRecipeIngredients.*;

/** Native Neohaul alloy furnace recipes. */
final class NeohaulAlloyFurnaceRecipeRecipes {
    static void add(RecipeOutput output) {
        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:copernicium_291"), 1)
                .addItemInput(itemTag("c:dusts/graphite"), 1)
                .addItemResult(item("qmd:copernicium_291_c"), 1)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/copernicium_291_c"), ResourceLocation.parse("qmd:alloy_furnace/copernicium_291_c"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:copernicium_291"), 1)
                .addItemInput(item("nuclearcraftneohaul:zirconium_ingot"), 1)
                .addItemResult(item("qmd:copernicium_291_za"), 1)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/copernicium_291_za"), ResourceLocation.parse("qmd:alloy_furnace/copernicium_291_za"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:mix_291"), 1)
                .addItemInput(itemTag("c:dusts/graphite"), 1)
                .addItemResult(item("qmd:mix_291_c"), 1)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/mix_291_c"), ResourceLocation.parse("qmd:alloy_furnace/mix_291_c"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:mix_291"), 1)
                .addItemInput(item("nuclearcraftneohaul:zirconium_ingot"), 1)
                .addItemResult(item("qmd:mix_291_za"), 1)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/mix_291_za"), ResourceLocation.parse("qmd:alloy_furnace/mix_291_za"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1, 1, 0)
                .addItemInput(itemTag("c:dusts/nickel"), 1)
                .addItemInput(itemTag("c:dusts/chromium"), 1)
                .addItemResult(item("qmd:ingot_nichrome"), 2)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/nichrome_dusts_dusts"), ResourceLocation.parse("qmd:alloy_furnace/nichrome_dusts_dusts"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1, 1, 0)
                .addItemInput(itemTag("c:dusts/nickel"), 1)
                .addItemInput(itemTag("c:ingots/chromium"), 1)
                .addItemResult(item("qmd:ingot_nichrome"), 2)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/nichrome_dusts_ingots"), ResourceLocation.parse("qmd:alloy_furnace/nichrome_dusts_ingots"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1, 1, 0)
                .addItemInput(itemTag("c:ingots/nickel"), 1)
                .addItemInput(itemTag("c:dusts/chromium"), 1)
                .addItemResult(item("qmd:ingot_nichrome"), 2)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/nichrome_ingots_dusts"), ResourceLocation.parse("qmd:alloy_furnace/nichrome_ingots_dusts"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1, 1, 0)
                .addItemInput(itemTag("c:ingots/nickel"), 1)
                .addItemInput(itemTag("c:ingots/chromium"), 1)
                .addItemResult(item("qmd:ingot_nichrome"), 2)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/nichrome_ingots_ingots"), ResourceLocation.parse("qmd:alloy_furnace/nichrome_ingots_ingots"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1, 1, 0)
                .addItemInput(itemTag("c:dusts/niobium"), 3)
                .addItemInput(itemTag("c:dusts/tin"), 1)
                .addItemResult(item("qmd:ingot_niobium_tin"), 4)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/niobium_tin_dusts_dusts"), ResourceLocation.parse("qmd:alloy_furnace/niobium_tin_dusts_dusts"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1, 1, 0)
                .addItemInput(itemTag("c:dusts/niobium"), 3)
                .addItemInput(itemTag("c:ingots/tin"), 1)
                .addItemResult(item("qmd:ingot_niobium_tin"), 4)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/niobium_tin_dusts_ingots"), ResourceLocation.parse("qmd:alloy_furnace/niobium_tin_dusts_ingots"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1, 1, 0)
                .addItemInput(itemTag("c:ingots/niobium"), 3)
                .addItemInput(itemTag("c:dusts/tin"), 1)
                .addItemResult(item("qmd:ingot_niobium_tin"), 4)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/niobium_tin_ingots_dusts"), ResourceLocation.parse("qmd:alloy_furnace/niobium_tin_ingots_dusts"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1, 1, 0)
                .addItemInput(itemTag("c:ingots/niobium"), 3)
                .addItemInput(itemTag("c:ingots/tin"), 1)
                .addItemResult(item("qmd:ingot_niobium_tin"), 4)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/niobium_tin_ingots_ingots"), ResourceLocation.parse("qmd:alloy_furnace/niobium_tin_ingots_ingots"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1, 1, 0)
                .addItemInput(itemTag("c:dusts/niobium"), 1)
                .addItemInput(itemTag("c:dusts/titanium"), 1)
                .addItemResult(item("qmd:ingot_niobium_titanium"), 2)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/niobium_titanium_dusts_dusts"), ResourceLocation.parse("qmd:alloy_furnace/niobium_titanium_dusts_dusts"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1, 1, 0)
                .addItemInput(itemTag("c:dusts/niobium"), 1)
                .addItemInput(itemTag("c:ingots/titanium"), 1)
                .addItemResult(item("qmd:ingot_niobium_titanium"), 2)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/niobium_titanium_dusts_ingots"), ResourceLocation.parse("qmd:alloy_furnace/niobium_titanium_dusts_ingots"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1, 1, 0)
                .addItemInput(itemTag("c:ingots/niobium"), 1)
                .addItemInput(itemTag("c:dusts/titanium"), 1)
                .addItemResult(item("qmd:ingot_niobium_titanium"), 2)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/niobium_titanium_ingots_dusts"), ResourceLocation.parse("qmd:alloy_furnace/niobium_titanium_ingots_dusts"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1, 1, 0)
                .addItemInput(itemTag("c:ingots/niobium"), 1)
                .addItemInput(itemTag("c:ingots/titanium"), 1)
                .addItemResult(item("qmd:ingot_niobium_titanium"), 2)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/niobium_titanium_ingots_ingots"), ResourceLocation.parse("qmd:alloy_furnace/niobium_titanium_ingots_ingots"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1, 1, 0)
                .addItemInput(itemTag("c:dusts/osmium"), 1)
                .addItemInput(itemTag("c:dusts/iridium"), 1)
                .addItemResult(item("qmd:ingot_osmiridium"), 2)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/osmiridium_dusts_dusts"), ResourceLocation.parse("qmd:alloy_furnace/osmiridium_dusts_dusts"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1, 1, 0)
                .addItemInput(itemTag("c:dusts/osmium"), 1)
                .addItemInput(itemTag("c:ingots/iridium"), 1)
                .addItemResult(item("qmd:ingot_osmiridium"), 2)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/osmiridium_dusts_ingots"), ResourceLocation.parse("qmd:alloy_furnace/osmiridium_dusts_ingots"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1, 1, 0)
                .addItemInput(itemTag("c:ingots/osmium"), 1)
                .addItemInput(itemTag("c:dusts/iridium"), 1)
                .addItemResult(item("qmd:ingot_osmiridium"), 2)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/osmiridium_ingots_dusts"), ResourceLocation.parse("qmd:alloy_furnace/osmiridium_ingots_dusts"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1, 1, 0)
                .addItemInput(itemTag("c:ingots/osmium"), 1)
                .addItemInput(itemTag("c:ingots/iridium"), 1)
                .addItemResult(item("qmd:ingot_osmiridium"), 2)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/osmiridium_ingots_ingots"), ResourceLocation.parse("qmd:alloy_furnace/osmiridium_ingots_ingots"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("nuclearcraftneohaul:boron_11"), 9)
                .addItemInput(item("nuclearcraftneohaul:boron_10"), 3)
                .addItemResult(item("nuclearcraftneohaul:boron_ingot"), 12)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/qmd_boron"), ResourceLocation.parse("qmd:alloy_furnace/qmd_boron"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("nuclearcraftneohaul:lithium_7"), 9)
                .addItemInput(item("nuclearcraftneohaul:lithium_6"), 1)
                .addItemResult(item("nuclearcraftneohaul:lithium_ingot"), 10)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/qmd_lithium"), ResourceLocation.parse("qmd:alloy_furnace/qmd_lithium"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:isotope_magnesium_24"), 8)
                .addItemInput(item("qmd:isotope_magnesium_26"), 1)
                .addItemResult(item("nuclearcraftneohaul:magnesium_ingot"), 9)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/qmd_magnesium"), ResourceLocation.parse("qmd:alloy_furnace/qmd_magnesium"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:ingot_nickel"), 1)
                .addItemInput(item("qmd:ingot_chromium"), 1)
                .addItemResult(item("qmd:ingot_nichrome"), 2)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/qmd_nichrome"), ResourceLocation.parse("qmd:alloy_furnace/qmd_nichrome"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:ingot_niobium"), 3)
                .addItemInput(item("nuclearcraftneohaul:tin_ingot"), 1)
                .addItemResult(item("qmd:ingot_niobium_tin"), 4)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/qmd_niobium_tin"), ResourceLocation.parse("qmd:alloy_furnace/qmd_niobium_tin"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:ingot_niobium"), 1)
                .addItemInput(item("qmd:ingot_titanium"), 1)
                .addItemResult(item("qmd:ingot_niobium_titanium"), 2)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/qmd_niobium_titanium"), ResourceLocation.parse("qmd:alloy_furnace/qmd_niobium_titanium"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:ingot_osmium"), 1)
                .addItemInput(item("qmd:ingot_iridium"), 1)
                .addItemResult(item("qmd:ingot_osmiridium"), 2)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/qmd_osmiridium"), ResourceLocation.parse("qmd:alloy_furnace/qmd_osmiridium"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(itemTag("c:ingots/steel"), 5)
                .addItemInput(item("qmd:ingot_chromium"), 1)
                .addItemResult(item("qmd:ingot_stainless_steel"), 6)
                .save(RecipeOutputs.named(output.withConditions(hasTag("c:ingots/steel")), "qmd:alloy_furnace/qmd_stainless_steel"), ResourceLocation.parse("qmd:alloy_furnace/qmd_stainless_steel"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:ingot_nichrome"), 2)
                .addItemInput(item("qmd:ingot_niobium_titanium"), 1)
                .addItemResult(item("qmd:ingot_super_alloy"), 3)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/qmd_super_alloy"), ResourceLocation.parse("qmd:alloy_furnace/qmd_super_alloy"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 2.0, 2.0, 0.0)
                .addItemInput(item("qmd:ingot_tungsten"), 1)
                .addItemInput(item("nuclearcraftneohaul:graphite_ingot"), 1)
                .addItemResult(item("qmd:ingot_tungsten_carbide"), 2)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/qmd_tungsten_carbide"), ResourceLocation.parse("qmd:alloy_furnace/qmd_tungsten_carbide"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("nuclearcraftneohaul:uranium_238"), 9)
                .addItemInput(item("nuclearcraftneohaul:uranium_235"), 1)
                .addItemResult(item("nuclearcraftneohaul:uranium_ingot"), 10)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/qmd_uranium"), ResourceLocation.parse("qmd:alloy_furnace/qmd_uranium"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1, 1, 0)
                .addItemInput(itemTag("c:dusts/steel"), 5)
                .addItemInput(itemTag("c:dusts/chromium"), 1)
                .addItemResult(item("qmd:ingot_stainless_steel"), 6)
                .save(RecipeOutputs.named(output.withConditions(hasTag("c:dusts/steel")), "qmd:alloy_furnace/stainless_steel_dusts_dusts"), ResourceLocation.parse("qmd:alloy_furnace/stainless_steel_dusts_dusts"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1, 1, 0)
                .addItemInput(itemTag("c:dusts/steel"), 5)
                .addItemInput(itemTag("c:ingots/chromium"), 1)
                .addItemResult(item("qmd:ingot_stainless_steel"), 6)
                .save(RecipeOutputs.named(output.withConditions(hasTag("c:dusts/steel")), "qmd:alloy_furnace/stainless_steel_dusts_ingots"), ResourceLocation.parse("qmd:alloy_furnace/stainless_steel_dusts_ingots"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1, 1, 0)
                .addItemInput(itemTag("c:ingots/steel"), 5)
                .addItemInput(itemTag("c:dusts/chromium"), 1)
                .addItemResult(item("qmd:ingot_stainless_steel"), 6)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/stainless_steel_ingots_dusts"), ResourceLocation.parse("qmd:alloy_furnace/stainless_steel_ingots_dusts"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1, 1, 0)
                .addItemInput(itemTag("c:ingots/steel"), 5)
                .addItemInput(itemTag("c:ingots/chromium"), 1)
                .addItemResult(item("qmd:ingot_stainless_steel"), 6)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/stainless_steel_ingots_ingots"), ResourceLocation.parse("qmd:alloy_furnace/stainless_steel_ingots_ingots"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1, 1, 0)
                .addItemInput(itemTag("c:dusts/nichrome"), 2)
                .addItemInput(itemTag("c:dusts/niobium_titanium"), 1)
                .addItemResult(item("qmd:ingot_super_alloy"), 3)
                .save(RecipeOutputs.named(output.withConditions(hasTag("c:dusts/nichrome"), hasTag("c:dusts/niobium_titanium")), "qmd:alloy_furnace/super_alloy_dusts_dusts"), ResourceLocation.parse("qmd:alloy_furnace/super_alloy_dusts_dusts"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1, 1, 0)
                .addItemInput(itemTag("c:dusts/nichrome"), 2)
                .addItemInput(itemTag("c:ingots/niobium_titanium"), 1)
                .addItemResult(item("qmd:ingot_super_alloy"), 3)
                .save(RecipeOutputs.named(output.withConditions(hasTag("c:dusts/nichrome")), "qmd:alloy_furnace/super_alloy_dusts_ingots"), ResourceLocation.parse("qmd:alloy_furnace/super_alloy_dusts_ingots"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1, 1, 0)
                .addItemInput(itemTag("c:ingots/nichrome"), 2)
                .addItemInput(itemTag("c:dusts/niobium_titanium"), 1)
                .addItemResult(item("qmd:ingot_super_alloy"), 3)
                .save(RecipeOutputs.named(output.withConditions(hasTag("c:dusts/niobium_titanium")), "qmd:alloy_furnace/super_alloy_ingots_dusts"), ResourceLocation.parse("qmd:alloy_furnace/super_alloy_ingots_dusts"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 1, 1, 0)
                .addItemInput(itemTag("c:ingots/nichrome"), 2)
                .addItemInput(itemTag("c:ingots/niobium_titanium"), 1)
                .addItemResult(item("qmd:ingot_super_alloy"), 3)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/super_alloy_ingots_ingots"), ResourceLocation.parse("qmd:alloy_furnace/super_alloy_ingots_ingots"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 2, 2, 0)
                .addItemInput(itemTag("c:dusts/tungsten"), 1)
                .addItemInput(itemTag("c:dusts/graphite"), 1)
                .addItemResult(item("qmd:ingot_tungsten_carbide"), 2)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/tungsten_carbide_dusts_dusts"), ResourceLocation.parse("qmd:alloy_furnace/tungsten_carbide_dusts_dusts"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 2, 2, 0)
                .addItemInput(itemTag("c:dusts/tungsten"), 1)
                .addItemInput(itemTag("c:ingots/graphite"), 1)
                .addItemResult(item("qmd:ingot_tungsten_carbide"), 2)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/tungsten_carbide_dusts_ingots"), ResourceLocation.parse("qmd:alloy_furnace/tungsten_carbide_dusts_ingots"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 2, 2, 0)
                .addItemInput(itemTag("c:ingots/tungsten"), 1)
                .addItemInput(itemTag("c:dusts/graphite"), 1)
                .addItemResult(item("qmd:ingot_tungsten_carbide"), 2)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/tungsten_carbide_ingots_dusts"), ResourceLocation.parse("qmd:alloy_furnace/tungsten_carbide_ingots_dusts"));

        new ProcessorRecipeBuilder(AlloyFurnaceRecipe.class, 2, 2, 0)
                .addItemInput(itemTag("c:ingots/tungsten"), 1)
                .addItemInput(itemTag("c:ingots/graphite"), 1)
                .addItemResult(item("qmd:ingot_tungsten_carbide"), 2)
                .save(RecipeOutputs.named(output, "qmd:alloy_furnace/tungsten_carbide_ingots_ingots"), ResourceLocation.parse("qmd:alloy_furnace/tungsten_carbide_ingots_ingots"));
    }

    private NeohaulAlloyFurnaceRecipeRecipes() {}
}
