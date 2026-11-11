package lach_01298.qmd.block.chamber;

import lach_01298.qmd.block.entity.chamber.BeamDumpBlockEntity;
import lach_01298.qmd.multiblock.ChamberPart;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class BeamDumpBlock extends AbstractChamberBlock {

    public BeamDumpBlock() {
        this(defaultProperties(ChamberPart.BEAM_DUMP_CONTROLLER));
    }

    BeamDumpBlock(Properties properties) {
        super(ChamberPart.BEAM_DUMP_CONTROLLER, properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BeamDumpBlockEntity(pos, state);
    }
}
