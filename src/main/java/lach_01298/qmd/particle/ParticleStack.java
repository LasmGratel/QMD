package lach_01298.qmd.particle;

import lach_01298.qmd.Config;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

/** A mutable beam: particle count, mean kinetic energy in keV and inverse-area focus. */
public class ParticleStack {
    private Particle particle;
    private int amount;
    private long meanEnergy;
    private double focus;

    public ParticleStack() { this(null, 0, 0, 0); }
    public ParticleStack(Particle particle) { this(particle, 1, 0, 0); }
    public ParticleStack(Particle particle, int amount) { this(particle, amount, 0, 0); }
    public ParticleStack(Particle particle, int amount, long meanEnergy) { this(particle, amount, meanEnergy, 0); }

    public ParticleStack(Particle particle, int amount, long meanEnergy, double focus) {
        this.particle = particle;
        setAmount(amount);
        setMeanEnergy(meanEnergy);
        setFocus(focus);
    }

    public Particle getParticle() { return particle; }
    public int getAmount() { return amount; }
    public long getMeanEnergy() { return meanEnergy; }
    public double getFocus() { return focus; }
    public boolean isEmpty() { return particle == null || amount <= 0; }

    public void setParticle(Particle particle) {
        this.particle = particle;
        if (particle == null) amount = 0;
    }

    public void setAmount(int amount) { this.amount = particle == null ? 0 : Math.max(0, amount); }
    public void setMeanEnergy(long meanEnergy) { this.meanEnergy = Math.max(0, meanEnergy); }
    public void setFocus(double focus) { this.focus = Double.isFinite(focus) ? Math.max(0, focus) : 0; }

    public void addMeanEnergy(long add) {
        if (add > 0) meanEnergy += Math.min(add, Long.MAX_VALUE - meanEnergy);
        else if (add < 0) meanEnergy = add < -meanEnergy ? 0 : meanEnergy + add;
    }

    public void addAmount(long add) {
        if (particle == null) return;
        if (add > 0) amount += (int) Math.min(add, Integer.MAX_VALUE - (long) amount);
        else if (add < 0) amount = add < -(long) amount ? 0 : (int) (amount + add);
    }

    public void removeAmount(long remove) {
        if (remove <= 0) return;
        amount = remove >= amount ? 0 : (int) (amount - remove);
    }

    public void addFocus(double add) { setFocus(focus + add); }

    public CompoundTag writeToNBT(CompoundTag nbt) {
        if (particle != null) nbt.putString("particle", particle.getName());
        else nbt.remove("particle");
        nbt.putInt("amount", amount);
        nbt.putLong("meanEnergy", meanEnergy);
        nbt.putDouble("focus", focus);
        return nbt;
    }

    public void readFromNBT(CompoundTag nbt) {
        particle = nbt.contains("particle", Tag.TAG_STRING) ? Particles.getParticleFromName(nbt.getString("particle")) : null;
        setAmount(nbt.getInt("amount"));
        setMeanEnergy(nbt.getLong("meanEnergy"));
        setFocus(nbt.getDouble("focus"));
    }

    public ParticleStack copy() { return new ParticleStack(particle, amount, meanEnergy, focus); }

    public static ParticleStack loadParticleStackFromNBT(CompoundTag nbt) {
        if (nbt == null || !nbt.contains("particle", Tag.TAG_STRING)) return null;
        ParticleStack stack = new ParticleStack();
        stack.readFromNBT(nbt);
        return stack.isEmpty() ? null : stack;
    }

    public static ParticleStack getParticleStack(String particleName, int amount, long meanEnergy, double focus) {
        return new ParticleStack(Particles.getParticleFromName(particleName), amount, meanEnergy, focus);
    }

    public boolean matchesType(ParticleStack stack) {
        return particle != null && stack != null && stack.particle == particle;
    }

    public boolean isInRange(ParticleStack stack, long maxEnergy) {
        return matchesType(stack)
                && stack.focus >= focus - Config.BEAM_ATTENUATION_RATE.get() / 10D
                && stack.meanEnergy >= meanEnergy && stack.meanEnergy <= maxEnergy;
    }
}
