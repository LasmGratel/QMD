package lach_01298.qmd.machine.client;

import com.nred.nuclearcraft.block_entity.internal.inventory.ItemOutputSetting;
import com.nred.nuclearcraft.block_entity.inventory.ITileInventory;
import com.nred.nuclearcraft.util.BlockHelper;
import net.minecraft.core.Direction;

import java.util.function.Consumer;

/** Common-safe behavior shared by the GUI and headless verification. */
public final class OreLeacherGuiActions {
    private OreLeacherGuiActions() {
    }

    /** The displayed order is bottom, top, left, right, front, back, relative to the machine. */
    public static Direction[] relativeDirections(Direction facing) {
        return new Direction[]{BlockHelper.bottom(facing), BlockHelper.top(facing),
                BlockHelper.left(facing), BlockHelper.right(facing), BlockHelper.front(facing), BlockHelper.back(facing)};
    }

    /** Cycle the dialog selection, which may be ahead of the tile's last server acknowledgement. */
    public static ItemOutputSetting cycleItemOutputSetting(ITileInventory tile, int slot, ItemOutputSetting currentSelection, boolean reverse,
                                                           Consumer<ItemOutputSetting> sender) {
        ItemOutputSetting setting = currentSelection.next(reverse);
        tile.setItemOutputSetting(slot, setting);
        sender.accept(setting);
        return setting;
    }
}
