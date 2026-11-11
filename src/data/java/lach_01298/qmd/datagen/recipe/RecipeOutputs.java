package lach_01298.qmd.datagen.recipe;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.ICondition;

/** Keeps published recipe IDs and recipe-only output when using native builders. */
final class RecipeOutputs {
    static RecipeOutput withoutAdvancements(RecipeOutput output) {
        return named(output, (ResourceLocation) null);
    }

    static RecipeOutput named(RecipeOutput output, String id) {
        return named(output, ResourceLocation.parse(id));
    }

    private static RecipeOutput named(RecipeOutput output, ResourceLocation fixedId) {
        return new RecipeOutput() {
            @Override
            public void accept(ResourceLocation id, Recipe<?> recipe, AdvancementHolder advancement, ICondition... conditions) {
                output.accept(fixedId == null ? id : fixedId, recipe, null, conditions);
            }

            @Override
            public Advancement.Builder advancement() {
                return output.advancement();
            }
        };
    }

    private RecipeOutputs() {}
}
