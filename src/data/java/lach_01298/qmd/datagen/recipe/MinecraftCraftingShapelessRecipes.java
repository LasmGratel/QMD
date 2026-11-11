package lach_01298.qmd.datagen.recipe;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;

/** Vanilla shapeless recipes built directly with Minecraft's recipe builder. */
public final class MinecraftCraftingShapelessRecipes extends RecipeProvider {
    public MinecraftCraftingShapelessRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipes) {
        RecipeOutput output = RecipeOutputs.withoutAdvancements(recipes);
        part0(output);
    }

    private static void part0(RecipeOutput output) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item("qmd:accelerator_casing"), 1)
                .requires(item("qmd:accelerator_glass"))
                .unlockedBy("has_material", has(item("qmd:accelerator_glass")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_casing_from_glass"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item("qmd:accelerator_cooler_cryotheum"), 1)
                .requires(item("nuclearcraftneohaul:cryotheum_bucket"))
                .requires(item("qmd:part_empty_cooler"))
                .unlockedBy("has_material", has(item("nuclearcraftneohaul:cryotheum_bucket")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_cooler_cryotheum"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item("qmd:accelerator_cooler_liquid_helium"), 1)
                .requires(item("nuclearcraftneohaul:liquid_helium_bucket"))
                .requires(item("qmd:part_empty_cooler"))
                .unlockedBy("has_material", has(item("nuclearcraftneohaul:liquid_helium_bucket")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_cooler_liquid_helium"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item("qmd:accelerator_cooler_liquid_nitrogen"), 1)
                .requires(item("nuclearcraftneohaul:liquid_nitrogen_bucket"))
                .requires(item("qmd:part_empty_cooler"))
                .unlockedBy("has_material", has(item("nuclearcraftneohaul:liquid_nitrogen_bucket")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_cooler_liquid_nitrogen"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item("qmd:accelerator_cooler_water"), 1)
                .requires(item("minecraft:water_bucket"))
                .requires(item("qmd:part_empty_cooler"))
                .unlockedBy("has_material", has(item("minecraft:water_bucket")))
                .save(output, ResourceLocation.parse("qmd:crafting/accelerator_cooler_water"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item("qmd:accelerator_glass"), 1)
                .requires(item("qmd:accelerator_casing"))
                .requires(itemTag("c:glass_blocks"))
                .unlockedBy("has_material", has(item("qmd:accelerator_casing")))
                .save(output.withConditions(new NotCondition(new TagEmptyCondition("c:glass_blocks"))), ResourceLocation.parse("qmd:crafting/accelerator_glass"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item("qmd:particle_chamber_casing"), 1)
                .requires(item("qmd:particle_chamber_glass"))
                .unlockedBy("has_material", has(item("qmd:particle_chamber_glass")))
                .save(output, ResourceLocation.parse("qmd:crafting/particle_chamber_casing_from_glass"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item("qmd:particle_chamber_glass"), 1)
                .requires(item("qmd:particle_chamber_casing"))
                .requires(itemTag("c:glass_blocks"))
                .unlockedBy("has_material", has(item("qmd:particle_chamber_casing")))
                .save(output, ResourceLocation.parse("qmd:crafting/particle_chamber_glass"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item("nuclearcraftneohaul:strontium_90_dust"), 9)
                .requires(item("qmd:strontium_90_block"))
                .unlockedBy("has_material", has(item("qmd:strontium_90_block")))
                .save(output, ResourceLocation.parse("qmd:crafting/strontium_90_unpacking"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item("qmd:vacuum_chamber_casing"), 1)
                .requires(item("qmd:vacuum_chamber_glass"))
                .unlockedBy("has_material", has(item("qmd:vacuum_chamber_glass")))
                .save(output, ResourceLocation.parse("qmd:crafting/vacuum_chamber_casing_from_glass"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item("qmd:vacuum_chamber_glass"), 1)
                .requires(item("qmd:vacuum_chamber_casing"))
                .requires(itemTag("c:glass_blocks"))
                .unlockedBy("has_material", has(item("qmd:vacuum_chamber_casing")))
                .save(output, ResourceLocation.parse("qmd:crafting/vacuum_chamber_glass"));
    }

    private static Item item(String id) {
        return BuiltInRegistries.ITEM.get(ResourceLocation.parse(id));
    }

    private static TagKey<Item> itemTag(String id) {
        return TagKey.create(Registries.ITEM, ResourceLocation.parse(id));
    }
}
