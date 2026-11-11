package lach_01298.qmd.client.screen.accelerator;

import lach_01298.qmd.block.menu.accelerator.BeamSplitterMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class BeamSplitterScreen extends AbstractBeamAcceleratorScreen<BeamSplitterMenu> {

    public BeamSplitterScreen(BeamSplitterMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }
}
