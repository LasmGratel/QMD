package lach_01298.qmd.particle;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

/** A configured source beam, sampled without consuming or exposing its mutable state. */
public class ParticleStorageSource extends ParticleStorage {

    public ParticleStorageSource() {
        super(null, Long.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public boolean canReceiveParticle(@Nullable Direction side, @Nullable ParticleStack stack) {
        return false;
    }

    @Override
    public boolean receiveParticle(@Nullable Direction side, @Nullable ParticleStack stack) {
        return false;
    }

    @Override
    @Nullable
    public ParticleStack extractParticle(@Nullable Direction side) {
        return canExtractParticle(side) ? getParticle() : null;
    }

    @Override
    @Nullable
    public ParticleStack extractParticle(@Nullable Direction side, int amount) {
        if (amount <= 0 || !canExtractParticle(side)) return null;
        ParticleStack extracted = getParticle();
        extracted.setAmount(Math.min(amount, extracted.getAmount()));
        return extracted;
    }
}
