package lach_01298.qmd.compat.jei.category;

import com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient;
import com.nred.nuclearcraft.recipe.SizedChanceItemIngredient;
import lach_01298.qmd.config.QMDConfig;
import lach_01298.qmd.recipe.OreLeacherRecipe;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.widgets.IDrawableWidget;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OreLeacherCategoryTest {

    @BeforeAll
    static void bootstrapVanillaRegistries() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void layoutKeepsAllAcidsAndQuantitiesAndSkipsPaddedOutputs() {
        Fixture fixture = new Fixture();
        fixture.category
            .setRecipe(fixture.layout(), recipe(List.of(SizedChanceItemIngredient.of(Items.IRON_INGOT, 3))), null);
        assertEquals(5, fixture.slots.size());
        assertEquals(
            List.of("item_input_0", "fluid_input_0", "item_output_0", "fluid_input_1", "fluid_input_2"),
            fixture.slots.stream()
                .map(slot -> slot.name)
                .toList());
        Slot ore = fixture.named("item_input_0");
        assertEquals(RecipeIngredientRole.INPUT, ore.role);
        assertEquals(List.of(6, 4), List.of(ore.x, ore.y));
        assertEquals(2, ((ItemStack) ore.ingredients.getFirst()).getCount());
        for (int index = 0; index < 3; index++) {
            Slot fluid = fixture.named("fluid_input_" + index);
            assertEquals(RecipeIngredientRole.INPUT, fluid.role);
            assertEquals(List.of(6 + 20 * index, 35), List.of(fluid.x, fluid.y));
            assertSame(NeoForgeTypes.FLUID_STACK, fluid.type);
            assertEquals(16 * (index + 1), fluid.capacity);
            assertEquals(fluid.capacity, ((FluidStack) fluid.ingredients.getFirst()).getAmount());
        }
        Slot product = fixture.named("item_output_0");
        assertEquals(RecipeIngredientRole.OUTPUT, product.role);
        assertEquals(List.of(82, 35), List.of(product.x, product.y));
        assertEquals(3, ((ItemStack) product.ingredients.getFirst()).getCount());
        assertFalse(product.chanceTooltip);
    }

    @Test
    void allThreeProductsKeepTheirMaximumCountsAndChanceTooltips() {
        Fixture fixture = new Fixture();
        fixture.category.setRecipe(
            fixture.layout(),
            recipe(
                List.of(
                    SizedChanceItemIngredient.of(Items.IRON_INGOT, 3),
                    new SizedChanceItemIngredient(Ingredient.of(Items.GOLD_INGOT), 2, 50, 1),
                    new SizedChanceItemIngredient(Ingredient.of(Items.COPPER_INGOT), 8, 25, 2))),
            null);
        assertEquals(7, fixture.slots.size());
        assertEquals(2, ((ItemStack) fixture.named("item_output_1").ingredients.getFirst()).getCount());
        assertEquals(8, ((ItemStack) fixture.named("item_output_2").ingredients.getFirst()).getCount());
        assertTrue(fixture.named("item_output_1").chanceTooltip);
        assertTrue(fixture.named("item_output_2").chanceTooltip);
        assertEquals(102, fixture.named("item_output_1").x);
        assertEquals(122, fixture.named("item_output_2").x);
    }

    @Test
    void progressWidgetUsesBothMachineStripsAndStartsFinalStripAfterFirstStage() {
        Fixture fixture = new Fixture();
        fixture.category.createRecipeExtras(
            fixture.extras(),
            recipe(List.of(SizedChanceItemIngredient.of(Items.IRON_INGOT, 3))),
            null);
        assertEquals(List.of(10, 22), fixture.widgetPosition);
        assertEquals(70, fixture.widget.getWidth());
        assertEquals(25, fixture.widget.getHeight());
        assertEquals(Math.max(5, QMDConfig.ORE_LEACHER_TIME.get() / 4), fixture.cycleTicks);

        fixture.timerValue = 32;
        fixture.widget.draw(null, 10, 22);
        assertEquals(List.of(new Draw(10, 22, 0, 0, 0, 16)), fixture.strips.getFirst().draws);
        assertTrue(fixture.strips.get(1).draws.isEmpty());

        fixture.timerValue = 40;
        fixture.widget.draw(null, 10, 22);
        assertEquals(new Draw(10, 22, 0, 0, 0, 8), fixture.strips.getFirst().draws.getLast());
        assertEquals(List.of(new Draw(64, 39, 0, 0, 0, 8)), fixture.strips.get(1).draws);

        fixture.timerValue = 48;
        fixture.widget.draw(null, 10, 22);
        assertEquals(new Draw(10, 22, 0, 0, 0, 0), fixture.strips.getFirst().draws.getLast());
        assertEquals(new Draw(64, 39, 0, 0, 0, 0), fixture.strips.get(1).draws.getLast());
    }

    private static RecipeHolder<OreLeacherRecipe> recipe(List<SizedChanceItemIngredient> products) {
        return new RecipeHolder<>(
            ResourceLocation.parse("qmd:test_jei_layout"),
            new OreLeacherRecipe(
                List.of(SizedChanceItemIngredient.of(Items.IRON_ORE, 2)),
                products,
                List.of(
                    SizedChanceFluidIngredient.of(Fluids.WATER, 16),
                    SizedChanceFluidIngredient.of(Fluids.LAVA, 32),
                    SizedChanceFluidIngredient.of(Fluids.WATER, 48)),
                List.of(),
                1D,
                1D,
                0D));
    }

    private static final class Fixture {

        final List<Slot> slots = new ArrayList<>();
        final List<Strip> strips = new ArrayList<>();
        final OreLeacherCategory category;
        IDrawable widget;
        List<Integer> widgetPosition;
        int timerValue;
        int cycleTicks;

        Fixture() {
            IGuiHelper helper = proxy(IGuiHelper.class, (instance, method, args) -> switch (method.getName()) {
                case "createDrawable" -> {
                    Strip strip = new Strip((int) args[3], (int) args[4]);
                    strips.add(strip);
                    yield strip;
                }
                case "createTickTimer" -> {
                    cycleTicks = (int) args[0];
                    assertEquals(48, args[1]);
                    assertEquals(false, args[2]);
                    yield new ITickTimer() {

                        @Override
                        public int getValue() {
                            return timerValue;
                        }

                        @Override
                        public int getMaxValue() {
                            return 48;
                        }
                    };
                }
                default -> throw new AssertionError("Unexpected GUI helper call: " + method.getName());
            });
            category = new OreLeacherCategory(helper, new Strip(16, 16));
        }

        IRecipeLayoutBuilder layout() {
            return proxy(IRecipeLayoutBuilder.class, (instance, method, args) -> {
                if (!method.getName()
                    .equals("addSlot") || args.length != 3) {
                    throw new AssertionError("Unexpected layout call: " + method.getName());
                }
                Slot slot = new Slot((RecipeIngredientRole) args[0], (int) args[1], (int) args[2]);
                slots.add(slot);
                return slot.builder();
            });
        }

        IRecipeExtrasBuilder extras() {
            return proxy(IRecipeExtrasBuilder.class, (instance, method, args) -> {
                if (!method.getName()
                    .equals("addDrawableWidget")) throw new AssertionError(method.getName());
                widget = (IDrawable) args[0];
                return proxy(IDrawableWidget.class, (widgetInstance, widgetMethod, widgetArgs) -> {
                    if (!widgetMethod.getName()
                        .equals("setPosition")) throw new AssertionError(widgetMethod.getName());
                    widgetPosition = List.of((int) widgetArgs[0], (int) widgetArgs[1]);
                    return widgetInstance;
                });
            });
        }

        Slot named(String name) {
            return slots.stream()
                .filter(slot -> name.equals(slot.name))
                .findFirst()
                .orElseThrow();
        }
    }

    private static final class Slot {

        final RecipeIngredientRole role;
        final int x;
        final int y;
        String name;
        Object type;
        List<?> ingredients;
        int capacity;
        boolean chanceTooltip;

        Slot(RecipeIngredientRole role, int x, int y) {
            this.role = role;
            this.x = x;
            this.y = y;
        }

        IRecipeSlotBuilder builder() {
            return proxy(IRecipeSlotBuilder.class, (instance, method, args) -> {
                switch (method.getName()) {
                    case "setSlotName" -> name = (String) args[0];
                    case "addItemStacks" -> ingredients = List.copyOf((List<?>) args[0]);
                    case "addIngredients" -> {
                        type = args[0];
                        ingredients = List.copyOf((List<?>) args[1]);
                    }
                    case "setFluidRenderer" -> {
                        capacity = ((Number) args[0]).intValue();
                        assertTrue(capacity > 0, "JEI rejects zero-capacity fluid renderers");
                        assertEquals(List.of(false, 16, 16), List.of(args[1], args[2], args[3]));
                    }
                    case "addRichTooltipCallback" -> chanceTooltip = true;
                    default -> throw new AssertionError("Unexpected slot builder call: " + method.getName());
                }
                return instance;
            });
        }
    }

    private record Draw(int x, int y, int top, int bottom, int left, int right) {}

    private static final class Strip implements IDrawableStatic {

        final int width;
        final int height;
        final List<Draw> draws = new ArrayList<>();

        Strip(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public int getHeight() {
            return height;
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y) {
            throw new AssertionError("Expected masked strip");
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int top, int bottom, int left, int right) {
            draws.add(new Draw(x, y, top, bottom, left, right));
        }
    }

    private static <T> T proxy(Class<T> type, InvocationHandler handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] { type }, handler));
    }
}
