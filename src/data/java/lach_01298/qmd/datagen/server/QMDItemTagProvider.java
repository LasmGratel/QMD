package lach_01298.qmd.datagen.server;

import lach_01298.qmd.registry.QMDItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/** Material families, aliases and optional native isotope forms use standard tag appenders. */
public final class QMDItemTagProvider extends ItemTagsProvider {
    private static final List<String> NATIVE_INGOTS = names("""
            americium_241 americium_242 americium_243 berkelium_247 berkelium_248
            boron_10 boron_11 curium_243 curium_247 lithium_6 lithium_7
            neptunium_236 neptunium_237 plutonium_238 plutonium_239 plutonium_241 plutonium_242
            uranium_233 uranium_235 uranium_238
            """);
    private static final List<String> TARGET_ISOTOPES = names("""
            americium_241 americium_242 americium_243 berkelium_247 berkelium_248
            californium_249 californium_250 californium_251 californium_252 copernicium_291
            curium_243 curium_245 curium_246 curium_247 neptunium_236 neptunium_237
            plutonium_238 plutonium_239 plutonium_241 plutonium_242 uranium_233 uranium_235 uranium_238
            """);
    private static final List<String> FUEL_FORMS = List.of("", "_c", "_ox", "_ni", "_za");

    public QMDItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                             CompletableFuture<TagsProvider.TagLookup<Block>> blockTags,
                             ExistingFileHelper existingFiles) {
        super(output, lookupProvider, blockTags, "qmd", existingFiles);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        materials();
        var buckets = tag(common("buckets"));
        QMDFluidTagProvider.COLLECTOR_FLUIDS.forEach(name -> buckets.add(item(name + "_bucket")));
        nuclear();
        tag(common("isotopes/calcium/48")).addTag(common("ingots/calcium_48"));
        copy(QMDBlockTagProvider.STRONTIUM_STORAGE_BLOCKS, common("storage_blocks/strontium_90"));
    }

    private void materials() {
        for (var material : QMDItems.materials()) {
            String id = material.id();
            String family = id.startsWith("dust_") || id.startsWith("chemical_dust_") ? "dusts"
                    : id.startsWith("ingot_") ? "ingots"
                    : id.startsWith("waste_fission_") ? "waste_fission"
                    : id.startsWith("waste_spallation_") ? "waste_spallation" : "";
            if (!family.isEmpty()) {
                var materialTag = common(family + "/" + material.variant());
                tag(materialTag).add(item(id));
                if (family.equals("dusts") || family.equals("ingots")) tag(common(family)).addTag(materialTag);
            }
        }
        for (String alias : List.of("niter", "saltpeter", "salt")) {
            var aliasTag = common("dusts/" + alias);
            tag(aliasTag).add(item("chemical_dust_" + (alias.equals("salt") ? "sodium_chloride" : "sodium_nitrate")));
            tag(common("dusts")).addTag(aliasTag);
        }
        group("wires", "part_wire_", "bscco gold_tungsten ssfaf ybco hbcco", "");
        group("processors", "semiconductor_", "basic advanced elite", "_processor");
        group("boules", "semiconductor_", "silicon", "_boule");
        group("wafers", "semiconductor_", "silicon", "_wafer");
        group("rods", "part_rod_", "nd_yag", "");
        var magnets = tag(common("magnets"));
        for (String[] magnet : new String[][]{{"neodymium", "nd"}, {"samarium_cobalt", "smc"}}) {
            var magnetTag = common("magnets/" + magnet[0]);
            tag(magnetTag).add(item("part_magnet_" + magnet[1]));
            magnets.addTag(magnetTag);
        }
        var silicon = tag(common("silicon"));
        for (String doping : List.of("p_doped", "n_doped")) {
            var dopingTag = common("silicon/" + doping);
            tag(dopingTag).add(item("semiconductor_silicon_" + doping));
            silicon.addTag(dopingTag);
        }
    }

    private void nuclear() {
        QMDItems.NUCLEAR_MATERIALS.forEach((name, item) -> {
            String tagName = name.startsWith("isotope_") ? name.substring("isotope_".length()) : name;
            tag(common("ingots/" + tagName)).add(item.get());
        });
        for (String isotope : NATIVE_INGOTS) {
            tag(common("ingots/" + isotope)).addOptional(ResourceLocation.fromNamespaceAndPath("nuclearcraftneohaul", isotope));
        }
        for (String isotope : TARGET_ISOTOPES) {
            var forms = tag(TagKey.create(Registries.ITEM,
                    ResourceLocation.fromNamespaceAndPath("qmd", "isotopes/" + isotope + "_all")));
            for (String namespace : isotope.equals("copernicium_291") ? List.of("nuclearcraftneohaul", "qmd") : List.of("nuclearcraftneohaul")) {
                for (String form : FUEL_FORMS) forms.addOptional(ResourceLocation.fromNamespaceAndPath(namespace, isotope + form));
            }
        }
    }

    private void group(String family, String prefix, String variants, String suffix) {
        var group = tag(common(family));
        for (String name : names(variants)) {
            var variantTag = common(family + "/" + name);
            tag(variantTag).add(item(prefix + name + suffix));
            group.addTag(variantTag);
        }
    }

    private static List<String> names(String names) {
        return Arrays.asList(names.strip().split("\\s+"));
    }

    private static TagKey<Item> common(String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", path));
    }

    private static ResourceKey<Item> item(String path) {
        return ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("qmd", path));
    }
}
