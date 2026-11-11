package lach_01298.qmd.block.menu.chamber;

import lach_01298.qmd.block.entity.chamber.AbstractChamberBlockEntity;
import lach_01298.qmd.recipe.chamber.ChamberKind;
import lach_01298.qmd.registry.QMDMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

/** Inventory contract and authored slot layout for beam dump controllers. */
public final class BeamDumpMenu extends AbstractChamberMenu {

    public BeamDumpMenu(int id, Inventory inventory, AbstractChamberBlockEntity controller) {
        super(QMDMenus.BEAM_DUMP.get(), id, inventory, controller, 89, 95);
    }

    public BeamDumpMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        this(id, inventory, requireController(inventory.player, buffer, ChamberKind.BEAM_DUMP));
    }

    @Override
    public ChamberKind kind() {
        return ChamberKind.BEAM_DUMP;
    }
}
