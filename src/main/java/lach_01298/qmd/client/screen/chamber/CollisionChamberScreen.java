package lach_01298.qmd.client.screen.chamber;

import lach_01298.qmd.block.menu.chamber.CollisionChamberMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/** Authored collision chamber instruments and active beam overlays. */
public final class CollisionChamberScreen extends AbstractChamberScreen<CollisionChamberMenu> {

    public CollisionChamberScreen(CollisionChamberMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, "collision_chamber_controller");
    }

    @Override
    public ScreenRectangle recipeArea() {
        return new ScreenRectangle(68, 37, 40, 16);
    }

    @Override
    protected void addMachineWidgets() {
        particle(true, 0, 46, 45);
        particle(true, 1, 114, 45);
        for (int i = 0; i < 4; i++) particle(false, i, i % 2 == 0 ? 49 : 111, i < 2 ? 14 : 76);
        power(161, 13, 6, 74, 176);
    }

    @Override
    protected void renderConnections(GuiGraphics graphics) {
        connection(graphics, true, 0, 63, 50, 182, 19, 16, 6);
        connection(graphics, true, 1, 97, 50, 216, 19, 16, 6);
        for (int i = 0; i < 4; i++) connection(
            graphics,
            false,
            i,
            i % 2 == 0 ? 66 : 89,
            i < 2 ? 31 : 54,
            i % 2 == 0 ? 185 : 208,
            i < 2 ? 0 : 23,
            21,
            21);
    }

    @Override
    protected ScreenRectangle statusBounds() {
        return new ScreenRectangle(8, 100, 140, 10);
    }

    @Override
    protected int clearY() {
        return 108;
    }
}
