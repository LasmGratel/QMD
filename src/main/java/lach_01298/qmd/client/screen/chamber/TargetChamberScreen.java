package lach_01298.qmd.client.screen.chamber;

import lach_01298.qmd.block.menu.chamber.TargetChamberMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/** Authored target chamber instruments and active beam overlays. */
public final class TargetChamberScreen extends AbstractChamberScreen<TargetChamberMenu> {

    public TargetChamberScreen(TargetChamberMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, "target_chamber_controller");
    }

    @Override
    public ScreenRectangle recipeArea() {
        return new ScreenRectangle(71, 48, 21, 12);
    }

    @Override
    protected void addMachineWidgets() {
        particle(true, 0, 18, 46);
        particle(false, 0, 86, 15);
        particle(false, 1, 129, 46);
        particle(false, 2, 86, 78);
        tank(0, 53, 55, 16, 16);
        tank(1, 94, 55, 16, 16);
        power(161, 13, 6, 74, 176);
        progress(71, 48, 21, 12, 182, 0, 0);
    }

    @Override
    protected void renderConnections(GuiGraphics graphics) {
        connection(graphics, true, 0, 35, 51, 182, 12, 16, 6);
        connection(graphics, false, 0, 69, 22, 182, 18, 16, 16);
        connection(graphics, false, 1, 112, 52, 182, 50, 16, 4);
        connection(graphics, false, 2, 69, 71, 182, 34, 16, 16);
    }

    @Override
    protected int clearY() {
        return 90;
    }

    @Override
    protected boolean embeddedInventory() {
        return true;
    }
}
