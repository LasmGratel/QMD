package lach_01298.qmd.client.screen.chamber;

import lach_01298.qmd.block.menu.chamber.BeamDumpMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/** Authored beam dump instruments and active beam overlays. */
public final class BeamDumpScreen extends AbstractChamberScreen<BeamDumpMenu> {

    public BeamDumpScreen(BeamDumpMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, "beam_dump_controller");
    }

    @Override
    public ScreenRectangle recipeArea() {
        return new ScreenRectangle(73, 38, 23, 14);
    }

    @Override
    protected void addMachineWidgets() {
        particle(true, 0, 56, 37);
        tank(0, 27, 16, 4, 16);
        tank(1, 100, 37, 16, 16);
        power(141, 5, 6, 74, 137);
        progress(73, 38, 23, 14, 143, 6, 0);
    }

    @Override
    protected void renderConnections(GuiGraphics graphics) {
        connection(graphics, true, 0, 39, 42, 143, 0, 16, 6);
    }

    @Override
    protected ScreenRectangle statusBounds() {
        return new ScreenRectangle(27, 66, 108, 10);
    }

    @Override
    protected int backgroundX() {
        return 19;
    }

    @Override
    protected int backgroundWidth() {
        return 137;
    }

    @Override
    protected int clearY() {
        return imageHeight - 21;
    }

    @Override
    protected void renderTitle(GuiGraphics graphics) {
        drawTitle(graphics, 27, 108);
    }
}
