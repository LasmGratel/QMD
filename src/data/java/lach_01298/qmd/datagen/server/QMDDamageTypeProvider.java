package lach_01298.qmd.datagen.server;

import lach_01298.qmd.registry.QMDDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/** Dynamic damage-type registry entries, shared with tag generation through the registry lookup. */
public final class QMDDamageTypeProvider extends DatapackBuiltinEntriesProvider {
    public QMDDamageTypeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, new RegistrySetBuilder().add(Registries.DAMAGE_TYPE, QMDDamageTypeProvider::bootstrap), Set.of("qmd"));
    }

    private static void bootstrap(BootstrapContext<DamageType> context) {
        context.register(QMDDamageTypes.ANNIHILATION, new DamageType("qmd.antimatter_annihilation", DamageScaling.NEVER, 0.1F));
        for (var key : List.of(QMDDamageTypes.LEPTON, QMDDamageTypes.GLUON, QMDDamageTypes.ANTIMATTER)) {
            context.register(key, new DamageType(key.location().getPath(), DamageScaling.NEVER, 0.1F));
        }
    }
}
