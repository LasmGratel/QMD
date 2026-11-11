package lach_01298.qmd.physics;

import lach_01298.qmd.config.QMDConfig;
import lach_01298.qmd.particle.Particle;
import lach_01298.qmd.particle.ParticleStack;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/** Shared beam equations. Energies are keV, masses MeV/c², lengths blocks and charge elementary units. */
public final class ParticlePhysics {

    private static final BigInteger LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);
    private static final BigDecimal DECIMAL_LONG_MAX = BigDecimal.valueOf(Long.MAX_VALUE);

    private ParticlePhysics() {}

    public static double focusLoss(double distance, ParticleStack stack) {
        return focusLoss(distance, stack, QMDConfig.BEAM_ATTENUATION_RATE.get(), QMDConfig.BEAM_SCALING.get());
    }

    public static double focusLoss(double distance, ParticleStack stack, double attenuation, int scaling) {
        nonnegative(distance, "distance");
        nonnegative(attenuation, "attenuation");
        if (scaling <= 0) throw new IllegalArgumentException("Beam scaling must be positive");
        if (empty(stack)) return 0;
        double loss = attenuation * distance
            * (1 + Math.abs(
                stack.getParticle()
                    .getCharge())
                * Math.sqrt(stack.getAmount() / (double) scaling));
        return finiteSaturate(loss);
    }

    public static double travelDistance(ParticleStack stack) {
        if (empty(stack)) return 0;
        double loss = focusLoss(1, stack);
        return loss == 0 ? Double.POSITIVE_INFINITY : stack.getFocus() / loss;
    }

    public static double focusGain(double quadrupoleStrength, ParticleStack stack) {
        nonnegative(quadrupoleStrength, "quadrupole strength");
        return empty(stack) ? 0
            : finiteSaturate(
                quadrupoleStrength * Math.abs(
                    stack.getParticle()
                        .getCharge()));
    }

    public static long linacEnergyGain(long cavityVoltage, ParticleStack stack) {
        nonnegative(cavityVoltage, "cavity voltage");
        return empty(stack) ? 0
            : saturatedFloor(
                cavityVoltage * Math.abs(
                    stack.getParticle()
                        .getCharge()));
    }

    public static long ringEnergyMaxEnergyFromDipole(double strength, double radius, double charge, double mass) {
        nonnegative(strength, "dipole strength");
        positive(radius, "radius");
        nonnegative(mass, "mass");
        finite(charge, "charge");
        // A magnetic field does not bend a neutral or massless beam.
        if (charge == 0 || mass == 0) return 0;
        return saturatedFloor(Math.pow(charge * strength * radius, 2) / (2 * mass) * 1_000_000D);
    }

    public static long ringEnergyMaxEnergyFromRadiation(long voltage, double radius, double charge, double mass) {
        nonnegative(voltage, "cavity voltage");
        positive(radius, "radius");
        nonnegative(mass, "mass");
        finite(charge, "charge");
        if (charge == 0 || mass == 0) return 0;
        return saturatedFloor(mass * Math.pow(3D * voltage * radius / Math.abs(charge), 0.25D) * 1_000_000D);
    }

    public static long ringEnergyMaxEnergy(double strength, long voltage, double radius, ParticleStack stack) {
        if (empty(stack)) return 0;
        Particle particle = stack.getParticle();
        return Math.min(
            ringEnergyMaxEnergyFromDipole(strength, radius, particle.getCharge(), particle.getMass()),
            ringEnergyMaxEnergyFromRadiation(voltage, radius, particle.getCharge(), particle.getMass()));
    }

    public static long synchrotronRadiationEnergy(double radius, ParticleStack stack) {
        positive(radius, "radius");
        if (empty(stack) || stack.getParticle()
            .getMass() == 0) return 0;
        return saturatedFloor(
            Math.pow(
                stack.getMeanEnergy() / (1000D * stack.getParticle()
                    .getMass()),
                3) / (2 * Math.PI * 1_000_000D * radius));
    }

    public static long cornerEnergyLoss(ParticleStack stack, double radius) {
        positive(radius, "radius");
        if (empty(stack) || stack.getParticle()
            .getMass() == 0
            || stack.getParticle()
                .getCharge() == 0)
            return 0;
        Particle particle = stack.getParticle();
        return saturatedFloor(
            stack.getMeanEnergy() * Math.pow(particle.getCharge(), 2)
                / (6 * Math.pow(particle.getMass(), 4) * Math.pow(radius, 2)));
    }

    /** Exact floor of 2√(E₁E₂), without overflowing the long product. */
    public static long collisionEnergy(long firstEnergy, long secondEnergy) {
        nonnegative(firstEnergy, "first energy");
        nonnegative(secondEnergy, "second energy");
        return saturated(
            BigInteger.valueOf(firstEnergy)
                .multiply(BigInteger.valueOf(secondEnergy))
                .shiftLeft(2)
                .sqrt());
    }

    public static long particleOutputAmount(long inputAmount, long recipeAmount, double crossSection,
        double efficiency) {
        nonnegative(inputAmount, "input count");
        nonnegative(recipeAmount, "recipe count");
        return saturatedProduct(inputAmount, recipeAmount, reactionProbability(crossSection, efficiency));
    }

    public static long particleOutputAmountCollisionChamber(long firstCount, long secondCount, long firstEnergy,
        long secondEnergy, long recipeAmount, double crossSection, double efficiency) {
        nonnegative(firstCount, "first count");
        nonnegative(secondCount, "second count");
        nonnegative(firstEnergy, "first energy");
        nonnegative(secondEnergy, "second energy");
        nonnegative(recipeAmount, "recipe count");
        double probability = reactionProbability(crossSection, efficiency);
        if (firstEnergy == 0 && secondEnergy == 0) return 0;
        // Keep the intended continuous energy-balance penalty; legacy's integer division discarded it.
        BigDecimal sum = BigDecimal.valueOf(firstEnergy)
            .add(BigDecimal.valueOf(secondEnergy));
        BigDecimal difference = BigDecimal.valueOf(firstEnergy)
            .subtract(BigDecimal.valueOf(secondEnergy))
            .abs();
        double balance = BigDecimal.ONE.subtract(difference.divide(sum, 30, RoundingMode.HALF_EVEN))
            .doubleValue();
        return saturatedProduct(Math.min(firstCount, secondCount), recipeAmount, probability * balance);
    }

    /** The signed Q value can remove kinetic energy; below-threshold output is clamped to zero. */
    public static long particleOutputEnergy(long inputEnergy, long recipeQ, long particleNumber) {
        nonnegative(inputEnergy, "input energy");
        if (particleNumber <= 0) throw new IllegalArgumentException("Output particle count must be positive");
        BigInteger energy = BigInteger.valueOf(inputEnergy)
            .add(BigInteger.valueOf(recipeQ));
        return energy.signum() <= 0 ? 0 : saturated(energy.divide(BigInteger.valueOf(particleNumber)));
    }

    public static int saturatedCount(long amount) {
        return (int) Math.clamp(amount, 0, Integer.MAX_VALUE);
    }

    public static long saturatedAdd(long first, long second) {
        nonnegative(first, "first value");
        nonnegative(second, "second value");
        return first > Long.MAX_VALUE - second ? Long.MAX_VALUE : first + second;
    }

    private static long saturatedProduct(long first, long second, double probability) {
        BigDecimal value = BigDecimal.valueOf(first)
            .multiply(BigDecimal.valueOf(second))
            .multiply(BigDecimal.valueOf(probability));
        return value.compareTo(DECIMAL_LONG_MAX) >= 0 ? Long.MAX_VALUE : value.longValue();
    }

    private static double reactionProbability(double crossSection, double efficiency) {
        nonnegative(crossSection, "cross section");
        nonnegative(efficiency, "efficiency");
        return Math.min(crossSection * efficiency, 1D);
    }

    private static long saturatedFloor(double value) {
        if (Double.isNaN(value) || value <= 0) return 0;
        return value >= Long.MAX_VALUE ? Long.MAX_VALUE : (long) value;
    }

    private static long saturated(BigInteger value) {
        return value.compareTo(LONG_MAX) >= 0 ? Long.MAX_VALUE : value.longValue();
    }

    private static boolean empty(ParticleStack stack) {
        return stack == null || stack.isEmpty();
    }

    private static double finiteSaturate(double value) {
        return value == Double.POSITIVE_INFINITY ? Double.MAX_VALUE : value;
    }

    private static void finite(double value, String name) {
        if (!Double.isFinite(value)) throw new IllegalArgumentException(name + " must be finite");
    }

    private static void nonnegative(double value, String name) {
        finite(value, name);
        if (value < 0) throw new IllegalArgumentException(name + " must not be negative");
    }

    private static void nonnegative(long value, String name) {
        if (value < 0) throw new IllegalArgumentException(name + " must not be negative");
    }

    private static void positive(double value, String name) {
        finite(value, name);
        if (value <= 0) throw new IllegalArgumentException(name + " must be positive");
    }
}
