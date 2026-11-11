package lach_01298.qmd.particle;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;

/** The common bounded particle store. Energy limits are inclusive. */
public class ParticleStorage implements IParticleStorage, IParticleStackHandler {

    @Nullable
    protected ParticleStack particleStack;
    @Nullable
    protected BlockEntity tile;
    protected long maxEnergy;
    protected long minEnergy;
    protected int capacity;

    public ParticleStorage(@Nullable ParticleStack stack, long maxEnergy, int capacity, long minEnergy) {
        if (capacity < 0 || minEnergy < 0 || maxEnergy < minEnergy) {
            throw new IllegalArgumentException("Invalid particle storage limits");
        }
        this.maxEnergy = maxEnergy;
        this.minEnergy = minEnergy;
        this.capacity = capacity;
        setParticleStack(stack);
    }

    public ParticleStorage(@Nullable ParticleStack stack, long maxEnergy, int capacity) {
        this(stack, maxEnergy, capacity, 0);
    }

    public ParticleStorage(@Nullable ParticleStack stack, long maxEnergy) {
        this(stack, maxEnergy, Integer.MAX_VALUE, 0);
    }

    public ParticleStorage() {
        this(null, Long.MAX_VALUE, Integer.MAX_VALUE, 0);
    }

    public void readFromNBT(CompoundTag nbt) {
        long readMax = nbt.contains("maxEnergy", Tag.TAG_ANY_NUMERIC) ? nbt.getLong("maxEnergy") : maxEnergy;
        long readMin = nbt.contains("minEnergy", Tag.TAG_ANY_NUMERIC) ? nbt.getLong("minEnergy") : minEnergy;
        int readCapacity = nbt.contains("capacity", Tag.TAG_ANY_NUMERIC) ? nbt.getInt("capacity") : capacity;
        minEnergy = Math.max(0, readMin);
        maxEnergy = Math.max(minEnergy, readMax);
        capacity = Math.max(0, readCapacity);
        particleStack = ParticleStack.loadParticleStackFromNBT(nbt.getCompound("particle_stack"));
        changed();
    }

    public CompoundTag writeToNBT(CompoundTag nbt) {
        nbt.put(
            "particle_stack",
            particleStack == null ? new CompoundTag() : particleStack.writeToNBT(new CompoundTag()));
        nbt.putLong("maxEnergy", maxEnergy);
        nbt.putInt("capacity", capacity);
        nbt.putLong("minEnergy", minEnergy);
        return nbt;
    }

    public CompoundTag writeToNBT(CompoundTag nbt, int id) {
        nbt.put("ParticleStorage" + id, writeToNBT(new CompoundTag()));
        return nbt;
    }

    public void readFromNBT(CompoundTag nbt, int id) {
        if (nbt.contains("ParticleStorage" + id, Tag.TAG_COMPOUND))
            readFromNBT(nbt.getCompound("ParticleStorage" + id));
    }

    /** Direct machine-state assignment, independent of the insertion limits. */
    public void setParticleStack(@Nullable ParticleStack stack) {
        particleStack = stack == null || stack.isEmpty() ? null : stack.copy();
        changed();
    }

    public void setTileEntity(@Nullable BlockEntity tile) {
        this.tile = tile;
    }

    protected void changed() {
        if (tile != null) tile.setChanged();
    }

    @Override
    public ParticleStorageInfo getInfo() {
        return new ParticleStorageInfo(this);
    }

    @Override
    public boolean canReceiveParticle(@Nullable Direction side, @Nullable ParticleStack stack) {
        if (stack == null || stack.isEmpty() || stack.getMeanEnergy() < minEnergy || stack.getMeanEnergy() > maxEnergy)
            return false;
        if (particleStack == null || particleStack.isEmpty()) return stack.getAmount() <= capacity;
        return particleStack.matchesType(stack) && (long) particleStack.getAmount() + stack.getAmount() <= capacity;
    }

    @Override
    public boolean receiveParticle(@Nullable Direction side, @Nullable ParticleStack stack) {
        if (!canReceiveParticle(side, stack)) return false;
        if (particleStack == null || particleStack.isEmpty()) {
            particleStack = stack.copy();
        } else {
            // Multiplication may exceed long even when both mean energies are valid.
            BigInteger energy = BigInteger.valueOf(particleStack.getMeanEnergy())
                .multiply(BigInteger.valueOf(particleStack.getAmount()))
                .add(
                    BigInteger.valueOf(stack.getMeanEnergy())
                        .multiply(BigInteger.valueOf(stack.getAmount())));
            long totalAmount = (long) particleStack.getAmount() + stack.getAmount();
            particleStack.setMeanEnergy(
                energy.divide(BigInteger.valueOf(totalAmount))
                    .longValueExact());
            particleStack.addAmount(stack.getAmount());
        }
        changed();
        return true;
    }

    @Override
    public boolean canExtractParticle(@Nullable Direction side) {
        return particleStack != null && !particleStack.isEmpty();
    }

    /** Mutable machine state, as in the original API; getParticle() returns a copy. */
    @Override
    @Nullable
    public ParticleStack getParticleStack() {
        return particleStack;
    }

    @Override
    public long getMaxEnergy() {
        return maxEnergy;
    }

    @Override
    public long getMinEnergy() {
        return minEnergy;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        if (capacity < 0) throw new IllegalArgumentException("Capacity must be nonnegative");
        this.capacity = capacity;
        changed();
    }

    public void setMaxEnergy(long maxEnergy) {
        if (maxEnergy < minEnergy) throw new IllegalArgumentException("Maximum energy is below minimum");
        this.maxEnergy = maxEnergy;
        changed();
    }

    public void setMinEnergy(long minEnergy) {
        if (minEnergy < 0 || minEnergy > maxEnergy) throw new IllegalArgumentException("Invalid minimum energy");
        this.minEnergy = minEnergy;
        changed();
    }

    @Override
    @Nullable
    public ParticleStack extractParticle(@Nullable Direction side) {
        if (!canExtractParticle(side)) return null;
        ParticleStack extracted = particleStack;
        particleStack = null;
        changed();
        return extracted;
    }

    @Override
    @Nullable
    public ParticleStack extractParticle(@Nullable Direction side, Particle type) {
        return canExtractParticle(side) && particleStack.getParticle() == type ? extractParticle(side) : null;
    }

    @Override
    @Nullable
    public ParticleStack extractParticle(@Nullable Direction side, int amount) {
        if (amount <= 0 || !canExtractParticle(side)) return null;
        ParticleStack extracted = particleStack.copy();
        extracted.setAmount(Math.min(amount, particleStack.getAmount()));
        particleStack.removeAmount(extracted.getAmount());
        if (particleStack.isEmpty()) particleStack = null;
        changed();
        return extracted;
    }

    @Override
    @Nullable
    public ParticleStack extractParticle(@Nullable Direction side, Particle type, int amount) {
        return canExtractParticle(side) && particleStack.getParticle() == type ? extractParticle(side, amount) : null;
    }

    @Override
    @Nullable
    public ParticleStack getParticle() {
        return particleStack == null ? null : particleStack.copy();
    }
}
