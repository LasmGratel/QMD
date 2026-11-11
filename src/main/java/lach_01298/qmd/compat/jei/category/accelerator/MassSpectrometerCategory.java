package lach_01298.qmd.compat.jei.category.accelerator;

import lach_01298.qmd.compat.jei.JeiPanel;
import lach_01298.qmd.compat.jei.QMDAcceleratorJei;
import lach_01298.qmd.recipe.accelerator.MassSpectrometerRecipe;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import java.util.List;

public final class MassSpectrometerCategory extends AbstractAcceleratorCategory<MassSpectrometerRecipe> {

    public MassSpectrometerCategory(IGuiHelper gui) {
        super(
            gui,
            QMDAcceleratorJei.SPECTROMETER,
            "mass_spectrometer",
            "mass_spectrometer_controller",
            JeiPanel.of("mass_spectrometer_controller", 45, 13, 111, 93));
    }

    @Override
    protected int fluidInputX() {
        return 1;
    }

    @Override
    protected int fluidInputY() {
        return 20;
    }

    @Override
    protected List<FormattedText> details(MassSpectrometerRecipe recipe) {
        return List.of(
            Component.translatable("jei.qmd.accelerator.work", recipe.time()),
            Component.translatable("jei.qmd.accelerator.assembly"));
    }

    @Override
    protected void animations(IRecipeExtrasBuilder builder, MassSpectrometerRecipe recipe) {
        panel.progress(gui, builder, 0, 201, 101, 55, x() + 7, 38, 80);
    }
}
