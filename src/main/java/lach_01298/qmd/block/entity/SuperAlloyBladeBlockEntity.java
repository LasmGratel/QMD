package lach_01298.qmd.block.entity;

import com.nred.nuclearcraft.block_entity.turbine.TurbineRotorBladeEntity;
import lach_01298.qmd.registry.QMDBlockEntities;
import lach_01298.qmd.registry.QMDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/** Shares native rotor validation, rendering, expansion, efficiency and failure handling. */
public final class SuperAlloyBladeBlockEntity extends TurbineRotorBladeEntity {

    public SuperAlloyBladeBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state, QMDBlocks.SUPER_ALLOY_BLADE_TYPE);
    }

    @Override
    public BlockEntityType<?> getType() {
        return QMDBlockEntities.SUPER_ALLOY_TURBINE_BLADE.get();
    }
}
