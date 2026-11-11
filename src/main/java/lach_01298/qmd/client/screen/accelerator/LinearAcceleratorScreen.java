package lach_01298.qmd.client.screen.accelerator;

import lach_01298.qmd.block.menu.accelerator.LinearAcceleratorMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class LinearAcceleratorScreen extends AbstractBeamAcceleratorScreen<LinearAcceleratorMenu> {

    public LinearAcceleratorScreen(LinearAcceleratorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }
}
