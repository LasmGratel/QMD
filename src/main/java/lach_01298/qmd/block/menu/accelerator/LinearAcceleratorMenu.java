package lach_01298.qmd.block.menu.accelerator;

import lach_01298.qmd.block.entity.accelerator.AbstractAcceleratorBlockEntity;
import lach_01298.qmd.registry.QMDMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public final class LinearAcceleratorMenu extends AbstractAcceleratorMenu {

    public LinearAcceleratorMenu(int id, Inventory inventory, AbstractAcceleratorBlockEntity part) {
        super(QMDMenus.LINEAR_ACCELERATOR.get(), id, inventory, part, 0, 18, 115);
    }

    public LinearAcceleratorMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        this(id, inventory, require(inventory.player, buffer));
    }
}
