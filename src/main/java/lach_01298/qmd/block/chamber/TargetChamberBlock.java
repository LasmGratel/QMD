package lach_01298.qmd.block.chamber;

import lach_01298.qmd.block.entity.chamber.TargetChamberBlockEntity;
import lach_01298.qmd.multiblock.ChamberPart;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class TargetChamberBlock extends AbstractChamberBlock {

    public TargetChamberBlock() {
        this(defaultProperties(ChamberPart.TARGET_CONTROLLER));
    }

    TargetChamberBlock(Properties properties) {
        super(ChamberPart.TARGET_CONTROLLER, properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TargetChamberBlockEntity(pos, state);
    }
}
