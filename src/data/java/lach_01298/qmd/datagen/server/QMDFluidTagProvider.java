package lach_01298.qmd.datagen.server;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import lach_01298.qmd.registry.QMDFluids;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/** Common fluid tags retain the original source/flowing-fluid membership contract. */
public final class QMDFluidTagProvider extends FluidTagsProvider {
    static final List<String> COLLECTOR_FLUIDS = List.of("hydrochloric_acid", "nitric_acid",
            "sodium_chloride_solution", "chlorine", "nitric_oxide", "nitrogen_dioxide", "salt_water",
            "compressed_air", "liquid_air", "argon", "neon");

    public QMDFluidTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                              ExistingFileHelper existingFiles) {
        super(output, lookupProvider, "qmd", existingFiles);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        Set<String> includeFlowing = Set.copyOf(COLLECTOR_FLUIDS);
        for (var fluid : QMDFluids.entries()) {
            String name = fluid.still.getId().getPath();
            var fluidTag = tag(common(name)).add(fluid.still.get());
            if (includeFlowing.contains(name)) fluidTag.add(fluid.flowing.get());
        }
        for (String nativeFluid : List.of("arsenic", "caesium_137", "californium_252", "curium_247",
                "europium_155", "molybdenum", "plutonium_242", "promethium_147", "ruthenium_106",
                "strontium_90", "uranium_238")) {
            tag(common(nativeFluid)).add(ResourceKey.create(Registries.FLUID,
                    ResourceLocation.fromNamespaceAndPath("nuclearcraftneohaul", nativeFluid)));
        }
    }

    private static TagKey<Fluid> common(String path) {
        return TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath("c", path));
    }
}
