package lach_01298.qmd.client.screen.accelerator;

import lach_01298.qmd.block.menu.accelerator.RingAcceleratorMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class RingAcceleratorScreen extends AbstractBeamAcceleratorScreen<RingAcceleratorMenu> {

    public RingAcceleratorScreen(RingAcceleratorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }
}
