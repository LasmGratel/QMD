package lach_01298.qmd.block.entity.liquefier;

import com.nred.nuclearcraft.block_entity.hx.AbstractHeatExchangerEntity;
import lach_01298.qmd.multiblock.liquefier.LiquefierLogic;
import lach_01298.qmd.registry.QMDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;

public final class LiquefierEnergyPortBlockEntity extends AbstractHeatExchangerEntity {

    private final IEnergyStorage input = new IEnergyStorage() {

        private LiquefierLogic logic() {
            return isMachineAssembled() && getLogic() instanceof LiquefierLogic logic ? logic : null;
        }

        @Override
        public int receiveEnergy(int amount, boolean simulate) {
            LiquefierLogic logic = logic();
            if (logic == null) return 0;
            int received = logic.energyStorage.receiveEnergy(amount, simulate);
            if (!simulate && received > 0) markTileDirty();
            return received;
        }

        @Override
        public int extractEnergy(int amount, boolean simulate) {
            return 0;
        }

        @Override
        public int getEnergyStored() {
            return logic() == null ? 0 : logic().energyStorage.getEnergyStored();
        }

        @Override
        public int getMaxEnergyStored() {
            return logic() == null ? 0 : logic().energyStorage.getMaxEnergyStored();
        }

        @Override
        public boolean canExtract() {
            return false;
        }

        @Override
        public boolean canReceive() {
            return logic() != null;
        }
    };

    public LiquefierEnergyPortBlockEntity(BlockPos pos, BlockState state) {
        super(QMDBlockEntities.LIQUEFIER_ENERGY_PORT.get(), pos, state);
    }

    public IEnergyStorage energyInput() {
        return input;
    }
}
