package lach_01298.qmd.datagen.recipe;

import lach_01298.qmd.item.equipment.EquipmentEnabledCondition;
import lach_01298.qmd.item.equipment.HevArmorDyeRecipe;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;

import static lach_01298.qmd.datagen.recipe.QmdRecipeBuilders.*;
import static lach_01298.qmd.datagen.recipe.QmdRecipeIngredients.*;

/** Typed Equipment recipe declarations. */
final class QmdEquipmentRecipes {
    static void add(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, itemStack("qmd:sword_tungsten_carbide", 1))
                .pattern("M")
                .pattern("M")
                .pattern("S")
                .define('M', tagItem("c:ingots/tungsten_carbide"))
                .define('S', item("minecraft:stick"))
                .unlockedBy("has_qmd_recipe", RecipeUnlockedTrigger.unlocked(id("qmd:item.qmd.sword_tungsten_carbide")))
                .save(RecipeOutputs.withoutAdvancements(output.withConditions(tagPresent("c:ingots/tungsten_carbide"), new EquipmentEnabledCondition(EquipmentEnabledCondition.Kind.TOOLS))), id("qmd:item.qmd.sword_tungsten_carbide"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, itemStack("qmd:pickaxe_tungsten_carbide", 1))
                .pattern("MMM")
                .pattern(" S ")
                .pattern(" S ")
                .define('M', tagItem("c:ingots/tungsten_carbide"))
                .define('S', item("minecraft:stick"))
                .unlockedBy("has_qmd_recipe", RecipeUnlockedTrigger.unlocked(id("qmd:item.qmd.pickaxe_tungsten_carbide")))
                .save(RecipeOutputs.withoutAdvancements(output.withConditions(tagPresent("c:ingots/tungsten_carbide"), new EquipmentEnabledCondition(EquipmentEnabledCondition.Kind.TOOLS))), id("qmd:item.qmd.pickaxe_tungsten_carbide"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, itemStack("qmd:shovel_tungsten_carbide", 1))
                .pattern("M")
                .pattern("S")
                .pattern("S")
                .define('M', tagItem("c:ingots/tungsten_carbide"))
                .define('S', item("minecraft:stick"))
                .unlockedBy("has_qmd_recipe", RecipeUnlockedTrigger.unlocked(id("qmd:item.qmd.shovel_tungsten_carbide")))
                .save(RecipeOutputs.withoutAdvancements(output.withConditions(tagPresent("c:ingots/tungsten_carbide"), new EquipmentEnabledCondition(EquipmentEnabledCondition.Kind.TOOLS))), id("qmd:item.qmd.shovel_tungsten_carbide"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, itemStack("qmd:axe_tungsten_carbide", 1))
                .pattern("MM")
                .pattern("MS")
                .pattern(" S")
                .define('M', tagItem("c:ingots/tungsten_carbide"))
                .define('S', item("minecraft:stick"))
                .unlockedBy("has_qmd_recipe", RecipeUnlockedTrigger.unlocked(id("qmd:item.qmd.axe_tungsten_carbide")))
                .save(RecipeOutputs.withoutAdvancements(output.withConditions(tagPresent("c:ingots/tungsten_carbide"), new EquipmentEnabledCondition(EquipmentEnabledCondition.Kind.TOOLS))), id("qmd:item.qmd.axe_tungsten_carbide"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, itemStack("qmd:axe_tungsten_carbide", 1))
                .pattern("MM")
                .pattern("SM")
                .pattern("S ")
                .define('M', tagItem("c:ingots/tungsten_carbide"))
                .define('S', item("minecraft:stick"))
                .unlockedBy("has_qmd_recipe", RecipeUnlockedTrigger.unlocked(id("qmd:item.qmd.axe_tungsten_carbide_1")))
                .save(RecipeOutputs.withoutAdvancements(output.withConditions(tagPresent("c:ingots/tungsten_carbide"), new EquipmentEnabledCondition(EquipmentEnabledCondition.Kind.TOOLS))), id("qmd:item.qmd.axe_tungsten_carbide_1"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, itemStack("qmd:hoe_tungsten_carbide", 1))
                .pattern("MM")
                .pattern(" S")
                .pattern(" S")
                .define('M', tagItem("c:ingots/tungsten_carbide"))
                .define('S', item("minecraft:stick"))
                .unlockedBy("has_qmd_recipe", RecipeUnlockedTrigger.unlocked(id("qmd:item.qmd.hoe_tungsten_carbide")))
                .save(RecipeOutputs.withoutAdvancements(output.withConditions(tagPresent("c:ingots/tungsten_carbide"), new EquipmentEnabledCondition(EquipmentEnabledCondition.Kind.TOOLS))), id("qmd:item.qmd.hoe_tungsten_carbide"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, itemStack("qmd:hoe_tungsten_carbide", 1))
                .pattern("MM")
                .pattern("S ")
                .pattern("S ")
                .define('M', tagItem("c:ingots/tungsten_carbide"))
                .define('S', item("minecraft:stick"))
                .unlockedBy("has_qmd_recipe", RecipeUnlockedTrigger.unlocked(id("qmd:item.qmd.hoe_tungsten_carbide_1")))
                .save(RecipeOutputs.withoutAdvancements(output.withConditions(tagPresent("c:ingots/tungsten_carbide"), new EquipmentEnabledCondition(EquipmentEnabledCondition.Kind.TOOLS))), id("qmd:item.qmd.hoe_tungsten_carbide_1"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, itemStack("qmd:drill_basic", 1))
                .pattern(" T ")
                .pattern("TMT")
                .pattern("SBS")
                .define('T', tagItem("c:ingots/tungsten_carbide"))
                .define('M', item("nuclearcraftneohaul:electric_motor"))
                .define('S', tagItem("c:ingots/steel"))
                .define('B', item("nuclearcraftneohaul:advanced_voltaic_pile"))
                .unlockedBy("has_qmd_recipe", RecipeUnlockedTrigger.unlocked(id("qmd:item.qmd.drill_basic")))
                .save(RecipeOutputs.withoutAdvancements(output.withConditions(tagPresent("c:ingots/tungsten_carbide"), tagPresent("c:ingots/steel"), new EquipmentEnabledCondition(EquipmentEnabledCondition.Kind.DRILLS))), id("qmd:item.qmd.drill_basic"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, itemStack("qmd:drill_advanced", 1))
                .pattern("NTN")
                .pattern("TMT")
                .pattern("SBS")
                .define('N', tagItem("c:gems/boron_nitride"))
                .define('T', tagItem("c:ingots/tungsten_carbide"))
                .define('M', item("nuclearcraftneohaul:electric_motor"))
                .define('S', item("qmd:part_magnet_nd"))
                .define('B', item("nuclearcraftneohaul:basic_lithium_ion_battery"))
                .unlockedBy("has_qmd_recipe", RecipeUnlockedTrigger.unlocked(id("qmd:item.qmd.drill_advanced")))
                .save(RecipeOutputs.withoutAdvancements(output.withConditions(tagPresent("c:gems/boron_nitride"), tagPresent("c:ingots/tungsten_carbide"), new EquipmentEnabledCondition(EquipmentEnabledCondition.Kind.DRILLS))), id("qmd:item.qmd.drill_advanced"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, itemStack("qmd:helm_hev", 1))
                .pattern("SBS")
                .pattern("HAH")
                .pattern("SPS")
                .define('S', tagItem("c:ingots/super_alloy"))
                .define('B', item("nuclearcraftneohaul:lithium_ion_cell"))
                .define('H', item("nuclearcraftneohaul:heavy_radiation_shielding"))
                .define('A', item("nuclearcraftneohaul:boron_nitride_helmet"))
                .define('P', item("qmd:semiconductor_elite_processor"))
                .unlockedBy("has_qmd_recipe", RecipeUnlockedTrigger.unlocked(id("qmd:item.qmd.helm_hev")))
                .save(RecipeOutputs.withoutAdvancements(output.withConditions(tagPresent("c:ingots/super_alloy"))), id("qmd:item.qmd.helm_hev"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, itemStack("qmd:chest_hev", 1))
                .pattern("SBS")
                .pattern("HAH")
                .pattern("SPS")
                .define('S', tagItem("c:ingots/super_alloy"))
                .define('B', item("nuclearcraftneohaul:lithium_ion_cell"))
                .define('H', item("nuclearcraftneohaul:heavy_radiation_shielding"))
                .define('A', item("nuclearcraftneohaul:boron_nitride_chestplate"))
                .define('P', item("qmd:semiconductor_elite_processor"))
                .unlockedBy("has_qmd_recipe", RecipeUnlockedTrigger.unlocked(id("qmd:item.qmd.chest_hev")))
                .save(RecipeOutputs.withoutAdvancements(output.withConditions(tagPresent("c:ingots/super_alloy"))), id("qmd:item.qmd.chest_hev"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, itemStack("qmd:legs_hev", 1))
                .pattern("SBS")
                .pattern("HAH")
                .pattern("SPS")
                .define('S', tagItem("c:ingots/super_alloy"))
                .define('B', item("nuclearcraftneohaul:lithium_ion_cell"))
                .define('H', item("nuclearcraftneohaul:heavy_radiation_shielding"))
                .define('A', item("nuclearcraftneohaul:boron_nitride_leggings"))
                .define('P', item("qmd:semiconductor_elite_processor"))
                .unlockedBy("has_qmd_recipe", RecipeUnlockedTrigger.unlocked(id("qmd:item.qmd.legs_hev")))
                .save(RecipeOutputs.withoutAdvancements(output.withConditions(tagPresent("c:ingots/super_alloy"))), id("qmd:item.qmd.legs_hev"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, itemStack("qmd:boots_hev", 1))
                .pattern("SBS")
                .pattern("HAH")
                .pattern("SPS")
                .define('S', tagItem("c:ingots/super_alloy"))
                .define('B', item("nuclearcraftneohaul:lithium_ion_cell"))
                .define('H', item("nuclearcraftneohaul:heavy_radiation_shielding"))
                .define('A', item("nuclearcraftneohaul:boron_nitride_boots"))
                .define('P', item("qmd:semiconductor_elite_processor"))
                .unlockedBy("has_qmd_recipe", RecipeUnlockedTrigger.unlocked(id("qmd:item.qmd.boots_hev")))
                .save(RecipeOutputs.withoutAdvancements(output.withConditions(tagPresent("c:ingots/super_alloy"))), id("qmd:item.qmd.boots_hev"));

        SpecialRecipeBuilder.special(HevArmorDyeRecipe::new).save(output, id("qmd:armour_dye"));
    }

    private QmdEquipmentRecipes() {}
}
