package lach_01298.qmd.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/** Gas discharge lamp; redstone suppresses its light, retaining the original QMD wiring behavior. */
public final class DischargeLampBlock extends Block {

    public static final MapCodec<DischargeLampBlock> CODEC = simpleCodec(DischargeLampBlock::new);
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public DischargeLampBlock(Properties properties) {
        super(properties.lightLevel(state -> state.getValue(LIT) ? 15 : 0));
        registerDefaultState(
            stateDefinition.any()
                .setValue(LIT, true));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(
            LIT,
            !context.getLevel()
                .hasNeighborSignal(context.getClickedPos()));
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, BlockPos neighborPos,
        boolean moved) {
        if (!level.isClientSide) {
            boolean lit = !level.hasNeighborSignal(pos);
            if (lit != state.getValue(LIT)) level.setBlock(pos, state.setValue(LIT, lit), 3);
        }
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState previous, boolean moved) {
        super.onPlace(state, level, pos, previous, moved);
        if (!previous.is(this)) neighborChanged(state, level, pos, this, pos, moved);
    }
}
