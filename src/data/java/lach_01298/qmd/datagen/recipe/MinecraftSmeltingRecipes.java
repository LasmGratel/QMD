package lach_01298.qmd.datagen.recipe;

import lach_01298.qmd.config.MaterialRecipeConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/** Furnace conversions use the native builder and the configured shared-material policy. */
public final class MinecraftSmeltingRecipes extends RecipeProvider {
    private static final String[] MATERIALS = {
            "barium", "calcium", "chromium", "cobalt", "hafnium", "iridium", "neodymium", "nickel",
            "niobium", "osmium", "platinum", "potassium", "sodium", "strontium", "titanium", "tungsten",
            "yttrium", "zinc"
    };

    public MinecraftSmeltingRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipes) {
        RecipeOutput output = RecipeOutputs.withoutAdvancements(recipes);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(item("qmd:copernicium_291_ni")), RecipeCategory.MISC,
                        item("qmd:copernicium_291"), 0.0F, 200)
                .unlockedBy("has_material", has(item("qmd:copernicium_291_ni")))
                .save(output, ResourceLocation.parse("qmd:smelting/copernicium_291_from_ni"));
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(item("qmd:copernicium_291_ox")), RecipeCategory.MISC,
                        item("qmd:copernicium_291"), 0.0F, 200)
                .unlockedBy("has_material", has(item("qmd:copernicium_291_ox")))
                .save(output, ResourceLocation.parse("qmd:smelting/copernicium_291_from_ox"));
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(item("qmd:mix_291_ni")), RecipeCategory.MISC,
                        item("qmd:mix_291"), 0.0F, 200)
                .unlockedBy("has_material", has(item("qmd:mix_291_ni")))
                .save(output, ResourceLocation.parse("qmd:smelting/mix_291_from_ni"));
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(item("qmd:mix_291_ox")), RecipeCategory.MISC,
                        item("qmd:mix_291"), 0.0F, 200)
                .unlockedBy("has_material", has(item("qmd:mix_291_ox")))
                .save(output, ResourceLocation.parse("qmd:smelting/mix_291_from_ox"));
        addMaterialConversions(output, MaterialRecipeConfig.configuredPolicy());
    }

    private static void addMaterialConversions(RecipeOutput output, MaterialRecipeConfig.Policy policy) {
        for (String material : MATERIALS) {
            ResourceLocation result = policy.preferredIngot(material, taggedIngots(material));
            TagKey<Item> dustTag = TagKey.create(Registries.ITEM,
                    ResourceLocation.fromNamespaceAndPath("c", "dusts/" + material));
            Item dust = item("qmd:dust_" + material);
            Ingredient ingredient = policy.sharedMaterialInputs() ? Ingredient.of(dustTag) : Ingredient.of(dust);
            SimpleCookingRecipeBuilder.smelting(ingredient, RecipeCategory.MISC,
                            BuiltInRegistries.ITEM.get(result), 0.0F, 200)
                    .unlockedBy("has_material", policy.sharedMaterialInputs() ? has(dustTag) : has(dust))
                    .save(output, ResourceLocation.fromNamespaceAndPath("qmd", "smelting/ingot_" + material + "_from_dust"));
        }
    }

    private static Set<ResourceLocation> taggedIngots(String material) {
        Set<ResourceLocation> candidates = new LinkedHashSet<>();
        candidates.add(ResourceLocation.fromNamespaceAndPath("qmd", "ingot_" + material));
        TagKey<Item> tag = TagKey.create(Registries.ITEM,
                ResourceLocation.fromNamespaceAndPath("c", "ingots/" + material));
        BuiltInRegistries.ITEM.getTag(tag).ifPresent(entries -> entries.forEach(holder ->
                holder.unwrapKey().ifPresent(key -> candidates.add(key.location()))));
        return candidates;
    }

    private static Item item(String id) {
        return BuiltInRegistries.ITEM.get(ResourceLocation.parse(id));
    }
}
