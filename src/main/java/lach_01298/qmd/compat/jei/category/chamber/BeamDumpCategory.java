package lach_01298.qmd.compat.jei.category.chamber;

import lach_01298.qmd.compat.jei.ChamberJei;
import lach_01298.qmd.compat.jei.JeiPanel;
import lach_01298.qmd.multiblock.ChamberPart;
import lach_01298.qmd.recipe.chamber.ChamberKind;
import lach_01298.qmd.recipe.chamber.BeamDumpRecipe;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;

import java.util.List;

/** Authored beam dump diagram and physical ingredient channels. */
public final class BeamDumpCategory extends AbstractChamberCategory<BeamDumpRecipe> {

    public BeamDumpCategory(IGuiHelper gui) {
        super(
            gui,
            ChamberKind.BEAM_DUMP,
            ChamberJei.BEAM_DUMP,
            ChamberPart.BEAM_DUMP_CONTROLLER,
            JeiPanel.of("beam_dump_controller", 18, 20, 82, 40),
            44);
    }

    @Override
    protected List<Point> particleInputs() {
        return List.of(new Point(19, 17));
    }

    @Override
    protected List<Point> fluidOutputs() {
        return List.of(new Point(63, 17));
    }

    @Override
    protected void addAnimations(IRecipeExtrasBuilder builder, BeamDumpRecipe recipe) {
        panel.progress(gui, builder, 143, 6, 23, 14, offset() + 36, 18, 80);
    }
}
