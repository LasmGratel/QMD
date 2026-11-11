package lach_01298.qmd.block.entity.chamber;

import lach_01298.qmd.block.menu.chamber.NucleosynthesisChamberMenu;
import lach_01298.qmd.multiblock.chamber.AbstractVacuumChamberState;
import lach_01298.qmd.multiblock.chamber.NucleosynthesisChamberState;
import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;

public final class NucleosynthesisChamberBlockEntity extends AbstractVacuumChamberBlockEntity {

    public NucleosynthesisChamberBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    protected AbstractVacuumChamberState createVacuumState() {
        return new NucleosynthesisChamberState(this);
    }

    @Override
    protected boolean acceptsFluid(int index, FluidStack stack) {
        if (level == null) return true;
        if (index < 2) return acceptsCoolant(index, stack);
        if (index < 4)
            return acceptsRecipeFluid(QMDRecipes.VACUUM_CHAMBER_HEATING_TYPE.get(), 0, index == 2, false, stack);
        return acceptsRecipeFluid(
            QMDRecipes.NUCLEOSYNTHESIS_CHAMBER_TYPE.get(),
            (index - 4) % 2,
            index < 6,
            index < 6,
            stack);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new NucleosynthesisChamberMenu(id, inventory, this);
    }
}
