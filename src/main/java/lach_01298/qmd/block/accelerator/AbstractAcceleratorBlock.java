package lach_01298.qmd.block.accelerator;

import com.nred.nuclearcraft.block.GenericTooltipDeviceBlock;
import lach_01298.qmd.block.entity.accelerator.AbstractAcceleratorBlockEntity;
import lach_01298.qmd.multiblock.accelerator.Accelerator;
import lach_01298.qmd.multiblock.accelerator.AcceleratorPartType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidUtil;

public abstract class AbstractAcceleratorBlock extends GenericTooltipDeviceBlock<Accelerator, AcceleratorPartType> {

    public static final DirectionProperty FACING = DirectionProperty.create("facing");
    public static final BooleanProperty OUTPUT = BooleanProperty.create("output");
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    private final boolean transparent;

    protected AbstractAcceleratorBlock(MultiblockPartProperties<AcceleratorPartType> properties) {
        this(properties, false);
    }

    protected AbstractAcceleratorBlock(MultiblockPartProperties<AcceleratorPartType> properties, boolean transparent) {
        super(properties);
        this.transparent = transparent;
        registerDefaultState(
            defaultBlockState().setValue(FACING, Direction.NORTH)
                .setValue(OUTPUT, false)
                .setValue(ACTIVE, false));
    }

    public static AbstractAcceleratorBlock create(MultiblockPartProperties<AcceleratorPartType> properties,
        AcceleratorPartType type) {
        if (type.role == AcceleratorPartType.Role.SOURCE) return new AcceleratorSourceBlock(properties);
        if (type.role != AcceleratorPartType.Role.CONTROLLER)
            return new AcceleratorComponentBlock(properties, type.role == AcceleratorPartType.Role.GLASS);
        return switch (type.kind) {
            case LINEAR -> new LinearAcceleratorBlock(properties);
            case RING -> new RingAcceleratorBlock(properties);
            case DECELERATOR -> new DeceleratorBlock(properties);
            case DIVERTER -> new BeamDiverterBlock(properties);
            case SPLITTER -> new BeamSplitterBlock(properties);
            case SPECTROMETER -> new MassSpectrometerBlock(properties);
        };
    }

    public AcceleratorPartType partType() {
        return lach_01298.qmd.registry.QMDBlocks.acceleratorTypeOf(this);
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacent, Direction side) {
        return transparent && adjacent.is(this) || super.skipRendering(state, adjacent, side);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OUTPUT, ACTIVE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(
            FACING,
            context.getNearestLookingDirection()
                .getOpposite());
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacement, boolean moving) {
        if (!state.is(replacement.getBlock()) && !level.isClientSide
            && level.getBlockEntity(pos) instanceof AbstractAcceleratorBlockEntity part) {
            for (int slot = 0; slot < part.inventory.getSlots(); slot++) {
                ItemStack stack = part.inventory.getStackInSlot(slot);
                if (!stack.isEmpty()) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack.copy());
                    part.inventory.setStackInSlot(slot, ItemStack.EMPTY);
                }
            }
        }
        super.onRemove(state, level, pos, replacement, moving);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
        Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (level.getBlockEntity(pos) instanceof AbstractAcceleratorBlockEntity part) {
            if (FluidUtil.getFluidHandler(stack)
                .isPresent() && FluidUtil.interactWithFluidHandler(player, hand, level, pos, hit.getDirection()))
                return ItemInteractionResult.SUCCESS;
            if (player.isCrouching() && (part.partType().role == AcceleratorPartType.Role.BEAM_PORT
                || part.partType().role == AcceleratorPartType.Role.REDSTONE_PORT)) {
                if (!level.isClientSide) {
                    level.setBlock(pos, state.cycle(OUTPUT), Block.UPDATE_ALL);
                    part.invalidateCapabilities();
                    part.setRedstoneLevel(0);
                    if (part.controller() != null) part.controller()
                        .checkIfMachineIsWhole();
                }
                return ItemInteractionResult.SUCCESS;
            }
            if (!player.isCrouching()) {
                if (!level.isClientSide) player.openMenu(part, part::writeMenuData);
                return ItemInteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
        BlockHitResult hit) {
        if (player.isCrouching() && level.getBlockEntity(pos) instanceof AbstractAcceleratorBlockEntity part
            && (part.partType().role == AcceleratorPartType.Role.BEAM_PORT
                || part.partType().role == AcceleratorPartType.Role.REDSTONE_PORT)) {
            if (!level.isClientSide) {
                level.setBlock(pos, state.cycle(OUTPUT), Block.UPDATE_ALL);
                part.invalidateCapabilities();
                part.setRedstoneLevel(0);
                if (part.controller() != null) part.controller()
                    .checkIfMachineIsWhole();
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (level.getBlockEntity(pos) instanceof AbstractAcceleratorBlockEntity part && !player.isCrouching()) {
            if (!level.isClientSide) player.openMenu(part, part::writeMenuData);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useWithoutItem(state, level, pos, player, hit);
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return state.getValue(OUTPUT);
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
        return level.getBlockEntity(pos) instanceof AbstractAcceleratorBlockEntity part ? part.redstoneLevel() : 0;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return state.getValue(OUTPUT);
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return getSignal(state, level, pos, Direction.UP);
    }
}
