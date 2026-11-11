package lach_01298.qmd.entity;

import lach_01298.qmd.config.ParticleCellConfig;
import lach_01298.qmd.config.WeaponConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public final class AntimatterProjectileEntity extends Projectile {

    private static final EntityDataAccessor<Integer> MASS = SynchedEntityData
        .defineId(AntimatterProjectileEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData
        .defineId(AntimatterProjectileEntity.class, EntityDataSerializers.INT);
    private int airTicks;

    public AntimatterProjectileEntity(EntityType<? extends AntimatterProjectileEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(MASS, 1)
            .define(COLOR, 0xB37AC4);
    }

    public void configure(Player owner, int mass, int color) {
        setOwner(owner);
        setPos(owner.getX(), owner.getEyeY() - 0.1D, owner.getZ());
        entityData.set(MASS, mass);
        entityData.set(COLOR, color);
    }

    public int mass() {
        return entityData.get(MASS);
    }

    public int color() {
        return entityData.get(COLOR);
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        if (!entity.isAlive() || entity.isSpectator() || !entity.isPickable()) return false;
        if (entity == getOwner() && airTicks < 5) return false;
        return !(entity instanceof Player target && getOwner() instanceof Player owner && !owner.canHarmPlayer(target));
    }

    @Override
    public void tick() {
        super.tick();
        if (++airTicks > 2000) {
            discard();
            return;
        }
        HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (!level().isClientSide && airTicks > 1 && hit.getType() != HitResult.Type.MISS) {
            if (!net.neoforged.neoforge.event.EventHooks.onProjectileImpact(this, hit)) onHit(hit);
            if (isRemoved()) return;
        }
        setPos(position().add(getDeltaMovement()));
        var movement = getDeltaMovement();
        setYRot((float) (net.minecraft.util.Mth.atan2(movement.x, movement.z) * 180 / Math.PI));
        setXRot((float) (net.minecraft.util.Mth.atan2(movement.y, movement.horizontalDistance()) * 180 / Math.PI));
        setDeltaMovement(
            getDeltaMovement().scale(0.99D)
                .add(0, -0.05D, 0));
        if (!level().isClientSide && (isInWater() || level().getFluidState(blockPosition())
            .is(net.minecraft.tags.FluidTags.WATER))) detonate();
    }

    @Override
    protected void onHit(HitResult hit) {
        setPos(hit.getLocation());
        if (hit instanceof EntityHitResult target) {
            if (target.getEntity()
                .hurt(
                    lach_01298.qmd.registry.QMDDamageTypes
                        .source(lach_01298.qmd.registry.QMDDamageTypes.ANTIMATTER, this, getOwner()),
                    WeaponConfig.ANTIMATTER_DAMAGE.get()
                        .floatValue() * mass())) {
                playSound(SoundEvents.ARROW_HIT, 1, 1.2F);
            }
        }
        detonate();
    }

    private void detonate() {
        GammaFlashEntity.create(
            level(),
            position(),
            mass(),
            WeaponConfig.ANTIMATTER_EXPLOSION.get()
                .floatValue() * mass(),
            ParticleCellConfig.RADIATION.get() * mass());
        discard();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Mass", mass());
        tag.putInt("Color", color());
        tag.putInt("AirTicks", airTicks);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(MASS, tag.getInt("Mass"));
        entityData.set(COLOR, tag.getInt("Color"));
        airTicks = tag.getInt("AirTicks");
    }
}
