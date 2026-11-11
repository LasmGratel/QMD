package lach_01298.qmd.datagen.server;

import lach_01298.qmd.registry.QMDDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

/** Weapon and annihilation damage retain their existing bypass tag memberships. */
public final class QMDDamageTypeTagProvider extends DamageTypeTagsProvider {
    public QMDDamageTypeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                   ExistingFileHelper existingFiles) {
        super(output, lookupProvider, "qmd", existingFiles);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(DamageTypeTags.BYPASSES_ARMOR).add(QMDDamageTypes.ANNIHILATION,
                QMDDamageTypes.LEPTON, QMDDamageTypes.GLUON, QMDDamageTypes.ANTIMATTER);
        tag(DamageTypeTags.BYPASSES_ENCHANTMENTS).add(QMDDamageTypes.ANNIHILATION);
        tag(DamageTypeTags.BYPASSES_RESISTANCE).add(QMDDamageTypes.ANNIHILATION);
    }
}
