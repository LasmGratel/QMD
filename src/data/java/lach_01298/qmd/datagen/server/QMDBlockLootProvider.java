package lach_01298.qmd.datagen.server;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/** Every survival block drops itself and survives the same explosion check. */
public final class QMDBlockLootProvider extends LootTableProvider {
    public QMDBlockLootProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Set.of(), List.of(new SubProviderEntry(Blocks::new, LootContextParamSets.BLOCK)), registries);
    }

    private static final class Blocks extends BlockLootSubProvider {
        private Blocks(HolderLookup.Provider registries) {
            super(Set.of(), FeatureFlags.DEFAULT_FLAGS, registries);
        }

        @Override
        protected void generate() {
            getKnownBlocks().forEach(this::dropSelf);
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return BuiltInRegistries.BLOCK.stream().filter(block -> {
                var id = BuiltInRegistries.BLOCK.getKey(block);
                return id.getNamespace().equals("qmd") && block.asItem() != Items.AIR
                        && !id.getPath().equals("creative_particle_source");
            }).toList();
        }
    }
}
