package lach_01298.qmd.client.screen.accelerator;

import lach_01298.qmd.block.menu.accelerator.AcceleratorPartMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class AcceleratorPartScreen extends AbstractBeamAcceleratorScreen<AcceleratorPartMenu> {

    public AcceleratorPartScreen(AcceleratorPartMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }
}
