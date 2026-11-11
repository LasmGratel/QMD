package lach_01298.qmd.recipe.ingredient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lach_01298.qmd.config.QMDConfig;
import lach_01298.qmd.particle.ParticleStack;
import lach_01298.qmd.particle.Particles;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/** A recipe beam requirement. Amount is work per reaction, rather than a minimum incoming rate. */
public record ParticleIngredient(List<ResourceLocation> particles, int amount, long minEnergy, long maxEnergy,
    double minFocus) {

    public static final ParticleIngredient EMPTY = new ParticleIngredient(List.of(), 0, 0, Long.MAX_VALUE, 0);

    private record Fields(List<ResourceLocation> particles, int amount, long minEnergy, long maxEnergy,
        double minFocus) {}
    public static final Codec<ParticleIngredient> CODEC = RecordCodecBuilder
        .<Fields>create(
            instance -> instance.group(
                ResourceLocation.CODEC.listOf(0, 64)
                    .fieldOf("particles")
                    .forGetter(Fields::particles),
                Codec.INT.optionalFieldOf("amount", 1)
                    .forGetter(Fields::amount),
                Codec.LONG.optionalFieldOf("min_energy", 0L)
                    .forGetter(Fields::minEnergy),
                Codec.LONG.optionalFieldOf("max_energy", Long.MAX_VALUE)
                    .forGetter(Fields::maxEnergy),
                Codec.DOUBLE.optionalFieldOf("min_focus", 0D)
                    .forGetter(Fields::minFocus))
                .apply(instance, Fields::new))
        .flatXmap(fields -> {
            try {
                return DataResult.success(
                    new ParticleIngredient(
                        fields.particles,
                        fields.amount,
                        fields.minEnergy,
                        fields.maxEnergy,
                        fields.minFocus));
            } catch (IllegalArgumentException exception) {
                return DataResult.error(exception::getMessage);
            }
        },
            value -> DataResult
                .success(new Fields(value.particles, value.amount, value.minEnergy, value.maxEnergy, value.minFocus)));

    public ParticleIngredient {
        particles = List.copyOf(particles);
        if (particles.size() > 64 || particles.stream()
            .distinct()
            .count() != particles.size()
            || amount < 0
            || minEnergy < 0
            || maxEnergy < minEnergy
            || !Double.isFinite(minFocus)
            || minFocus < 0
            || particles.isEmpty() != (amount == 0)) {
            throw new IllegalArgumentException("Invalid particle alternatives, work amount, energy range or focus");
        }
        Particles.initialize();
        if (particles.stream()
            .anyMatch(id -> Particles.getParticleFromName(id.toString()) == null)) {
            throw new IllegalArgumentException("Unknown particle in recipe ingredient");
        }
    }

    public static ParticleIngredient of(ParticleStack stack, long maxEnergy) {
        if (stack == null || stack.isEmpty()) return EMPTY;
        return new ParticleIngredient(
            List.of(
                ResourceLocation.fromNamespaceAndPath(
                    "qmd",
                    stack.getParticle()
                        .getName())),
            stack.getAmount(),
            stack.getMeanEnergy(),
            maxEnergy,
            stack.getFocus());
    }

    public boolean isEmpty() {
        return particles.isEmpty();
    }

    public boolean matches(ParticleStack beam, boolean testAmount) {
        if (isEmpty()) return beam == null || beam.isEmpty();
        return beam != null && !beam.isEmpty()
            && particles.contains(
                ResourceLocation.fromNamespaceAndPath(
                    "qmd",
                    beam.getParticle()
                        .getName()))
            && (!testAmount || beam.getAmount() >= amount)
            && beam.getMeanEnergy() >= minEnergy
            && beam.getMeanEnergy() <= maxEnergy
            && beam.getFocus() >= minFocus - QMDConfig.BEAM_ATTENUATION_RATE.get() / 10D;
    }

    /** Fresh mutable display values; callers cannot mutate the recipe requirement. */
    public List<ParticleStack> alternatives() {
        return particles.stream()
            .map(id -> new ParticleStack(Particles.getParticleFromName(id.toString()), amount, minEnergy, minFocus))
            .toList();
    }
}
