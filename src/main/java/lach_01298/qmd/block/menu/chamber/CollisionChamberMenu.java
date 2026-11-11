package lach_01298.qmd.block.menu.chamber;

import lach_01298.qmd.block.entity.chamber.AbstractChamberBlockEntity;
import lach_01298.qmd.recipe.chamber.ChamberKind;
import lach_01298.qmd.registry.QMDMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

/** Inventory contract and authored slot layout for collision chamber controllers. */
public final class CollisionChamberMenu extends AbstractChamberMenu {

    public CollisionChamberMenu(int id, Inventory inventory, AbstractChamberBlockEntity controller) {
        super(QMDMenus.COLLISION_CHAMBER.get(), id, inventory, controller, 130, 136);
    }

    public CollisionChamberMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        this(id, inventory, requireController(inventory.player, buffer, ChamberKind.COLLISION_CHAMBER));
    }

    @Override
    public ChamberKind kind() {
        return ChamberKind.COLLISION_CHAMBER;
    }
}
