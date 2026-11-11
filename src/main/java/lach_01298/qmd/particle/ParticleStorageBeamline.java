package lach_01298.qmd.particle;

import lach_01298.qmd.config.QMDConfig;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

/** A beamline's most recently received beam, attenuated when it leaves the line. */
public class ParticleStorageBeamline extends ParticleStorage {

    private int length;

    public ParticleStorageBeamline(int length) {
        super(null, Long.MAX_VALUE, Integer.MAX_VALUE);
        setLength(length);
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        if (length < 0) throw new IllegalArgumentException("Beamline length must be nonnegative");
        this.length = length;
        changed();
    }

    @Override
    public boolean canReceiveParticle(@Nullable Direction side, @Nullable ParticleStack stack) {
        return stack != null && !stack.isEmpty()
            && stack.getFocus() > 0
            && stack.getAmount() <= capacity
            && stack.getMeanEnergy() >= minEnergy
            && stack.getMeanEnergy() <= maxEnergy;
    }

    @Override
    public boolean receiveParticle(@Nullable Direction side, @Nullable ParticleStack stack) {
        if (!canReceiveParticle(side, stack)) return false;
        setParticleStack(stack);
        return true;
    }

    @Override
    public boolean canExtractParticle(@Nullable Direction side) {
        // Preserve the legacy strict gate; charge-dependent loss is applied afterward.
        return super.canExtractParticle(side)
            && particleStack.getFocus() > length * QMDConfig.BEAM_ATTENUATION_RATE.get();
    }

    @Override
    @Nullable
    public ParticleStack extractParticle(@Nullable Direction side) {
        return extractParticle(side, Integer.MAX_VALUE);
    }

    @Override
    @Nullable
    public ParticleStack extractParticle(@Nullable Direction side, int amount) {
        ParticleStack extracted = super.extractParticle(side, amount);
        if (extracted != null) {
            double loss = QMDConfig.BEAM_ATTENUATION_RATE.get() * length
                * (1 + Math.abs(
                    extracted.getParticle()
                        .getCharge())
                    * Math.sqrt(extracted.getAmount() / (double) QMDConfig.BEAM_SCALING.get()));
            extracted.addFocus(-loss);
        }
        return extracted;
    }

    @Override
    public CompoundTag writeToNBT(CompoundTag nbt) {
        super.writeToNBT(nbt);
        nbt.putInt("length", length);
        return nbt;
    }

    @Override
    public void readFromNBT(CompoundTag nbt) {
        super.readFromNBT(nbt);
        if (nbt.contains("length", Tag.TAG_ANY_NUMERIC)) length = Math.max(0, nbt.getInt("length"));
    }
}
