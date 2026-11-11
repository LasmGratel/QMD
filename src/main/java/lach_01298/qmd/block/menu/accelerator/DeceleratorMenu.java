package lach_01298.qmd.block.menu.accelerator;

import lach_01298.qmd.block.entity.accelerator.AbstractAcceleratorBlockEntity;
import lach_01298.qmd.registry.QMDMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public final class DeceleratorMenu extends AbstractAcceleratorMenu {

    public DeceleratorMenu(int id, Inventory inventory, AbstractAcceleratorBlockEntity part) {
        super(QMDMenus.DECELERATOR.get(), id, inventory, part, 0, 18, 115);
    }

    public DeceleratorMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        this(id, inventory, require(inventory.player, buffer));
    }
}
