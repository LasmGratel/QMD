package lach_01298.qmd.client.screen.chamber;

import lach_01298.qmd.block.menu.chamber.NucleosynthesisChamberMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/** Authored nucleosynthesis chamber instruments and active beam overlays. */
public final class NucleosynthesisChamberScreen extends AbstractChamberScreen<NucleosynthesisChamberMenu> {

    public NucleosynthesisChamberScreen(NucleosynthesisChamberMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, "nucleosynthesis_chamber_controller");
    }

    @Override
    public ScreenRectangle recipeArea() {
        return new ScreenRectangle(71, 24, 40, 28);
    }

    @Override
    protected void addMachineWidgets() {
        particle(true, 0, 32, 30);
        tank(0, 22, 8, 4, 74);
        tank(1, 28, 67, 4, 15);
        tank(2, 163, 8, 4, 74);
        tank(3, 168, 67, 4, 15);
        tank(4, 54, 18, 16, 16);
        tank(5, 54, 42, 16, 16);
        tank(6, 134, 18, 16, 16);
        tank(7, 134, 42, 16, 16);
        power(8, 8, 4, 74, 176);
        heat(15, 8, 4, 74, 180, false);
        heat(156, 8, 4, 74, 180, true);
        progress(71, 24, 40, 28, 188, 2, 0);
    }

    @Override
    protected void renderConnections(GuiGraphics graphics) {
        connection(graphics, true, 0, 49, 37, 188, 0, 59, 2);
    }

    @Override
    protected int clearY() {
        return 90;
    }

    @Override
    protected void renderTitle(GuiGraphics graphics) {
        drawTitle(graphics, 32, 120);
    }
}
