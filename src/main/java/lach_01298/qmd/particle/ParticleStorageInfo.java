package lach_01298.qmd.particle;

import org.jetbrains.annotations.Nullable;

/** An independent snapshot of a particle storage, including long energy limits. */
public final class ParticleStorageInfo {

    @Nullable
    public final ParticleStack particleStack;
    public final long maxEnergy;
    public final long minEnergy;
    public final int capacity;

    public ParticleStorageInfo(@Nullable ParticleStack particleStack, long maxEnergy, int capacity, long minEnergy) {
        this.particleStack = particleStack == null ? null : particleStack.copy();
        this.maxEnergy = maxEnergy;
        this.minEnergy = minEnergy;
        this.capacity = capacity;
    }

    public ParticleStorageInfo(IParticleStorage storage) {
        this(storage.getParticleStack(), storage.getMaxEnergy(), storage.getCapacity(), storage.getMinEnergy());
    }
}
