package lach_01298.qmd.compat.jei.category.chamber;

import lach_01298.qmd.compat.jei.ChamberJei;
import lach_01298.qmd.compat.jei.JeiPanel;
import lach_01298.qmd.multiblock.ChamberPart;
import lach_01298.qmd.recipe.chamber.ChamberKind;
import lach_01298.qmd.recipe.chamber.CellFillingRecipe;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

import java.util.List;

/** Authored cell filling diagram and physical ingredient channels. */
public final class CellFillingCategory extends AbstractChamberCategory<CellFillingRecipe> {

    public CellFillingCategory(IGuiHelper gui) {
        super(
            gui,
            ChamberKind.CELL_FILLING,
            ChamberJei.CELL_FILLING,
            ChamberPart.CONTAINMENT_CONTROLLER,
            JeiPanel.of("jei/cell_filling", 0, 0, 64, 45),
            49);
    }

    @Override
    protected List<Point> itemInputs() {
        return List.of(new Point(3, 3));
    }

    @Override
    protected List<Point> itemOutputs() {
        return List.of(new Point(45, 3));
    }

    @Override
    protected List<Point> fluidInputs() {
        return List.of(new Point(3, 26));
    }

    @Override
    protected List<Point> fluidOutputs() {
        return List.of(new Point(45, 26));
    }

    @Override
    protected void addRecipeDetails(List<FormattedText> lines, CellFillingRecipe recipe) {
        lines.add(Component.translatable("jei.qmd.chamber.cell_exchange"));
    }
}
