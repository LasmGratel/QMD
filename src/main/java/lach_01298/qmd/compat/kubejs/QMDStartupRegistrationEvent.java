package lach_01298.qmd.compat.kubejs;

import dev.latvian.mods.kubejs.event.KubeStartupEvent;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Collects one immutable startup extension snapshot before Minecraft registries freeze. */
public final class QMDStartupRegistrationEvent implements KubeStartupEvent {

    private final Map<String, QMDStartupCatalogue.ParticleDefinition> particles = new LinkedHashMap<>();
    private final List<QMDStartupCatalogue.CompositionDefinition> compositions = new ArrayList<>();
    private final List<QMDStartupCatalogue.AntiparticleDefinition> antiparticles = new ArrayList<>();
    private final Map<ResourceLocation, MutableItemSource> itemSources = new LinkedHashMap<>();
    private final Map<ResourceLocation, ResourceLocation> emptyItems = new LinkedHashMap<>();
    private final Map<ResourceLocation, QMDStartupCatalogue.MachineComponentDefinition> machineComponents = new LinkedHashMap<>();
    private boolean frozen;

    public void particle(String name, String texture, double mass, double charge, double spin) {
        particle(name, texture, mass, charge, spin, false, false);
    }

    public void particle(String name, String texture, double mass, double charge, double spin, boolean weakInteraction,
        boolean strongInteraction) {
        mutable();
        String normalized = QMDStartupCatalogue.normalizeParticleName(name);
        ResourceLocation textureId = QMDStartupCatalogue.parseTexture(texture);
        putUnique(
            particles,
            normalized,
            new QMDStartupCatalogue.ParticleDefinition(
                normalized,
                textureId,
                mass,
                charge,
                spin,
                weakInteraction,
                strongInteraction),
            "particle");
    }

    public void componentParticle(String parent, String component) {
        componentParticle(parent, component, 1);
    }

    public void componentParticle(String parent, String component, int amount) {
        mutable();
        compositions.add(
            new QMDStartupCatalogue.CompositionDefinition(
                QMDStartupCatalogue.normalizeParticleName(parent),
                QMDStartupCatalogue.normalizeParticleName(component),
                amount));
    }

    public void antiparticles(String particle, String antiparticle) {
        mutable();
        antiparticles.add(
            new QMDStartupCatalogue.AntiparticleDefinition(
                QMDStartupCatalogue.normalizeParticleName(particle),
                QMDStartupCatalogue.normalizeParticleName(antiparticle)));
    }

    public void itemSource(String id, int capacity, int stackSize) {
        itemSource(id, capacity, stackSize, 0D, 0D);
    }

    public void itemSource(String id, int capacity, int stackSize, double explosionSize, double radiation) {
        mutable();
        ResourceLocation key = QMDStartupCatalogue.parseId(id);
        putUnique(
            itemSources,
            key,
            new MutableItemSource(key, capacity, stackSize, explosionSize, radiation),
            "particle source item");
    }

    public void emptyItemSource(String source, String emptyItem) {
        mutable();
        ResourceLocation sourceId = QMDStartupCatalogue.parseId(source);
        ResourceLocation emptyId = QMDStartupCatalogue.parseId(emptyItem);
        if (emptyItems.putIfAbsent(sourceId, emptyId) != null) {
            throw new IllegalArgumentException("Empty item already assigned for QMD particle source " + sourceId);
        }
    }

    public void acceleratorCooler(String name, int cooling, String rule) {
        machine(
            new QMDStartupCatalogue.MachineComponentDefinition(
                QMDStartupCatalogue.MachineKind.ACCELERATOR_COOLER,
                QMDStartupCatalogue.componentId("accelerator_cooler_", name),
                name,
                cooling,
                0,
                0,
                0,
                0D,
                0D,
                false,
                rule));
    }

    public void acceleratorRfCavity(String name, int voltage, double efficiency, int heat, int basePower,
        int maxTemperature) {
        machine(
            new QMDStartupCatalogue.MachineComponentDefinition(
                QMDStartupCatalogue.MachineKind.ACCELERATOR_RF_CAVITY,
                QMDStartupCatalogue.componentId("accelerator_rf_cavity_", name),
                name,
                voltage,
                heat,
                basePower,
                maxTemperature,
                efficiency,
                0D,
                false,
                ""));
    }

