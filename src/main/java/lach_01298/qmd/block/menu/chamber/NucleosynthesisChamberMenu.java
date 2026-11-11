package lach_01298.qmd.block.menu.chamber;

import lach_01298.qmd.block.entity.chamber.AbstractChamberBlockEntity;
import lach_01298.qmd.recipe.chamber.ChamberKind;
import lach_01298.qmd.registry.QMDMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

/** Inventory contract and authored slot layout for nucleosynthesis chamber controllers. */
public final class NucleosynthesisChamberMenu extends AbstractChamberMenu {

    public NucleosynthesisChamberMenu(int id, Inventory inventory, AbstractChamberBlockEntity controller) {
        super(QMDMenus.NUCLEOSYNTHESIS_CHAMBER.get(), id, inventory, controller, 113, 119);
    }

    public NucleosynthesisChamberMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        this(id, inventory, requireController(inventory.player, buffer, ChamberKind.NUCLEOSYNTHESIS_CHAMBER));
    }

    @Override
    public ChamberKind kind() {
        return ChamberKind.NUCLEOSYNTHESIS_CHAMBER;
    }
}
