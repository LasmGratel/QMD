package lach_01298.qmd.datagen.recipe;

import com.nred.nuclearcraft.recipe.ProcessorRecipeBuilder;
import com.nred.nuclearcraft.recipe.processor.FluidInfuserRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import static lach_01298.qmd.datagen.recipe.NeohaulRecipeIngredients.*;

/** Native Neohaul infuser recipes. */
final class NeohaulInfuserRecipeRecipes {
    static void add(RecipeOutput output) {
        new ProcessorRecipeBuilder(FluidInfuserRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:part_detector_casing"), 1)
                .addFluidInput(fluidTag("c:liquid_hydrogen"), 1000)
                .addItemResult(item("qmd:bubble_chamber"), 1)
                .save(RecipeOutputs.named(output, "qmd:fluid_infuser/bubble_chamber"), ResourceLocation.parse("qmd:fluid_infuser/bubble_chamber"));

        new ProcessorRecipeBuilder(FluidInfuserRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:copernicium_291"), 1)
                .addFluidInput(fluidTag("c:nitrogen"), 1000)
                .addItemResult(item("qmd:copernicium_291_ni"), 1)
                .save(RecipeOutputs.named(output, "qmd:fluid_infuser/copernicium_291_ni"), ResourceLocation.parse("qmd:fluid_infuser/copernicium_291_ni"));

        new ProcessorRecipeBuilder(FluidInfuserRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:copernicium_291"), 1)
                .addFluidInput(fluidTag("c:oxygen"), 1000)
                .addItemResult(item("qmd:copernicium_291_ox"), 1)
                .save(RecipeOutputs.named(output, "qmd:fluid_infuser/copernicium_291_ox"), ResourceLocation.parse("qmd:fluid_infuser/copernicium_291_ox"));

        new ProcessorRecipeBuilder(FluidInfuserRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:discharge_lamp_empty"), 1)
                .addFluidInput(fluidTag("c:argon"), 250)
                .addItemResult(item("qmd:discharge_lamp_argon"), 1)
                .save(RecipeOutputs.named(output, "qmd:fluid_infuser/discharge_lamp_argon"), ResourceLocation.parse("qmd:fluid_infuser/discharge_lamp_argon"));

        new ProcessorRecipeBuilder(FluidInfuserRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:discharge_lamp_empty"), 1)
                .addFluidInput(fluidTag("c:helium"), 250)
                .addItemResult(item("qmd:discharge_lamp_helium"), 1)
                .save(RecipeOutputs.named(output, "qmd:fluid_infuser/discharge_lamp_helium"), ResourceLocation.parse("qmd:fluid_infuser/discharge_lamp_helium"));

        new ProcessorRecipeBuilder(FluidInfuserRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:discharge_lamp_empty"), 1)
                .addFluidInput(fluidTag("c:hydrogen"), 250)
                .addItemResult(item("qmd:discharge_lamp_hydrogen"), 1)
                .save(RecipeOutputs.named(output, "qmd:fluid_infuser/discharge_lamp_hydrogen"), ResourceLocation.parse("qmd:fluid_infuser/discharge_lamp_hydrogen"));

        new ProcessorRecipeBuilder(FluidInfuserRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:discharge_lamp_argon"), 1)
                .addFluidInput(fluidTag("c:mercury"), 144)
                .addItemResult(item("qmd:discharge_lamp_mercury"), 1)
                .save(RecipeOutputs.named(output, "qmd:fluid_infuser/discharge_lamp_mercury"), ResourceLocation.parse("qmd:fluid_infuser/discharge_lamp_mercury"));

        new ProcessorRecipeBuilder(FluidInfuserRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:discharge_lamp_empty"), 1)
                .addFluidInput(fluidTag("c:neon"), 250)
                .addItemResult(item("qmd:discharge_lamp_neon"), 1)
                .save(RecipeOutputs.named(output, "qmd:fluid_infuser/discharge_lamp_neon"), ResourceLocation.parse("qmd:fluid_infuser/discharge_lamp_neon"));

        new ProcessorRecipeBuilder(FluidInfuserRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:discharge_lamp_empty"), 1)
                .addFluidInput(fluidTag("c:nitrogen"), 250)
                .addItemResult(item("qmd:discharge_lamp_nitrogen"), 1)
                .save(RecipeOutputs.named(output, "qmd:fluid_infuser/discharge_lamp_nitrogen"), ResourceLocation.parse("qmd:fluid_infuser/discharge_lamp_nitrogen"));

        new ProcessorRecipeBuilder(FluidInfuserRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:discharge_lamp_empty"), 1)
                .addFluidInput(fluidTag("c:oxygen"), 250)
                .addItemResult(item("qmd:discharge_lamp_oxygen"), 1)
                .save(RecipeOutputs.named(output, "qmd:fluid_infuser/discharge_lamp_oxygen"), ResourceLocation.parse("qmd:fluid_infuser/discharge_lamp_oxygen"));

        new ProcessorRecipeBuilder(FluidInfuserRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:discharge_lamp_argon"), 1)
                .addFluidInput(fluidTag("c:sodium"), 144)
                .addItemResult(item("qmd:discharge_lamp_sodium"), 1)
                .save(RecipeOutputs.named(output, "qmd:fluid_infuser/discharge_lamp_sodium"), ResourceLocation.parse("qmd:fluid_infuser/discharge_lamp_sodium"));

        new ProcessorRecipeBuilder(FluidInfuserRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:mix_291"), 1)
                .addFluidInput(fluidTag("c:nitrogen"), 1000)
                .addItemResult(item("qmd:mix_291_ni"), 1)
                .save(RecipeOutputs.named(output, "qmd:fluid_infuser/mix_291_ni"), ResourceLocation.parse("qmd:fluid_infuser/mix_291_ni"));

        new ProcessorRecipeBuilder(FluidInfuserRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:mix_291"), 1)
                .addFluidInput(fluidTag("c:oxygen"), 1000)
                .addItemResult(item("qmd:mix_291_ox"), 1)
                .save(RecipeOutputs.named(output, "qmd:fluid_infuser/mix_291_ox"), ResourceLocation.parse("qmd:fluid_infuser/mix_291_ox"));

        new ProcessorRecipeBuilder(FluidInfuserRecipe.class, 1.0, 1.0, 0.0)
                .addItemInput(item("qmd:part_wire_chamber_casing"), 1)
                .addFluidInput(fluidTag("c:argon"), 1000)
                .addItemResult(item("qmd:wire_chamber"), 1)
                .save(RecipeOutputs.named(output, "qmd:fluid_infuser/wire_chamber"), ResourceLocation.parse("qmd:fluid_infuser/wire_chamber"));

        new ProcessorRecipeBuilder(FluidInfuserRecipe.class, 1, 1, 0)
                .addItemInput(item("qmd:part_empty_cooler"), 1)
                .addFluidInput(fluid("nuclearcraftneohaul:cryotheum"), 1000)
                .addItemResult(item("qmd:accelerator_cooler_cryotheum"), 1)
                .save(RecipeOutputs.named(output, "qmd:infuser/accelerator_cooler_cryotheum"), ResourceLocation.parse("qmd:infuser/accelerator_cooler_cryotheum"));

        new ProcessorRecipeBuilder(FluidInfuserRecipe.class, 1, 1, 0)
                .addItemInput(item("qmd:part_empty_cooler"), 1)
                .addFluidInput(fluid("nuclearcraftneohaul:enderium"), 576)
                .addItemResult(item("qmd:accelerator_cooler_enderium"), 1)
                .save(RecipeOutputs.named(output, "qmd:infuser/accelerator_cooler_enderium"), ResourceLocation.parse("qmd:infuser/accelerator_cooler_enderium"));

        new ProcessorRecipeBuilder(FluidInfuserRecipe.class, 1, 1, 0)
                .addItemInput(item("qmd:part_empty_cooler"), 1)
                .addFluidInput(fluid("nuclearcraftneohaul:liquid_helium"), 1000)
                .addItemResult(item("qmd:accelerator_cooler_liquid_helium"), 1)
                .save(RecipeOutputs.named(output, "qmd:infuser/accelerator_cooler_liquid_helium"), ResourceLocation.parse("qmd:infuser/accelerator_cooler_liquid_helium"));

        new ProcessorRecipeBuilder(FluidInfuserRecipe.class, 1, 1, 0)
                .addItemInput(item("qmd:part_empty_cooler"), 1)
                .addFluidInput(fluid("nuclearcraftneohaul:liquid_nitrogen"), 1000)
                .addItemResult(item("qmd:accelerator_cooler_liquid_nitrogen"), 1)
                .save(RecipeOutputs.named(output, "qmd:infuser/accelerator_cooler_liquid_nitrogen"), ResourceLocation.parse("qmd:infuser/accelerator_cooler_liquid_nitrogen"));

        new ProcessorRecipeBuilder(FluidInfuserRecipe.class, 1, 1, 0)
                .addItemInput(item("qmd:part_empty_cooler"), 1)
                .addFluidInput(fluid("minecraft:water"), 1000)
                .addItemResult(item("qmd:accelerator_cooler_water"), 1)
                .save(RecipeOutputs.named(output, "qmd:infuser/accelerator_cooler_water"), ResourceLocation.parse("qmd:infuser/accelerator_cooler_water"));

        new ProcessorRecipeBuilder(FluidInfuserRecipe.class, 1, 1, 0)
                .addItemInput(itemTag("c:dusts/copper"), 1)
                .addFluidInput(fluid("nuclearcraftneohaul:oxygen"), 1000)
                .addItemResult(item("qmd:chemical_dust_copper_oxide"), 1)
                .save(RecipeOutputs.named(output, "qmd:infuser/copper_oxide"), ResourceLocation.parse("qmd:infuser/copper_oxide"));

        new ProcessorRecipeBuilder(FluidInfuserRecipe.class, 1, 1, 0)
                .addItemInput(itemTag("c:dusts/hafnium"), 1)
                .addFluidInput(fluid("nuclearcraftneohaul:oxygen"), 1000)
                .addItemResult(item("qmd:chemical_dust_hafnium_oxide"), 1)
                .save(RecipeOutputs.named(output, "qmd:infuser/hafnium_oxide"), ResourceLocation.parse("qmd:infuser/hafnium_oxide"));

        new ProcessorRecipeBuilder(FluidInfuserRecipe.class, 1, 1, 0)
                .addItemInput(itemTag("c:dusts/iron"), 1)
                .addFluidInput(fluid("nuclearcraftneohaul:hydrofluoric_acid"), 1500)
                .addItemResult(item("qmd:chemical_dust_iron_fluoride"), 1)
                .save(RecipeOutputs.named(output.withConditions(hasTag("c:dusts/iron")), "qmd:infuser/iron_fluoride_from_dusts"), ResourceLocation.parse("qmd:infuser/iron_fluoride_from_dusts"));

        new ProcessorRecipeBuilder(FluidInfuserRecipe.class, 1, 1, 0)
                .addItemInput(itemTag("c:ingots/iron"), 1)
                .addFluidInput(fluid("nuclearcraftneohaul:hydrofluoric_acid"), 1500)
                .addItemResult(item("qmd:chemical_dust_iron_fluoride"), 1)
                .save(RecipeOutputs.named(output.withConditions(hasTag("c:ingots/iron")), "qmd:infuser/iron_fluoride_from_ingots"), ResourceLocation.parse("qmd:infuser/iron_fluoride_from_ingots"));
    }

    private NeohaulInfuserRecipeRecipes() {}
}
