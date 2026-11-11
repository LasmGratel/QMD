package lach_01298.qmd.compat.kubejs;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

/** Immutable definitions collected by the startup registration event. */
final class QMDStartupCatalogue {

    static String normalizeParticleName(String name) {
        ResourceLocation id = ResourceLocation.parse(name.contains(":") ? name : "qmd:" + name);
        if (!id.getNamespace()
            .equals("qmd")) {
            throw new IllegalArgumentException("QMD particle names must use the qmd namespace: " + name);
        }
        return id.getPath();
    }

    static ResourceLocation parseTexture(String texture) {
        return ResourceLocation.parse(texture.contains(":") ? texture : "qmd:" + texture);
    }

    static ResourceLocation parseId(String id) {
        return ResourceLocation.parse(id.contains(":") ? id : "qmd:" + id);
    }

    static ResourceLocation componentId(String prefix, String name) {
        ResourceLocation id = parseId(name);
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), prefix + id.getPath());
    }

    record ParticleDefinition(String name, ResourceLocation texture, double mass, double charge, double spin,
        boolean weakInteraction, boolean strongInteraction) {}

    record CompositionDefinition(String parent, String component, int amount) {}

    record AntiparticleDefinition(String particle, String antiparticle) {}

    record ItemSourceDefinition(ResourceLocation id, int capacity, int stackSize, ResourceLocation emptyItem,
        double explosionSize, double radiation) {}

    enum MachineKind {
        ACCELERATOR_COOLER,
        ACCELERATOR_RF_CAVITY,
        ACCELERATOR_MAGNET,
        PARTICLE_CHAMBER_DETECTOR,
        VACUUM_CHAMBER_HEATER,
        ACCELERATOR_ION_SOURCE,
        LIQUEFIER_COMPRESSOR
    }

    record MachineComponentDefinition(MachineKind kind, ResourceLocation id, String name, int first, int second,
        int third, int fourth, double firstValue, double secondValue, boolean within, String rule) {}

    record Snapshot(List<ParticleDefinition> particles, List<CompositionDefinition> compositions,
        List<AntiparticleDefinition> antiparticles, List<ItemSourceDefinition> itemSources,
        List<MachineComponentDefinition> machineComponents) {}

    private QMDStartupCatalogue() {}
}
