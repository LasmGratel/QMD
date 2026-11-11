package lach_01298.qmd.compat.jei.category.chamber;

import lach_01298.qmd.compat.jei.ChamberJei;
import lach_01298.qmd.compat.jei.JeiPanel;
import lach_01298.qmd.multiblock.ChamberPart;
import lach_01298.qmd.recipe.chamber.ChamberKind;
import lach_01298.qmd.recipe.chamber.VacuumChamberHeatingRecipe;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

import java.util.List;

/** Authored vacuum chamber heating diagram and physical ingredient channels. */
public final class VacuumChamberHeatingCategory extends AbstractChamberCategory<VacuumChamberHeatingRecipe> {

    public VacuumChamberHeatingCategory(IGuiHelper gui) {
        super(
            gui,
            ChamberKind.VACUUM_CHAMBER_HEATING,
            ChamberJei.VACUUM_CHAMBER_HEATING,
            ChamberPart.HEATER_VENT,
            JeiPanel.of("jei/accelerator_cooling", 0, 0, 90, 26),
            30);
    }

    @Override
    protected List<Point> fluidInputs() {
        return List.of(new Point(9, 5));
    }

    @Override
    protected List<Point> fluidOutputs() {
        return List.of(new Point(65, 1));
    }

    @Override
    protected int fluidSize(boolean input, int index) {
        return input ? 16 : 24;
    }

    @Override
    protected void addRecipeDetails(List<FormattedText> lines, VacuumChamberHeatingRecipe recipe) {
        lines.add(Component.translatable("jei.qmd.chamber.cryogenic"));
    }
}