    public void acceleratorMagnet(String name, double strength, double efficiency, int heat, int basePower,
        int maxTemperature) {
        machine(
            new QMDStartupCatalogue.MachineComponentDefinition(
                QMDStartupCatalogue.MachineKind.ACCELERATOR_MAGNET,
                QMDStartupCatalogue.componentId("accelerator_magnet_", name),
                name,
                heat,
                basePower,
                maxTemperature,
                0,
                strength,
                efficiency,
                false,
                ""));
    }

    public void particleChamberDetector(String name, double efficiency, int basePower, int distance, boolean within) {
        machine(
            new QMDStartupCatalogue.MachineComponentDefinition(
                QMDStartupCatalogue.MachineKind.PARTICLE_CHAMBER_DETECTOR,
                QMDStartupCatalogue.componentId("particle_chamber_detector_", name),
                name,
                basePower,
                distance,
                0,
                0,
                efficiency,
                0D,
                within,
                ""));
    }

    public void vacuumChamberHeater(String name, int cooling, String rule) {
        machine(
            new QMDStartupCatalogue.MachineComponentDefinition(
                QMDStartupCatalogue.MachineKind.VACUUM_CHAMBER_HEATER,
                QMDStartupCatalogue.componentId("vacuum_chamber_heater_", name),
                name,
                cooling,
                0,
                0,
                0,
                0D,
                0D,
                false,
                rule));
    }

    public void ionSource(String name, int particleOutputMultiplier, double outputFocus, int basePower) {
        machine(
            new QMDStartupCatalogue.MachineComponentDefinition(
                QMDStartupCatalogue.MachineKind.ACCELERATOR_ION_SOURCE,
                QMDStartupCatalogue.componentId("accelerator_source_", name),
                name,
                particleOutputMultiplier,
                basePower,
                0,
                0,
                outputFocus,
                0D,
                false,
                ""));
    }

    public void liquefierCompressor(String name, double energyEfficiency, double heatEfficiency) {
        machine(
            new QMDStartupCatalogue.MachineComponentDefinition(
                QMDStartupCatalogue.MachineKind.LIQUEFIER_COMPRESSOR,
                QMDStartupCatalogue.componentId("liquefier_compressor_", name),
                name,
                0,
                0,
                0,
                0,
                energyEfficiency,
                heatEfficiency,
                false,
                ""));
    }

    QMDStartupCatalogue.Snapshot freeze() {
        mutable();
        frozen = true;
        List<QMDStartupCatalogue.ItemSourceDefinition> sources = new ArrayList<>(itemSources.size());
        for (MutableItemSource source : itemSources.values()) {
            sources.add(
                new QMDStartupCatalogue.ItemSourceDefinition(
                    source.id,
                    source.capacity,
                    source.stackSize,
                    emptyItems.get(source.id),
                    source.explosionSize,
                    source.radiation));
        }
        for (ResourceLocation source : emptyItems.keySet()) {
            if (!itemSources.containsKey(source)) {
                throw new IllegalArgumentException(
                    "Cannot set the empty item of unknown QMD particle source " + source);
            }
        }
        return new QMDStartupCatalogue.Snapshot(
            List.copyOf(particles.values()),
            List.copyOf(compositions),
            List.copyOf(antiparticles),
            sources,
            List.copyOf(machineComponents.values()));
    }

    private void machine(QMDStartupCatalogue.MachineComponentDefinition definition) {
        mutable();
        putUnique(machineComponents, definition.id(), definition, "machine component");
    }

    private void mutable() {
        if (frozen) {
            throw new IllegalStateException("QMD startup registrations are already frozen");
        }
    }

    private static <K, V> void putUnique(Map<K, V> map, K key, V value, String kind) {
        if (map.putIfAbsent(key, value) != null) {
            throw new IllegalArgumentException("Duplicate QMD " + kind + " registration: " + key);
        }
    }

    private record MutableItemSource(ResourceLocation id, int capacity, int stackSize, double explosionSize,
        double radiation) {}
}
