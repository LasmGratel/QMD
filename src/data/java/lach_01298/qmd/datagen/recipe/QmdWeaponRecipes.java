package lach_01298.qmd.datagen.recipe;

import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;

import static lach_01298.qmd.datagen.recipe.QmdRecipeBuilders.*;
import static lach_01298.qmd.datagen.recipe.QmdRecipeIngredients.*;

/** Typed Weapon recipe declarations. */
final class QmdWeaponRecipes {
    static void add(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, itemStack("qmd:lepton_cannon", 1))
                .pattern("EL ")
                .pattern("OBS")
                .pattern("T  ")
                .define('E', item("qmd:semiconductor_elite_processor"))
                .define('L', item("qmd:part_laser_assembly"))
                .define('O', tagItem("c:ingots/osmiridium"))
                .define('B', item("qmd:part_accelerating_barrel"))
                .define('S', item("nuclearcraftneohaul:copper_solenoid"))
                .define('T', tagItem("c:ingots/super_alloy"))
                .unlockedBy("has_qmd_recipe", RecipeUnlockedTrigger.unlocked(id("qmd:item.qmd.lepton_cannon")))
                .save(RecipeOutputs.withoutAdvancements(output), id("qmd:item.qmd.lepton_cannon"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, itemStack("qmd:gluon_gun", 1))
                .pattern("TT ")
                .pattern("OBB")
                .pattern("EL ")
                .define('E', item("qmd:semiconductor_elite_processor"))
                .define('L', item("qmd:part_laser_assembly"))
                .define('O', tagItem("c:ingots/osmiridium"))
                .define('B', item("qmd:part_accelerating_barrel"))
                .define('T', tagItem("c:ingots/super_alloy"))
                .unlockedBy("has_qmd_recipe", RecipeUnlockedTrigger.unlocked(id("qmd:item.qmd.gluon_gun")))
                .save(RecipeOutputs.withoutAdvancements(output), id("qmd:item.qmd.gluon_gun"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, itemStack("qmd:antimatter_launcher", 1))
                .pattern("TT ")
                .pattern("LBB")
                .pattern("EO ")
                .define('E', item("qmd:semiconductor_elite_processor"))
                .define('L', item("qmd:part_laser_assembly"))
                .define('O', tagItem("c:ingots/osmiridium"))
                .define('B', item("qmd:part_accelerating_barrel"))
                .define('T', tagItem("c:ingots/super_alloy"))
                .unlockedBy("has_qmd_recipe", RecipeUnlockedTrigger.unlocked(id("qmd:item.qmd.antimatter_launcher")))
                .save(RecipeOutputs.withoutAdvancements(output), id("qmd:item.qmd.antimatter_launcher"));
    }

    private QmdWeaponRecipes() {}
}
