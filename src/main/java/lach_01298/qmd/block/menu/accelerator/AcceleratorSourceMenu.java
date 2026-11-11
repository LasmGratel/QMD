package lach_01298.qmd.block.menu.accelerator;

import lach_01298.qmd.block.entity.accelerator.AbstractAcceleratorBlockEntity;
import lach_01298.qmd.registry.QMDMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public final class AcceleratorSourceMenu extends AbstractAcceleratorMenu {

    public AcceleratorSourceMenu(int id, Inventory inventory, AbstractAcceleratorBlockEntity part) {
        super(QMDMenus.ACCELERATOR_SOURCE.get(), id, inventory, part, 2, 8, 84);
    }

    public AcceleratorSourceMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        this(id, inventory, require(inventory.player, buffer));
    }

    @Override
    public boolean source() {
        return true;
    }

    @Override
    protected void addMachineSlots() {
        machineSlot(0, 71, 26, true, true);
        machineSlot(1, 89, 26, true, true);
    }

    @Override
    protected boolean handlePartButton(int button) {
        if (button != 20) return false;
        part().sourceFluid()
            .setFluid(net.neoforged.neoforge.fluids.FluidStack.EMPTY);
        part().setChanged();
        return true;
    }
}
