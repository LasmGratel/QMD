package lach_01298.qmd.compat.jei.category.accelerator;

import lach_01298.qmd.compat.jei.JeiPanel;
import lach_01298.qmd.compat.jei.QMDAcceleratorJei;
import lach_01298.qmd.recipe.accelerator.AcceleratorSourceRecipe;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import java.util.List;

public final class AcceleratorSourceCategory extends AbstractAcceleratorCategory<AcceleratorSourceRecipe> {

    public AcceleratorSourceCategory(IGuiHelper gui) {
        super(
            gui,
            QMDAcceleratorJei.SOURCE,
            "accelerator_source",
            "accelerator_ion_source_basic",
            JeiPanel.of("jei/ion_source", 7, 3, 72, 37));
    }

    @Override
    protected int itemInputX() {
        return 3;
    }

    @Override
    protected int itemInputY() {
        return 3;
    }

    @Override
    protected int fluidInputX() {
        return 2;
    }

    @Override
    protected int fluidInputY() {
        return 19;
    }

    @Override
    protected List<FormattedText> details(AcceleratorSourceRecipe recipe) {
        var stats = lach_01298.qmd.config.MachineViewerStats.client();
        return List.of(
            Component.translatable("jei.qmd.accelerator.source_rate"),
            Component.translatable(
                "jei.qmd.accelerator.source_multiplier",
                stats.basicSourceMultiplier(),
                stats.laserSourceMultiplier()));
    }
}
