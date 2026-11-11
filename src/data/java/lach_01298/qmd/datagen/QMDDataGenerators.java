package lach_01298.qmd.datagen;

import lach_01298.qmd.QuantumMinecraftDynamics;
import lach_01298.qmd.datagen.client.QMDBlockStateProvider;
import lach_01298.qmd.datagen.client.QMDItemModelProvider;
import lach_01298.qmd.datagen.recipe.QMDRecipeProvider;
import lach_01298.qmd.datagen.server.QMDBlockLootProvider;
import lach_01298.qmd.datagen.server.QMDBlockTagProvider;
import lach_01298.qmd.datagen.server.QMDDamageTypeProvider;
import lach_01298.qmd.datagen.server.QMDDamageTypeTagProvider;
import lach_01298.qmd.datagen.server.QMDFluidTagProvider;
import lach_01298.qmd.datagen.server.QMDItemTagProvider;
import lach_01298.qmd.datagen.server.QMDNativeDataProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/** Entry point for reproducible assets and server data; never runs during normal gameplay. */
@EventBusSubscriber(modid = QuantumMinecraftDynamics.MODID)
public final class QMDDataGenerators {
    private QMDDataGenerators() {}

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var output = generator.getPackOutput();
        var existingFiles = event.getExistingFileHelper();
        generator.addProvider(event.includeClient(), new QMDBlockStateProvider(output, existingFiles));
        generator.addProvider(event.includeClient(), new QMDItemModelProvider(output, existingFiles));

        var damageTypes = new QMDDamageTypeProvider(output, event.getLookupProvider());
        generator.addProvider(event.includeServer(), damageTypes);
        var registries = damageTypes.getRegistryProvider();
        generator.addProvider(event.includeServer(), new QMDRecipeProvider(output, registries));
        var blockTags = new QMDBlockTagProvider(output, registries, existingFiles);
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), new QMDItemTagProvider(output, registries, blockTags.contentsGetter(), existingFiles));
        generator.addProvider(event.includeServer(), new QMDFluidTagProvider(output, registries, existingFiles));
        generator.addProvider(event.includeServer(), new QMDDamageTypeTagProvider(output, registries, existingFiles));
        generator.addProvider(event.includeServer(), new QMDBlockLootProvider(output, registries));
        generator.addProvider(event.includeServer(), new QMDNativeDataProvider(output, registries));
    }
}
