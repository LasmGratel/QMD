package lach_01298.qmd.client.screen.chamber;

import lach_01298.qmd.block.menu.chamber.NeutralContainmentMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/** Authored neutral containment instruments and active beam overlays. */
public final class NeutralContainmentScreen extends AbstractChamberScreen<NeutralContainmentMenu> {

    public NeutralContainmentScreen(NeutralContainmentMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, "neutral_containment_controller");
    }

    @Override
    public ScreenRectangle recipeArea() {
        return new ScreenRectangle(60, 33, 70, 6);
    }

    @Override
    protected void addMachineWidgets() {
        particle(true, 0, 27, 28);
        particle(true, 1, 131, 28);
        tank(0, 15, 8, 4, 74);
        tank(1, 21, 66, 4, 16);
        tank(2, 71, 20, 32, 32);
        power(160, 8, 6, 74, 176);
        heat(8, 8, 4, 74, 182, false);
        progress(60, 33, 10, 6, 206, 0, 0);
        progress(104, 33, 10, 6, 206, 6, 1);
    }

    @Override
    protected void renderConnections(GuiGraphics graphics) {
        connection(graphics, true, 0, 44, 33, 190, 0, 16, 6);
        connection(graphics, true, 1, 114, 33, 190, 6, 16, 6);
    }

    @Override
    protected ScreenRectangle statusBounds() {
        return new ScreenRectangle(8, 80, 140, 10);
    }

    @Override
    protected int clearX() {
        return 130;
    }

    @Override
    protected int clearY() {
        return 59;
    }

    @Override
    protected boolean embeddedInventory() {
        return true;
    }

    @Override
    protected void renderTitle(GuiGraphics graphics) {
        // The authored containment panel reserves this space for its thermal instruments.
    }
}
