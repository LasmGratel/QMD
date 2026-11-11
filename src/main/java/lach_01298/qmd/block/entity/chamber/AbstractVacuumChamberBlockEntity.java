package lach_01298.qmd.block.entity.chamber;

import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

/** Shared coolant filtering and vacuum-state tick delegation. */
public abstract class AbstractVacuumChamberBlockEntity extends AbstractChamberBlockEntity {

    protected AbstractVacuumChamberBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    protected final boolean acceptsCoolant(int index, FluidStack stack) {
        return level.getRecipeManager()
            .getAllRecipesFor(QMDRecipes.ACCELERATOR_COOLING_TYPE.get())
            .stream()
            .anyMatch(
                holder -> index == 0 ? FluidStack.isSameFluidSameComponents(
                    holder.value()
                        .fluid(),
                    stack)
                    : holder.value()
                        .fluidProducts()
                        .stream()
                        .anyMatch(product -> product.test(stack)));
    }

    @Override
    public final void processTick(List<AbstractChamberBlockEntity> inputs, List<AbstractChamberBlockEntity> outputs,
        int length) {
        if (level == null || level.isClientSide) return;
        vacuum().tick(inputs);
    }
}
