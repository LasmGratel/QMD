package lach_01298.qmd.compat.jei.category.accelerator;

import lach_01298.qmd.compat.jei.JeiPanel;
import lach_01298.qmd.compat.jei.QMDAcceleratorJei;
import lach_01298.qmd.recipe.accelerator.AcceleratorCoolingRecipe;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import java.util.List;

public final class AcceleratorCoolingCategory extends AbstractAcceleratorCategory<AcceleratorCoolingRecipe> {

    public AcceleratorCoolingCategory(IGuiHelper gui) {
        super(
            gui,
            QMDAcceleratorJei.COOLING,
            "accelerator_cooling",
            "accelerator_cooler_water",
            JeiPanel.of("jei/accelerator_cooling", 0, 0, 90, 26));
    }

    @Override
    protected int fluidInputX() {
        return 9;
    }

    @Override
    protected int fluidInputY() {
        return 5;
    }

    @Override
    protected int fluidOutputX(int index) {
        return 65;
    }

    @Override
    protected int fluidOutputY() {
        return 1;
    }

    @Override
    protected int fluidOutputSize() {
        return 24;
    }

    @Override
    protected List<FormattedText> details(AcceleratorCoolingRecipe recipe) {
        return List.of(
            Component.translatable("qmd.gui.jei.accelerator_cooling.heat_required")
                .append(" " + recipe.heat() + " H"),
            Component.translatable("qmd.gui.jei.accelerator_cooling.temperature")
                .append(" " + recipe.temperature() + " K"));
    }

    @Override
    protected void animations(IRecipeExtrasBuilder builder, AcceleratorCoolingRecipe recipe) {
        panel.progress(gui, builder, 90, 0, 36, 15, x() + 27, 6, Math.max(5, (int) Math.min(200, recipe.heat() / 100)));
    }
}
