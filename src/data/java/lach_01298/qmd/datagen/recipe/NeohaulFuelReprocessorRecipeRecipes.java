package lach_01298.qmd.datagen.recipe;

import com.nred.nuclearcraft.recipe.ProcessorRecipeBuilder;
import com.nred.nuclearcraft.recipe.processor.FuelReprocessorRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import static lach_01298.qmd.datagen.recipe.NeohaulRecipeIngredients.*;

/** Native Neohaul fuel reprocessor recipes. */
final class NeohaulFuelReprocessorRecipeRecipes {
    static void add(RecipeOutput output) {
        new ProcessorRecipeBuilder(FuelReprocessorRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:depleted_mix_291_ni"), 9)
                .addItemResult(item("nuclearcraftneohaul:americium_243_ni"), 4)
                .addItemResult(item("nuclearcraftneohaul:curium_243_ni"), 2)
                .addItemResult(item("nuclearcraftneohaul:ruthenium_106_dust"), 2, 30)
                .addItemResult(item("nuclearcraftneohaul:curium_245_ni"), 1)
                .addItemResult(item("nuclearcraftneohaul:berkelium_247_ni"), 1)
                .addItemResult(item("nuclearcraftneohaul:europium_155_dust"), 2, 20)
                .save(RecipeOutputs.named(output, "qmd:fuel_reprocessor/mix_291_ni"), ResourceLocation.parse("qmd:fuel_reprocessor/mix_291_ni"));

        new ProcessorRecipeBuilder(FuelReprocessorRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:depleted_mix_291_ox"), 9)
                .addItemResult(item("nuclearcraftneohaul:americium_243_ox"), 4)
                .addItemResult(item("nuclearcraftneohaul:curium_243_ox"), 2)
                .addItemResult(item("nuclearcraftneohaul:ruthenium_106_dust"), 2, 30)
                .addItemResult(item("nuclearcraftneohaul:curium_245_ox"), 1)
                .addItemResult(item("nuclearcraftneohaul:berkelium_247_ox"), 1)
                .addItemResult(item("nuclearcraftneohaul:europium_155_dust"), 2, 20)
                .save(RecipeOutputs.named(output, "qmd:fuel_reprocessor/mix_291_ox"), ResourceLocation.parse("qmd:fuel_reprocessor/mix_291_ox"));

        new ProcessorRecipeBuilder(FuelReprocessorRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:depleted_mix_291_tr"), 9)
                .addItemResult(item("nuclearcraftneohaul:americium_243_c"), 4)
                .addItemResult(item("nuclearcraftneohaul:curium_243_c"), 2)
                .addItemResult(item("nuclearcraftneohaul:ruthenium_106_dust"), 2, 30)
                .addItemResult(item("nuclearcraftneohaul:graphite_dust"), 3)
                .addItemResult(item("nuclearcraftneohaul:curium_245_c"), 1)
                .addItemResult(item("nuclearcraftneohaul:berkelium_247_c"), 1)
                .addItemResult(item("nuclearcraftneohaul:europium_155_dust"), 2, 20)
                .addItemResult(item("nuclearcraftneohaul:silicon_carbide_alloy"), 1)
                .save(RecipeOutputs.named(output, "qmd:fuel_reprocessor/mix_291_tr"), ResourceLocation.parse("qmd:fuel_reprocessor/mix_291_tr"));

        new ProcessorRecipeBuilder(FuelReprocessorRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:depleted_mix_291_za"), 9)
                .addItemResult(item("nuclearcraftneohaul:americium_243_za"), 4)
                .addItemResult(item("nuclearcraftneohaul:curium_243_za"), 2)
                .addItemResult(item("nuclearcraftneohaul:ruthenium_106_dust"), 2, 30)
                .addItemResult(item("nuclearcraftneohaul:zirconium_dust"), 1)
                .addItemResult(item("nuclearcraftneohaul:curium_245_za"), 1)
                .addItemResult(item("nuclearcraftneohaul:berkelium_247_za"), 1)
                .addItemResult(item("nuclearcraftneohaul:europium_155_dust"), 2, 20)
                .save(RecipeOutputs.named(output, "qmd:fuel_reprocessor/mix_291_za"), ResourceLocation.parse("qmd:fuel_reprocessor/mix_291_za"));

        new ProcessorRecipeBuilder(FuelReprocessorRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:waste_fission_heavy"), 1)
                .addItemResult(item("qmd:dust_neodymium"), 1, 17)
                .addItemResult(item("qmd:dust_barium"), 1, 11)
                .addItemResult(item("nuclearcraftneohaul:tin_dust"), 1, 59)
                .addItemResult(item("nuclearcraftneohaul:silver_dust"), 1, 9)
                .addItemResult(item("nuclearcraftneohaul:palladium_dust"), 1, 29)
                .addItemResult(item("nuclearcraftneohaul:ruthenium_106_dust"), 1, 9)
                .addItemResult(item("nuclearcraftneohaul:molybdenum_dust"), 1, 37)
                .addItemResult(item("nuclearcraftneohaul:zirconium_dust"), 1, 29)
                .save(RecipeOutputs.named(output, "qmd:fuel_reprocessor/waste_fission_heavy"), ResourceLocation.parse("qmd:fuel_reprocessor/waste_fission_heavy"));

        new ProcessorRecipeBuilder(FuelReprocessorRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:waste_fission_light"), 1)
                .addItemResult(item("nuclearcraftneohaul:tin_dust"), 1, 19)
                .addItemResult(item("nuclearcraftneohaul:silver_dust"), 1, 10)
                .addItemResult(item("nuclearcraftneohaul:palladium_dust"), 1, 32)
                .addItemResult(item("nuclearcraftneohaul:molybdenum_dust"), 1, 56)
                .addItemResult(item("nuclearcraftneohaul:zirconium_dust"), 1, 48)
                .addItemResult(item("qmd:dust_yttrium"), 1, 11)
                .addItemResult(item("nuclearcraftneohaul:strontium_90_dust"), 1, 10)
                .addItemResult(item("qmd:dust_strontium"), 1, 14)
                .save(RecipeOutputs.named(output, "qmd:fuel_reprocessor/waste_fission_light"), ResourceLocation.parse("qmd:fuel_reprocessor/waste_fission_light"));

        new ProcessorRecipeBuilder(FuelReprocessorRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:waste_spallation_americium"), 1)
                .addItemResult(item("nuclearcraftneohaul:polonium_dust"), 1, 9)
                .addItemResult(item("nuclearcraftneohaul:bismuth_dust"), 1, 15)
                .addItemResult(item("nuclearcraftneohaul:lead_dust"), 1, 38)
                .addItemResult(item("qmd:ingot_mercury"), 1, 15)
                .addItemResult(item("qmd:dust_platinum"), 1, 11)
                .addItemResult(item("qmd:dust_osmium"), 1, 12)
                .save(RecipeOutputs.named(output, "qmd:fuel_reprocessor/waste_spallation_americium"), ResourceLocation.parse("qmd:fuel_reprocessor/waste_spallation_americium"));

        new ProcessorRecipeBuilder(FuelReprocessorRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:waste_spallation_berkelium"), 1)
                .addItemResult(item("nuclearcraftneohaul:radium_dust"), 1, 15)
                .addItemResult(item("nuclearcraftneohaul:polonium_dust"), 1, 18)
                .addItemResult(item("nuclearcraftneohaul:bismuth_dust"), 1, 12)
                .addItemResult(item("nuclearcraftneohaul:lead_dust"), 1, 31)
                .addItemResult(item("qmd:ingot_mercury"), 1, 11)
                .addItemResult(item("qmd:dust_platinum"), 1, 13)
                .save(RecipeOutputs.named(output, "qmd:fuel_reprocessor/waste_spallation_berkelium"), ResourceLocation.parse("qmd:fuel_reprocessor/waste_spallation_berkelium"));

        new ProcessorRecipeBuilder(FuelReprocessorRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:waste_spallation_bismuth"), 1)
                .addItemResult(item("nuclearcraftneohaul:lead_dust"), 1, 21)
                .addItemResult(item("qmd:ingot_mercury"), 1, 16)
                .addItemResult(item("qmd:dust_osmium"), 1, 17)
                .addItemResult(item("qmd:dust_tungsten"), 1, 14)
                .addItemResult(item("qmd:dust_hafnium"), 1, 18)
                .addItemResult(item("qmd:dust_ytterbium"), 1, 14)
                .save(RecipeOutputs.named(output, "qmd:fuel_reprocessor/waste_spallation_bismuth"), ResourceLocation.parse("qmd:fuel_reprocessor/waste_spallation_bismuth"));

        new ProcessorRecipeBuilder(FuelReprocessorRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:waste_spallation_californium"), 1)
                .addItemResult(item("nuclearcraftneohaul:polonium_dust"), 1, 10)
                .addItemResult(item("nuclearcraftneohaul:bismuth_dust"), 1, 15)
                .addItemResult(item("nuclearcraftneohaul:lead_dust"), 1, 41)
                .addItemResult(item("qmd:ingot_mercury"), 1, 15)
                .addItemResult(item("qmd:dust_platinum"), 1, 10)
                .addItemResult(item("qmd:dust_osmium"), 1, 9)
                .save(RecipeOutputs.named(output, "qmd:fuel_reprocessor/waste_spallation_californium"), ResourceLocation.parse("qmd:fuel_reprocessor/waste_spallation_californium"));

        new ProcessorRecipeBuilder(FuelReprocessorRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:waste_spallation_curium"), 1)
                .addItemResult(item("nuclearcraftneohaul:polonium_dust"), 1, 10)
                .addItemResult(item("nuclearcraftneohaul:bismuth_dust"), 1, 16)
                .addItemResult(item("nuclearcraftneohaul:lead_dust"), 1, 37)
                .addItemResult(item("qmd:ingot_mercury"), 1, 15)
                .addItemResult(item("qmd:dust_platinum"), 1, 11)
                .addItemResult(item("qmd:dust_osmium"), 1, 11)
                .save(RecipeOutputs.named(output, "qmd:fuel_reprocessor/waste_spallation_curium"), ResourceLocation.parse("qmd:fuel_reprocessor/waste_spallation_curium"));

        new ProcessorRecipeBuilder(FuelReprocessorRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:waste_spallation_gold"), 1)
                .addItemResult(item("qmd:dust_platinum"), 1, 19)
                .addItemResult(item("qmd:dust_osmium"), 1, 15)
                .addItemResult(item("qmd:dust_hafnium"), 1, 17)
                .addItemResult(item("qmd:dust_erbium"), 1, 14)
                .addItemResult(item("nuclearcraftneohaul:gadolinium_dust"), 1, 17)
                .addItemResult(item("qmd:dust_samarium"), 1, 18)
                .save(RecipeOutputs.named(output, "qmd:fuel_reprocessor/waste_spallation_gold"), ResourceLocation.parse("qmd:fuel_reprocessor/waste_spallation_gold"));

        new ProcessorRecipeBuilder(FuelReprocessorRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:waste_spallation_hafnium"), 1)
                .addItemResult(item("qmd:dust_erbium"), 1, 12)
                .addItemResult(item("nuclearcraftneohaul:dysprosium_dust"), 1, 13)
                .addItemResult(item("nuclearcraftneohaul:gadolinium_dust"), 1, 17)
                .addItemResult(item("qmd:dust_samarium"), 1, 21)
                .addItemResult(item("qmd:dust_neodymium"), 1, 17)
                .addItemResult(item("qmd:dust_barium"), 1, 20)
                .save(RecipeOutputs.named(output, "qmd:fuel_reprocessor/waste_spallation_hafnium"), ResourceLocation.parse("qmd:fuel_reprocessor/waste_spallation_hafnium"));

        new ProcessorRecipeBuilder(FuelReprocessorRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:waste_spallation_iridium"), 1)
                .addItemResult(item("qmd:dust_osmium"), 1, 22)
                .addItemResult(item("qmd:dust_hafnium"), 1, 15)
                .addItemResult(item("qmd:dust_erbium"), 1, 13)
                .addItemResult(item("nuclearcraftneohaul:dysprosium_dust"), 1, 14)
                .addItemResult(item("nuclearcraftneohaul:gadolinium_dust"), 1, 17)
                .addItemResult(item("qmd:dust_samarium"), 1, 19)
                .save(RecipeOutputs.named(output, "qmd:fuel_reprocessor/waste_spallation_iridium"), ResourceLocation.parse("qmd:fuel_reprocessor/waste_spallation_iridium"));

        new ProcessorRecipeBuilder(FuelReprocessorRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:waste_spallation_lead"), 1)
                .addItemResult(item("qmd:ingot_mercury"), 1, 18)
                .addItemResult(item("qmd:dust_osmium"), 1, 18)
                .addItemResult(item("qmd:dust_tungsten"), 1, 15)
                .addItemResult(item("qmd:dust_hafnium"), 1, 19)
                .addItemResult(item("qmd:dust_ytterbium"), 1, 15)
                .addItemResult(item("nuclearcraftneohaul:gadolinium_dust"), 1, 15)
                .save(RecipeOutputs.named(output, "qmd:fuel_reprocessor/waste_spallation_lead"), ResourceLocation.parse("qmd:fuel_reprocessor/waste_spallation_lead"));

        new ProcessorRecipeBuilder(FuelReprocessorRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:waste_spallation_mercury"), 1)
                .addItemResult(item("qmd:dust_osmium"), 1, 17)
                .addItemResult(item("qmd:dust_hafnium"), 1, 19)
                .addItemResult(item("qmd:dust_ytterbium"), 1, 15)
                .addItemResult(item("qmd:dust_erbium"), 1, 15)
                .addItemResult(item("nuclearcraftneohaul:gadolinium_dust"), 1, 17)
                .addItemResult(item("qmd:dust_samarium"), 1, 17)
                .save(RecipeOutputs.named(output, "qmd:fuel_reprocessor/waste_spallation_mercury"), ResourceLocation.parse("qmd:fuel_reprocessor/waste_spallation_mercury"));

        new ProcessorRecipeBuilder(FuelReprocessorRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:waste_spallation_neptunium"), 1)
                .addItemResult(item("nuclearcraftneohaul:bismuth_dust"), 1, 15)
                .addItemResult(item("nuclearcraftneohaul:lead_dust"), 1, 35)
                .addItemResult(item("qmd:ingot_mercury"), 1, 15)
                .addItemResult(item("qmd:dust_platinum"), 1, 12)
                .addItemResult(item("qmd:dust_osmium"), 1, 14)
                .addItemResult(item("qmd:dust_tungsten"), 1, 9)
                .save(RecipeOutputs.named(output, "qmd:fuel_reprocessor/waste_spallation_neptunium"), ResourceLocation.parse("qmd:fuel_reprocessor/waste_spallation_neptunium"));

        new ProcessorRecipeBuilder(FuelReprocessorRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:waste_spallation_osmium"), 1)
                .addItemResult(item("qmd:dust_hafnium"), 1, 17)
                .addItemResult(item("qmd:dust_erbium"), 1, 15)
                .addItemResult(item("nuclearcraftneohaul:dysprosium_dust"), 1, 15)
                .addItemResult(item("nuclearcraftneohaul:gadolinium_dust"), 1, 18)
                .addItemResult(item("qmd:dust_samarium"), 1, 20)
                .addItemResult(item("qmd:dust_neodymium"), 1, 15)
                .save(RecipeOutputs.named(output, "qmd:fuel_reprocessor/waste_spallation_osmium"), ResourceLocation.parse("qmd:fuel_reprocessor/waste_spallation_osmium"));

        new ProcessorRecipeBuilder(FuelReprocessorRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:waste_spallation_platinum"), 1)
                .addItemResult(item("qmd:dust_osmium"), 1, 16)
                .addItemResult(item("qmd:dust_hafnium"), 1, 17)
                .addItemResult(item("qmd:dust_erbium"), 1, 15)
                .addItemResult(item("nuclearcraftneohaul:dysprosium_dust"), 1, 15)
                .addItemResult(item("nuclearcraftneohaul:gadolinium_dust"), 1, 18)
                .addItemResult(item("qmd:dust_samarium"), 1, 19)
                .save(RecipeOutputs.named(output, "qmd:fuel_reprocessor/waste_spallation_platinum"), ResourceLocation.parse("qmd:fuel_reprocessor/waste_spallation_platinum"));

        new ProcessorRecipeBuilder(FuelReprocessorRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:waste_spallation_plutonium"), 1)
                .addItemResult(item("nuclearcraftneohaul:polonium_dust"), 1, 9)
                .addItemResult(item("nuclearcraftneohaul:bismuth_dust"), 1, 15)
                .addItemResult(item("nuclearcraftneohaul:lead_dust"), 1, 36)
                .addItemResult(item("qmd:ingot_mercury"), 1, 15)
                .addItemResult(item("qmd:dust_platinum"), 1, 12)
                .addItemResult(item("qmd:dust_osmium"), 1, 13)
                .save(RecipeOutputs.named(output, "qmd:fuel_reprocessor/waste_spallation_plutonium"), ResourceLocation.parse("qmd:fuel_reprocessor/waste_spallation_plutonium"));

        new ProcessorRecipeBuilder(FuelReprocessorRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:waste_spallation_polonium"), 1)
                .addItemResult(item("nuclearcraftneohaul:bismuth_dust"), 1, 16)
                .addItemResult(item("nuclearcraftneohaul:lead_dust"), 1, 17)
                .addItemResult(item("qmd:ingot_mercury"), 1, 16)
                .addItemResult(item("qmd:dust_osmium"), 1, 18)
                .addItemResult(item("qmd:dust_tungsten"), 1, 15)
                .addItemResult(item("qmd:dust_hafnium"), 1, 18)
                .save(RecipeOutputs.named(output, "qmd:fuel_reprocessor/waste_spallation_polonium"), ResourceLocation.parse("qmd:fuel_reprocessor/waste_spallation_polonium"));

        new ProcessorRecipeBuilder(FuelReprocessorRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:waste_spallation_protactinium"), 1)
                .addItemResult(item("nuclearcraftneohaul:bismuth_dust"), 1, 13)
                .addItemResult(item("nuclearcraftneohaul:lead_dust"), 1, 32)
                .addItemResult(item("qmd:ingot_mercury"), 1, 16)
                .addItemResult(item("qmd:dust_platinum"), 1, 12)
                .addItemResult(item("qmd:dust_osmium"), 1, 15)
                .addItemResult(item("qmd:dust_hafnium"), 1, 12)
                .save(RecipeOutputs.named(output, "qmd:fuel_reprocessor/waste_spallation_protactinium"), ResourceLocation.parse("qmd:fuel_reprocessor/waste_spallation_protactinium"));

        new ProcessorRecipeBuilder(FuelReprocessorRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:waste_spallation_radium"), 1)
                .addItemResult(item("nuclearcraftneohaul:bismuth_dust"), 1, 16)
                .addItemResult(item("nuclearcraftneohaul:lead_dust"), 1, 32)
                .addItemResult(item("qmd:ingot_mercury"), 1, 14)
                .addItemResult(item("qmd:dust_platinum"), 1, 11)
                .addItemResult(item("qmd:dust_osmium"), 1, 14)
                .addItemResult(item("qmd:dust_hafnium"), 1, 13)
                .save(RecipeOutputs.named(output, "qmd:fuel_reprocessor/waste_spallation_radium"), ResourceLocation.parse("qmd:fuel_reprocessor/waste_spallation_radium"));

        new ProcessorRecipeBuilder(FuelReprocessorRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:waste_spallation_thorium"), 1)
                .addItemResult(item("nuclearcraftneohaul:bismuth_dust"), 1, 13)
                .addItemResult(item("nuclearcraftneohaul:lead_dust"), 1, 33)
                .addItemResult(item("qmd:ingot_mercury"), 1, 15)
                .addItemResult(item("qmd:dust_platinum"), 1, 12)
                .addItemResult(item("qmd:dust_osmium"), 1, 15)
                .addItemResult(item("qmd:dust_hafnium"), 1, 12)
                .save(RecipeOutputs.named(output, "qmd:fuel_reprocessor/waste_spallation_thorium"), ResourceLocation.parse("qmd:fuel_reprocessor/waste_spallation_thorium"));

        new ProcessorRecipeBuilder(FuelReprocessorRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:waste_spallation_tungsten"), 1)
                .addItemResult(item("qmd:dust_hafnium"), 1, 15)
                .addItemResult(item("nuclearcraftneohaul:dysprosium_dust"), 1, 14)
                .addItemResult(item("nuclearcraftneohaul:gadolinium_dust"), 1, 17)
                .addItemResult(item("qmd:dust_samarium"), 1, 20)
                .addItemResult(item("qmd:dust_neodymium"), 1, 16)
                .addItemResult(item("qmd:dust_barium"), 1, 18)
                .save(RecipeOutputs.named(output, "qmd:fuel_reprocessor/waste_spallation_tungsten"), ResourceLocation.parse("qmd:fuel_reprocessor/waste_spallation_tungsten"));

        new ProcessorRecipeBuilder(FuelReprocessorRecipe.class, 1, 1, 0.0)
                .addItemInput(item("qmd:waste_spallation_uranium"), 1)
                .addItemResult(item("nuclearcraftneohaul:bismuth_dust"), 1, 15)
                .addItemResult(item("nuclearcraftneohaul:lead_dust"), 1, 35)
                .addItemResult(item("qmd:ingot_mercury"), 1, 15)
                .addItemResult(item("qmd:dust_platinum"), 1, 12)
                .addItemResult(item("qmd:dust_osmium"), 1, 14)
                .addItemResult(item("qmd:dust_tungsten"), 1, 9)
                .save(RecipeOutputs.named(output, "qmd:fuel_reprocessor/waste_spallation_uranium"), ResourceLocation.parse("qmd:fuel_reprocessor/waste_spallation_uranium"));
    }

    private NeohaulFuelReprocessorRecipeRecipes() {}
}
