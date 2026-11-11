package lach_01298.qmd.compat.jei.category.accelerator;

import lach_01298.qmd.compat.jei.JeiPanel;
import lach_01298.qmd.compat.jei.ingredient.ParticleType;
import lach_01298.qmd.recipe.accelerator.AbstractAcceleratorRecipe;
import lach_01298.qmd.registry.QMDBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractAcceleratorCategory<R extends AbstractAcceleratorRecipe>
    implements IRecipeCategory<RecipeHolder<R>> {

    private final RecipeType<RecipeHolder<R>> recipeType;
    private final String id;
    private final IDrawable icon;
    protected final IGuiHelper gui;
    protected final JeiPanel panel;

    protected AbstractAcceleratorCategory(IGuiHelper gui, RecipeType<RecipeHolder<R>> recipeType, String id,
        String block, JeiPanel panel) {
        this.gui = gui;
        this.recipeType = recipeType;
        this.id = id;
        this.panel = panel;
        icon = gui.createDrawableItemLike(
            QMDBlocks.ACCELERATOR_BLOCKS.get(block)
                .get());
    }

    protected int itemInputX() {
        return 2;
    }

    protected int itemInputY() {
        return 2;
    }

    protected abstract int fluidInputX();

    protected abstract int fluidInputY();

    protected int fluidOutputX(int index) {
        return 37 + index * 19;
    }

    protected int fluidOutputY() {
        return 20;
    }

    protected int fluidOutputSize() {
        return 16;
    }

    protected abstract List<net.minecraft.network.chat.FormattedText> details(R recipe);

    protected void animations(IRecipeExtrasBuilder builder, R recipe) {}

    @Override
    public RecipeType<RecipeHolder<R>> getRecipeType() {
        return recipeType;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("qmd.gui.jei.category." + id);
    }

    @Override
    public int getWidth() {
        return 176;
    }

    @Override
    public int getHeight() {
        return panel.height() + 52;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public boolean isHandled(RecipeHolder<R> holder) {
        return !holder.value()
            .isIncomplete();
    }

    protected final int x() {
        return (getWidth() - panel.width()) / 2;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<R> holder, IFocusGroup focuses) {
        var recipe = holder.value();
        if (!recipe.item()
            .isEmpty())
            builder.addInputSlot(x() + itemInputX(), itemInputY())
                .addItemStacks(
                    Arrays.stream(
                        recipe.item()
                            .getItems())
                        .map(stack -> stack.copyWithCount(recipe.itemCount()))
                        .toList());
        if (!recipe.fluid()
            .isEmpty())
            builder.addInputSlot(x() + fluidInputX(), fluidInputY())
                .setFluidRenderer(
                    recipe.fluid()
                        .getAmount(),
                    false,
                    16,
                    16)
                .addIngredient(NeoForgeTypes.FLUID_STACK, recipe.fluid());
        if (recipe.particle()
            .toStack() != null)
            builder.addOutputSlot(x() + 55, 12)
                .addIngredient(
                    ParticleType.PARTICLE,
                    recipe.particle()
                        .toStack());
        for (int i = 0; i < recipe.itemProducts()
            .size(); i++) {
            var output = recipe.itemProducts()
                .get(i);
            if (output.isEmpty()) continue;
            builder.addOutputSlot(x() + 38 + i * 19, 2)
                .addItemStacks(
                    Arrays.stream(output.getItemsRaw())
                        .map(ItemStack::copy)
                        .toList())
                .addRichTooltipCallback((slot, tip) -> {
                    if (output.chancePercent() < 100)
                        tip.add(Component.translatable("jei.qmd.chance_per_item", output.chancePercent()));
                    if (output.minStackSize() > 0) tip.add(Component.literal("≥ " + output.minStackSize()));
                });
        }
        for (int i = 0; i < recipe.fluidProducts()
            .size(); i++) {
            var output = recipe.fluidProducts()
                .get(i);
            if (output.isEmpty()) continue;
            int size = fluidOutputSize();
            builder.addOutputSlot(x() + fluidOutputX(i), fluidOutputY())
                .setFluidRenderer(Math.max(1, output.amount()), false, size, size)
                .addIngredients(
                    NeoForgeTypes.FLUID_STACK,
                    Arrays.stream(output.getFluidsRaw())
                        .map(stack -> stack.copy())
                        .toList())
                .addRichTooltipCallback((slot, tip) -> {
                    if (output.chancePercent() < 100) tip.add(
                        Component.literal(
                            output.chancePercent() + "% / "
                                + output.increment()
                                + " mB; ≥ "
                                + output.minStackSize()
                                + " mB"));
                });
        }
    }

    @Override
    public void draw(RecipeHolder<R> holder, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX,
        double mouseY) {
        panel.draw(graphics, x(), 0);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<R> holder, IFocusGroup focuses) {
        animations(builder, holder.value());
        builder.addScrollBoxWidget(getWidth(), 48, 0, panel.height() + 4)
            .setContents(details(holder.value()));
    }
}
