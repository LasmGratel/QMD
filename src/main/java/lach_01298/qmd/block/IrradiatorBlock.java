package lach_01298.qmd.block;

import com.mojang.serialization.MapCodec;
import lach_01298.qmd.block.entity.IrradiatorBlockEntity;
import lach_01298.qmd.registry.QMDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

public final class IrradiatorBlock extends BaseEntityBlock {

    public static final MapCodec<IrradiatorBlock> CODEC = simpleCodec(IrradiatorBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public IrradiatorBlock(Properties properties) {
        super(properties);
        registerDefaultState(
            stateDefinition.any()
                .setValue(FACING, net.minecraft.core.Direction.NORTH)
                .setValue(ACTIVE, false));
    }

    public IrradiatorBlock() {
        this(
            Properties.of()
                .mapColor(MapColor.METAL)
                .sound(SoundType.METAL)
                .strength(4F, 30F)
                .requiresCorrectToolForDrops());
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ACTIVE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(
            FACING,
            context.getHorizontalDirection()
                .getOpposite());
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new IrradiatorBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
        BlockEntityType<T> type) {
        return level.isClientSide ? null
            : createTickerHelper(
                type,
                QMDBlockEntities.IRRADIATOR.get(),
                (world, pos, current, tile) -> tile.serverTick());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
        BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof IrradiatorBlockEntity tile)
            player.openMenu(tile, buffer -> buffer.writeBlockPos(pos));
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState next, boolean moved) {
        if (!state.is(next.getBlock()) && level.getBlockEntity(pos) instanceof IrradiatorBlockEntity tile) {
            for (int i = 0; i < 3; i++) Containers.dropItemStack(
                level,
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                tile.inventory()
                    .getStackInSlot(i));
            level.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, level, pos, next, moved);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof IrradiatorBlockEntity tile)) return 0;
        double fraction = 0;
        for (int i = 0; i < 3; i++) {
            var stack = tile.inventory()
                .getStackInSlot(i);
            if (!stack.isEmpty()) fraction += (double) stack.getCount() / Math.min(
                stack.getMaxStackSize(),
                tile.inventory()
                    .getSlotLimit(i));
        }
        return fraction <= 0 ? 0 : 1 + (int) Math.floor(fraction / 3 * 14);
    }
}
