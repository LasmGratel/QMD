package lach_01298.qmd.datagen.recipe;

import com.nred.nuclearcraft.recipe.ProcessorRecipeBuilder;
import com.nred.nuclearcraft.recipe.processor.AssemblerRecipe;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import vazkii.patchouli.api.PatchouliAPI;
import net.minecraft.data.recipes.RecipeOutput;

import static lach_01298.qmd.datagen.recipe.QmdRecipeBuilders.*;
import static lach_01298.qmd.datagen.recipe.QmdRecipeIngredients.*;

/** Typed PlayerContent recipe declarations. */
public final class QmdPlayerContentRecipes {
    public static void add(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, itemStack("qmd:beam_meter", 1))
                .pattern("FFS")
                .pattern("PPC")
                .pattern("FFB")
                .define('F', tagItem("c:ingots/ferroboron"))
                .define('S', item("qmd:part_scintillator_plastic"))
                .define('P', tagItem("c:processors/basic"))
                .define('C', tagItem("c:ingots/copper"))
                .define('B', item("nuclearcraftneohaul:bioplastic"))
                .unlockedBy("has_qmd_recipe", RecipeUnlockedTrigger.unlocked(id("qmd:crafting/beam_meter")))
                .save(RecipeOutputs.withoutAdvancements(output.withConditions(tagPresent("c:ingots/ferroboron"), tagPresent("c:processors/basic"), tagPresent("c:ingots/copper"))), id("qmd:crafting/beam_meter"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, itemStack("qmd:green_luminous_paint", 16))
                .requires(tagItem("c:dusts/zinc_sulfide"))
                .requires(tagItem("c:dusts/radium"))
                .requires(tagItem("c:dusts/copper"))
                .unlockedBy("has_qmd_recipe", RecipeUnlockedTrigger.unlocked(id("qmd:crafting/green_luminous_paint_radium")))
                .save(RecipeOutputs.withoutAdvancements(output.withConditions(tagPresent("c:dusts/zinc_sulfide"), tagPresent("c:dusts/radium"), tagPresent("c:dusts/copper"))), id("qmd:crafting/green_luminous_paint_radium"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, itemStack("qmd:green_luminous_paint", 16))
                .requires(tagItem("c:dusts/zinc_sulfide"))
                .requires(tagItem("c:dusts/promethium_147"))
                .requires(tagItem("c:dusts/copper"))
                .unlockedBy("has_qmd_recipe", RecipeUnlockedTrigger.unlocked(id("qmd:crafting/green_luminous_paint_promethium")))
                .save(RecipeOutputs.withoutAdvancements(output.withConditions(tagPresent("c:dusts/zinc_sulfide"), tagPresent("c:dusts/promethium_147"), tagPresent("c:dusts/copper"))), id("qmd:crafting/green_luminous_paint_promethium"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, itemStack("qmd:blue_luminous_paint", 16))
                .requires(tagItem("c:dusts/zinc_sulfide"))
                .requires(tagItem("c:dusts/radium"))
                .requires(tagItem("c:dusts/silver"))
                .unlockedBy("has_qmd_recipe", RecipeUnlockedTrigger.unlocked(id("qmd:crafting/blue_luminous_paint_radium")))
                .save(RecipeOutputs.withoutAdvancements(output.withConditions(tagPresent("c:dusts/zinc_sulfide"), tagPresent("c:dusts/radium"), tagPresent("c:dusts/silver"))), id("qmd:crafting/blue_luminous_paint_radium"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, itemStack("qmd:blue_luminous_paint", 16))
                .requires(tagItem("c:dusts/zinc_sulfide"))
                .requires(tagItem("c:dusts/promethium_147"))
                .requires(tagItem("c:dusts/silver"))
                .unlockedBy("has_qmd_recipe", RecipeUnlockedTrigger.unlocked(id("qmd:crafting/blue_luminous_paint_promethium")))
                .save(RecipeOutputs.withoutAdvancements(output.withConditions(tagPresent("c:dusts/zinc_sulfide"), tagPresent("c:dusts/promethium_147"), tagPresent("c:dusts/silver"))), id("qmd:crafting/blue_luminous_paint_promethium"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, itemStack("qmd:orange_luminous_paint", 16))
                .requires(tagItem("c:dusts/zinc_sulfide"))
                .requires(tagItem("c:dusts/radium"))
                .requires(tagItem("c:dusts/magnesium"))
                .unlockedBy("has_qmd_recipe", RecipeUnlockedTrigger.unlocked(id("qmd:crafting/orange_luminous_paint_radium")))
                .save(RecipeOutputs.withoutAdvancements(output.withConditions(tagPresent("c:dusts/zinc_sulfide"), tagPresent("c:dusts/radium"), tagPresent("c:dusts/magnesium"))), id("qmd:crafting/orange_luminous_paint_radium"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, itemStack("qmd:orange_luminous_paint", 16))
                .requires(tagItem("c:dusts/zinc_sulfide"))
                .requires(tagItem("c:dusts/promethium_147"))
                .requires(tagItem("c:dusts/magnesium"))
                .unlockedBy("has_qmd_recipe", RecipeUnlockedTrigger.unlocked(id("qmd:crafting/orange_luminous_paint_promethium")))
                .save(RecipeOutputs.withoutAdvancements(output.withConditions(tagPresent("c:dusts/zinc_sulfide"), tagPresent("c:dusts/promethium_147"), tagPresent("c:dusts/magnesium"))), id("qmd:crafting/orange_luminous_paint_promethium"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, PatchouliAPI.get().getBookStack(id("qmd:qmd")))
                .requires(item("minecraft:book"))
                .unlockedBy("has_qmd_recipe", RecipeUnlockedTrigger.unlocked(id("qmd:crafting/guide")))
                .save(RecipeOutputs.withoutAdvancements(output), id("qmd:crafting/guide"));

        new ProcessorRecipeBuilder(AssemblerRecipe.class, 1.0D, 1.0D, 0.0D)
                .addItemInput(sizedItem(tagItem("c:dusts/potassium"), 1))
                .addItemInput(sizedItem(tagItem("c:dusts/iodine"), 1))
                .addItemInput(sizedItem(item("minecraft:sugar"), 4))
                .addItemInput(sizedItem(item("nuclearcraftneohaul:bioplastic"), 1))
                .addItemResult(sizedItem(item("qmd:potassium_iodine_tablet"), 4))
                .save(RecipeOutputs.named(output.withConditions(tagPresent("c:dusts/potassium"), tagPresent("c:dusts/iodine")), "qmd:assembler/potassium_iodine_tablet"), id("qmd:assembler/potassium_iodine_tablet"));
    }

    private QmdPlayerContentRecipes() {}
}
