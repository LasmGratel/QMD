package lach_01298.qmd.block.chamber;

import lach_01298.qmd.block.entity.chamber.NeutralContainmentBlockEntity;
import lach_01298.qmd.multiblock.ChamberPart;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class NeutralContainmentBlock extends AbstractChamberBlock {

    public NeutralContainmentBlock() {
        this(defaultProperties(ChamberPart.CONTAINMENT_CONTROLLER));
    }

    NeutralContainmentBlock(Properties properties) {
        super(ChamberPart.CONTAINMENT_CONTROLLER, properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new NeutralContainmentBlockEntity(pos, state);
    }
}
