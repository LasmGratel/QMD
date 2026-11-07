package lach_01298.qmd.item;

import lach_01298.qmd.QuantumMinecraftDynamics;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** The legacy material variants, flattened into individually registered 1.21.1 items. */
public final class QMDItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(QuantumMinecraftDynamics.MODID);

    private static final Map<String, DeferredItem<Item>> MATERIAL_ITEMS = new LinkedHashMap<>();
    private static final Map<LegacyMaterial, DeferredItem<Item>> LEGACY_ITEMS = new LinkedHashMap<>();
    private static final List<Material> MATERIALS = new ArrayList<>();

    static {
        registerGroup("dust", "dust",
                "tungsten", "niobium", "chromium", "titanium",
                "cobalt", "nickel", "hafnium", "zinc",
                "osmium", "iridium", "platinum", "sodium",
                "potassium", "calcium", "strontium", "barium");
        registerGroup("dust2", "dust",
                "yttrium", "neodymium", "iodine", "samarium",
                "terbium", "erbium", "ytterbium");
        registerGroup("ingot", "ingot",
                "tungsten", "niobium", "chromium", "titanium",
                "cobalt", "nickel", "hafnium", "zinc",
                "osmium", "iridium", "platinum", "sodium",
                "potassium", "calcium", "strontium", "barium");
        registerGroup("ingot2", "ingot",
                "yttrium", "neodymium", "mercury");
        registerGroup("ingot_alloy", "ingot",
                "tungsten_carbide", "niobium_tin", "stainless_steel", "niobium_titanium",
                "osmiridium", "nichrome", "super_alloy");
        registerGroup("part", "part",
                "empty_cooler", "detector_casing", "scintillator_pwo", "scintillator_plastic",
                "wire_bscco", "rod_nd_yag", "wire_gold_tungsten", "wire_chamber_casing",
                "magnet_nd", "accelerating_barrel", "laser_assembly", "wire_ssfaf",
                "wire_ybco", "magnet_smc", "wire_hbcco");
        registerGroup("semiconductor", "semiconductor",
                "silicon_p_doped", "silicon_n_doped", "silicon_boule", "silicon_wafer",
                "basic_processor", "advanced_processor", "elite_processor");
        registerGroup("chemical_dust", "chemical_dust",
                "tungsten_oxide", "bscco", "sodium_nitrate", "sodium_chloride",
                "copper_oxide", "hafnium_oxide", "strontium_chloride", "zinc_sulfide",
                "iron_fluoride", "ssfaf", "ybco", "hbcco");
    }

    private QMDItems() {
    }

    private static void registerGroup(String legacyItem, String prefix, String... variants) {
        for (int metadata = 0; metadata < variants.length; metadata++) {
            String id = prefix + "_" + variants[metadata];
            if (MATERIAL_ITEMS.containsKey(id)) {
                throw new IllegalStateException("Duplicate QMD material: " + id);
            }
            DeferredItem<Item> item = ITEMS.registerSimpleItem(id);
            MATERIAL_ITEMS.put(id, item);
            LEGACY_ITEMS.put(new LegacyMaterial(legacyItem, metadata), item);
            MATERIALS.add(new Material(id, legacyItem, metadata, variants[metadata]));
        }
    }

    /** Returns a registered item by its QMD path, for example {@code ingot_tungsten}. */
    public static DeferredItem<Item> get(String id) {
        DeferredItem<Item> item = MATERIAL_ITEMS.get(id);
        if (item == null) {
            throw new IllegalArgumentException("Unknown QMD material: " + id);
        }
        return item;
    }

    /** Resolves a legacy QMD registry path and metadata value for recipe migration. */
    public static DeferredItem<Item> fromLegacy(String legacyItem, int metadata) {
        DeferredItem<Item> item = LEGACY_ITEMS.get(new LegacyMaterial(legacyItem, metadata));
        if (item == null) {
            throw new IllegalArgumentException("Unknown legacy QMD material: " + legacyItem + ":" + metadata);
        }
        return item;
    }

    public static Collection<DeferredItem<Item>> entries() {
        return Collections.unmodifiableCollection(MATERIAL_ITEMS.values());
    }

    public static List<Material> materials() {
        return Collections.unmodifiableList(MATERIALS);
    }

    public record Material(String id, String legacyItem, int legacyMetadata, String variant) {
    }

    private record LegacyMaterial(String item, int metadata) {
    }
}

