package lach_01298.qmd.block.chamber;

import lach_01298.qmd.block.entity.chamber.DecayChamberBlockEntity;
import lach_01298.qmd.multiblock.ChamberPart;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class DecayChamberBlock extends AbstractChamberBlock {

    public DecayChamberBlock() {
        this(defaultProperties(ChamberPart.DECAY_CONTROLLER));
    }

    DecayChamberBlock(Properties properties) {
        super(ChamberPart.DECAY_CONTROLLER, properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DecayChamberBlockEntity(pos, state);
    }
}
