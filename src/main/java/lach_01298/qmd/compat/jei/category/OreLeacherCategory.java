package lach_01298.qmd.compat.jei.category;

import com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient;
import com.nred.nuclearcraft.recipe.SizedChanceItemIngredient;
import lach_01298.qmd.compat.jei.OreLeacherDisplay;
import lach_01298.qmd.compat.jei.QMDJeiRecipeTypes;
import lach_01298.qmd.recipe.OreLeacherRecipe;
import lach_01298.qmd.registry.QMDBlocks;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/** The complete ore-leacher recipe, retaining the machine's two-stage progress layout. */
public final class OreLeacherCategory implements IRecipeCategory<RecipeHolder<OreLeacherRecipe>> {

    private static final ResourceLocation TEXTURE = ResourceLocation.parse("qmd:textures/gui/ore_leacher.png");
    private final IGuiHelper guiHelper;
    private final IDrawable icon;
    private final IDrawableStatic primaryProgress;
    private final IDrawableStatic finalProgress;

    public OreLeacherCategory(IGuiHelper guiHelper) {
        this(guiHelper, guiHelper.createDrawableItemLike(QMDBlocks.ORE_LEACHER.get()));
    }

    OreLeacherCategory(IGuiHelper guiHelper, IDrawable icon) {
        this.guiHelper = guiHelper;
        this.icon = icon;
        primaryProgress = guiHelper.createDrawable(TEXTURE, 176, 0, 48, 11);
        finalProgress = guiHelper.createDrawable(TEXTURE, 176, 12, 16, 8);
    }

    @Override
    public RecipeType<RecipeHolder<OreLeacherRecipe>> getRecipeType() {
        return QMDJeiRecipeTypes.ORE_LEACHER;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("qmd.gui.jei.category.ore_leacher");
    }

    @Override
    public int getWidth() {
        return 142;
    }

    @Override
    public int getHeight() {
        return 56;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<OreLeacherRecipe> holder, IFocusGroup focuses) {
        OreLeacherRecipe recipe = holder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, 6, 4)
            .setSlotName("item_input_0")
            .addItemStacks(
                OreLeacherDisplay.itemAlternatives(
                    recipe.getItemIngredients()
                        .getFirst()));
        for (int index = 0; index < 3; ++index) {
            SizedChanceFluidIngredient fluid = recipe.getFluidIngredients()
                .get(index);
            if (!fluid.isEmpty()) {
                builder.addSlot(RecipeIngredientRole.INPUT, 6 + 20 * index, 35)
                    .setSlotName("fluid_input_" + index)
                    .setFluidRenderer(fluid.amount(), false, 16, 16)
                    .addIngredients(NeoForgeTypes.FLUID_STACK, OreLeacherDisplay.fluidAlternatives(fluid));
            }
            SizedChanceItemIngredient product = recipe.getItemProducts()
                .get(index);
            if (product.isEmpty()) continue;
            var output = builder.addSlot(RecipeIngredientRole.OUTPUT, 82 + 20 * index, 35)
                .setSlotName("item_output_" + index)
                .addItemStacks(OreLeacherDisplay.itemAlternatives(product));
            if (product.chancePercent() < 100 || product.minStackSize() > 0) {
                output.addRichTooltipCallback((slot, tooltip) -> {
                    tooltip.add(
                        Component
                            .translatable(
                                "jei.qmd.chance_output",
                                product.minStackSize(),
                                product.count(),
                                number(OreLeacherDisplay.meanOutput(product)))
                            .withStyle(ChatFormatting.GRAY));
                    tooltip.add(
                        Component.translatable("jei.qmd.chance_per_item", product.chancePercent())
                            .withStyle(ChatFormatting.GRAY));
                });
            }
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<OreLeacherRecipe> holder,
        IFocusGroup focuses) {
        double cycle = OreLeacherDisplay.processStats(holder.value())
            .timeTicks() / 4D;
        int cycleTicks = (int) Math.clamp(cycle, 5D, Integer.MAX_VALUE);
        ITickTimer timer = guiHelper.createTickTimer(cycleTicks, 48, false);
        builder.addDrawableWidget(new IDrawable() {

            @Override
            public int getWidth() {
                return 70;
            }

            @Override
            public int getHeight() {
                return 25;
            }

            @Override
            public void draw(GuiGraphics graphics, int x, int y) {
                int width = Math.clamp(timer.getValue(), 0, 48);
                if (width > 0) primaryProgress.draw(graphics, x, y, 0, 0, 0, 48 - width);
                int finalWidth = Math.max(0, width - 32);
                if (finalWidth > 0) finalProgress.draw(graphics, x + 54, y + 17, 0, 0, 0, 16 - finalWidth);
            }
        })
            .setPosition(10, 22);
    }

    @Override
    public void draw(RecipeHolder<OreLeacherRecipe> holder, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX,
        double mouseY) {
        graphics.blit(TEXTURE, 0, 0, 30, 7, getWidth(), getHeight());
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, RecipeHolder<OreLeacherRecipe> holder, IRecipeSlotsView slots,
        double mouseX, double mouseY) {
        if (!inside(mouseX, mouseY, 10, 22, 48, 11) && !inside(mouseX, mouseY, 64, 39, 16, 8)) return;
        OreLeacherDisplay.ProcessStats stats = OreLeacherDisplay.processStats(holder.value());
        tooltip.add(
            Component.translatable("jei.qmd.base_time", number(stats.timeTicks()), number(stats.timeTicks() / 20D))
                .withStyle(ChatFormatting.GREEN));
        tooltip.add(
            Component.translatable("jei.qmd.base_power", number(stats.powerPerTick()))
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(
            Component.translatable("jei.qmd.base_energy", number(stats.totalEnergy()))
                .withStyle(ChatFormatting.GRAY));
        if (stats.radiation() > 0) {
            tooltip.add(
                Component.translatable("jei.qmd.base_radiation", number(stats.radiation()))
                    .withStyle(ChatFormatting.GOLD));
        }
    }

    private static boolean inside(double x, double y, int left, int top, int width, int height) {
        return x >= left && x < left + width && y >= top && y < top + height;
    }

    private static String number(double value) {
        String pattern = Math.abs(value) >= 1.0E9 || value != 0 && Math.abs(value) < 0.001 ? "0.###E0" : "0.###";
        return new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(Locale.ROOT)).format(value);
    }
}
