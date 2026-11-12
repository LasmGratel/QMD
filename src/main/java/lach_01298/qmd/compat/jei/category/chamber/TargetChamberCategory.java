package lach_01298.qmd.compat.jei.category.chamber;

import lach_01298.qmd.compat.jei.ChamberJei;
import lach_01298.qmd.compat.jei.JeiPanel;
import lach_01298.qmd.multiblock.ChamberPart;
import lach_01298.qmd.recipe.chamber.ChamberKind;
import lach_01298.qmd.recipe.chamber.TargetChamberRecipe;
import mezz.jei.api.helpers.IGuiHelper;
import lach_01298.qmd.config.MachineViewerStats;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;

import java.util.List;

/** Authored target chamber diagram and physical ingredient channels. */
public final class TargetChamberCategory extends AbstractChamberCategory<TargetChamberRecipe> {

    public TargetChamberCategory(IGuiHelper gui) {
        super(
            gui,
            ChamberKind.TARGET_CHAMBER,
            ChamberJei.TARGET_CHAMBER,
            ChamberPart.TARGET_CONTROLLER,
            JeiPanel.of("jei/target_chamber", 0, 0, 150, 115),
            88);
    }

    @Override
    protected List<Point> particleInputs() {
        return List.of(new Point(8, 34));
    }

    @Override
    protected List<Point> particleOutputs() {
        return List.of(new Point(76, 3), new Point(119, 34), new Point(76, 66));
    }

    @Override
    protected List<Point> itemInputs() {
        return List.of(new Point(43, 26));
    }

    @Override
    protected List<Point> itemOutputs() {
        return List.of(new Point(84, 26));
    }

    @Override
    protected List<Point> fluidInputs() {
        return List.of(new Point(43, 43));
    }

    @Override
    protected List<Point> fluidOutputs() {
        return List.of(new Point(84, 43));
    }

    @Override
    protected int workScale() {
        return MachineViewerStats.client()
            .targetChamberScale();
    }

    @Override
    protected void addAnimations(IRecipeExtrasBuilder builder, TargetChamberRecipe recipe) {
        JeiPanel.of("target_chamber_controller", 0, 0, 176, 200)
            .progress(gui, builder, 182, 0, 21, 12, offset() + 61, 36, 80);
    }
}
