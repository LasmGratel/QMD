package lach_01298.qmd.block.entity;

import com.nred.nuclearcraft.block_entity.fission.FissionShieldEntity;
import lach_01298.qmd.registry.QMDBlockEntities;
import lach_01298.qmd.registry.QMDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/** Uses Neohaul's full moderator-line, flux, heat-cluster and shield-manager behavior. */
public final class HafniumShieldBlockEntity extends FissionShieldEntity {

    public HafniumShieldBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state, QMDBlocks.HAFNIUM_SHIELD_TYPE);
    }

    @Override
    public BlockEntityType<?> getType() {
        return QMDBlockEntities.HAFNIUM_SHIELD.get();
    }
}
