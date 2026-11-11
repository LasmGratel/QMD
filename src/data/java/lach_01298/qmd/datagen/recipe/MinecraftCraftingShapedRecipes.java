package lach_01298.qmd.datagen.recipe;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;

/** Vanilla shaped recipes built directly with Minecraft's recipe builder. */
public final class MinecraftCraftingShapedRecipes extends RecipeProvider {
    public MinecraftCraftingShapedRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipes) {
        RecipeOutput output = RecipeOutputs.withoutAdvancements(recipes);
        part0(output);
        part1(output);
        part2(output);
    }

    private static void part0(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:atmosphere_collector"), 1)
                .pattern("ASA")
                .pattern("BMB")
                .pattern("ASA")
                .define('A', item("nuclearcraftneohaul:advanced_plating"))
                .define('S', itemTag("c:ingots/steel"))
                .define('M', item("nuclearcraftneohaul:electric_motor"))
                .define('B', item("minecraft:bucket"))
                .unlockedBy("has_material", has(item("nuclearcraftneohaul:advanced_plating")))
                .save(output, ResourceLocation.parse("qmd:atmosphere_collector"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:copper_liquefier_compressor"), 4)
                .pattern("ISI")
                .pattern("MCM")
                .pattern("ISI")
                .define('I', itemTag("c:ingots/steel"))
                .define('C', item("nuclearcraftneohaul:steel_chassis"))
                .define('S', itemTag("c:ingots/copper"))
                .define('M', item("nuclearcraftneohaul:electric_motor"))
                .unlockedBy("has_material", has(itemTag("c:ingots/steel")))
                .save(output, ResourceLocation.parse("qmd:copper_liquefier_compressor"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_beam"), 3)
                .pattern("SSS")
                .pattern("BBB")
                .pattern("SSS")
                .define('S', itemTag("c:ingots/stainless_steel"))
                .define('B', item("qmd:beamline"))
                .unlockedBy("has_material", has(itemTag("c:ingots/stainless_steel")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_beam"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_beam_port"), 4)
                .pattern("STS")
                .pattern("BFB")
                .pattern("STS")
                .define('S', itemTag("c:ingots/stainless_steel"))
                .define('T', itemTag("c:ingots/tough"))
                .define('B', item("qmd:beamline"))
                .define('F', item("nuclearcraftneohaul:steel_chassis"))
                .unlockedBy("has_material", has(itemTag("c:ingots/stainless_steel")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_beam_port"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_casing"), 16)
                .pattern("STS")
                .pattern("TFT")
                .pattern("STS")
                .define('S', itemTag("c:ingots/stainless_steel"))
                .define('T', itemTag("c:ingots/tough"))
                .define('F', item("nuclearcraftneohaul:steel_chassis"))
                .unlockedBy("has_material", has(itemTag("c:ingots/stainless_steel")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_casing"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_computer_port"), 1)
                .pattern("STS")
                .pattern("TFT")
                .pattern("STS")
                .define('S', item("qmd:semiconductor_basic_processor"))
                .define('T', itemTag("c:ingots/gold"))
                .define('F', item("qmd:accelerator_casing"))
                .unlockedBy("has_material", has(item("qmd:semiconductor_basic_processor")))
                .save(output.withConditions(new NotCondition(new TagEmptyCondition("c:ingots/gold"))), ResourceLocation.parse("qmd:crafting/accelerator_computer_port"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_coolant_inlet"), 4)
                .pattern("SIS")
                .pattern("TFT")
                .pattern("STS")
                .define('S', itemTag("c:ingots/stainless_steel"))
                .define('I', item("nuclearcraftneohaul:servomechanism"))
                .define('T', itemTag("c:ingots/tough"))
                .define('F', item("nuclearcraftneohaul:steel_chassis"))
                .unlockedBy("has_material", has(itemTag("c:ingots/stainless_steel")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_coolant_inlet"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_coolant_outlet"), 4)
                .pattern("STS")
                .pattern("TFT")
                .pattern("SIS")
                .define('S', itemTag("c:ingots/stainless_steel"))
                .define('I', item("nuclearcraftneohaul:servomechanism"))
                .define('T', itemTag("c:ingots/tough"))
                .define('F', item("nuclearcraftneohaul:steel_chassis"))
                .unlockedBy("has_material", has(itemTag("c:ingots/stainless_steel")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_coolant_outlet"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_cooler_aluminum"), 1)
                .pattern(" I ")
                .pattern("ICI")
                .pattern(" I ")
                .define('C', item("qmd:part_empty_cooler"))
                .define('I', itemTag("c:ingots/aluminum"))
                .unlockedBy("has_material", has(item("qmd:part_empty_cooler")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_cooler_aluminum"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_cooler_arsenic"), 1)
                .pattern("III")
                .pattern("ICI")
                .pattern("III")
                .define('C', item("qmd:part_empty_cooler"))
                .define('I', itemTag("c:dusts/arsenic"))
                .unlockedBy("has_material", has(item("qmd:part_empty_cooler")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_cooler_arsenic"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_cooler_boron"), 1)
                .pattern(" I ")
                .pattern("ICI")
                .pattern(" I ")
                .define('C', item("qmd:part_empty_cooler"))
                .define('I', itemTag("c:ingots/boron"))
                .unlockedBy("has_material", has(item("qmd:part_empty_cooler")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_cooler_boron"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_cooler_carobbiite"), 1)
                .pattern("III")
                .pattern("ICI")
                .pattern("III")
                .define('C', item("qmd:part_empty_cooler"))
                .define('I', itemTag("c:gems/carobbiite"))
                .unlockedBy("has_material", has(item("qmd:part_empty_cooler")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_cooler_carobbiite"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_cooler_copper"), 1)
                .pattern(" I ")
                .pattern("ICI")
                .pattern(" I ")
                .define('C', item("qmd:part_empty_cooler"))
                .define('I', itemTag("c:ingots/copper"))
                .unlockedBy("has_material", has(item("qmd:part_empty_cooler")))
                .save(output.withConditions(new NotCondition(new TagEmptyCondition("c:ingots/copper"))), ResourceLocation.parse("qmd:crafting/accelerator_cooler_copper"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_cooler_diamond"), 1)
                .pattern(" I ")
                .pattern("ICI")
                .pattern(" I ")
                .define('C', item("qmd:part_empty_cooler"))
                .define('I', itemTag("c:gems/diamond"))
                .unlockedBy("has_material", has(item("qmd:part_empty_cooler")))
                .save(output.withConditions(new NotCondition(new TagEmptyCondition("c:gems/diamond"))), ResourceLocation.parse("qmd:crafting/accelerator_cooler_diamond"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_cooler_emerald"), 1)
                .pattern(" I ")
                .pattern("ICI")
                .pattern(" I ")
                .define('C', item("qmd:part_empty_cooler"))
                .define('I', itemTag("c:gems/emerald"))
                .unlockedBy("has_material", has(item("qmd:part_empty_cooler")))
                .save(output.withConditions(new NotCondition(new TagEmptyCondition("c:gems/emerald"))), ResourceLocation.parse("qmd:crafting/accelerator_cooler_emerald"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_cooler_end_stone"), 1)
                .pattern("DID")
                .pattern("ICI")
                .pattern("DID")
                .define('C', item("qmd:part_empty_cooler"))
                .define('I', item("minecraft:end_stone"))
                .define('D', itemTag("c:dusts/end_stone"))
                .unlockedBy("has_material", has(item("qmd:part_empty_cooler")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_cooler_end_stone"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_cooler_enderium"), 1)
                .pattern(" I ")
                .pattern("ICI")
                .pattern(" I ")
                .define('I', itemTag("c:ingots/enderium"))
                .define('C', item("qmd:part_empty_cooler"))
                .unlockedBy("has_material", has(itemTag("c:ingots/enderium")))
                .save(output.withConditions(new NotCondition(new TagEmptyCondition("c:ingots/enderium"))), ResourceLocation.parse("qmd:crafting/accelerator_cooler_enderium"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_cooler_fluorite"), 1)
                .pattern("III")
                .pattern("ICI")
                .pattern("III")
                .define('C', item("qmd:part_empty_cooler"))
                .define('I', itemTag("c:gems/fluorite"))
                .unlockedBy("has_material", has(item("qmd:part_empty_cooler")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_cooler_fluorite"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_cooler_glowstone"), 1)
                .pattern("III")
                .pattern("ICI")
                .pattern("III")
                .define('C', item("qmd:part_empty_cooler"))
                .define('I', itemTag("c:dusts/glowstone"))
                .unlockedBy("has_material", has(item("qmd:part_empty_cooler")))
                .save(output.withConditions(new NotCondition(new TagEmptyCondition("c:dusts/glowstone"))), ResourceLocation.parse("qmd:crafting/accelerator_cooler_glowstone"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_cooler_gold"), 1)
                .pattern(" I ")
                .pattern("ICI")
                .pattern(" I ")
                .define('C', item("qmd:part_empty_cooler"))
                .define('I', itemTag("c:ingots/gold"))
                .unlockedBy("has_material", has(item("qmd:part_empty_cooler")))
                .save(output.withConditions(new NotCondition(new TagEmptyCondition("c:ingots/gold"))), ResourceLocation.parse("qmd:crafting/accelerator_cooler_gold"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_cooler_iron"), 1)
                .pattern(" I ")
                .pattern("ICI")
                .pattern(" I ")
                .define('C', item("qmd:part_empty_cooler"))
                .define('I', itemTag("c:ingots/iron"))
                .unlockedBy("has_material", has(item("qmd:part_empty_cooler")))
                .save(output.withConditions(new NotCondition(new TagEmptyCondition("c:ingots/iron"))), ResourceLocation.parse("qmd:crafting/accelerator_cooler_iron"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_cooler_lapis"), 1)
                .pattern("III")
                .pattern("ICI")
                .pattern("III")
                .define('C', item("qmd:part_empty_cooler"))
                .define('I', itemTag("c:gems/lapis"))
                .unlockedBy("has_material", has(item("qmd:part_empty_cooler")))
                .save(output.withConditions(new NotCondition(new TagEmptyCondition("c:gems/lapis"))), ResourceLocation.parse("qmd:crafting/accelerator_cooler_lapis"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_cooler_lead"), 1)
                .pattern(" I ")
                .pattern("ICI")
                .pattern(" I ")
                .define('C', item("qmd:part_empty_cooler"))
                .define('I', itemTag("c:ingots/lead"))
                .unlockedBy("has_material", has(item("qmd:part_empty_cooler")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_cooler_lead"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_cooler_lithium"), 1)
                .pattern(" I ")
                .pattern("ICI")
                .pattern(" I ")
                .define('C', item("qmd:part_empty_cooler"))
                .define('I', itemTag("c:ingots/lithium"))
                .unlockedBy("has_material", has(item("qmd:part_empty_cooler")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_cooler_lithium"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_cooler_magnesium"), 1)
                .pattern(" I ")
                .pattern("ICI")
                .pattern(" I ")
                .define('C', item("qmd:part_empty_cooler"))
                .define('I', itemTag("c:ingots/magnesium"))
                .unlockedBy("has_material", has(item("qmd:part_empty_cooler")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_cooler_magnesium"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_cooler_manganese"), 1)
                .pattern(" I ")
                .pattern("ICI")
                .pattern(" I ")
                .define('C', item("qmd:part_empty_cooler"))
                .define('I', itemTag("c:ingots/manganese"))
                .unlockedBy("has_material", has(item("qmd:part_empty_cooler")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_cooler_manganese"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_cooler_nether_brick"), 1)
                .pattern("DID")
                .pattern("ICI")
                .pattern("DID")
                .define('C', item("qmd:part_empty_cooler"))
                .define('I', item("minecraft:nether_bricks"))
                .define('D', item("minecraft:nether_brick"))
                .unlockedBy("has_material", has(item("qmd:part_empty_cooler")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_cooler_nether_brick"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_cooler_obsidian"), 1)
                .pattern("DID")
                .pattern("ICI")
                .pattern("DID")
                .define('C', item("qmd:part_empty_cooler"))
                .define('I', item("minecraft:obsidian"))
                .define('D', itemTag("c:dusts/obsidian"))
                .unlockedBy("has_material", has(item("qmd:part_empty_cooler")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_cooler_obsidian"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_cooler_prismarine"), 1)
                .pattern(" I ")
                .pattern("ICI")
                .pattern(" I ")
                .define('C', item("qmd:part_empty_cooler"))
                .define('I', item("minecraft:prismarine_shard"))
                .unlockedBy("has_material", has(item("qmd:part_empty_cooler")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_cooler_prismarine"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_cooler_purpur"), 1)
                .pattern("DID")
                .pattern("ICI")
                .pattern("DID")
                .define('C', item("qmd:part_empty_cooler"))
                .define('I', item("minecraft:purpur_block"))
                .define('D', item("minecraft:popped_chorus_fruit"))
                .unlockedBy("has_material", has(item("qmd:part_empty_cooler")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_cooler_purpur"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_cooler_quartz"), 1)
                .pattern("III")
                .pattern("ICI")
                .pattern("III")
                .define('C', item("qmd:part_empty_cooler"))
                .define('I', itemTag("c:gems/quartz"))
                .unlockedBy("has_material", has(item("qmd:part_empty_cooler")))
                .save(output.withConditions(new NotCondition(new TagEmptyCondition("c:gems/quartz"))), ResourceLocation.parse("qmd:crafting/accelerator_cooler_quartz"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_cooler_redstone"), 1)
                .pattern("III")
                .pattern("ICI")
                .pattern("III")
                .define('C', item("qmd:part_empty_cooler"))
                .define('I', itemTag("c:dusts/redstone"))
                .unlockedBy("has_material", has(item("qmd:part_empty_cooler")))
                .save(output.withConditions(new NotCondition(new TagEmptyCondition("c:dusts/redstone"))), ResourceLocation.parse("qmd:crafting/accelerator_cooler_redstone"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_cooler_silver"), 1)
                .pattern(" I ")
                .pattern("ICI")
                .pattern(" I ")
                .define('C', item("qmd:part_empty_cooler"))
                .define('I', itemTag("c:ingots/silver"))
                .unlockedBy("has_material", has(item("qmd:part_empty_cooler")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_cooler_silver"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_cooler_slime"), 1)
                .pattern("III")
                .pattern("ICI")
                .pattern("III")
                .define('C', item("qmd:part_empty_cooler"))
                .define('I', itemTag("c:slimeballs"))
                .unlockedBy("has_material", has(item("qmd:part_empty_cooler")))
                .save(output.withConditions(new NotCondition(new TagEmptyCondition("c:slimeballs"))), ResourceLocation.parse("qmd:crafting/accelerator_cooler_slime"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_cooler_tin"), 1)
                .pattern(" I ")
                .pattern("ICI")
                .pattern(" I ")
                .define('C', item("qmd:part_empty_cooler"))
                .define('I', itemTag("c:ingots/tin"))
                .unlockedBy("has_material", has(item("qmd:part_empty_cooler")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_cooler_tin"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_cooler_villiaumite"), 1)
                .pattern("III")
                .pattern("ICI")
                .pattern("III")
                .define('C', item("qmd:part_empty_cooler"))
                .define('I', itemTag("c:gems/villiaumite"))
                .unlockedBy("has_material", has(item("qmd:part_empty_cooler")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_cooler_villiaumite"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_energy_port"), 4)
                .pattern("SIS")
                .pattern("TFT")
                .pattern("SIS")
                .define('S', itemTag("c:ingots/stainless_steel"))
                .define('I', itemTag("c:ingots/niobium_tin"))
                .define('T', itemTag("c:ingots/tough"))
                .define('F', item("nuclearcraftneohaul:steel_chassis"))
                .unlockedBy("has_material", has(itemTag("c:ingots/stainless_steel")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_energy_port"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_ion_collector"), 1)
                .pattern("PCP")
                .pattern("CFC")
                .pattern("PCP")
                .define('P', item("nuclearcraftneohaul:advanced_plating"))
                .define('C', itemTag("c:ingots/copper"))
                .define('F', item("qmd:accelerator_casing"))
                .unlockedBy("has_material", has(item("nuclearcraftneohaul:advanced_plating")))
                .save(output.withConditions(new NotCondition(new TagEmptyCondition("c:ingots/copper"))), ResourceLocation.parse("qmd:crafting/accelerator_ion_collector"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_ion_source_basic"), 1)
                .pattern("AAA")
                .pattern(" CT")
                .pattern("AAA")
                .define('A', item("nuclearcraftneohaul:advanced_plating"))
                .define('C', item("qmd:accelerator_casing"))
                .define('T', item("qmd:source_tungsten_filament"))
                .unlockedBy("has_material", has(item("nuclearcraftneohaul:advanced_plating")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_ion_source_basic"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_ion_source_laser"), 1)
                .pattern("LPL")
                .pattern("EIE")
                .pattern("LPL")
                .define('L', item("qmd:part_laser_assembly"))
                .define('P', item("nuclearcraftneohaul:elite_plating"))
                .define('E', item("qmd:semiconductor_elite_processor"))
                .define('I', item("qmd:accelerator_ion_source_basic"))
                .unlockedBy("has_material", has(item("qmd:part_laser_assembly")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_ion_source_laser"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_magnet_aluminium"), 2)
                .pattern("CCC")
                .pattern("STS")
                .pattern("CCC")
                .define('C', itemTag("c:ingots/aluminum"))
                .define('S', itemTag("c:ingots/stainless_steel"))
                .define('T', itemTag("c:ingots/tough"))
                .unlockedBy("has_material", has(itemTag("c:ingots/aluminum")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_magnet_aluminium"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_magnet_bscco"), 2)
                .pattern("CCC")
                .pattern("STS")
                .pattern("CCC")
                .define('C', itemTag("c:wires/bscco"))
                .define('S', itemTag("c:ingots/stainless_steel"))
                .define('T', itemTag("c:ingots/tough"))
                .unlockedBy("has_material", has(itemTag("c:wires/bscco")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_magnet_bscco"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_magnet_copper"), 2)
                .pattern("CCC")
                .pattern("STS")
                .pattern("CCC")
                .define('C', itemTag("c:ingots/copper"))
                .define('S', itemTag("c:ingots/stainless_steel"))
                .define('T', itemTag("c:ingots/tough"))
                .unlockedBy("has_material", has(itemTag("c:ingots/copper")))
                .save(output.withConditions(new NotCondition(new TagEmptyCondition("c:ingots/copper"))), ResourceLocation.parse("qmd:crafting/accelerator_magnet_copper"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_magnet_hbcco"), 2)
                .pattern("CCC")
                .pattern("STS")
                .pattern("CCC")
                .define('C', itemTag("c:wires/hbcco"))
                .define('S', itemTag("c:ingots/stainless_steel"))
                .define('T', itemTag("c:ingots/tough"))
                .unlockedBy("has_material", has(itemTag("c:wires/hbcco")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_magnet_hbcco"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_magnet_magnesium_diboride"), 2)
                .pattern("CCC")
                .pattern("STS")
                .pattern("CCC")
                .define('C', itemTag("c:ingots/magnesium_diboride"))
                .define('S', itemTag("c:ingots/stainless_steel"))
                .define('T', itemTag("c:ingots/tough"))
                .unlockedBy("has_material", has(itemTag("c:ingots/magnesium_diboride")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_magnet_magnesium_diboride"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_magnet_niobium_tin"), 2)
                .pattern("CCC")
                .pattern("STS")
                .pattern("CCC")
                .define('C', itemTag("c:ingots/niobium_tin"))
                .define('S', itemTag("c:ingots/stainless_steel"))
                .define('T', itemTag("c:ingots/tough"))
                .unlockedBy("has_material", has(itemTag("c:ingots/niobium_tin")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_magnet_niobium_tin"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_magnet_niobium_titanium"), 2)
                .pattern("CCC")
                .pattern("STS")
                .pattern("CCC")
                .define('C', itemTag("c:ingots/niobium_titanium"))
                .define('S', itemTag("c:ingots/stainless_steel"))
                .define('T', itemTag("c:ingots/tough"))
                .unlockedBy("has_material", has(itemTag("c:ingots/niobium_titanium")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_magnet_niobium_titanium"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_magnet_ssfaf"), 2)
                .pattern("CCC")
                .pattern("STS")
                .pattern("CCC")
                .define('C', itemTag("c:wires/ssfaf"))
                .define('S', itemTag("c:ingots/stainless_steel"))
                .define('T', itemTag("c:ingots/tough"))
                .unlockedBy("has_material", has(itemTag("c:wires/ssfaf")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_magnet_ssfaf"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_magnet_ybco"), 2)
                .pattern("CCC")
                .pattern("STS")
                .pattern("CCC")
                .define('C', itemTag("c:wires/ybco"))
                .define('S', itemTag("c:ingots/stainless_steel"))
                .define('T', itemTag("c:ingots/tough"))
                .unlockedBy("has_material", has(itemTag("c:wires/ybco")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_magnet_ybco"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_port"), 4)
                .pattern("SHS")
                .pattern("VFV")
                .pattern("SHS")
                .define('S', itemTag("c:ingots/stainless_steel"))
                .define('H', item("minecraft:hopper"))
                .define('V', item("nuclearcraftneohaul:servomechanism"))
                .define('F', item("nuclearcraftneohaul:steel_chassis"))
                .unlockedBy("has_material", has(itemTag("c:ingots/stainless_steel")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_port"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_redstone_port"), 1)
                .pattern("STS")
                .pattern("TFT")
                .pattern("STS")
                .define('S', itemTag("c:dusts/redstone"))
                .define('T', itemTag("c:ingots/steel"))
                .define('F', item("qmd:accelerator_casing"))
                .unlockedBy("has_material", has(itemTag("c:dusts/redstone")))
                .save(output.withConditions(new NotCondition(new TagEmptyCondition("c:dusts/redstone"))), ResourceLocation.parse("qmd:crafting/accelerator_redstone_port"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_rf_cavity_aluminium"), 4)
                .pattern("CCC")
                .pattern("S S")
                .pattern("CCC")
                .define('C', itemTag("c:ingots/aluminum"))
                .define('S', itemTag("c:ingots/stainless_steel"))
                .unlockedBy("has_material", has(itemTag("c:ingots/aluminum")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_rf_cavity_aluminium"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_rf_cavity_bscco"), 4)
                .pattern("CCC")
                .pattern("S S")
                .pattern("CCC")
                .define('C', itemTag("c:wires/bscco"))
                .define('S', itemTag("c:ingots/stainless_steel"))
                .unlockedBy("has_material", has(itemTag("c:wires/bscco")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_rf_cavity_bscco"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_rf_cavity_copper"), 4)
                .pattern("CCC")
                .pattern("S S")
                .pattern("CCC")
                .define('C', itemTag("c:ingots/copper"))
                .define('S', itemTag("c:ingots/stainless_steel"))
                .unlockedBy("has_material", has(itemTag("c:ingots/copper")))
                .save(output.withConditions(new NotCondition(new TagEmptyCondition("c:ingots/copper"))), ResourceLocation.parse("qmd:crafting/accelerator_rf_cavity_copper"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_rf_cavity_hbcco"), 4)
                .pattern("CCC")
                .pattern("S S")
                .pattern("CCC")
                .define('C', itemTag("c:wires/hbcco"))
                .define('S', itemTag("c:ingots/stainless_steel"))
                .unlockedBy("has_material", has(itemTag("c:wires/hbcco")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_rf_cavity_hbcco"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_rf_cavity_magnesium_diboride"), 4)
                .pattern("CCC")
                .pattern("S S")
                .pattern("CCC")
                .define('C', itemTag("c:ingots/magnesium_diboride"))
                .define('S', itemTag("c:ingots/stainless_steel"))
                .unlockedBy("has_material", has(itemTag("c:ingots/magnesium_diboride")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_rf_cavity_magnesium_diboride"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_rf_cavity_niobium_tin"), 4)
                .pattern("CCC")
                .pattern("S S")
                .pattern("CCC")
                .define('C', itemTag("c:ingots/niobium_tin"))
                .define('S', itemTag("c:ingots/stainless_steel"))
                .unlockedBy("has_material", has(itemTag("c:ingots/niobium_tin")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_rf_cavity_niobium_tin"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_rf_cavity_niobium_titanium"), 4)
                .pattern("CCC")
                .pattern("S S")
                .pattern("CCC")
                .define('C', itemTag("c:ingots/niobium_titanium"))
                .define('S', itemTag("c:ingots/stainless_steel"))
                .unlockedBy("has_material", has(itemTag("c:ingots/niobium_titanium")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_rf_cavity_niobium_titanium"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_rf_cavity_ssfaf"), 4)
                .pattern("CCC")
                .pattern("S S")
                .pattern("CCC")
                .define('C', itemTag("c:wires/ssfaf"))
                .define('S', itemTag("c:ingots/stainless_steel"))
                .unlockedBy("has_material", has(itemTag("c:wires/ssfaf")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_rf_cavity_ssfaf"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_rf_cavity_ybco"), 4)
                .pattern("CCC")
                .pattern("S S")
                .pattern("CCC")
                .define('C', itemTag("c:wires/ybco"))
                .define('S', itemTag("c:ingots/stainless_steel"))
                .unlockedBy("has_material", has(itemTag("c:wires/ybco")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_rf_cavity_ybco"));
    }

    private static void part1(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_synchrotron_port"), 4)
                .pattern("STS")
                .pattern("AFA")
                .pattern("STS")
                .define('S', itemTag("c:ingots/stainless_steel"))
                .define('T', itemTag("c:ingots/tough"))
                .define('A', itemTag("c:ingots/aluminum"))
                .define('F', item("nuclearcraftneohaul:steel_chassis"))
                .unlockedBy("has_material", has(itemTag("c:ingots/stainless_steel")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_synchrotron_port"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:accelerator_yoke"), 4)
                .pattern("IBI")
                .pattern("IBI")
                .pattern("IBI")
                .define('I', itemTag("c:ingots/iron"))
                .define('B', item("nuclearcraftneohaul:bioplastic"))
                .unlockedBy("has_material", has(itemTag("c:ingots/iron")))
                .save(output.withConditions(new NotCondition(new TagEmptyCondition("c:ingots/iron"))), ResourceLocation.parse("qmd:crafting/accelerator_yoke"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:beam_diverter_controller"), 1)
                .pattern("PEP")
                .pattern("BFB")
                .pattern("PEP")
                .define('P', item("nuclearcraftneohaul:advanced_plating"))
                .define('E', itemTag("c:ingots/tough"))
                .define('B', item("qmd:semiconductor_advanced_processor"))
                .define('F', item("qmd:accelerator_casing"))
                .unlockedBy("has_material", has(item("nuclearcraftneohaul:advanced_plating")))
                .save(output, ResourceLocation.parse("qmd:crafting/beam_diverter_controller"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:beam_dump_controller"), 1)
                .pattern("PTP")
                .pattern("BFB")
                .pattern("PTP")
                .define('P', item("nuclearcraftneohaul:elite_plating"))
                .define('T', itemTag("c:ingots/tough"))
                .define('B', item("qmd:semiconductor_basic_processor"))
                .define('F', item("minecraft:copper_block"))
                .unlockedBy("has_material", has(item("nuclearcraftneohaul:elite_plating")))
                .save(output, ResourceLocation.parse("qmd:crafting/beam_dump_controller"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:beam_splitter_controller"), 1)
                .pattern("PEP")
                .pattern("BFB")
                .pattern("PEP")
                .define('P', item("nuclearcraftneohaul:advanced_plating"))
                .define('E', itemTag("c:ingots/super_alloy"))
                .define('B', item("qmd:semiconductor_advanced_processor"))
                .define('F', item("qmd:accelerator_casing"))
                .unlockedBy("has_material", has(item("nuclearcraftneohaul:advanced_plating")))
                .save(output, ResourceLocation.parse("qmd:crafting/beam_splitter_controller"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:beamline"), 6)
                .pattern("SSS")
                .pattern("   ")
                .pattern("SSS")
                .define('S', itemTag("c:ingots/stainless_steel"))
                .unlockedBy("has_material", has(itemTag("c:ingots/stainless_steel")))
                .save(output, ResourceLocation.parse("qmd:crafting/beamline"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:cell_empty"), 1)
                .pattern("WEW")
                .pattern("O O")
                .pattern("WRW")
                .define('W', item("qmd:part_wire_bscco"))
                .define('O', item("qmd:ingot_osmiridium"))
                .define('R', item("nuclearcraftneohaul:rtg_californium"))
                .define('E', item("qmd:semiconductor_elite_processor"))
                .unlockedBy("has_material", has(item("qmd:part_wire_bscco")))
                .save(output, ResourceLocation.parse("qmd:crafting/cell_empty"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:collision_chamber_controller"), 1)
                .pattern("PTP")
                .pattern("BFB")
                .pattern("PTP")
                .define('P', item("nuclearcraftneohaul:elite_plating"))
                .define('T', itemTag("c:ingots/tough"))
                .define('B', item("qmd:semiconductor_elite_processor"))
                .define('F', item("qmd:particle_chamber_casing"))
                .unlockedBy("has_material", has(item("nuclearcraftneohaul:elite_plating")))
                .save(output, ResourceLocation.parse("qmd:crafting/collision_chamber_controller"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:decay_chamber_controller"), 1)
                .pattern("PTP")
                .pattern("BFB")
                .pattern("PTP")
                .define('P', item("nuclearcraftneohaul:elite_plating"))
                .define('T', itemTag("c:ingots/tough"))
                .define('B', item("qmd:semiconductor_basic_processor"))
                .define('F', item("nuclearcraftneohaul:decay_hastener"))
                .unlockedBy("has_material", has(item("nuclearcraftneohaul:elite_plating")))
                .save(output, ResourceLocation.parse("qmd:crafting/decay_chamber_controller"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:decelerator_controller"), 1)
                .pattern("PEP")
                .pattern("BFB")
                .pattern("PEP")
                .define('P', item("nuclearcraftneohaul:elite_plating"))
                .define('E', itemTag("c:ingots/extreme"))
                .define('B', item("qmd:semiconductor_elite_processor"))
                .define('F', item("qmd:accelerator_casing"))
                .unlockedBy("has_material", has(item("nuclearcraftneohaul:elite_plating")))
                .save(output, ResourceLocation.parse("qmd:crafting/decelerator_controller"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:discharge_lamp_empty"), 4)
                .pattern("GGG")
                .pattern("GFG")
                .pattern("GRG")
                .define('G', itemTag("c:glass_panes"))
                .define('F', item("qmd:source_tungsten_filament"))
                .define('R', item("minecraft:redstone"))
                .unlockedBy("has_material", has(itemTag("c:glass_panes")))
                .save(output, ResourceLocation.parse("qmd:crafting/discharge_lamp_empty"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:em_calorimeter"), 1)
                .pattern("SSS")
                .pattern("SCS")
                .pattern("SSS")
                .define('S', item("qmd:part_scintillator_pwo"))
                .define('C', item("qmd:part_detector_casing"))
                .unlockedBy("has_material", has(item("qmd:part_scintillator_pwo")))
                .save(output, ResourceLocation.parse("qmd:crafting/em_calorimeter"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:hadron_calorimeter"), 1)
                .pattern("SSS")
                .pattern("SCS")
                .pattern("SSS")
                .define('S', item("qmd:part_scintillator_plastic"))
                .define('C', item("qmd:part_detector_casing"))
                .unlockedBy("has_material", has(item("qmd:part_scintillator_plastic")))
                .save(output, ResourceLocation.parse("qmd:crafting/hadron_calorimeter"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:irradiator"), 1)
                .pattern("TPT")
                .pattern("PFP")
                .pattern("TPT")
                .define('T', item("qmd:ingot_tungsten"))
                .define('P', item("nuclearcraftneohaul:du_plating"))
                .define('F', item("nuclearcraftneohaul:machine_chassis"))
                .unlockedBy("has_material", has(item("qmd:ingot_tungsten")))
                .save(output, ResourceLocation.parse("qmd:crafting/irradiator"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:linear_accelerator_controller"), 1)
                .pattern("PEP")
                .pattern("BFB")
                .pattern("PEP")
                .define('P', item("nuclearcraftneohaul:elite_plating"))
                .define('E', itemTag("c:ingots/extreme"))
                .define('B', item("qmd:semiconductor_basic_processor"))
                .define('F', item("qmd:accelerator_casing"))
                .unlockedBy("has_material", has(item("nuclearcraftneohaul:elite_plating")))
                .save(output, ResourceLocation.parse("qmd:crafting/linear_accelerator_controller"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:mass_spectrometer_controller"), 1)
                .pattern("PWP")
                .pattern("AFA")
                .pattern("PWP")
                .define('P', item("nuclearcraftneohaul:elite_plating"))
                .define('W', itemTag("c:wires/bscco"))
                .define('A', item("qmd:semiconductor_elite_processor"))
                .define('F', item("qmd:accelerator_casing"))
                .unlockedBy("has_material", has(item("nuclearcraftneohaul:elite_plating")))
                .save(output, ResourceLocation.parse("qmd:crafting/mass_spectrometer_controller"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:neutral_containment_controller"), 1)
                .pattern("PEP")
                .pattern("BFB")
                .pattern("PEP")
                .define('P', item("nuclearcraftneohaul:elite_plating"))
                .define('E', itemTag("c:ingots/extreme"))
                .define('B', item("qmd:semiconductor_elite_processor"))
                .define('F', item("qmd:vacuum_chamber_casing"))
                .unlockedBy("has_material", has(item("nuclearcraftneohaul:elite_plating")))
                .save(output, ResourceLocation.parse("qmd:crafting/neutral_containment_controller"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:neutron_shield_hafnium"), 4)
                .pattern("BHB")
                .pattern("EFE")
                .pattern("BHB")
                .define('B', item("nuclearcraftneohaul:basic_plating"))
                .define('H', item("qmd:ingot_hafnium"))
                .define('E', item("qmd:dust_erbium"))
                .define('F', item("nuclearcraftneohaul:steel_chassis"))
                .unlockedBy("has_material", has(item("nuclearcraftneohaul:basic_plating")))
                .save(output, ResourceLocation.parse("qmd:crafting/neutron_shield_hafnium"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:nucleosynthesis_chamber_controller"), 1)
                .pattern("PEP")
                .pattern("BFB")
                .pattern("PEP")
                .define('P', item("nuclearcraftneohaul:elite_plating"))
                .define('E', itemTag("c:ingots/super_alloy"))
                .define('B', item("qmd:semiconductor_elite_processor"))
                .define('F', item("qmd:vacuum_chamber_casing"))
                .unlockedBy("has_material", has(item("nuclearcraftneohaul:elite_plating")))
                .save(output, ResourceLocation.parse("qmd:crafting/nucleosynthesis_chamber_controller"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:part_empty_cooler"), 8)
                .pattern("STS")
                .pattern("SHS")
                .pattern("STS")
                .define('S', itemTag("c:ingots/stainless_steel"))
                .define('T', itemTag("c:ingots/tough"))
                .define('H', itemTag("c:ingots/thermoconducting"))
                .unlockedBy("has_material", has(itemTag("c:ingots/stainless_steel")))
                .save(output, ResourceLocation.parse("qmd:crafting/part_empty_cooler"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:particle_chamber"), 1)
                .pattern("NSN")
                .pattern("NCN")
                .pattern("NSN")
                .define('N', itemTag("c:ingots/niobium_tin"))
                .define('S', itemTag("c:ingots/stainless_steel"))
                .define('C', item("nuclearcraftneohaul:machine_chassis"))
                .unlockedBy("has_material", has(itemTag("c:ingots/niobium_tin")))
                .save(output, ResourceLocation.parse("qmd:crafting/particle_chamber"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:particle_chamber_beam"), 3)
                .pattern("STS")
                .pattern("BBB")
                .pattern("STS")
                .define('S', itemTag("c:ingots/stainless_steel"))
                .define('T', itemTag("c:ingots/tungsten"))
                .define('B', item("qmd:beamline"))
                .unlockedBy("has_material", has(itemTag("c:ingots/stainless_steel")))
                .save(output, ResourceLocation.parse("qmd:crafting/particle_chamber_beam"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:particle_chamber_beam_port"), 4)
                .pattern("STS")
                .pattern("BFB")
                .pattern("STS")
                .define('S', itemTag("c:ingots/stainless_steel"))
                .define('T', itemTag("c:ingots/tungsten"))
                .define('B', item("qmd:beamline"))
                .define('F', item("nuclearcraftneohaul:empty_frame"))
                .unlockedBy("has_material", has(itemTag("c:ingots/stainless_steel")))
                .save(output, ResourceLocation.parse("qmd:crafting/particle_chamber_beam_port"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:particle_chamber_casing"), 16)
                .pattern("STS")
                .pattern("TFT")
                .pattern("STS")
                .define('S', itemTag("c:ingots/stainless_steel"))
                .define('T', itemTag("c:ingots/tungsten"))
                .define('F', item("nuclearcraftneohaul:empty_frame"))
                .unlockedBy("has_material", has(itemTag("c:ingots/stainless_steel")))
                .save(output, ResourceLocation.parse("qmd:crafting/particle_chamber_casing"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:particle_chamber_energy_port"), 4)
                .pattern("SIS")
                .pattern("TFT")
                .pattern("SIS")
                .define('S', itemTag("c:ingots/stainless_steel"))
                .define('I', itemTag("c:ingots/niobium_tin"))
                .define('T', itemTag("c:ingots/tungsten"))
                .define('F', item("nuclearcraftneohaul:empty_frame"))
                .unlockedBy("has_material", has(itemTag("c:ingots/stainless_steel")))
                .save(output, ResourceLocation.parse("qmd:crafting/particle_chamber_energy_port"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:particle_chamber_fluid_port"), 4)
                .pattern("SIS")
                .pattern("TFT")
                .pattern("STS")
                .define('S', itemTag("c:ingots/stainless_steel"))
                .define('T', itemTag("c:ingots/tungsten"))
                .define('I', item("nuclearcraftneohaul:servomechanism"))
                .define('F', item("nuclearcraftneohaul:empty_frame"))
                .unlockedBy("has_material", has(itemTag("c:ingots/stainless_steel")))
                .save(output, ResourceLocation.parse("qmd:crafting/particle_chamber_fluid_port"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:particle_chamber_port"), 4)
                .pattern("THT")
                .pattern("VFV")
                .pattern("THT")
                .define('T', itemTag("c:ingots/tungsten"))
                .define('H', item("minecraft:hopper"))
                .define('V', item("nuclearcraftneohaul:servomechanism"))
                .define('F', item("nuclearcraftneohaul:empty_frame"))
                .unlockedBy("has_material", has(itemTag("c:ingots/tungsten")))
                .save(output, ResourceLocation.parse("qmd:crafting/particle_chamber_port"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:reflector_tungsten_carbide"), 2)
                .pattern("TTT")
                .pattern("TFT")
                .pattern("TTT")
                .define('T', item("qmd:ingot_tungsten_carbide"))
                .define('F', item("nuclearcraftneohaul:steel_chassis"))
                .unlockedBy("has_material", has(item("qmd:ingot_tungsten_carbide")))
                .save(output, ResourceLocation.parse("qmd:crafting/reflector_tungsten_carbide"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:ring_accelerator_controller"), 1)
                .pattern("PEP")
                .pattern("BFB")
                .pattern("PEP")
                .define('P', item("nuclearcraftneohaul:elite_plating"))
                .define('E', itemTag("c:ingots/extreme"))
                .define('B', item("qmd:semiconductor_advanced_processor"))
                .define('F', item("qmd:accelerator_casing"))
                .unlockedBy("has_material", has(item("nuclearcraftneohaul:elite_plating")))
                .save(output, ResourceLocation.parse("qmd:crafting/ring_accelerator_controller"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:rtg_strontium"), 1)
                .pattern("AGA")
                .pattern("GSG")
                .pattern("AGA")
                .define('A', item("nuclearcraftneohaul:advanced_plating"))
                .define('G', item("nuclearcraftneohaul:graphite_ingot"))
                .define('S', item("qmd:strontium_90_block"))
                .unlockedBy("has_material", has(item("nuclearcraftneohaul:advanced_plating")))
                .save(output, ResourceLocation.parse("qmd:crafting/rtg_strontium"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:silicon_tracker"), 1)
                .pattern("BAB")
                .pattern("ACA")
                .pattern("BAB")
                .define('B', item("qmd:semiconductor_basic_processor"))
                .define('A', item("qmd:semiconductor_advanced_processor"))
                .define('C', item("qmd:part_detector_casing"))
                .unlockedBy("has_material", has(item("qmd:semiconductor_basic_processor")))
                .save(output, ResourceLocation.parse("qmd:crafting/silicon_tracker"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:source_calcium_48"), 1)
                .pattern("BSB")
                .pattern("SSS")
                .pattern("BSB")
                .define('S', item("qmd:isotope_calcium_48"))
                .define('B', item("nuclearcraftneohaul:bioplastic"))
                .unlockedBy("has_material", has(item("qmd:isotope_calcium_48")))
                .save(output, ResourceLocation.parse("qmd:crafting/source_calcium_48"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:source_cobalt_60"), 1)
                .pattern("BBB")
                .pattern("BSB")
                .pattern("BBB")
                .define('S', item("qmd:isotope_cobalt_60"))
                .define('B', item("nuclearcraftneohaul:bioplastic"))
                .unlockedBy("has_material", has(item("qmd:isotope_cobalt_60")))
                .save(output, ResourceLocation.parse("qmd:crafting/source_cobalt_60"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:source_iridium_192"), 1)
                .pattern("BBB")
                .pattern("BSB")
                .pattern("BBB")
                .define('S', item("qmd:isotope_iridium_192"))
                .define('B', item("nuclearcraftneohaul:bioplastic"))
                .unlockedBy("has_material", has(item("qmd:isotope_iridium_192")))
                .save(output, ResourceLocation.parse("qmd:crafting/source_iridium_192"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:source_sodium_22"), 1)
                .pattern("BSB")
                .pattern("SSS")
                .pattern("BSB")
                .define('S', item("qmd:isotope_sodium_22"))
                .define('B', item("nuclearcraftneohaul:bioplastic"))
                .unlockedBy("has_material", has(item("qmd:isotope_sodium_22")))
                .save(output, ResourceLocation.parse("qmd:crafting/source_sodium_22"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:strontium_90_block"), 1)
                .pattern("SSS")
                .pattern("SSS")
                .pattern("SSS")
                .define('S', item("nuclearcraftneohaul:strontium_90_dust"))
                .unlockedBy("has_material", has(item("nuclearcraftneohaul:strontium_90_dust")))
                .save(output, ResourceLocation.parse("qmd:crafting/strontium_90_block"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:target_chamber_controller"), 1)
                .pattern("PTP")
                .pattern("BFB")
                .pattern("PTP")
                .define('P', item("nuclearcraftneohaul:elite_plating"))
                .define('T', itemTag("c:ingots/tough"))
                .define('B', item("qmd:semiconductor_basic_processor"))
                .define('F', item("qmd:particle_chamber_casing"))
                .unlockedBy("has_material", has(item("nuclearcraftneohaul:elite_plating")))
                .save(output, ResourceLocation.parse("qmd:crafting/target_chamber_controller"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:turbine_blade_super_alloy"), 4)
                .pattern("SHS")
                .pattern("SHS")
                .pattern("SHS")
                .define('S', itemTag("c:ingots/super_alloy"))
                .define('H', itemTag("c:ingots/hsla_steel"))
                .unlockedBy("has_material", has(itemTag("c:ingots/super_alloy")))
                .save(output, ResourceLocation.parse("qmd:crafting/turbine_blade_super_alloy"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:vacuum_chamber_beam"), 2)
                .pattern("SBS")
                .pattern("B B")
                .pattern("SBS")
                .define('S', itemTag("c:ingots/super_alloy"))
                .define('B', item("qmd:part_wire_bscco"))
                .unlockedBy("has_material", has(itemTag("c:ingots/super_alloy")))
                .save(output, ResourceLocation.parse("qmd:crafting/vacuum_chamber_beam"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:vacuum_chamber_beam_port"), 4)
                .pattern("OTO")
                .pattern("BFB")
                .pattern("OTO")
                .define('O', itemTag("c:ingots/osmiridium"))
                .define('T', itemTag("c:ingots/tough"))
                .define('B', item("qmd:beamline"))
                .define('F', item("nuclearcraftneohaul:empty_frame"))
                .unlockedBy("has_material", has(itemTag("c:ingots/osmiridium")))
                .save(output, ResourceLocation.parse("qmd:crafting/vacuum_chamber_beam_port"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:vacuum_chamber_casing"), 8)
                .pattern("OTO")
                .pattern("SFS")
                .pattern("OTO")
                .define('O', itemTag("c:ingots/osmiridium"))
                .define('T', itemTag("c:ingots/tough"))
                .define('S', itemTag("c:ingots/stainless_steel"))
                .define('F', item("nuclearcraftneohaul:empty_frame"))
                .unlockedBy("has_material", has(itemTag("c:ingots/osmiridium")))
                .save(output, ResourceLocation.parse("qmd:crafting/vacuum_chamber_casing"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:vacuum_chamber_coil"), 2)
                .pattern("CCC")
                .pattern("OOO")
                .pattern("CCC")
                .define('O', itemTag("c:ingots/osmiridium"))
                .define('C', item("qmd:part_wire_bscco"))
                .unlockedBy("has_material", has(itemTag("c:ingots/osmiridium")))
                .save(output, ResourceLocation.parse("qmd:crafting/vacuum_chamber_coil"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:vacuum_chamber_energy_port"), 4)
                .pattern("OIO")
                .pattern("TFT")
                .pattern("OIO")
                .define('O', itemTag("c:ingots/osmiridium"))
                .define('T', itemTag("c:ingots/tough"))
                .define('F', item("nuclearcraftneohaul:empty_frame"))
                .define('I', item("qmd:part_wire_bscco"))
                .unlockedBy("has_material", has(itemTag("c:ingots/osmiridium")))
                .save(output, ResourceLocation.parse("qmd:crafting/vacuum_chamber_energy_port"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:vacuum_chamber_fluid_port"), 2)
                .pattern("SIS")
                .pattern("TFT")
                .pattern("STS")
                .define('S', itemTag("c:ingots/osmiridium"))
                .define('T', itemTag("c:ingots/tough"))
                .define('F', item("qmd:vacuum_chamber_casing"))
                .define('I', item("nuclearcraftneohaul:servomechanism"))
                .unlockedBy("has_material", has(itemTag("c:ingots/osmiridium")))
                .save(output, ResourceLocation.parse("qmd:crafting/vacuum_chamber_fluid_port"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:vacuum_chamber_heater_diamond"), 1)
                .pattern("IOI")
                .pattern("OCO")
                .pattern("IOI")
                .define('I', itemTag("c:gems/diamond"))
                .define('O', itemTag("c:ingots/osmiridium"))
                .define('C', item("qmd:part_empty_cooler"))
                .unlockedBy("has_material", has(itemTag("c:gems/diamond")))
                .save(output, ResourceLocation.parse("qmd:crafting/vacuum_chamber_heater_diamond"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:vacuum_chamber_heater_glowstone"), 1)
                .pattern("IOI")
                .pattern("OCO")
                .pattern("IOI")
                .define('I', itemTag("c:dusts/glowstone"))
                .define('O', itemTag("c:ingots/osmiridium"))
                .define('C', item("qmd:part_empty_cooler"))
                .unlockedBy("has_material", has(itemTag("c:dusts/glowstone")))
                .save(output, ResourceLocation.parse("qmd:crafting/vacuum_chamber_heater_glowstone"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:vacuum_chamber_heater_gold"), 1)
                .pattern("IOI")
                .pattern("OCO")
                .pattern("IOI")
                .define('I', itemTag("c:ingots/gold"))
                .define('O', itemTag("c:ingots/osmiridium"))
                .define('C', item("qmd:part_empty_cooler"))
                .unlockedBy("has_material", has(itemTag("c:ingots/gold")))
                .save(output, ResourceLocation.parse("qmd:crafting/vacuum_chamber_heater_gold"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:vacuum_chamber_heater_iron"), 1)
                .pattern("IOI")
                .pattern("OCO")
                .pattern("IOI")
                .define('I', itemTag("c:ingots/iron"))
                .define('O', itemTag("c:ingots/osmiridium"))
                .define('C', item("qmd:part_empty_cooler"))
                .unlockedBy("has_material", has(itemTag("c:ingots/iron")))
                .save(output, ResourceLocation.parse("qmd:crafting/vacuum_chamber_heater_iron"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:vacuum_chamber_heater_lapis"), 1)
                .pattern("IOI")
                .pattern("OCO")
                .pattern("IOI")
                .define('I', itemTag("c:gems/lapis"))
                .define('O', itemTag("c:ingots/osmiridium"))
                .define('C', item("qmd:part_empty_cooler"))
                .unlockedBy("has_material", has(itemTag("c:gems/lapis")))
                .save(output, ResourceLocation.parse("qmd:crafting/vacuum_chamber_heater_lapis"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:vacuum_chamber_heater_obsidian"), 1)
                .pattern("IOI")
                .pattern("OCO")
                .pattern("IOI")
                .define('I', item("minecraft:obsidian"))
                .define('O', itemTag("c:ingots/osmiridium"))
                .define('C', item("qmd:part_empty_cooler"))
                .unlockedBy("has_material", has(item("minecraft:obsidian")))
                .save(output, ResourceLocation.parse("qmd:crafting/vacuum_chamber_heater_obsidian"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:vacuum_chamber_heater_quartz"), 1)
                .pattern("IOI")
                .pattern("OCO")
                .pattern("IOI")
                .define('I', itemTag("c:gems/quartz"))
                .define('O', itemTag("c:ingots/osmiridium"))
                .define('C', item("qmd:part_empty_cooler"))
                .unlockedBy("has_material", has(itemTag("c:gems/quartz")))
                .save(output, ResourceLocation.parse("qmd:crafting/vacuum_chamber_heater_quartz"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:vacuum_chamber_heater_redstone"), 1)
                .pattern("IOI")
                .pattern("OCO")
                .pattern("IOI")
                .define('I', itemTag("c:dusts/redstone"))
                .define('O', itemTag("c:ingots/osmiridium"))
                .define('C', item("qmd:part_empty_cooler"))
                .unlockedBy("has_material", has(itemTag("c:dusts/redstone")))
                .save(output, ResourceLocation.parse("qmd:crafting/vacuum_chamber_heater_redstone"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:vacuum_chamber_heater_vent"), 4)
                .pattern("OTO")
                .pattern("TFT")
                .pattern("OIO")
                .define('O', itemTag("c:ingots/osmiridium"))
                .define('T', itemTag("c:ingots/tough"))
                .define('F', item("nuclearcraftneohaul:empty_frame"))
                .define('I', item("nuclearcraftneohaul:servomechanism"))
                .unlockedBy("has_material", has(itemTag("c:ingots/osmiridium")))
                .save(output, ResourceLocation.parse("qmd:crafting/vacuum_chamber_heater_vent"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:vacuum_chamber_laser"), 1)
                .pattern("OEO")
                .pattern("ELL")
                .pattern("OEO")
                .define('O', itemTag("c:ingots/osmiridium"))
                .define('E', item("nuclearcraftneohaul:elite_plating"))
                .define('L', item("qmd:part_laser_assembly"))
                .unlockedBy("has_material", has(itemTag("c:ingots/osmiridium")))
                .save(output, ResourceLocation.parse("qmd:crafting/vacuum_chamber_laser"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:vacuum_chamber_plasma_glass"), 2)
                .pattern("SBS")
                .pattern("BNB")
                .pattern("SBS")
                .define('S', itemTag("c:ingots/super_alloy"))
                .define('B', item("qmd:part_wire_bscco"))
                .define('N', itemTag("c:gems/boron_nitride"))
                .unlockedBy("has_material", has(itemTag("c:ingots/super_alloy")))
                .save(output, ResourceLocation.parse("qmd:crafting/vacuum_chamber_plasma_glass"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:vacuum_chamber_plasma_nozzle"), 2)
                .pattern("SBS")
                .pattern("BNB")
                .pattern("SBS")
                .define('S', itemTag("c:ingots/super_alloy"))
                .define('B', item("qmd:part_wire_bscco"))
                .define('N', item("qmd:part_accelerating_barrel"))
                .unlockedBy("has_material", has(itemTag("c:ingots/super_alloy")))
                .save(output, ResourceLocation.parse("qmd:crafting/vacuum_chamber_plasma_nozzle"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:vacuum_chamber_port"), 4)
                .pattern("OHO")
                .pattern("VFV")
                .pattern("OHO")
                .define('O', itemTag("c:ingots/osmiridium"))
                .define('H', item("minecraft:hopper"))
                .define('V', item("nuclearcraftneohaul:servomechanism"))
                .define('F', item("nuclearcraftneohaul:empty_frame"))
                .unlockedBy("has_material", has(itemTag("c:ingots/osmiridium")))
                .save(output, ResourceLocation.parse("qmd:crafting/vacuum_chamber_port"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:vacuum_chamber_redstone_port"), 1)
                .pattern("STS")
                .pattern("TFT")
                .pattern("STS")
                .define('S', itemTag("c:dusts/redstone"))
                .define('T', itemTag("c:ingots/steel"))
                .define('F', item("qmd:vacuum_chamber_casing"))
                .unlockedBy("has_material", has(itemTag("c:dusts/redstone")))
                .save(output, ResourceLocation.parse("qmd:crafting/vacuum_chamber_redstone_port"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:vacuum_chamber_vent"), 4)
                .pattern("OIO")
                .pattern("TFT")
                .pattern("OTO")
                .define('O', itemTag("c:ingots/osmiridium"))
                .define('T', itemTag("c:ingots/tough"))
                .define('F', item("nuclearcraftneohaul:empty_frame"))
                .define('I', item("nuclearcraftneohaul:servomechanism"))
                .unlockedBy("has_material", has(itemTag("c:ingots/osmiridium")))
                .save(output, ResourceLocation.parse("qmd:crafting/vacuum_chamber_vent"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:liquefier_controller"), 1)
                .pattern("ITI")
                .pattern("TCT")
                .pattern("ITI")
                .define('I', itemTag("c:ingots/steel"))
                .define('T', itemTag("c:ingots/thermoconducting"))
                .define('C', item("nuclearcraftneohaul:supercooler"))
                .unlockedBy("has_material", has(itemTag("c:ingots/steel")))
                .save(output, ResourceLocation.parse("qmd:liquefier_controller"));
    }

    private static void part2(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:liquefier_energy_port"), 4)
                .pattern("ISI")
                .pattern("SCS")
                .pattern("ISI")
                .define('I', itemTag("c:ingots/steel"))
                .define('C', item("nuclearcraftneohaul:steel_chassis"))
                .define('S', itemTag("c:ingots/copper"))
                .unlockedBy("has_material", has(itemTag("c:ingots/steel")))
                .save(output, ResourceLocation.parse("qmd:liquefier_energy_port"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:liquefier_fluid_port"), 4)
                .pattern("ISI")
                .pattern("MCM")
                .pattern("ISI")
                .define('I', itemTag("c:ingots/steel"))
                .define('C', item("nuclearcraftneohaul:steel_chassis"))
                .define('S', itemTag("c:ingots/stainless_steel"))
                .define('M', item("nuclearcraftneohaul:servomechanism"))
                .unlockedBy("has_material", has(itemTag("c:ingots/steel")))
                .save(output, ResourceLocation.parse("qmd:liquefier_fluid_port"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:liquefier_nozzle"), 4)
                .pattern("I I")
                .pattern("SCS")
                .pattern("I I")
                .define('I', itemTag("c:ingots/steel"))
                .define('C', item("nuclearcraftneohaul:steel_chassis"))
                .define('S', itemTag("c:ingots/stainless_steel"))
                .unlockedBy("has_material", has(itemTag("c:ingots/steel")))
                .save(output, ResourceLocation.parse("qmd:liquefier_nozzle"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:liquid_collector"), 1)
                .pattern("ASA")
                .pattern("BMB")
                .pattern("ASA")
                .define('A', item("nuclearcraftneohaul:advanced_plating"))
                .define('S', itemTag("c:ingots/bronze"))
                .define('M', item("nuclearcraftneohaul:electric_motor"))
                .define('B', item("minecraft:bucket"))
                .unlockedBy("has_material", has(item("nuclearcraftneohaul:advanced_plating")))
                .save(output, ResourceLocation.parse("qmd:liquid_collector"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:neodymium_liquefier_compressor"), 4)
                .pattern("ISI")
                .pattern("MCM")
                .pattern("ISI")
                .define('I', itemTag("c:ingots/steel"))
                .define('C', item("nuclearcraftneohaul:steel_chassis"))
                .define('S', item("qmd:part_magnet_nd"))
                .define('M', item("nuclearcraftneohaul:electric_motor"))
                .unlockedBy("has_material", has(itemTag("c:ingots/steel")))
                .save(output, ResourceLocation.parse("qmd:neodymium_liquefier_compressor"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:ore_leacher"), 1)
                .pattern("PRP")
                .pattern("CFC")
                .pattern("PHP")
                .define('P', item("nuclearcraftneohaul:elite_plating"))
                .define('R', item("nuclearcraftneohaul:rock_crusher"))
                .define('C', item("nuclearcraftneohaul:chemical_reactor"))
                .define('F', item("nuclearcraftneohaul:machine_chassis"))
                .define('H', itemTag("c:ingots/hard_carbon"))
                .unlockedBy("has_material", has(item("nuclearcraftneohaul:elite_plating")))
                .save(output, ResourceLocation.parse("qmd:ore_leacher"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("qmd:samarium_cobalt_liquefier_compressor"), 4)
                .pattern("ISI")
                .pattern("MCM")
                .pattern("ISI")
                .define('I', itemTag("c:ingots/steel"))
                .define('C', item("nuclearcraftneohaul:steel_chassis"))
                .define('S', item("qmd:part_magnet_smc"))
                .define('M', item("nuclearcraftneohaul:electric_motor"))
                .unlockedBy("has_material", has(itemTag("c:ingots/steel")))
                .save(output, ResourceLocation.parse("qmd:samarium_cobalt_liquefier_compressor"));
    }

    private static Item item(String id) {
        return BuiltInRegistries.ITEM.get(ResourceLocation.parse(id));
    }

    private static TagKey<Item> itemTag(String id) {
        return TagKey.create(Registries.ITEM, ResourceLocation.parse(id));
    }
}
