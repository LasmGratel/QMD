package lach_01298.qmd.block.entity;

import com.nred.nuclearcraft.block_entity.rtg.RTGEntity;
import lach_01298.qmd.registry.QMDBlockEntities;
import lach_01298.qmd.registry.QMDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/** Shares the native RTG controller, energy output, radiation and persistence. */
public final class StrontiumRtgBlockEntity extends RTGEntity {

    public StrontiumRtgBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state, QMDBlocks.STRONTIUM_RTG_TYPE);
    }

    @Override
    public BlockEntityType<?> getType() {
        return QMDBlockEntities.STRONTIUM_RTG.get();
    }
}
