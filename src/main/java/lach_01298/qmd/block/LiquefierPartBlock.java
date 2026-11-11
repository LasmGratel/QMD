package lach_01298.qmd.block;

import com.nred.nuclearcraft.block.GenericDirectionalTooltipDeviceBlock;
import com.nred.nuclearcraft.block.IActivatable;
import com.nred.nuclearcraft.multiblock.hx.HeatExchanger;
import com.nred.nuclearcraft.multiblock.hx.IHeatExchangerPartType;
import lach_01298.qmd.block.entity.liquefier.LiquefierControllerBlockEntity;
import lach_01298.qmd.block.entity.liquefier.LiquefierFluidPortBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidUtil;
import static com.nred.nuclearcraft.registration.BlockRegistration.ACTIVE;
import static com.nred.nuclearcraft.registration.BlockRegistration.FACING_ALL;

/** All parts participate in Neohaul's existing HX controller and assembly lifecycle. */
public final class LiquefierPartBlock
    extends GenericDirectionalTooltipDeviceBlock<HeatExchanger, IHeatExchangerPartType> implements IActivatable {

    public LiquefierPartBlock(MultiblockPartProperties<IHeatExchangerPartType> properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING_ALL, ACTIVE);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos,
        net.minecraft.core.Direction direction) {
        return true;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
        BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof LiquefierControllerBlockEntity controller
            && controller.isMachineAssembled()) {
            if (player instanceof ServerPlayer serverPlayer)
                serverPlayer.openMenu(controller, buf -> buf.writeBlockPos(pos));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useWithoutItem(state, level, pos, player, hit);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
        Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof LiquefierFluidPortBlockEntity
            && FluidUtil.interactWithFluidHandler(player, hand, level, pos, hit.getDirection()))
            return ItemInteractionResult.SUCCESS;
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
        BlockEntityType<T> type) {
        return level.isClientSide ? null
            : (world, pos, blockState, entity) -> {
                if (entity instanceof LiquefierFluidPortBlockEntity port) port.serverTick();
            };
    }
}
