package lach_01298.qmd.compat.kubejs;

import lach_01298.qmd.particle.Particle;
import lach_01298.qmd.particle.ParticleStack;
import lach_01298.qmd.particle.Particles;

import java.util.List;

/** Script entry points for the built-in catalog and particle beam values. */
public final class QMDParticles {

    // JavaScript numbers cannot represent every integer above this value.
    private static final double MAX_SAFE_INTEGER = 9_007_199_254_740_991D;

    private QMDParticles() {}

    public static List<Particle> catalog() {
        Particles.initialize();
        return List.copyOf(Particles.getRegisteredParticles());
    }

    public static List<String> names() {
        return catalog().stream()
            .map(Particle::getName)
            .toList();
    }

    public static Particle get(String name) {
        Particles.initialize();
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                "QMD particle name must not be blank. Use QMDParticles.names() to list valid names.");
        }
        Particle particle = Particles.getParticleFromName(name);
        if (particle == null) {
            throw new IllegalArgumentException(
                "Unknown QMD particle '" + name + "'. Use QMDParticles.names() to list valid names.");
        }
        return particle;
    }

    public static ParticleStack stack(String name) {
        return stack(name, 1, 0, 0);
    }

    public static ParticleStack stack(String name, double amount) {
        return stack(name, amount, 0, 0);
    }

    public static ParticleStack stack(String name, double amount, double meanEnergy) {
        return stack(name, amount, meanEnergy, 0);
    }

    public static ParticleStack stack(String name, double amount, double meanEnergy, double focus) {
        if (!Double.isFinite(meanEnergy) || meanEnergy < 0
            || meanEnergy > MAX_SAFE_INTEGER
            || meanEnergy != Math.rint(meanEnergy)) {
            throw new IllegalArgumentException(
                "QMD mean energy must be a non-negative whole number of keV up to 9007199254740991. Pass larger exact energies as a decimal string.");
        }
        return createStack(name, amount, (long) meanEnergy, focus);
    }

    /** Decimal strings preserve the complete non-negative Java long energy range. */
    public static ParticleStack stack(String name, double amount, String meanEnergy, double focus) {
        long energy;
        try {
            energy = Long.parseLong(meanEnergy);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                "QMD mean energy must be a decimal integer string between 0 and 9223372036854775807 keV.",
                exception);
        }
        if (energy < 0) {
            throw new IllegalArgumentException(
                "QMD mean energy must be a decimal integer string between 0 and 9223372036854775807 keV.");
        }
        return createStack(name, amount, energy, focus);
    }

    private static ParticleStack createStack(String name, double amount, long meanEnergy, double focus) {
        if (!Double.isFinite(amount) || amount < 0 || amount > Integer.MAX_VALUE || amount != Math.rint(amount)) {
            throw new IllegalArgumentException(
                "QMD particle amount must be a whole number between 0 and 2147483647 pu.");
        }
        if (!Double.isFinite(focus) || focus < 0) {
            throw new IllegalArgumentException("QMD beam focus must be a finite, non-negative number.");
        }
        return new ParticleStack(get(name), (int) amount, meanEnergy, focus);
    }
}
