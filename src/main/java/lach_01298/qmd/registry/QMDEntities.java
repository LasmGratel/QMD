package lach_01298.qmd.registry;

import lach_01298.qmd.QuantumMinecraftDynamics;
import lach_01298.qmd.entity.AntimatterProjectileEntity;
import lach_01298.qmd.entity.GammaFlashEntity;
import lach_01298.qmd.entity.GluonBeamEntity;
import lach_01298.qmd.entity.LeptonBeamEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class QMDEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister
        .create(Registries.ENTITY_TYPE, QuantumMinecraftDynamics.MODID);
    public static final DeferredHolder<EntityType<?>, EntityType<GammaFlashEntity>> GAMMA_FLASH = ENTITIES.register(
        "gamma_flash",
        () -> EntityType.Builder.<GammaFlashEntity>of(GammaFlashEntity::new, MobCategory.MISC)
            .sized(0.1F, 0.1F)
            .clientTrackingRange(16)
            .updateInterval(10)
            .fireImmune()
            .build("qmd:gamma_flash"));
    public static final DeferredHolder<EntityType<?>, EntityType<LeptonBeamEntity>> LEPTON_BEAM = ENTITIES.register(
        "lepton_beam",
        () -> EntityType.Builder.<LeptonBeamEntity>of(LeptonBeamEntity::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
            .clientTrackingRange(16)
            .updateInterval(1)
            .fireImmune()
            .build("qmd:lepton_beam"));
    public static final DeferredHolder<EntityType<?>, EntityType<GluonBeamEntity>> GLUON_BEAM = ENTITIES.register(
        "gluon_beam",
        () -> EntityType.Builder.<GluonBeamEntity>of(GluonBeamEntity::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
            .clientTrackingRange(16)
            .updateInterval(1)
            .fireImmune()
            .build("qmd:gluon_beam"));
    public static final DeferredHolder<EntityType<?>, EntityType<AntimatterProjectileEntity>> ANTIMATTER_PROJECTILE = ENTITIES
        .register(
            "antimatter_projectile",
            () -> EntityType.Builder.<AntimatterProjectileEntity>of(AntimatterProjectileEntity::new, MobCategory.MISC)
                .sized(0.5F, 0.5F)
                .clientTrackingRange(16)
                .updateInterval(1)
                .fireImmune()
                .build("qmd:antimatter_projectile"));

    private QMDEntities() {}
}
