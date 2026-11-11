package lach_01298.qmd.client.screen.accelerator;

import lach_01298.qmd.block.menu.accelerator.AbstractAcceleratorMenu;
import lach_01298.qmd.client.screen.widget.MachineWidget;
import lach_01298.qmd.recipe.accelerator.AcceleratorRecipeKind;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import java.util.Locale;

/** Shared artwork and instruments of beam acceleration/routing controllers. */
public abstract class AbstractBeamAcceleratorScreen<M extends AbstractAcceleratorMenu>
    extends AbstractAcceleratorScreen<M> {

    protected AbstractBeamAcceleratorScreen(M menu, Inventory inventory, Component title) {
        super(menu, inventory, title, "accelerator_controller", 196, 219, 109);
    }

    @Override
    public final AcceleratorRecipeKind recipeKind() {
        return AcceleratorRecipeKind.COOLING;
    }

    @Override
    public final ScreenRectangle recipeArea() {
        return new ScreenRectangle(40, 51, 85, 40);
    }

    @Override
    protected final void addMachineWidgets() {
        controllerGauges(40, 145, 6, 6, 95, 196, 18, 202, 28);
        for (int i = 0; i < 3; i++) {
            int index = i;
            instrument(new MachineWidget.Particle(leftPos + 40 + i * 42, topPos + 21, () -> beam(index)));
        }
        clearMaterial(150, 20);
        tank("coolantOutput", imageWidth - 10, 6, 4, 95);
        controls();
    }

    @Override
    protected final void renderInventory(GuiGraphics graphics) {
        graphics.blit(SOURCE, leftPos + 10, topPos + 109, 0, 78, 176, 88);
    }

    @Override
    protected final void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        int color = state.getBoolean("operating") ? 0xFFFFFF : 0xEEAA55;
        line(graphics, title, 40, 5, 148, color);
        line(
            graphics,
            Component.translatable(
                "gui.qmd.container.accelerator.cavities",
                state.getInt("cavities"),
                state.getLong("voltage") + " V"),
            40,
            52,
            145,
            color);
        line(
            graphics,
            Component.translatable(
                "gui.qmd.container.accelerator.quadrupoles",
                state.getInt("quadrupoles"),
                state.getDouble("quadrupoleStrength") + " T"),
            40,
            63,
            145,
            color);
        line(
            graphics,
            Component.translatable(
                "gui.qmd.container.accelerator.dipoles",
                state.getInt("dipoles"),
                state.getDouble("dipoleStrength") + " T"),
            40,
            74,
            145,
            color);
        line(
            graphics,
            Component.translatable(
                "gui.qmd.container.temperature",
                String.format(Locale.ROOT, "%.1f K", 400 * ratio("heat", "heatCapacity"))),
            40,
            85,
            145,
            color);
        line(
            graphics,
            Component.translatable("gui.qmd.container.max_temperature", state.getDouble("maxTemperature") + " K"),
            40,
            96,
            145,
            color);
    }
}
