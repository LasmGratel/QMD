package lach_01298.qmd.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;

/** Damage type keys and indirect weapon damage sources. */
public final class QMDDamageTypes {

    public static final ResourceKey<DamageType> ANNIHILATION = ResourceKey
        .create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("qmd", "antimatter_annihilation"));

    public static final ResourceKey<DamageType> LEPTON = type("lepton_cannon");

    public static final ResourceKey<DamageType> GLUON = type("gluon_gun");

    public static final ResourceKey<DamageType> ANTIMATTER = type("antimatter_launcher");

    private static ResourceKey<DamageType> type(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("qmd", name));
    }

    public static DamageSource source(ResourceKey<DamageType> type, Entity projectile, Entity owner) {
        return new DamageSource(
            projectile.level()
                .registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(type),
            projectile,
            owner);
    }

    private QMDDamageTypes() {}
}
