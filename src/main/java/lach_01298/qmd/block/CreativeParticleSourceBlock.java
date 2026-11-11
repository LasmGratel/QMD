package lach_01298.qmd.block;

import com.mojang.serialization.MapCodec;
import lach_01298.qmd.block.entity.CreativeParticleSourceBlockEntity;
import lach_01298.qmd.registry.QMDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public final class CreativeParticleSourceBlock extends BaseEntityBlock {

    public static final MapCodec<CreativeParticleSourceBlock> CODEC = simpleCodec(CreativeParticleSourceBlock::new);

    public CreativeParticleSourceBlock(Properties properties) {
        super(properties);
    }

    public CreativeParticleSourceBlock() {
        this(
            Properties.of()
                .mapColor(MapColor.METAL)
                .sound(SoundType.METAL)
                .strength(-1.0F, 6000000.0F)
                .noLootTable());
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
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CreativeParticleSourceBlockEntity(pos, state);
    }

    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
        return false;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
        BlockEntityType<T> type) {
        return level.isClientSide ? null
            : createTickerHelper(
                type,
                QMDBlockEntities.CREATIVE_PARTICLE_SOURCE.get(),
                (tickLevel, pos, tickState, tile) -> tile.serverTick());
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
        Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND || !canOpen(player))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        open(level, pos, player);
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
        BlockHitResult hit) {
        if (!canOpen(player)) return InteractionResult.PASS;
        open(level, pos, player);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static boolean canOpen(Player player) {
        return player.isCreative() && !player.isCrouching();
    }

    private static void open(Level level, BlockPos pos, Player player) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof CreativeParticleSourceBlockEntity source) {
            player.openMenu(source, source::writeMenuData);
        }
    }
}
