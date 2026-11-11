package lach_01298.qmd.compat.jei.category.chamber;

import lach_01298.qmd.compat.jei.ChamberJei;
import lach_01298.qmd.compat.jei.JeiPanel;
import lach_01298.qmd.multiblock.ChamberPart;
import lach_01298.qmd.recipe.chamber.ChamberKind;
import lach_01298.qmd.recipe.chamber.NucleosynthesisChamberRecipe;
import mezz.jei.api.helpers.IGuiHelper;
import lach_01298.qmd.config.MachineViewerStats;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

import java.util.List;

/** Authored nucleosynthesis chamber diagram and physical ingredient channels. */
public final class NucleosynthesisChamberCategory extends AbstractChamberCategory<NucleosynthesisChamberRecipe> {

    public NucleosynthesisChamberCategory(IGuiHelper gui) {
        super(
            gui,
            ChamberKind.NUCLEOSYNTHESIS_CHAMBER,
            ChamberJei.NUCLEOSYNTHESIS_CHAMBER,
            ChamberPart.NUCLEOSYNTHESIS_CONTROLLER,
            JeiPanel.of("nucleosynthesis_chamber_controller", 29, 15, 124, 66),
            70);
    }

    @Override
    protected List<Point> particleInputs() {
        return List.of(new Point(3, 15));
    }

    @Override
    protected List<Point> fluidInputs() {
        return List.of(new Point(25, 3), new Point(25, 27));
    }

    @Override
    protected List<Point> fluidOutputs() {
        return List.of(new Point(105, 3), new Point(105, 27));
    }

    @Override
    protected int workScale() {
        return MachineViewerStats.client()
            .nucleosynthesisScale();
    }

    @Override
    protected void addRecipeDetails(List<FormattedText> lines, NucleosynthesisChamberRecipe recipe) {
        lines.add(Component.translatable("jei.qmd.chamber.cryogenic"));
    }

    @Override
    protected void addAnimations(IRecipeExtrasBuilder builder, NucleosynthesisChamberRecipe recipe) {
        panel.progress(gui, builder, 188, 2, 40, 28, offset() + 42, 9, 80);
    }
}
