package lach_01298.qmd.block;

import com.nred.nuclearcraft.block.fluid.LiquidFluidBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

public final class ExoticFluidBlock extends LiquidFluidBlock {

    public ExoticFluidBlock(FlowingFluid fluid, Properties properties) {
        super(fluid, properties);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide) {
            entity.hurt(
                level.damageSources()
                    .source(lach_01298.qmd.registry.QMDDamageTypes.ANNIHILATION),
                10000F);
            level.explode(
                null,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                10F,
                Level.ExplosionInteraction.BLOCK);
        }
    }
}
