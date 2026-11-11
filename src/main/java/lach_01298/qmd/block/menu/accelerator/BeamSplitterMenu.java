package lach_01298.qmd.block.menu.accelerator;

import lach_01298.qmd.block.entity.accelerator.AbstractAcceleratorBlockEntity;
import lach_01298.qmd.registry.QMDMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public final class BeamSplitterMenu extends AbstractAcceleratorMenu {

    public BeamSplitterMenu(int id, Inventory inventory, AbstractAcceleratorBlockEntity part) {
        super(QMDMenus.BEAM_SPLITTER.get(), id, inventory, part, 0, 18, 115);
    }

    public BeamSplitterMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        this(id, inventory, require(inventory.player, buffer));
    }
}
