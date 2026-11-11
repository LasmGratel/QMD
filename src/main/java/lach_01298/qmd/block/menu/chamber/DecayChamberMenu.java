package lach_01298.qmd.block.menu.chamber;

import lach_01298.qmd.block.entity.chamber.AbstractChamberBlockEntity;
import lach_01298.qmd.recipe.chamber.ChamberKind;
import lach_01298.qmd.registry.QMDMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

/** Inventory contract and authored slot layout for decay chamber controllers. */
public final class DecayChamberMenu extends AbstractChamberMenu {

    public DecayChamberMenu(int id, Inventory inventory, AbstractChamberBlockEntity controller) {
        super(QMDMenus.DECAY_CHAMBER.get(), id, inventory, controller, 113, 119);
    }

    public DecayChamberMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        this(id, inventory, requireController(inventory.player, buffer, ChamberKind.DECAY_CHAMBER));
    }

    @Override
    public ChamberKind kind() {
        return ChamberKind.DECAY_CHAMBER;
    }
}
