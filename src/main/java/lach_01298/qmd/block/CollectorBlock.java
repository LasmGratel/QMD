package lach_01298.qmd.block;

import com.nred.nuclearcraft.block.tile.BlockTile;
import com.nred.nuclearcraft.util.BlockHelper;
import javax.annotation.Nullable;
import lach_01298.qmd.block.entity.AtmosphereCollectorBlockEntity;
import lach_01298.qmd.block.entity.FluidCollectorBlockEntity;
import lach_01298.qmd.block.entity.LiquidCollectorBlockEntity;
import lach_01298.qmd.recipe.CollectorRecipe;
import lach_01298.qmd.registry.QMDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;

/** Legacy collectors are nondirectional and use bucket transfer plus an empty-hand status message. */
public final class CollectorBlock extends BlockTile {

    private final CollectorRecipe.Kind kind;

    public CollectorBlock(CollectorRecipe.Kind kind) {
        super(
            properties -> properties.mapColor(MapColor.METAL)
                .sound(SoundType.METAL));
        this.kind = kind;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return kind == CollectorRecipe.Kind.LIQUID ? new LiquidCollectorBlockEntity(pos, state)
            : new AtmosphereCollectorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
        BlockEntityType<T> type) {
        BlockEntityType<?> expected = kind == CollectorRecipe.Kind.LIQUID ? QMDBlockEntities.LIQUID_COLLECTOR.get()
            : QMDBlockEntities.ATMOSPHERE_COLLECTOR.get();
        return level.isClientSide() || type != expected ? null : (tickLevel, pos, tickState, tile) -> {
            if (tile instanceof FluidCollectorBlockEntity collector) {
                collector.serverTick();
            }
        };
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
        Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (stack.isEmpty()) {
            showCollectionStatus(level, pos, player);
            return ItemInteractionResult.SUCCESS;
        }
        if (player.isCrouching()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!(level.getBlockEntity(pos) instanceof FluidCollectorBlockEntity collector)
            || FluidUtil.getFluidHandler(stack)
                .isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.isClientSide()) {
            return ItemInteractionResult.SUCCESS;
        }
        if (BlockHelper.accessTanks(player, hand, hit.getDirection(), collector)) {
            collector.setChanged();
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
        BlockHitResult hit) {
        if (!player.getMainHandItem()
            .isEmpty()) {
            return InteractionResult.PASS;
        }
        showCollectionStatus(level, pos, player);
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private void showCollectionStatus(Level level, BlockPos pos, Player player) {
        if (level.isClientSide() || !(level.getBlockEntity(pos) instanceof FluidCollectorBlockEntity collector)) {
            return;
        }
        FluidStack fluid = collector.getCollectionFluid();
        player.sendSystemMessage(
            fluid.isEmpty() ? Component.translatable("message.qmd.collector_no_fluid")
                : Component.translatable(
                    "message.qmd.collector",
                    fluid.getHoverName(),
                    collector.getCollectionRate() + " mB/t"));
    }
}
