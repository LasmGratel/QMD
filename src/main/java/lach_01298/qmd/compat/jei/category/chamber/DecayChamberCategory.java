package lach_01298.qmd.compat.jei.category.chamber;

import lach_01298.qmd.compat.jei.ChamberJei;
import lach_01298.qmd.compat.jei.JeiPanel;
import lach_01298.qmd.multiblock.ChamberPart;
import lach_01298.qmd.recipe.chamber.ChamberKind;
import lach_01298.qmd.recipe.chamber.DecayChamberRecipe;
import mezz.jei.api.helpers.IGuiHelper;

import java.util.List;

/** Authored decay chamber diagram and physical ingredient channels. */
public final class DecayChamberCategory extends AbstractChamberCategory<DecayChamberRecipe> {

    public DecayChamberCategory(IGuiHelper gui) {
        super(
            gui,
            ChamberKind.DECAY_CHAMBER,
            ChamberJei.DECAY_CHAMBER,
            ChamberPart.DECAY_CONTROLLER,
            JeiPanel.of("jei/decay_chamber", 0, 0, 150, 100),
            78);
    }

    @Override
    protected List<Point> particleInputs() {
        return List.of(new Point(49, 31));
    }

    @Override
    protected List<Point> particleOutputs() {
        return List.of(new Point(82, 8), new Point(82, 31), new Point(82, 54));
    }
}
