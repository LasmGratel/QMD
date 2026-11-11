package lach_01298.qmd.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

public abstract class WeaponBeamEntity extends Entity {

    private static final EntityDataAccessor<Integer> OWNER = SynchedEntityData
        .defineId(WeaponBeamEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> OFF_HAND = SynchedEntityData
        .defineId(WeaponBeamEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> LENGTH = SynchedEntityData
        .defineId(WeaponBeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData
        .defineId(WeaponBeamEntity.class, EntityDataSerializers.INT);
    protected int lifetime;
    private UUID ownerUUID;

    protected WeaponBeamEntity(EntityType<?> type, Level level, int lifetime) {
        super(type, level);
        this.lifetime = lifetime;
        noPhysics = true;
        setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(OWNER, -1)
            .define(OFF_HAND, false)
            .define(LENGTH, 1F)
            .define(COLOR, 0xFFFFFF);
    }

    public void configure(Player player, InteractionHand hand, double length, int color) {
        ownerUUID = player.getUUID();
        entityData.set(OWNER, player.getId());
        entityData.set(OFF_HAND, hand == InteractionHand.OFF_HAND);
        entityData.set(COLOR, color);
        setLength(length);
        follow(player);
    }

    protected void follow(Player player) {
        setPos(player.getEyePosition());
        setRot(player.getYRot(), player.getXRot());
    }

    public Player owner() {
        Entity entity = level() instanceof ServerLevel server && ownerUUID != null ? server.getEntity(ownerUUID)
            : level().getEntity(entityData.get(OWNER));
        if (entity instanceof Player player) {
            if (!level().isClientSide) entityData.set(OWNER, player.getId());
            return player;
        }
        return null;
    }

    public InteractionHand hand() {
        return entityData.get(OFF_HAND) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    }

    public float length() {
        return entityData.get(LENGTH);
    }

    public int color() {
        return entityData.get(COLOR);
    }

    public void setLength(double length) {
        entityData.set(LENGTH, (float) length);
    }

    @Override
    public void tick() {
        super.tick();
        if (--lifetime <= 0) discard();
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 256D * 256D;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (ownerUUID != null) tag.putUUID("Owner", ownerUUID);
        tag.putBoolean("OffHand", hand() == InteractionHand.OFF_HAND);
        tag.putFloat("Length", length());
        tag.putInt("Color", color());
        tag.putInt("Lifetime", lifetime);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        ownerUUID = tag.hasUUID("Owner") ? tag.getUUID("Owner") : null;
        entityData.set(OFF_HAND, tag.getBoolean("OffHand"));
        entityData.set(LENGTH, tag.getFloat("Length"));
        entityData.set(COLOR, tag.getInt("Color"));
        lifetime = tag.getInt("Lifetime");
    }
}
