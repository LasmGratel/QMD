package lach_01298.qmd.entity;

import com.nred.nuclearcraft.radiation.RadiationHelper;
import com.nred.nuclearcraft.registration.DamageTypeRegistration;
import lach_01298.qmd.registry.QMDEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class GammaFlashEntity extends Entity {

    private static final EntityDataAccessor<Float> SIZE = SynchedEntityData
        .defineId(GammaFlashEntity.class, EntityDataSerializers.FLOAT);
    private int lifetime = 500;

    public GammaFlashEntity(EntityType<? extends GammaFlashEntity> type, Level level) {
        super(type, level);
        noPhysics = true;
        setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SIZE, 1F);
    }

    public float size() {
        return entityData.get(SIZE);
    }

    @Override
    public void tick() {
        super.tick();
        if (--lifetime <= 0) discard();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        lifetime = Math.clamp(tag.getInt("Lifetime"), 0, 500);
        float size = tag.getFloat("Size");
        entityData.set(SIZE, Float.isFinite(size) ? Math.clamp(size, 0F, 1000F) : 1F);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Lifetime", lifetime);
        tag.putFloat("Size", size());
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 256D * 256D;
    }

    public static void create(Level level, Vec3 position, double size, float explosion, double radiation) {
        if (level.isClientSide || !Double.isFinite(size) || size <= 0) return;
        if (Float.isFinite(explosion) && explosion > 0)
            level.explode(null, position.x, position.y, position.z, explosion, Level.ExplosionInteraction.BLOCK);
        GammaFlashEntity flash = QMDEntities.GAMMA_FLASH.get()
            .create(level);
        if (flash != null) {
            flash.setPos(position);
            flash.entityData.set(SIZE, (float) Math.min(1000, size));
            level.addFreshEntity(flash);
        }
        double radius = 128 * Math.sqrt(size);
        for (LivingEntity entity : level
            .getEntitiesOfClass(LivingEntity.class, new AABB(position, position).inflate(radius))) {
            var rads = RadiationHelper.getEntityRadiation(entity);
            if (rads == null) continue;
            double dose = radiation / Math.max(1, position.distanceToSqr(entity.position()));
            // Neohaul squares the adjusted dose when applying shielding; keep that intermediate finite.
            double multiplier = entity.isInWater() ? com.nred.nuclearcraft.config.NCConfig.radiation_swim_mult : 1;
            double safeDose = Math.sqrt(Double.MAX_VALUE) / 2 / Math.max(1, multiplier);
            dose = Double.isNaN(dose) ? 0 : Math.clamp(dose, 0, safeDose);
            rads.setRadiationLevel(RadiationHelper.addRadsToEntity(rads, entity, dose, false, false, 1));
            if (rads.isFatal() && !rads.isImmune()) entity.hurt(
                level.damageSources()
                    .source(DamageTypeRegistration.FATAL_RADS),
                Float.MAX_VALUE);
        }
    }
}
