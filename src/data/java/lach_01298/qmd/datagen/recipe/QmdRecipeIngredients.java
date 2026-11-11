package lach_01298.qmd.datagen.recipe;

import com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient;
import com.nred.nuclearcraft.recipe.SizedChanceItemIngredient;
import lach_01298.qmd.particle.ParticleBeam;
import lach_01298.qmd.recipe.ingredient.ParticleIngredient;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.OrCondition;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;
import net.neoforged.neoforge.common.crafting.DifferenceIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import java.util.Arrays;
import java.util.List;

/** Typed registry references and physical ingredients shared by QMD's recipe declarations. */
final class QmdRecipeIngredients {
    static ResourceLocation id(String value) { return ResourceLocation.parse(value); }

    static Ingredient item(String name) { return Ingredient.of(BuiltInRegistries.ITEM.get(id(name))); }
    static Ingredient tagItem(String name) { return Ingredient.of(TagKey.create(Registries.ITEM, id(name))); }
    static Ingredient anyItem(Ingredient... alternatives) {
        return Ingredient.fromValues(Arrays.stream(alternatives).flatMap(ingredient -> Arrays.stream(ingredient.getValues())));
    }
    static Ingredient excludingItem(Ingredient base, Ingredient exclusion) { return DifferenceIngredient.of(base, exclusion); }
    static FluidIngredient fluid(String name) { return FluidIngredient.single(BuiltInRegistries.FLUID.get(id(name))); }
    static FluidIngredient tagFluid(String name) { return FluidIngredient.tag(TagKey.create(Registries.FLUID, id(name))); }

    static ItemStack itemStack(String name, int count) { return new ItemStack(BuiltInRegistries.ITEM.get(id(name)), count); }
    static FluidStack fluidStack(String name, int amount) { return new FluidStack(BuiltInRegistries.FLUID.get(id(name)), amount); }

    static SizedChanceItemIngredient sizedItem(Ingredient ingredient, int count) {
        return new SizedChanceItemIngredient(ingredient, count);
    }

    static SizedChanceItemIngredient sizedItem(Ingredient ingredient, int count, int chance, int minimum) {
        return new SizedChanceItemIngredient(ingredient, count, chance, minimum);
    }

    static SizedChanceFluidIngredient sizedFluid(FluidIngredient ingredient, int amount) {
        return new SizedChanceFluidIngredient(ingredient, amount);
    }

    static SizedChanceFluidIngredient sizedFluid(FluidIngredient ingredient, int amount, int chance, int increment) {
        return new SizedChanceFluidIngredient(ingredient, amount, chance, 0, increment);
    }

    static ParticleBeam beam(String name, int amount, long energy, double focus) {
        return new ParticleBeam(name, amount, energy, focus);
    }

    static ParticleIngredient beamInput(int amount, long minimumEnergy, long maximumEnergy, double minimumFocus,
                                       ResourceLocation... particles) {
        return new ParticleIngredient(List.of(particles), amount, minimumEnergy, maximumEnergy, minimumFocus);
    }

    static ICondition tagPresent(String name) { return new NotCondition(new TagEmptyCondition(id(name))); }
    static ICondition anyTagPresent(String... names) {
        return new OrCondition(Arrays.stream(names).map(QmdRecipeIngredients::tagPresent).toList());
    }

    private QmdRecipeIngredients() {}
}
