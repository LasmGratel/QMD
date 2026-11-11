package lach_01298.qmd.block.menu.chamber;

import lach_01298.qmd.block.entity.chamber.AbstractChamberBlockEntity;
import lach_01298.qmd.recipe.chamber.ChamberKind;
import lach_01298.qmd.registry.QMDMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

/** Inventory contract and authored slot layout for target chamber controllers. */
public final class TargetChamberMenu extends AbstractChamberMenu {

    public TargetChamberMenu(int id, Inventory inventory, AbstractChamberBlockEntity controller) {
        super(QMDMenus.TARGET_CHAMBER.get(), id, inventory, controller, 200, 118);
    }

    public TargetChamberMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        this(id, inventory, requireController(inventory.player, buffer, ChamberKind.TARGET_CHAMBER));
    }

    @Override
    public ChamberKind kind() {
        return ChamberKind.TARGET_CHAMBER;
    }

    @Override
    protected void addMachineSlots() {
        addItemSlots(53, 94, 38);
    }
}
