package lach_01298.qmd.particle;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

/** A complete beam is received atomically; extraction can be limited by species or count. */
public interface IParticleStackHandler {

    boolean receiveParticle(@Nullable Direction side, @Nullable ParticleStack stack);

    @Nullable
    ParticleStack extractParticle(@Nullable Direction side);

    @Nullable
    ParticleStack extractParticle(@Nullable Direction side, Particle type);

    @Nullable
    ParticleStack extractParticle(@Nullable Direction side, int amount);

    @Nullable
    ParticleStack extractParticle(@Nullable Direction side, Particle type, int amount);

    @Nullable
    ParticleStack getParticle();

    boolean canReceiveParticle(@Nullable Direction side, @Nullable ParticleStack stack);

    boolean canExtractParticle(@Nullable Direction side);
}
