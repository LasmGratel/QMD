package lach_01298.qmd.block.menu.chamber;

import lach_01298.qmd.block.entity.chamber.AbstractChamberBlockEntity;
import lach_01298.qmd.recipe.chamber.ChamberKind;
import lach_01298.qmd.registry.QMDMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

/** Inventory contract and authored slot layout for neutral containment controllers. */
public final class NeutralContainmentMenu extends AbstractChamberMenu {

    public NeutralContainmentMenu(int id, Inventory inventory, AbstractChamberBlockEntity controller) {
        super(QMDMenus.NEUTRAL_CONTAINMENT.get(), id, inventory, controller, 177, 95);
    }

    public NeutralContainmentMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        this(id, inventory, requireController(inventory.player, buffer, ChamberKind.NEUTRAL_CONTAINMENT));
    }

    @Override
    public ChamberKind kind() {
        return ChamberKind.NEUTRAL_CONTAINMENT;
    }

    @Override
    protected void addMachineSlots() {
        addItemSlots(59, 101, 59);
    }
}
