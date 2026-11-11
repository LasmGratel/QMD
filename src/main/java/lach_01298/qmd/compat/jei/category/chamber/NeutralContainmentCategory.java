package lach_01298.qmd.compat.jei.category.chamber;

import lach_01298.qmd.compat.jei.ChamberJei;
import lach_01298.qmd.compat.jei.JeiPanel;
import lach_01298.qmd.multiblock.ChamberPart;
import lach_01298.qmd.recipe.chamber.ChamberKind;
import lach_01298.qmd.recipe.chamber.NeutralContainmentRecipe;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

import java.util.List;

/** Authored neutral containment diagram and physical ingredient channels. */
public final class NeutralContainmentCategory extends AbstractChamberCategory<NeutralContainmentRecipe> {

    public NeutralContainmentCategory(IGuiHelper gui) {
        super(
            gui,
            ChamberKind.NEUTRAL_CONTAINMENT,
            ChamberJei.NEUTRAL_CONTAINMENT,
            ChamberPart.CONTAINMENT_CONTROLLER,
            JeiPanel.of("neutral_containment_controller", 24, 9, 126, 47),
            51);
    }

    @Override
    protected List<Point> particleInputs() {
        return List.of(new Point(3, 19), new Point(107, 19));
    }

    @Override
    protected List<Point> fluidOutputs() {
        return List.of(new Point(47, 11));
    }

    @Override
    protected int fluidSize(boolean input, int index) {
        return 32;
    }

    @Override
    protected void addRecipeDetails(List<FormattedText> lines, NeutralContainmentRecipe recipe) {
        lines.add(Component.translatable("jei.qmd.chamber.cryogenic"));
    }
}
