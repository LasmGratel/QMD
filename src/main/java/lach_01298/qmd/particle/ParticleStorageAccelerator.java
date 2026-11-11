package lach_01298.qmd.particle;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

/** The accelerator's current output beam: each update replaces the previous snapshot. */
public class ParticleStorageAccelerator extends ParticleStorageSource {

    @Override
    public boolean canReceiveParticle(@Nullable Direction side, @Nullable ParticleStack stack) {
        // A missing beam explicitly clears an accelerator's previous output.
        return stack == null || (!stack.isEmpty() && stack.getAmount() <= capacity
            && stack.getMeanEnergy() >= minEnergy
            && stack.getMeanEnergy() <= maxEnergy);
    }

    @Override
    public boolean receiveParticle(@Nullable Direction side, @Nullable ParticleStack stack) {
        if (!canReceiveParticle(side, stack)) return false;
        setParticleStack(stack);
        return true;
    }
}
