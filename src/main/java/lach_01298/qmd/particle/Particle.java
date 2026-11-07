package lach_01298.qmd.particle;

import lach_01298.qmd.QuantumMinecraftDynamics;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** A particle species. Mass is in MeV/c², charge in e and spin in units of ħ. */
public class Particle {
    protected final String name;
    protected final double mass;
    protected final double charge;
    protected final double spin;
    protected final boolean strongInteract;
    protected final boolean weakInteract;
    protected Particle antiparticle;
    protected final HashMap<Particle, Integer> componentParticles = new HashMap<>();
    protected final ResourceLocation texture;
    private boolean frozen;

    public Particle(String name, ResourceLocation texture, double mass, double charge, double spin) {
        this(name, texture, mass, charge, spin, true, true);
    }

    public Particle(String name, ResourceLocation texture, double mass, double charge, double spin, boolean weakInteract) {
        this(name, texture, mass, charge, spin, weakInteract, false);
    }

    public Particle(String name, ResourceLocation texture, double mass, double charge, double spin,
                    boolean weakInteract, boolean strongInteract) {
        this.name = Objects.requireNonNull(name);
        this.texture = Objects.requireNonNull(texture);
        this.mass = mass;
        this.charge = charge;
        this.spin = spin;
        this.weakInteract = weakInteract;
        this.strongInteract = strongInteract;
        this.antiparticle = this;
    }

    public String getName() { return name; }
    public double getMass() { return mass; }
    public double getCharge() { return charge; }
    public double getSpin() { return spin; }
    public Particle getAntiParticle() { return antiparticle; }
    public ResourceLocation getTexture() { return texture; }
    public boolean interactsWithEM() { return charge != 0; }
    public boolean interactsWithStrong() { return strongInteract; }
    public boolean interactsWithWeak() { return weakInteract; }

    public void setAntiParticle(Particle antiparticle) {
        ensureMutable();
        Objects.requireNonNull(antiparticle).ensureMutable();
        this.antiparticle = antiparticle;
        antiparticle.antiparticle = this;
    }

    public void addComponentParticle(Particle component) {
        addComponentParticle(component, 1);
    }

    public void addComponentParticle(Particle component, int amount) {
        ensureMutable();
        Objects.requireNonNull(component);
        if (amount <= 0) throw new IllegalArgumentException("Component amount must be positive");
        componentParticles.merge(component, amount, Math::addExact);
    }

    public void setComponentParticles(Map<Particle, Integer> particles) {
        ensureMutable();
        HashMap<Particle, Integer> replacement = new HashMap<>();
        particles.forEach((particle, amount) -> {
            Objects.requireNonNull(particle);
            if (amount == null || amount <= 0) throw new IllegalArgumentException("Component amount must be positive");
            replacement.put(particle, amount);
        });
        componentParticles.clear();
        componentParticles.putAll(replacement);
    }

    public HashMap<Particle, Integer> getComponentParticles() {
        return new HashMap<>(componentParticles);
    }

    public boolean hasComponentParticles() { return !componentParticles.isEmpty(); }

    /** Registration completes construction; runtime lookups must not change catalog physics. */
    final void freeze() { frozen = true; }

    private void ensureMutable() {
        if (frozen) throw new IllegalStateException("Registered particle definition is immutable: " + name);
    }

    /** Kept for legacy callers and translations. */
    public String getUnlocalizedName() {
        return QuantumMinecraftDynamics.MODID + ".particle." + name + ".name";
    }
}
