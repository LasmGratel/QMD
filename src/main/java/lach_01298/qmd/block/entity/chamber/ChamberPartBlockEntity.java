package lach_01298.qmd.block.entity.chamber;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/** Casing, detector, beamline and configurable port; processing belongs to its controller. */
public final class ChamberPartBlockEntity extends AbstractChamberBlockEntity {

    public ChamberPartBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public void processTick(List<AbstractChamberBlockEntity> inputs, List<AbstractChamberBlockEntity> outputs,
        int length) {}

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return null;
    }
}
