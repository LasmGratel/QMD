package lach_01298.qmd.block.entity.liquefier;

import com.nred.nuclearcraft.block_entity.hx.AbstractHeatExchangerEntity;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.PartPosition;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import lach_01298.qmd.config.LiquefierConfig;
import lach_01298.qmd.registry.QMDBlockEntities;
import lach_01298.qmd.registry.QMDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public final class LiquefierCompressorBlockEntity extends AbstractHeatExchangerEntity {

    private final int material;

    public LiquefierCompressorBlockEntity(BlockPos pos, BlockState state) {
        super(QMDBlockEntities.LIQUEFIER_COMPRESSOR.get(), pos, state);
        material = state.is(QMDBlocks.LIQUEFIER_NEODYMIUM_COMPRESSOR.get()) ? 1
            : state.is(QMDBlocks.LIQUEFIER_SAMARIUM_COBALT_COMPRESSOR.get()) ? 2 : 0;
    }

    public double energyEfficiency() {
        return LiquefierConfig.ENERGY_EFFICIENCY[material].get();
    }

    public double heatEfficiency() {
        return LiquefierConfig.HEAT_EFFICIENCY[material].get();
    }

    @Override
    public boolean isGoodForPosition(PartPosition position, IMultiblockValidator validator) {
        return position.isFace();
    }
}
