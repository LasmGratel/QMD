package lach_01298.qmd.compat.jei.category;

import lach_01298.qmd.compat.jei.JeiPanel;
import lach_01298.qmd.compat.jei.LiquefierJeiPlugin;
import lach_01298.qmd.recipe.LiquefierRecipe;
import lach_01298.qmd.registry.QMDBlocks;
import mezz.jei.api.constants.VanillaTypes;
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
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Displays every gas/coolant property used by the runtime, including pressure and inversion limits. */
public final class LiquefierCategory implements IRecipeCategory<RecipeHolder<LiquefierRecipe>> {

    private final LiquefierRecipe.Kind kind;
    private final IDrawable icon;
    private final IGuiHelper gui;
    private final JeiPanel panel;

    public LiquefierCategory(IGuiHelper helper, LiquefierRecipe.Kind kind) {
        this.kind = kind;
        gui = helper;
        panel = JeiPanel.of(kind == LiquefierRecipe.Kind.GAS ? "jei/liquefier" : "jei/liquefier_coolant", 0, 0, 90, 26);
        icon = helper
            .createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(QMDBlocks.LIQUEFIER_NOZZLE.get()));
    }

    @Override
    public RecipeType<RecipeHolder<LiquefierRecipe>> getRecipeType() {
        return kind == LiquefierRecipe.Kind.GAS ? LiquefierJeiPlugin.GAS : LiquefierJeiPlugin.COOLANT;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(
            "qmd.gui.jei.category." + (kind == LiquefierRecipe.Kind.GAS ? "liquefier" : "liquefier_coolant"));
    }

    @Override
    public int getWidth() {
        return 176;
    }

    @Override
    public int getHeight() {
        return kind == LiquefierRecipe.Kind.GAS ? 126 : 96;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public boolean isHandled(RecipeHolder<LiquefierRecipe> holder) {
        return holder.value()
            .kind() == kind;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<LiquefierRecipe> holder, IFocusGroup focuses) {
        var recipe = holder.value();
        var inputs = Arrays.stream(
            recipe.ingredient()
                .getStacks())
            .map(stack -> stack.copyWithAmount(recipe.amount()))
            .toList();
        builder.addInputSlot(52, 5)
            .setFluidRenderer(recipe.amount(), false, 16, 16)
            .addIngredients(NeoForgeTypes.FLUID_STACK, inputs);
        builder.addOutputSlot(108, 1)
            .setFluidRenderer(
                recipe.output()
                    .getAmount(),
                false,
                24,
                24)
            .addIngredient(NeoForgeTypes.FLUID_STACK, recipe.output());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<LiquefierRecipe> holder,
        IFocusGroup focuses) {
        var r = holder.value();
        List<FormattedText> lines = new ArrayList<>();
        panel.progress(gui, builder, 90, 0, 36, 15, 70, 6, Math.max(5, (int) Math.min(200, r.heat() / 100)));
        lines.add(Component.translatable("gui.qmd.jei.liquefier.heat", r.heat()));
        if (kind == LiquefierRecipe.Kind.GAS) {
            lines.add(Component.translatable("gui.qmd.jei.liquefier.energy", r.energy()));
            lines.add(Component.translatable("gui.qmd.jei.liquefier.inversion", r.inputTemperature()));
            lines.add(Component.translatable("gui.qmd.jei.liquefier.gas_temperature", r.outputTemperature()));
            lines.add(Component.translatable("gui.qmd.jei.liquefier.coefficient", r.pressureCoefficient()));
            lines.add(Component.translatable("gui.qmd.jei.liquefier.efficiency"));
        } else {
            lines.add(
                Component.translatable(
                    "gui.qmd.jei.liquefier.coolant_temperature",
                    r.inputTemperature(),
                    r.outputTemperature()));
            lines.add(Component.translatable("gui.qmd.jei.liquefier.coolant_limit"));
        }
        builder.addScrollBoxWidget(176, getHeight() - 32, 0, 32)
            .setContents(lines);
    }

    @Override
    public void draw(RecipeHolder<LiquefierRecipe> holder, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX,
        double mouseY) {
        panel.draw(graphics, 43, 0);
    }
}
