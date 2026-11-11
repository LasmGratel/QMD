package lach_01298.qmd.multiblock;

import lach_01298.qmd.particle.ParticleBeam;
import lach_01298.qmd.particle.ParticleStack;
import lach_01298.qmd.physics.ParticlePhysics;
import lach_01298.qmd.recipe.chamber.AbstractChamberRecipe;
import lach_01298.qmd.recipe.chamber.CollisionChamberRecipe;
import lach_01298.qmd.recipe.chamber.DecayChamberRecipe;
import lach_01298.qmd.recipe.chamber.TargetChamberRecipe;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.function.ToDoubleFunction;

/** Reaction equations; controller subclasses choose radial, decay or collision behavior. */
public final class ChamberReactions {

    public static List<ParticleBeam> radialProducts(AbstractChamberRecipe recipe, List<ParticleStack> inputs,
        double efficiency, double length, RoundingMode rounding) {
        validate(efficiency, length);
        if (!recipe.matchesParticles(inputs)) return emptyProducts(recipe.kind().particleOutputs);
        if (recipe.particleOutputs()
            .stream()
            .mapToLong(ParticleBeam::amount)
            .sum() == 0) return emptyProducts(recipe.kind().particleOutputs);
        ParticleStack first = inputs.getFirst();
        return products(
            recipe,
            first.getMeanEnergy(),
            first.getAmount(),
            Math.min(recipe.crossSection() * efficiency, 1D),
            rounding,
            output -> first.getFocus() - ParticlePhysics.focusLoss(length / 2D, first)
                - ParticlePhysics.focusLoss(length / 2D, output));
    }

    public static List<ParticleBeam> collisionProducts(AbstractChamberRecipe recipe, List<ParticleStack> inputs,
        double efficiency, double length) {
        validate(efficiency, length);
        if (!recipe.matchesParticles(inputs)) return emptyProducts(recipe.kind().particleOutputs);
        if (recipe.particleOutputs()
            .stream()
            .mapToLong(ParticleBeam::amount)
            .sum() == 0) return emptyProducts(recipe.kind().particleOutputs);
        ParticleStack first = inputs.getFirst(), second = inputs.get(1);
        long kinetic = ParticlePhysics.collisionEnergy(first.getMeanEnergy(), second.getMeanEnergy());
        BigDecimal a = BigDecimal.valueOf(first.getMeanEnergy()), b = BigDecimal.valueOf(second.getMeanEnergy());
        BigDecimal total = a.add(b);
        double probability = Math.min(recipe.crossSection() * efficiency, 1D) * (total.signum() == 0 ? 0
            : BigDecimal.ONE.subtract(
                a.subtract(b)
                    .abs()
                    .divide(total, 30, RoundingMode.HALF_EVEN))
                .doubleValue());
        return products(
            recipe,
            kinetic,
            Math.min(first.getAmount(), second.getAmount()),
            probability,
            RoundingMode.HALF_UP,
            output -> Math.min(first.getFocus(), second.getFocus()) - ParticlePhysics.focusLoss(length, output));
    }

    /** Convenience entry point for callers evaluating recipes independently of an assembled machine. */
    public static List<ParticleBeam> products(AbstractChamberRecipe recipe, List<ParticleStack> inputs,
        double efficiency, double length) {
        return recipe instanceof CollisionChamberRecipe ? collisionProducts(recipe, inputs, efficiency, length)
            : radialProducts(
                recipe,
                inputs,
                efficiency,
                length,
                recipe instanceof DecayChamberRecipe ? RoundingMode.FLOOR : RoundingMode.HALF_UP);
    }

    private static List<ParticleBeam> products(AbstractChamberRecipe recipe, long kinetic, int incoming,
        double probability, RoundingMode rounding, ToDoubleFunction<ParticleStack> focus) {
        long sum = recipe.particleOutputs()
            .stream()
            .mapToLong(ParticleBeam::amount)
            .sum();
        if (sum == 0) return emptyProducts(recipe.kind().particleOutputs);
        long energy = roundedEnergy(kinetic, recipe.energyReleased(), sum);
        var results = new ArrayList<ParticleBeam>(recipe.kind().particleOutputs);
        for (ParticleBeam product : recipe.particleOutputs()) {
            if (product.particleName()
                .isEmpty()) {
                results.add(ParticleBeam.EMPTY);
                continue;
            }
            BigDecimal count = BigDecimal.valueOf(incoming)
                .multiply(BigDecimal.valueOf(product.amount()))
                .multiply(BigDecimal.valueOf(probability));
            long number = count.min(BigDecimal.valueOf(Integer.MAX_VALUE))
                .setScale(0, rounding)
                .longValue();
            if (number == 0) {
                results.add(ParticleBeam.EMPTY);
                continue;
            }
            ParticleStack output = product.toStack();
            output.setAmount((int) number);
            output.setMeanEnergy(energy);
            double outputFocus = focus.applyAsDouble(output);
            results.add(
                outputFocus <= 0 ? ParticleBeam.EMPTY
                    : new ParticleBeam(product.particleName(), (int) number, energy, outputFocus));
        }
        return List.copyOf(results);
    }

    private static void validate(double efficiency, double length) {
        if (!Double.isFinite(efficiency) || efficiency < 0 || !Double.isFinite(length) || length < 0)
            throw new IllegalArgumentException("Invalid chamber efficiency/beam length");
    }

    private static long roundedEnergy(long energy, long released, long count) {
        BigInteger numerator = BigInteger.valueOf(energy)
            .add(BigInteger.valueOf(released));
        if (numerator.signum() <= 0) return 0;
        return numerator.add(BigInteger.valueOf(count / 2))
            .divide(BigInteger.valueOf(count))
            .min(BigInteger.valueOf(Long.MAX_VALUE))
            .longValue();
    }

    public static List<ParticleBeam> emptyProducts(int count) {
        return java.util.Collections.nCopies(count, ParticleBeam.EMPTY);
    }

    public static long threshold(AbstractChamberRecipe recipe) {
        return Math.max(1, workAmount(recipe));
    }

    public static long targetThreshold(AbstractChamberRecipe recipe, double efficiency) {
        long amount = workAmount(recipe);
        return Math.max(
            1L,
            (long) Math.min(
                Long.MAX_VALUE,
                Math.max(recipe.crossSection() * amount, amount / Math.max(efficiency, Double.MIN_NORMAL))));
    }

    public static long threshold(AbstractChamberRecipe recipe, double efficiency) {
        return recipe instanceof TargetChamberRecipe ? targetThreshold(recipe, efficiency) : threshold(recipe);
    }

    private static long workAmount(AbstractChamberRecipe recipe) {
        return recipe.particleInputs()
            .isEmpty() ? 1
                : lach_01298.qmd.config.ChamberConfig.scaledWork(
                    recipe.kind(),
                    recipe.particleInputs()
                        .getFirst()
                        .amount());
    }

    private ChamberReactions() {}
}
