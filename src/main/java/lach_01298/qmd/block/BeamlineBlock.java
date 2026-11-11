package lach_01298.qmd.block;

import com.mojang.serialization.MapCodec;
import lach_01298.qmd.block.entity.BeamlineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** A straight beam conduit, retaining the original ten-pixel bore and three placement axes. */
public final class BeamlineBlock extends BaseEntityBlock {

    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    public static final MapCodec<BeamlineBlock> CODEC = simpleCodec(BeamlineBlock::new);
    private static final VoxelShape X = Block.box(0, 3, 3, 16, 13, 13), Y = Block.box(3, 0, 3, 13, 16, 13),
        Z = Block.box(3, 3, 0, 13, 13, 16);

    public BeamlineBlock() {
        this(
            Properties.of()
                .strength(3, 10)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops()
                .noOcclusion());
    }

    public BeamlineBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(AXIS, Direction.Axis.X));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        b.add(AXIS);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BeamlineBlockEntity(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction.Axis axis = context.getNearestLookingDirection()
            .getAxis();
        for (Direction d : Direction.values()) {
            BlockPos neighbor = context.getClickedPos()
                .relative(d);
            if (!context.getLevel()
                .hasChunkAt(neighbor)) continue;
            BlockState state = context.getLevel()
                .getBlockState(neighbor);
            if (state.getBlock() instanceof BeamlineBlock && state.getValue(AXIS) == d.getAxis()) {
                axis = d.getAxis();
                break;
            }
        }
        return defaultBlockState().setValue(AXIS, axis);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(AXIS)) {
            case X -> X;
            case Y -> Y;
            case Z -> Z;
        };
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
        BlockHitResult hit) {
        if (!player.isCrouching()) return InteractionResult.PASS;
        for (Direction d : Direction.values())
            if (level.hasChunkAt(pos.relative(d)) && level.getBlockState(pos.relative(d))
                .getBlock() instanceof BeamlineBlock) return InteractionResult.PASS;
        if (!level.isClientSide) level.setBlock(pos, state.cycle(AXIS), Block.UPDATE_ALL);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
