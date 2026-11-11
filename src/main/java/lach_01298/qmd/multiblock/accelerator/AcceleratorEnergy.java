package lach_01298.qmd.multiblock.accelerator;

import net.neoforged.neoforge.energy.IEnergyStorage;

/** FE's int transfer surface over a long-capacity multiblock store. */
public final class AcceleratorEnergy implements IEnergyStorage {

    public long stored, capacity = 40000;

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = (int) Math.min(Math.max(0, maxReceive), Math.max(0, capacity - stored));
        if (!simulate) stored += received;
        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    public boolean use(long amount) {
        if (amount < 0 || stored < amount) return false;
        stored -= amount;
        return true;
    }

    @Override
    public int getEnergyStored() {
        return (int) Math.min(Integer.MAX_VALUE, stored);
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) Math.min(Integer.MAX_VALUE, capacity);
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
    }
}
