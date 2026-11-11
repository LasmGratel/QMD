package lach_01298.qmd.compat.jei;

import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OreLeacherTransferHandlerTest {

    @BeforeAll
    static void bootstrapVanillaRegistries() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void transferFiltersAcidTanksOutputsCatalystsAndEmptySlots() {
        var ore = slot(RecipeIngredientRole.INPUT, new ItemStack(Items.IRON_ORE));
        var acid = slot(RecipeIngredientRole.INPUT);
        var empty = slot(RecipeIngredientRole.INPUT);
        var product = slot(RecipeIngredientRole.OUTPUT, new ItemStack(Items.IRON_INGOT));
        var catalyst = slot(RecipeIngredientRole.CATALYST, new ItemStack(Items.BUCKET));
        List<IRecipeSlotView> original = new ArrayList<>(List.of(acid, product, ore, catalyst, empty));
        IRecipeSlotsView view = () -> original;

        assertEquals(List.of(ore), OreLeacherTransferHandler.itemInputSlots(view));
        assertEquals(List.of(acid, product, ore, catalyst, empty), original);
    }

    @Test
    void transferKeepsAllItemInputAlternativesAndTheirSlotOrder() {
        var first = slot(
            RecipeIngredientRole.INPUT,
            new ItemStack(Items.IRON_ORE),
            new ItemStack(Items.DEEPSLATE_IRON_ORE));
        var acid = slot(RecipeIngredientRole.INPUT);
        var second = slot(RecipeIngredientRole.INPUT, new ItemStack(Items.GOLD_ORE));
        IRecipeSlotsView view = () -> List.of(first, acid, second);

        assertEquals(List.of(first, second), OreLeacherTransferHandler.itemInputSlots(view));
        assertEquals(List.of(first, second), OreLeacherTransferHandler.itemInputSlots(view));
        assertEquals(
            2,
            first.getItemStacks()
                .count());
    }

    /** Only the public slot-view contract is needed; no JEI runtime or client screen is constructed. */
    private static IRecipeSlotView slot(RecipeIngredientRole role, ItemStack... items) {
        return (IRecipeSlotView) Proxy.newProxyInstance(
            IRecipeSlotView.class.getClassLoader(),
            new Class<?>[] { IRecipeSlotView.class },
            (proxy, method, args) -> switch (method.getName()) {
            case "getRole" -> role;
            case "getItemStacks" -> List.of(items)
                .stream();
            case "equals" -> proxy == args[0];
            case "hashCode" -> System.identityHashCode(proxy);
            case "toString" -> role + " slot with " + items.length + " item alternatives";
            default -> throw new AssertionError("Unexpected slot access: " + method.getName());
            });
    }
}
