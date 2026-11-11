package lach_01298.qmd.config;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/** Configuration and deterministic selection rules for shared-material furnace recipes. */
public final class MaterialRecipeConfig {

    public static final boolean DEFAULT_SHARED_MATERIAL_INPUTS = false;
    public static final List<String> DEFAULT_PREFERRED_OUTPUT_MODS = List.of("qmd");

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue SHARED_MATERIAL_INPUTS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> PREFERRED_OUTPUT_MODS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("furnace_materials");
        SHARED_MATERIAL_INPUTS = builder
            .comment(
                "False uses only QMD dust inputs. True uses the matching c:dusts/<material> tags.",
                "This is a data-generation policy; regenerate QMD's data after changing it.")
            .define("shared_material_inputs", DEFAULT_SHARED_MATERIAL_INPUTS);
        PREFERRED_OUTPUT_MODS = builder
            .comment(
                "Mod namespaces considered in order for furnace outputs from c:ingots/<material>.",
                "The QMD ingot is used when no preferred tagged item is available.",
                "This is a data-generation policy; regenerate QMD's data after changing it.")
            .defineList("preferred_output_mods", DEFAULT_PREFERRED_OUTPUT_MODS, MaterialRecipeConfig::isModNamespace);
        builder.pop();
        SPEC = builder.build();
    }

    public record Policy(boolean sharedMaterialInputs, List<String> preferredOutputMods) {

        public Policy {
            preferredOutputMods = List.copyOf(preferredOutputMods);
            if (preferredOutputMods.stream()
                .anyMatch(namespace -> !isModNamespace(namespace))) {
                throw new IllegalArgumentException("Invalid preferred output mod namespace");
            }
        }

        public String dustIngredient(String material) {
            String path = materialPath(material);
            return sharedMaterialInputs ? "#c:dusts/" + path : "qmd:dust_" + path;
        }

        public ResourceLocation preferredIngot(String material, Iterable<ResourceLocation> taggedIngots) {
            String path = materialPath(material);
            ResourceLocation fallback = ResourceLocation.fromNamespaceAndPath("qmd", "ingot_" + path);
            List<ResourceLocation> candidates = new ArrayList<>();
            taggedIngots.forEach(candidate -> candidates.add(Objects.requireNonNull(candidate)));
            candidates.sort(Comparator.comparing(ResourceLocation::toString));

            for (String namespace : preferredOutputMods) {
                for (ResourceLocation candidate : candidates) {
                    if (candidate.getNamespace()
                        .equals(namespace)) {
                        return candidate;
                    }
                }
            }
            return fallback;
        }
    }

    public static Policy configuredPolicy() {
        return new Policy(
            SPEC.isLoaded() ? SHARED_MATERIAL_INPUTS.get() : SHARED_MATERIAL_INPUTS.getDefault(),
            (SPEC.isLoaded() ? PREFERRED_OUTPUT_MODS.get() : PREFERRED_OUTPUT_MODS.getDefault()).stream()
                .map(namespace -> namespace.toLowerCase(Locale.ROOT))
                .toList());
    }

    private static String materialPath(String material) {
        Objects.requireNonNull(material, "material");
        if (material.isEmpty() || !ResourceLocation.isValidPath(material) || material.indexOf('/') >= 0) {
            throw new IllegalArgumentException("Invalid material path: " + material);
        }
        return material;
    }

    private static boolean isModNamespace(Object value) {
        return value instanceof String namespace && !namespace.isEmpty()
            && ResourceLocation.tryParse(namespace + ":configured_output") != null;
    }

    private MaterialRecipeConfig() {}
}
