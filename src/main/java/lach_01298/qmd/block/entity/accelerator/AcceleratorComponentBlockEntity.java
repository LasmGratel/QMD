package lach_01298.qmd.block.entity.accelerator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/** Shared casing, beamline, magnet, cooler and configurable port storage. */
public final class AcceleratorComponentBlockEntity extends AbstractAcceleratorBlockEntity {

    public AcceleratorComponentBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }
}
