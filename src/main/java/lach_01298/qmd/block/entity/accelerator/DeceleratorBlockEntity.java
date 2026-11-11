package lach_01298.qmd.block.entity.accelerator;

import lach_01298.qmd.block.menu.accelerator.DeceleratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public final class DeceleratorBlockEntity extends AbstractAcceleratorBlockEntity {

    public DeceleratorBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new DeceleratorMenu(id, inventory, this);
    }
}
