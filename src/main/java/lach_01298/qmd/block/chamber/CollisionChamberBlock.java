package lach_01298.qmd.block.chamber;

import lach_01298.qmd.block.entity.chamber.CollisionChamberBlockEntity;
import lach_01298.qmd.multiblock.ChamberPart;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class CollisionChamberBlock extends AbstractChamberBlock {

    public CollisionChamberBlock() {
        this(defaultProperties(ChamberPart.COLLISION_CONTROLLER));
    }

    CollisionChamberBlock(Properties properties) {
        super(ChamberPart.COLLISION_CONTROLLER, properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CollisionChamberBlockEntity(pos, state);
    }
}
