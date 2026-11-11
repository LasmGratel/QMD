package lach_01298.qmd.block.entity.liquefier;

import com.nred.nuclearcraft.block_entity.hx.AbstractHeatExchangerEntity;
import com.nred.nuclearcraft.block_entity.internal.fluid.Tank;
import com.nred.nuclearcraft.multiblock.hx.HeatExchanger;
import lach_01298.qmd.multiblock.liquefier.LiquefierLogic;
import lach_01298.qmd.registry.QMDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;

import static com.nred.nuclearcraft.registration.BlockRegistration.ACTIVE;
import static com.nred.nuclearcraft.registration.BlockRegistration.FACING_ALL;

/** Input and output modes expose the shared gas and liquid tanks on every side. */
public final class LiquefierFluidPortBlockEntity extends AbstractHeatExchangerEntity {

    private final IFluidHandler handler = new IFluidHandler() {

        private List<Tank> tanks() {
            return isMachineAssembled() && getLogic() instanceof LiquefierLogic logic ? logic.gasTanks : List.of();
        }

        @Override
        public int getTanks() {
            return 2;
        }

        @Override
        public FluidStack getFluidInTank(int index) {
            return index < 0 || index >= 2 || tanks().isEmpty() ? FluidStack.EMPTY
                : tanks().get(index)
                    .getFluid()
                    .copy();
        }

        @Override
        public int getTankCapacity(int index) {
            return index < 0 || index >= 2 || tanks().isEmpty() ? 0
                : tanks().get(index)
                    .getCapacity();
        }

        @Override
        public boolean isFluidValid(int index, FluidStack fluid) {
            return index == 0 && !isOutput()
                && !tanks().isEmpty()
                && tanks().getFirst()
                    .isFluidValid(fluid);
        }

        @Override
        public int fill(FluidStack fluid, FluidAction action) {
            if (isOutput() || tanks().isEmpty()) return 0;
            int filled = tanks().getFirst()
                .fill(fluid, action);
            if (action.execute() && filled > 0) markTileDirty();
            return filled;
        }

        @Override
        public FluidStack drain(FluidStack fluid, FluidAction action) {
            return !isOutput() || tanks().isEmpty() ? FluidStack.EMPTY
                : dirtyDrain(
                    tanks().get(1)
                        .drain(fluid, action),
                    action);
        }

        @Override
        public FluidStack drain(int amount, FluidAction action) {
            return !isOutput() || tanks().isEmpty() ? FluidStack.EMPTY
                : dirtyDrain(
                    tanks().get(1)
                        .drain(amount, action),
                    action);
        }

        private FluidStack dirtyDrain(FluidStack fluid, FluidAction action) {
            if (action.execute() && !fluid.isEmpty()) markTileDirty();
            return fluid;
        }
    };

    public LiquefierFluidPortBlockEntity(BlockPos pos, BlockState state) {
        super(QMDBlockEntities.LIQUEFIER_FLUID_PORT.get(), pos, state);
    }

    public boolean isOutput() {
        return getBlockState().getValue(ACTIVE);
    }

    public IFluidHandler fluidHandler() {
        return handler;
    }

    @Override
    public void onPreMachineAssembled(HeatExchanger controller) {
        super.onPreMachineAssembled(controller);
        if (!level.isClientSide) getPartPosition().getDirection()
            .ifPresent(dir -> level.setBlock(worldPosition, getBlockState().setValue(FACING_ALL, dir), 2));
    }

    @Override
    public boolean onUseMultitool(ItemStack tool, ServerPlayer player, Level level, Direction facing, BlockPos hitPos) {
        if (!player.isCrouching()) {
            setActivity(!isOutput());
            markDirtyAndNotify(true);
            getMultiblockController().ifPresent(HeatExchanger::checkIfMachineIsWhole);
            player.sendSystemMessage(
                Component.translatable(isOutput() ? "gui.qmd.liquefier.port_output" : "gui.qmd.liquefier.port_input"));
            return true;
        }
        return super.onUseMultitool(tool, player, level, facing, hitPos);
    }

    public void serverTick() {
        if (level == null || level.isClientSide || !isMachineAssembled() || !isOutput()) return;
        Direction facing = getPartPosition().getDirection()
            .orElse(null);
        if (facing == null || !level.hasChunkAt(worldPosition.relative(facing))) return;
        IFluidHandler adjacent = level
            .getCapability(Capabilities.FluidHandler.BLOCK, worldPosition.relative(facing), facing.getOpposite());
        if (adjacent == null) return;
        FluidStack offered = handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
        if (!offered.isEmpty())
            handler.drain(adjacent.fill(offered, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
    }
}
