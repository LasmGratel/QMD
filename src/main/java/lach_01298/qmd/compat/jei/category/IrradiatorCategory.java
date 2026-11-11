package lach_01298.qmd.compat.jei.category;

import lach_01298.qmd.compat.jei.IrradiatorJei;
import lach_01298.qmd.compat.jei.JeiPanel;
import lach_01298.qmd.recipe.IrradiatorRecipe;
import lach_01298.qmd.registry.QMDBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Arrays;
import java.util.List;

public final class IrradiatorCategory implements IRecipeCategory<RecipeHolder<IrradiatorRecipe>> {

    private final IrradiatorRecipe.Kind kind;
    private final IDrawable icon;
    private final IGuiHelper gui;
    private final JeiPanel panel;

    public IrradiatorCategory(IGuiHelper gui, IrradiatorRecipe.Kind kind) {
        this.kind = kind;
        this.gui = gui;
        panel = kind == IrradiatorRecipe.Kind.PROCESS ? JeiPanel.of("irradiator", 41, 38, 94, 41)
            : JeiPanel.of("irradiator", 62, 5, 52, 52);
        icon = gui.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(QMDBlocks.IRRADIATOR.get()));
    }

    @Override
    public RecipeType<RecipeHolder<IrradiatorRecipe>> getRecipeType() {
        return kind == IrradiatorRecipe.Kind.PROCESS ? IrradiatorJei.PROCESS : IrradiatorJei.FUEL;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(
            "qmd.gui.jei.category." + (kind == IrradiatorRecipe.Kind.PROCESS ? "irradiator" : "irradiator_fuel"));
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return 176;
    }

    @Override
    public int getHeight() {
        return panel.height() + 49;
    }

    @Override
    public boolean isHandled(RecipeHolder<IrradiatorRecipe> holder) {
        return holder.value()
            .kind() == kind;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<IrradiatorRecipe> holder, IFocusGroup focuses) {
        builder.addInputSlot(kind == IrradiatorRecipe.Kind.PROCESS ? 44 : 80, 16)
            .addItemStacks(
                Arrays.asList(
                    holder.value()
                        .ingredient()
                        .getItems()));
        if (kind == IrradiatorRecipe.Kind.PROCESS) builder.addOutputSlot(116, 16)
            .addItemStack(
                holder.value()
                    .output());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<IrradiatorRecipe> holder,
        IFocusGroup focuses) {
        var stats = lach_01298.qmd.config.MachineViewerStats.client();
        int duration = Math.max(
            1,
            (int) Math.min(
                200,
                stats.irradiatorTime() * holder.value()
                    .multiplier()));
        if (kind == IrradiatorRecipe.Kind.PROCESS) {
            panel.progress(gui, builder, 176, 0, 52, 10, 62, 19, duration);
            builder
                .addDrawableWidget(
                    gui.createAnimatedDrawable(
                        gui.createDrawable(panel.texture(), 176, 10, 40, 19),
                        duration,
                        mezz.jei.api.gui.drawable.IDrawableAnimated.StartDirection.TOP,
                        false))
                .setPosition(68, 0);
        }
        if (kind == IrradiatorRecipe.Kind.PROCESS) builder.addScrollBoxWidget(176, 44, 0, panel.height() + 5)
            .setContents(
                List.of(
                    Component.translatable(
                        "gui.qmd.jei.irradiator.work",
                        Math.max(
                            1D,
                            stats.irradiatorTime() * holder.value()
                                .multiplier())),
                    Component.translatable(
                        "gui.qmd.jei.irradiator.time",
                        holder.value()
                            .multiplier()),
                    Component.translatable(
                        "gui.qmd.jei.irradiator.radiation",
                        holder.value()
                            .radiation())));
        else builder.addScrollBoxWidget(176, 44, 0, panel.height() + 5)
            .setContents(
                List.of(
                    Component.translatable(
                        "gui.qmd.jei.irradiator.speed",
                        holder.value()
                            .multiplier()),
                    Component.translatable(
                        "gui.qmd.jei.irradiator.particles",
                        stats.irradiatorFuelUsage() * holder.value()
                            .multiplier()),
                    Component.translatable("gui.qmd.jei.irradiator.fuel_usage")));
    }

    @Override
    public void draw(RecipeHolder<IrradiatorRecipe> holder, mezz.jei.api.gui.ingredient.IRecipeSlotsView slots,
        net.minecraft.client.gui.GuiGraphics graphics, double mouseX, double mouseY) {
        panel.draw(graphics, kind == IrradiatorRecipe.Kind.PROCESS ? 41 : 62, 0);
    }
}
