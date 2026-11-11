package lach_01298.qmd.block.chamber;

import lach_01298.qmd.block.entity.chamber.NucleosynthesisChamberBlockEntity;
import lach_01298.qmd.multiblock.ChamberPart;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class NucleosynthesisChamberBlock extends AbstractChamberBlock {

    public NucleosynthesisChamberBlock() {
        this(defaultProperties(ChamberPart.NUCLEOSYNTHESIS_CONTROLLER));
    }

    NucleosynthesisChamberBlock(Properties properties) {
        super(ChamberPart.NUCLEOSYNTHESIS_CONTROLLER, properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new NucleosynthesisChamberBlockEntity(pos, state);
    }
}
