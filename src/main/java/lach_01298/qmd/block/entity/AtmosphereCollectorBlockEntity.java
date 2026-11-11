package lach_01298.qmd.block.entity;

import lach_01298.qmd.recipe.CollectorRecipe;
import lach_01298.qmd.registry.QMDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.EndRodBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.common.Tags;

/** Counts the original volume above the machine; transparent solids still restrict collection. */
public final class AtmosphereCollectorBlockEntity extends FluidCollectorBlockEntity {

    public AtmosphereCollectorBlockEntity(BlockPos pos, BlockState state) {
        super(QMDBlockEntities.ATMOSPHERE_COLLECTOR.get(), pos, state, CollectorRecipe.Kind.ATMOSPHERE);
    }

    @Override
    protected double scanEfficiency(BlockState sampledBlock) {
        int occlusive = 0;
        for (BlockPos scanned : BlockPos
            .betweenClosed(worldPosition.offset(-2, 0, -2), worldPosition.offset(2, 4, 2))) {
            if (!scanned.equals(worldPosition) && isOcclusive(level.getBlockState(scanned), scanned)) {
                occlusive++;
            }
        }
        return 1D - occlusive / (double) SCANNED_NEIGHBORS;
    }

    public boolean isOcclusive(BlockState state, BlockPos pos) {
        // MaterialHelper.isEmpty formerly ignored replaceable and foliage materials. Modern block
        // map colors no longer identify all those materials (notably gourds and chorus plants).
        if (state.isAir() || state.canBeReplaced()
            || state.is(BlockTags.LEAVES)
            || state.getMapColor(level, pos) == MapColor.PLANT
            || state.getBlock() instanceof BushBlock
            || state.getBlock() instanceof CocoaBlock
            || state.is(Blocks.PUMPKIN)
            || state.is(Blocks.CARVED_PUMPKIN)
            || state.is(Blocks.JACK_O_LANTERN)
            || state.is(Blocks.MELON)
            || state.is(Blocks.DRAGON_EGG)
            || state.is(Blocks.CHORUS_PLANT)
            || state.is(Blocks.CHORUS_FLOWER)
            || state.is(Blocks.STRUCTURE_VOID)) {
            return false;
        }
        if (state.isSolidRender(level, pos)) {
            return true;
        }
        // Modern noOcclusion is a rendering choice, not the old material's opacity. Wood/iron
        // doors, ladders and plates remained opaque-material openings even with noOcclusion.
        if (state.is(BlockTags.DOORS) || state.is(BlockTags.TRAPDOORS)
            || state.getBlock() instanceof LadderBlock
            || state.getBlock() instanceof BasePressurePlateBlock
            || state.getBlock() instanceof MovingPistonBlock
            || state.getBlock() instanceof CakeBlock) {
            return false;
        }
        // The legacy solid-shape property distinguishes circuit-like nonblocking materials from
        // partial wood/stone/iron shapes; transparent glass/ice and end rods need explicit parity.
        return !state.blocksMotion() || state.is(Tags.Blocks.GLASS_BLOCKS)
            || state.is(Tags.Blocks.GLASS_PANES)
            || state.is(Blocks.ICE)
            || state.is(Blocks.FROSTED_ICE)
            || state.getBlock() instanceof EndRodBlock;
    }
}
