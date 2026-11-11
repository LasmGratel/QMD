package lach_01298.qmd.client.screen.accelerator;

import lach_01298.qmd.block.menu.accelerator.BeamDiverterMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class BeamDiverterScreen extends AbstractBeamAcceleratorScreen<BeamDiverterMenu> {

    public BeamDiverterScreen(BeamDiverterMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }
}
