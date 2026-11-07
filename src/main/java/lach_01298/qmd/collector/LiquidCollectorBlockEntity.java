package lach_01298.qmd.collector;

import lach_01298.qmd.recipe.CollectorRecipe;
import lach_01298.qmd.registration.QMDMachines;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/** Samples the block below and the original five-by-five-by-five collection volume beneath it. */
public final class LiquidCollectorBlockEntity extends FluidCollectorBlockEntity {
    public LiquidCollectorBlockEntity(BlockPos pos, BlockState state) {
        super(QMDMachines.LIQUID_COLLECTOR_ENTITY.get(), pos, state, CollectorRecipe.Kind.LIQUID);
    }

    @Override
    protected double scanEfficiency(BlockState sampledBlock) {
        int matching = 0;
        for (BlockPos scanned : BlockPos.betweenClosed(worldPosition.offset(-2, -4, -2), worldPosition.offset(2, 0, 2))) {
            if (!scanned.equals(worldPosition) && level.getBlockState(scanned).is(sampledBlock.getBlock())) {
                matching++;
            }
        }
        return matching / (double) SCANNED_NEIGHBORS;
    }
}
