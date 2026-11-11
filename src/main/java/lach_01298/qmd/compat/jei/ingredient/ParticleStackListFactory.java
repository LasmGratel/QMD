package lach_01298.qmd.compat.jei.ingredient;

import lach_01298.qmd.particle.Particle;
import lach_01298.qmd.particle.ParticleStack;
import lach_01298.qmd.particle.Particles;

import java.util.Comparator;
import java.util.List;

public final class ParticleStackListFactory {

    private ParticleStackListFactory() {}

    public static List<ParticleStack> create() {
        Particles.initialize();
        return Particles.getRegisteredParticles()
            .stream()
            .sorted(Comparator.comparing(Particle::getName))
            .map(ParticleStack::new)
            .toList();
    }
}
