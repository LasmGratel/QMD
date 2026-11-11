package lach_01298.qmd.client.screen.accelerator;

import lach_01298.qmd.block.menu.accelerator.DeceleratorMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class DeceleratorScreen extends AbstractBeamAcceleratorScreen<DeceleratorMenu> {

    public DeceleratorScreen(DeceleratorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }
}
