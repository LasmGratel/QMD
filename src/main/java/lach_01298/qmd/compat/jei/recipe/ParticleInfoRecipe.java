package lach_01298.qmd.compat.jei.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import lach_01298.qmd.particle.Particle;
import lach_01298.qmd.particle.ParticleStack;
import lach_01298.qmd.particle.Particles;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Catalogue information is derived from registered physics definitions, not synthetic machine recipes. */
public record ParticleInfoRecipe(Particle particle) {

    public static final Codec<ParticleInfoRecipe> CODEC = Codec.STRING.comapFlatMap(name -> {
        Particles.initialize();
        Particle particle = Particles.getParticleFromName(name);
        return particle == null ? DataResult.error(() -> "Unknown QMD particle: " + name)
            : DataResult.success(new ParticleInfoRecipe(particle));
    },
        recipe -> recipe.particle()
            .getName());

    public ParticleInfoRecipe {
        Objects.requireNonNull(particle);
    }

    public ParticleStack particleStack() {
        return new ParticleStack(particle);
    }

    public List<ParticleStack> components() {
        return particle.getComponentParticles()
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey(Comparator.comparing(Particle::getName)))
            .map(entry -> new ParticleStack(entry.getKey(), entry.getValue()))
            .toList();
    }

    public static List<ParticleInfoRecipe> createAll() {
        Particles.initialize();
        return Particles.getRegisteredParticles()
            .stream()
            .sorted(Comparator.comparing(Particle::getName))
            .map(ParticleInfoRecipe::new)
            .toList();
    }
}
