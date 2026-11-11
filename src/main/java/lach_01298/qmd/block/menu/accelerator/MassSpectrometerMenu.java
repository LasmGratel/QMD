package lach_01298.qmd.block.menu.accelerator;

import lach_01298.qmd.block.entity.accelerator.AbstractAcceleratorBlockEntity;
import lach_01298.qmd.registry.QMDMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public final class MassSpectrometerMenu extends AbstractAcceleratorMenu {

    public MassSpectrometerMenu(int id, Inventory inventory, AbstractAcceleratorBlockEntity part) {
        super(QMDMenus.MASS_SPECTROMETER.get(), id, inventory, part, 6, 8, 119);
    }

    public MassSpectrometerMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        this(id, inventory, require(inventory.player, buffer));
    }

    @Override
    public boolean spectrometer() {
        return true;
    }

    @Override
    protected void addMachineSlots() {
        machineSlot(0, 46, 14, true, true);
        machineSlot(1, -1000, -1000, false, false);
        for (int index = 2; index < 6; index++) machineSlot(index, 82 + 19 * (index - 2), 14, false, true);
    }
}
