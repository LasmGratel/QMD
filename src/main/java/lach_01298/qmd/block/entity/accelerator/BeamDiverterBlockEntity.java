package lach_01298.qmd.block.entity.accelerator;

import lach_01298.qmd.block.menu.accelerator.BeamDiverterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public final class BeamDiverterBlockEntity extends AbstractAcceleratorBlockEntity {

    public BeamDiverterBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new BeamDiverterMenu(id, inventory, this);
    }
}
