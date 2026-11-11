package lach_01298.qmd.particle;

import org.jetbrains.annotations.Nullable;

public interface IParticleStorage {

    @Nullable
    ParticleStack getParticleStack();

    long getMaxEnergy();

    long getMinEnergy();

    int getCapacity();

    ParticleStorageInfo getInfo();
}
