package lach_01298.qmd.compat.jei.category.chamber;

import lach_01298.qmd.compat.jei.ChamberJei;
import lach_01298.qmd.compat.jei.JeiPanel;
import lach_01298.qmd.multiblock.ChamberPart;
import lach_01298.qmd.recipe.chamber.ChamberKind;
import lach_01298.qmd.recipe.chamber.CollisionChamberRecipe;
import mezz.jei.api.helpers.IGuiHelper;

import java.util.List;

/** Authored collision chamber diagram and physical ingredient channels. */
public final class CollisionChamberCategory extends AbstractChamberCategory<CollisionChamberRecipe> {

    public CollisionChamberCategory(IGuiHelper gui) {
        super(
            gui,
            ChamberKind.COLLISION_CHAMBER,
            ChamberJei.COLLISION_CHAMBER,
            ChamberPart.COLLISION_CONTROLLER,
            JeiPanel.of("jei/collision_chamber", 0, 0, 160, 112),
            86);
    }

    @Override
    protected List<Point> particleInputs() {
        return List.of(new Point(43, 33), new Point(111, 33));
    }

    @Override
    protected List<Point> particleOutputs() {
        return List.of(new Point(46, 2), new Point(108, 2), new Point(46, 64), new Point(108, 64));
    }
}
