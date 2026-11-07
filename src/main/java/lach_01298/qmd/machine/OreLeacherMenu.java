package lach_01298.qmd.machine;

import com.nred.nuclearcraft.menu.processor.ProcessorMenuImpl.BasicUpgradableEnergyProcessorMenu;
import com.nred.nuclearcraft.recipe.ProcessorRecipe;
import lach_01298.qmd.registration.QMDMachines;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

/** Complete machine inventory, player inventory and Neohaul upgrade/shift-click behavior. */
public final class OreLeacherMenu extends BasicUpgradableEnergyProcessorMenu<OreLeacherBlockEntity, ProcessorRecipe> {
    public OreLeacherMenu(int containerId, Inventory inventory, OreLeacherBlockEntity tile) {
        super(QMDMachines.ORE_LEACHER_MENU.get(), containerId, inventory, tile);
    }

    public OreLeacherMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerId, inventory, requireMachine(inventory, extraData.readBlockPos()));
    }

    @Override
    public boolean stillValid(Player player) {
        return !tile.isRemoved() && tile.getLevel() != null
                && tile.getLevel().getBlockEntity(tile.getBlockPos()) == tile
                && super.stillValid(player);
    }

    private static OreLeacherBlockEntity requireMachine(Inventory inventory, BlockPos pos) {
        if (inventory.player.level().getBlockEntity(pos) instanceof OreLeacherBlockEntity machine) {
            return machine;
        }
        throw new IllegalStateException("Cannot open the ore leacher menu: no ore leacher at " + pos);
    }
}
