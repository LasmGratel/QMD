package lach_01298.qmd.datagen.recipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;

/** Typed lookups for the individual registry identifiers used by Neohaul recipe builders. */
final class NeohaulRecipeIngredients {
    static Item item(String id) {
        return BuiltInRegistries.ITEM.get(ResourceLocation.parse(id));
    }

    static Fluid fluid(String id) {
        return BuiltInRegistries.FLUID.get(ResourceLocation.parse(id));
    }

    static TagKey<Item> itemTag(String id) {
        return TagKey.create(Registries.ITEM, ResourceLocation.parse(id));
    }

    static TagKey<Fluid> fluidTag(String id) {
        return TagKey.create(Registries.FLUID, ResourceLocation.parse(id));
    }

    static ICondition hasTag(String id) {
        return new NotCondition(new TagEmptyCondition(ResourceLocation.parse(id)));
    }

    private NeohaulRecipeIngredients() {}
}
