package lach_01298.qmd.client.screen.chamber;

import lach_01298.qmd.block.menu.chamber.DecayChamberMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/** Authored decay chamber instruments and active beam overlays. */
public final class DecayChamberScreen extends AbstractChamberScreen<DecayChamberMenu> {

    public DecayChamberScreen(DecayChamberMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, "decay_chamber_controller");
    }

    @Override
    public ScreenRectangle recipeArea() {
        return new ScreenRectangle(68, 37, 40, 16);
    }

    @Override
    protected void addMachineWidgets() {
        particle(true, 0, 68, 37);
        for (int i = 0; i < 3; i++) particle(false, i, 101, 14 + i * 23);
        power(161, 5, 6, 74, 176);
    }

    @Override
    protected void renderConnections(GuiGraphics graphics) {
        connection(graphics, true, 0, 51, 42, 182, 0, 16, 6);
        connection(graphics, false, 0, 84, 20, 182, 6, 16, 16);
        connection(graphics, false, 1, 85, 43, 182, 38, 16, 4);
        connection(graphics, false, 2, 84, 53, 182, 22, 16, 16);
    }
}
