package lach_01298.qmd.block.entity.liquefier;

import com.nred.nuclearcraft.block_entity.hx.AbstractHeatExchangerEntity;
import com.nred.nuclearcraft.multiblock.hx.HeatExchangerTubeNetwork;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.PartPosition;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import lach_01298.qmd.registry.QMDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public final class LiquefierNozzleBlockEntity extends AbstractHeatExchangerEntity {

    public HeatExchangerTubeNetwork network;

    public LiquefierNozzleBlockEntity(BlockPos pos, BlockState state) {
        super(QMDBlockEntities.LIQUEFIER_NOZZLE.get(), pos, state);
    }

    @Override
    public boolean isGoodForPosition(PartPosition position, IMultiblockValidator validator) {
        return position == PartPosition.Interior;
    }
}
