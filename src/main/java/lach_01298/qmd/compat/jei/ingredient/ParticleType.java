package lach_01298.qmd.compat.jei.ingredient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import lach_01298.qmd.particle.ParticleBeam;
import lach_01298.qmd.particle.ParticleStack;
import lach_01298.qmd.particle.Particles;
import mezz.jei.api.ingredients.IIngredientType;

import java.util.Optional;

/** Particle species are searchable ingredients, independently of a beam's operating conditions. */
public final class ParticleType {

    public static final IIngredientType<ParticleStack> PARTICLE = new IIngredientType<>() {

        @Override
        public Class<? extends ParticleStack> getIngredientClass() {
            return ParticleStack.class;
        }

        @Override
        public String getUid() {
            return "qmd:particle";
        }

        @Override
        public Optional<ParticleStack> getRepresentativeIngredient() {
            Particles.initialize();
            return Optional.of(new ParticleStack(Particles.alpha));
        }
    };

    /** Preserves complete beam values while rejecting empty or unknown bookmarked ingredients. */
    public static final Codec<ParticleStack> CODEC = ParticleBeam.CODEC.flatXmap(beam -> {
        ParticleStack stack = beam.toStack();
        return stack == null ? DataResult.error(() -> "A JEI particle ingredient must not be empty")
            : DataResult.success(stack);
    },
        stack -> stack == null || stack.isEmpty()
            || Particles.getParticleFromName(
                stack.getParticle()
                    .getName())
                != stack.getParticle()
                    ? DataResult.error(() -> "A JEI particle ingredient must be a nonempty registered particle")
                    : DataResult.success(ParticleBeam.fromStack(stack)));

    private ParticleType() {}
}
