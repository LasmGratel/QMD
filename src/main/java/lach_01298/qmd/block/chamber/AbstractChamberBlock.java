package lach_01298.qmd.block.chamber;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lach_01298.qmd.block.entity.chamber.AbstractChamberBlockEntity;
import lach_01298.qmd.multiblock.ChamberPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

public abstract class AbstractChamberBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final MapCodec<AbstractChamberBlock> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            com.mojang.serialization.Codec.STRING.fieldOf("role")
                .forGetter(block -> block.role.name()),
            propertiesCodec())
            .apply(instance, (role, properties) -> create(ChamberPart.valueOf(role), properties)));
    private final ChamberPart role;

    public static AbstractChamberBlock create(ChamberPart role) {
        return create(role, defaultProperties(role));
    }

    private static AbstractChamberBlock create(ChamberPart role, Properties properties) {
        return switch (role) {
            case TARGET_CONTROLLER -> new TargetChamberBlock(properties);
            case DECAY_CONTROLLER -> new DecayChamberBlock(properties);
            case COLLISION_CONTROLLER -> new CollisionChamberBlock(properties);
            case BEAM_DUMP_CONTROLLER -> new BeamDumpBlock(properties);
            case CONTAINMENT_CONTROLLER -> new NeutralContainmentBlock(properties);
            case NUCLEOSYNTHESIS_CONTROLLER -> new NucleosynthesisChamberBlock(properties);
            default -> new ChamberPartBlock(role, properties);
        };
    }

    protected static Properties defaultProperties(ChamberPart role) {
        return Properties.of()
            .mapColor(MapColor.METAL)
            .sound(SoundType.METAL)
            .strength(transparent(role) ? 1.5F : 3.5F, transparent(role) ? 10F : 30F)
            .requiresCorrectToolForDrops();
    }

    protected AbstractChamberBlock(ChamberPart role, Properties properties) {
        super(transparent(role) ? properties.noOcclusion() : properties);
        this.role = role;
        registerDefaultState(
            stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(ACTIVE, false));
    }

    private static boolean transparent(ChamberPart role) {
        return role == ChamberPart.GLASS || role == ChamberPart.VACUUM_GLASS || role == ChamberPart.PLASMA_GLASS;
    }

    public ChamberPart role() {
        return role;
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacent, Direction side) {
        return transparent(role) && adjacent.is(this) || super.skipRendering(state, adjacent, side);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(
        StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FACING, ACTIVE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(
            FACING,
            context.getNearestLookingDirection()
                .getOpposite());
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return role == ChamberPart.REDSTONE_PORT;
    }

    @Override
    protected int getSignal(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos,
        Direction side) {
        return level.getBlockEntity(pos) instanceof AbstractChamberBlockEntity part ? part.redstoneLevel() : 0;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return AbstractChamberBlockEntity.create(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
        BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof AbstractChamberBlockEntity part)) return InteractionResult.PASS;
        if (!level.isClientSide) {
            if (player.isCrouching()
                && (role.beamPort() || role.fluidPort() || role.itemPort() || role == ChamberPart.REDSTONE_PORT)) {
                part.togglePort();
            } else {
                AbstractChamberBlockEntity controller = part.controller();
                if (controller != null)
                    player.openMenu(controller, buffer -> buffer.writeBlockPos(controller.getBlockPos()));
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void onRemove(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (oldState.getBlock() != newState.getBlock()
            && level.getBlockEntity(pos) instanceof AbstractChamberBlockEntity part) part.dropInventory();
        super.onRemove(oldState, level, pos, newState, moving);
    }
}
