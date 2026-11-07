package lach_01298.qmd.recipe;

import com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient;
import com.nred.nuclearcraft.recipe.SizedChanceItemIngredient;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.crafting.DifferenceIngredient;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class OreLeacherRecipeTest {
    @BeforeAll
    static void bootstrapVanillaRegistries() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void missingDustUsesIngotAndDifferencePreservesFirstAvailableProduct() {
        Ingredient missingDustThenIngot = Ingredient.fromValues(Stream.of(
                new Ingredient.TagValue(TagKey.create(Registries.ITEM, ResourceLocation.parse("qmd:test_missing_dust"))),
                new Ingredient.ItemValue(new ItemStack(Items.IRON_INGOT))));
        assertTrue(missingDustThenIngot.getItems()[0].is(Items.BARRIER));

        Ingredient fallback = DifferenceIngredient.of(missingDustThenIngot, Ingredient.of(Items.BARRIER));
        assertEquals(1, fallback.getItems().length);
        assertTrue(new SizedChanceItemIngredient(fallback, 3).getStackRaw().is(Items.IRON_INGOT));

        Ingredient preferredThenIngot = Ingredient.of(Items.RAW_IRON, Items.IRON_INGOT);
        Ingredient available = DifferenceIngredient.of(preferredThenIngot, Ingredient.of(Items.BARRIER));
        assertTrue(new SizedChanceItemIngredient(available, 3).getStackRaw().is(Items.RAW_IRON));
    }

    @Test
    void productsArePaddedWithoutAliasingCallerLists() {
        List<SizedChanceItemIngredient> inputs = new ArrayList<>(inputs());
        List<SizedChanceItemIngredient> products = new ArrayList<>(products());
        List<SizedChanceFluidIngredient> fluids = new ArrayList<>(fluids());
        OreLeacherRecipe recipe = new OreLeacherRecipe(inputs, products, fluids, List.of(), 1, 1, 0);
        inputs.clear();
        products.clear();
        fluids.clear();
        assertEquals(1, recipe.getItemIngredients().size());
        assertEquals(3, recipe.getFluidIngredients().size());
        assertEquals(3, recipe.getItemProducts().size());
        assertEquals(3, recipe.getItemProducts().getFirst().count());
        assertTrue(recipe.getItemProducts().get(1).isEmpty());
        assertTrue(recipe.getItemProducts().get(2).isEmpty());
        assertThrows(UnsupportedOperationException.class, recipe.getItemProducts()::clear);
    }

    @Test
    void invalidMachineDimensionsAndEmptyPrimaryProductAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> recipe(List.of(), products(), fluids(), List.of(), 1, 1, 0));
        assertThrows(IllegalArgumentException.class,
                () -> recipe(List.of(inputs().getFirst(), inputs().getFirst()), products(), fluids(), List.of(), 1, 1, 0));
        assertThrows(IllegalArgumentException.class,
                () -> recipe(inputs(), products(), fluids().subList(0, 2), List.of(), 1, 1, 0));
        assertThrows(IllegalArgumentException.class,
                () -> recipe(inputs(), products(), fluids(), List.of(fluids().getFirst()), 1, 1, 0));
        assertThrows(IllegalArgumentException.class,
                () -> recipe(inputs(), List.of(), fluids(), List.of(), 1, 1, 0));
        assertThrows(IllegalArgumentException.class,
                () -> recipe(inputs(), List.of(SizedChanceItemIngredient.EMPTY), fluids(), List.of(), 1, 1, 0));
        assertThrows(IllegalArgumentException.class,
                () -> recipe(inputs(), List.of(products().getFirst(), products().getFirst(), products().getFirst(), products().getFirst()),
                        fluids(), List.of(), 1, 1, 0));
    }

    @Test
    void invalidCountsAndChanceRangesAreRejected() {
        for (SizedChanceItemIngredient invalid : List.of(
                SizedChanceItemIngredient.of(Items.IRON_INGOT, 0),
                new SizedChanceItemIngredient(Ingredient.of(Items.IRON_INGOT), 1, 101, 0),
                new SizedChanceItemIngredient(Ingredient.of(Items.IRON_INGOT), 1, 50, 2))) {
            assertThrows(IllegalArgumentException.class,
                    () -> recipe(inputs(), List.of(invalid), fluids(), List.of(), 1, 1, 0));
        }
        OreLeacherRecipe salt = recipe(inputs(), List.of(
                new SizedChanceItemIngredient(Ingredient.of(Items.IRON_INGOT), 2, 50, 1)), fluids(), List.of(), 1, 1, 0);
        assertEquals(1.5D, salt.getItemProducts().getFirst().getMeanStackSize());
    }

    @Test
    void inputsCannotRollChanceOrConsumeZeroItemsOrFluids() {
        for (SizedChanceItemIngredient invalid : List.of(
                SizedChanceItemIngredient.of(Items.IRON_ORE, 0),
                new SizedChanceItemIngredient(Ingredient.of(Items.IRON_ORE), 1, 50, 0),
                new SizedChanceItemIngredient(Ingredient.of(Items.IRON_ORE), 1, 100, 1))) {
            assertThrows(IllegalArgumentException.class,
                    () -> recipe(List.of(invalid), products(), fluids(), List.of(), 1, 1, 0));
        }
        for (SizedChanceFluidIngredient invalid : List.of(
                SizedChanceFluidIngredient.of(Fluids.WATER, 0),
                SizedChanceFluidIngredient.of(Fluids.WATER, 16, 50, 0),
                SizedChanceFluidIngredient.of(Fluids.WATER, 16, 100, 1),
                SizedChanceFluidIngredient.of(Fluids.WATER, 16, 100, 0, 2))) {
            assertThrows(IllegalArgumentException.class,
                    () -> recipe(inputs(), products(), List.of(invalid, fluids().get(1), fluids().get(2)), List.of(), 1, 1, 0));
        }
    }

    @Test
    void processModifiersRejectNonfiniteAndOutOfRangeValues() {
        for (double invalid : new double[]{0, -1, Double.NaN, Double.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class,
                    () -> recipe(inputs(), products(), fluids(), List.of(), invalid, 1, 0));
        }
        for (double invalid : new double[]{-1, Double.NaN, Double.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class,
                    () -> recipe(inputs(), products(), fluids(), List.of(), 1, invalid, 0));
            assertThrows(IllegalArgumentException.class,
                    () -> recipe(inputs(), products(), fluids(), List.of(), 1, 1, invalid));
        }
        assertEquals(0D, recipe(inputs(), products(), fluids(), List.of(), 1, 0, 0).getProcessPowerMultiplier());
    }

    private static List<SizedChanceItemIngredient> inputs() {
        return List.of(SizedChanceItemIngredient.of(Items.IRON_ORE, 1));
    }

    private static List<SizedChanceItemIngredient> products() {
        return List.of(SizedChanceItemIngredient.of(Items.IRON_INGOT, 3));
    }

    private static List<SizedChanceFluidIngredient> fluids() {
        return List.of(SizedChanceFluidIngredient.of(Fluids.WATER, 16),
                new SizedChanceFluidIngredient(FluidIngredient.of(Fluids.LAVA), 16),
                SizedChanceFluidIngredient.of(Fluids.WATER, 16));
    }

    private static OreLeacherRecipe recipe(List<SizedChanceItemIngredient> inputs,
                                          List<SizedChanceItemIngredient> products,
                                          List<SizedChanceFluidIngredient> fluids,
                                          List<SizedChanceFluidIngredient> fluidProducts,
                                          double time, double power, double radiation) {
        return new OreLeacherRecipe(inputs, products, fluids, fluidProducts, time, power, radiation);
    }
}
