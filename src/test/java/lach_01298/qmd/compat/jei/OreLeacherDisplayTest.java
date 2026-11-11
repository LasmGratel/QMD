package lach_01298.qmd.compat.jei;

import lach_01298.qmd.config.MachineViewerStats;

import com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient;
import com.nred.nuclearcraft.recipe.SizedChanceItemIngredient;
import lach_01298.qmd.config.QMDConfig;
import lach_01298.qmd.recipe.OreLeacherRecipe;
import net.minecraft.SharedConstants;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OreLeacherDisplayTest {

    @BeforeAll
    static void bootstrapVanillaRegistries() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void itemAlternativesUseExactCountsWithoutRollingChanceAndCopyComponents() {
        var ingredient = new SizedChanceItemIngredient(Ingredient.of(Items.IRON_INGOT, Items.GOLD_INGOT), 8, 25, 2) {

            @Override
            public ItemStack[] getItems() {
                throw new AssertionError("JEI must not roll chance outputs");
            }
        };
        ItemStack[] raw = ingredient.getItemsRaw();
        raw[0].set(DataComponents.CUSTOM_NAME, Component.literal("Named product"));
        raw[0].setCount(1);

        var alternatives = OreLeacherDisplay.itemAlternatives(ingredient);
        assertEquals(
            Arrays.stream(raw)
                .map(ItemStack::getItem)
                .toList(),
            alternatives.stream()
                .map(ItemStack::getItem)
                .toList());
        assertTrue(
            alternatives.stream()
                .allMatch(stack -> stack.getCount() == 8));
        assertEquals(
            Component.literal("Named product"),
            alternatives.getFirst()
                .get(DataComponents.CUSTOM_NAME));
        assertNotSame(raw[0], alternatives.getFirst());

        alternatives.getFirst()
            .setCount(3);
        alternatives.getFirst()
            .set(DataComponents.CUSTOM_NAME, Component.literal("Changed display"));
        assertEquals(1, raw[0].getCount());
        assertEquals(Component.literal("Named product"), raw[0].get(DataComponents.CUSTOM_NAME));
        var fresh = OreLeacherDisplay.itemAlternatives(ingredient);
        assertEquals(
            8,
            fresh.getFirst()
                .getCount());
        assertEquals(
            Component.literal("Named product"),
            fresh.getFirst()
                .get(DataComponents.CUSTOM_NAME));
    }

    @Test
    void fluidAlternativesUseExactAmountsWithoutRollingChanceAndCopyComponents() {
        var ingredient = new SizedChanceFluidIngredient(FluidIngredient.of(Fluids.WATER, Fluids.LAVA), 125, 25, 5) {

            @Override
            public FluidStack[] getFluids() {
                throw new AssertionError("JEI must not roll chance fluids");
            }
        };
        FluidStack[] raw = ingredient.getFluidsRaw();
        raw[0].set(DataComponents.CUSTOM_NAME, Component.literal("Named acid"));
        raw[0].setAmount(1);

        var alternatives = OreLeacherDisplay.fluidAlternatives(ingredient);
        assertEquals(
            Arrays.stream(raw)
                .map(FluidStack::getFluid)
                .toList(),
            alternatives.stream()
                .map(FluidStack::getFluid)
                .toList());
        assertTrue(
            alternatives.stream()
                .allMatch(stack -> stack.getAmount() == 125));
        assertEquals(
            Component.literal("Named acid"),
            alternatives.getFirst()
                .get(DataComponents.CUSTOM_NAME));
        assertNotSame(raw[0], alternatives.getFirst());

        alternatives.getFirst()
            .setAmount(3);
        alternatives.getFirst()
            .set(DataComponents.CUSTOM_NAME, Component.literal("Changed display"));
        assertEquals(1, raw[0].getAmount());
        assertEquals(Component.literal("Named acid"), raw[0].get(DataComponents.CUSTOM_NAME));
        var fresh = OreLeacherDisplay.fluidAlternatives(ingredient);
        assertEquals(
            125,
            fresh.getFirst()
                .getAmount());
        assertEquals(
            Component.literal("Named acid"),
            fresh.getFirst()
                .get(DataComponents.CUSTOM_NAME));
    }

    @Test
    void emptyProductsAndFluidsDoNotCreateDisplayIngredients() {
        assertTrue(
            OreLeacherDisplay.itemAlternatives(SizedChanceItemIngredient.EMPTY)
                .isEmpty());
        assertTrue(
            OreLeacherDisplay.fluidAlternatives(SizedChanceFluidIngredient.EMPTY)
                .isEmpty());
    }

    @Test
    void chanceMeanIncludesGuaranteedMinimumAndFractionalAdditionalProducts() {
        assertEquals(
            3.5D,
            OreLeacherDisplay.meanOutput(new SizedChanceItemIngredient(Ingredient.of(Items.IRON_INGOT), 8, 25, 2)));
        assertEquals(
            1.5D,
            OreLeacherDisplay.meanOutput(new SizedChanceItemIngredient(Ingredient.of(Items.IRON_INGOT), 2, 50, 1)));
        assertEquals(
            8D,
            OreLeacherDisplay.meanOutput(new SizedChanceItemIngredient(Ingredient.of(Items.IRON_INGOT), 8, 100, 2)));
        assertEquals(
            2D,
            OreLeacherDisplay.meanOutput(new SizedChanceItemIngredient(Ingredient.of(Items.IRON_INGOT), 2, 25, 2)));
        assertEquals(0D, OreLeacherDisplay.meanOutput(SizedChanceItemIngredient.EMPTY));
    }

    @Test
    void processStatisticsUseReceivedServerSettingsWhenClientConfigDiffers() {
        try {
            MachineViewerStats.receiveClient(new MachineViewerStats(800, 125, 75, 150));
            var stats = OreLeacherDisplay.processStats(recipe(1.5D, 2D, 0D));
            assertEquals(1200D, stats.timeTicks());
            assertEquals(250D, stats.powerPerTick());
            assertEquals(300000D, stats.totalEnergy());
        } finally {
            MachineViewerStats.resetClient();
        }
    }

    @Test
    void processStatisticsApplyCurrentConfigAndRecipeModifiers() {
        var stats = OreLeacherDisplay.processStats(recipe(1.5D, 2.25D, 0.125D));
        double expectedTime = QMDConfig.ORE_LEACHER_TIME.get() * 1.5D;
        double expectedPower = QMDConfig.ORE_LEACHER_POWER.get() * 2.25D;
        assertEquals(expectedTime, stats.timeTicks());
        assertEquals(expectedPower, stats.powerPerTick());
        assertEquals(expectedTime * expectedPower, stats.totalEnergy());
        assertEquals(0.125D, stats.radiation());

        var noPower = OreLeacherDisplay.processStats(recipe(1D, 0D, 0D));
        assertEquals(0D, noPower.powerPerTick());
        assertEquals(0D, noPower.totalEnergy());
        assertEquals(0D, noPower.radiation());
    }

    private static OreLeacherRecipe recipe(double time, double power, double radiation) {
        return new OreLeacherRecipe(
            List.of(SizedChanceItemIngredient.of(Items.IRON_ORE, 1)),
            List.of(SizedChanceItemIngredient.of(Items.IRON_INGOT, 3)),
            List.of(
                SizedChanceFluidIngredient.of(Fluids.WATER, 16),
                SizedChanceFluidIngredient.of(Fluids.LAVA, 16),
                SizedChanceFluidIngredient.of(Fluids.WATER, 16)),
            List.of(),
            time,
            power,
            radiation);
    }
}
