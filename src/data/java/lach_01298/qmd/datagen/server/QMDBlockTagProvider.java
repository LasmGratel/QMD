package lach_01298.qmd.datagen.server;

import lach_01298.qmd.multiblock.ChamberPart;
import lach_01298.qmd.registry.QMDBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/** Machine families and mining tags, built from the registered block catalogues. */
public final class QMDBlockTagProvider extends BlockTagsProvider {
    static final TagKey<Block> STRONTIUM_STORAGE_BLOCKS = TagKey.create(Registries.BLOCK,
            ResourceLocation.parse("c:storage_blocks/strontium_90"));

    public QMDBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                              ExistingFileHelper existingFiles) {
        super(output, lookupProvider, "qmd", existingFiles);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        var accelerators = tag(qmd("accelerator_parts"));
        // Preserve the published component order, placing utility ports after the coolers.
        QMDBlocks.ACCELERATOR_BLOCKS.keySet().stream()
                .filter(id -> !id.equals("accelerator_port") && !id.equals("accelerator_computer_port"))
                .forEach(id -> accelerators.add(block(id)));
        accelerators.add(block("accelerator_port"), block("accelerator_computer_port"));

        for (boolean vacuum : List.of(false, true)) {
            var parts = tag(qmd(vacuum ? "vacuum_parts" : "chamber_parts"));
            for (var part : ChamberPart.values()) {
                if (part.vacuum() == vacuum) parts.add(block(part.id));
            }
        }
        tag(qmd("fission_parts")).add(block("reflector_tungsten_carbide"), block("neutron_shield_hafnium"),
                block("rtg_strontium"), block("strontium_90_block"));
        tag(qmd("irradiator")).add(block("irradiator"));
        var lamps = tag(qmd("lamps"));
        QMDBlocks.DISCHARGE_LAMPS.values().forEach(lamp -> lamps.add(lamp.get()));
        var liquefier = tag(qmd("liquefier_parts"));
        for (String id : List.of("liquefier_controller", "liquefier_nozzle", "liquefier_fluid_port",
                "liquefier_energy_port", "copper_liquefier_compressor", "neodymium_liquefier_compressor",
                "samarium_cobalt_liquefier_compressor")) {
            liquefier.add(block(id));
        }

        var mineable = tag(BlockTags.MINEABLE_WITH_PICKAXE);
        for (String family : List.of("accelerator_parts", "chamber_parts", "fission_parts", "irradiator",
                "lamps", "liquefier_parts", "vacuum_parts")) {
            mineable.addTag(qmd(family));
        }
        for (String id : List.of("accelerator_computer_port", "accelerator_port", "atmosphere_collector",
                "beamline", "liquid_collector", "ore_leacher", "turbine_blade_super_alloy")) {
            mineable.add(block(id));
        }
        tag(STRONTIUM_STORAGE_BLOCKS).add(block("strontium_90_block"));
    }

    private static TagKey<Block> qmd(String path) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("qmd", path));
    }

    private static ResourceKey<Block> block(String path) {
        return ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("qmd", path));
    }
}
