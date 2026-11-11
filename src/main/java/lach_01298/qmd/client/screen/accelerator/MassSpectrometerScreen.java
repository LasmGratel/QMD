package lach_01298.qmd.client.screen.accelerator;

import lach_01298.qmd.block.menu.accelerator.MassSpectrometerMenu;
import lach_01298.qmd.client.screen.widget.MachineWidget;
import lach_01298.qmd.recipe.accelerator.AcceleratorRecipeKind;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import java.util.List;

public final class MassSpectrometerScreen extends AbstractAcceleratorScreen<MassSpectrometerMenu> {

    public MassSpectrometerScreen(MassSpectrometerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, "mass_spectrometer_controller", 176, 223, 201);
    }

    @Override
    public AcceleratorRecipeKind recipeKind() {
        return AcceleratorRecipeKind.SPECTROMETER;
    }

    @Override
    public ScreenRectangle recipeArea() {
        return new ScreenRectangle(40, 51, 85, 40);
    }

    @Override
    protected void addMachineWidgets() {
        controllerGauges(107, 124, 13, 4, 92, 176, 15, 180, 22);
        clearMaterial(30, 80);
        for (int i = 0; i < 5; i++) tank("processFluid" + i, i == 0 ? 46 : 82 + (i - 1) * 19, 33, 16, 16);
        instrument(
            new MachineWidget.Gauge(
                leftPos + 52,
                topPos + 51,
                101,
                55,
                texture,
                0,
                201,
                false,
                () -> state.getDouble("work") / Math.max(1, state.getDouble("recipeWork")),
                () -> List.of(value(state.getDouble("work") + " / " + state.getDouble("recipeWork") + " pu"))));
        tank("coolantOutput", imageWidth - 10, 13, 4, 92);
        controls();
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        line(graphics, title, 40, 6, 124, 0x404040);
    }
}
