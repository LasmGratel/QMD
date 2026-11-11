package lach_01298.qmd.item.weapon;

import com.nred.nuclearcraft.radiation.RadiationHelper;
import com.nred.nuclearcraft.registration.DamageTypeRegistration;
import lach_01298.qmd.item.ParticleCellItem;
import lach_01298.qmd.item.ParticleReservoirItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public abstract class ParticleWeaponItem extends Item {

    private final List<String> species;

    protected ParticleWeaponItem(String... species) {
        super(new Properties().stacksTo(1));
        this.species = List.of(species);
    }

    /** Inventory order is significant: an insufficient first matching cell blocks firing. */
    protected int ammunitionSlot(Player player) {
        for (int slot = 0; slot < player.getInventory()
            .getContainerSize(); slot++) {
            ItemStack stack = player.getInventory()
                .getItem(slot);
            if (stack.getItem() instanceof ParticleCellItem cell && species.contains(cell.species())) return slot;
        }
        return -1;
    }

    protected int ammunitionKind(ItemStack stack) {
        return species.indexOf(((ParticleCellItem) stack.getItem()).species());
    }

    protected boolean hasAmmunition(Player player, int slot, int amount) {
        return slot >= 0 && ParticleReservoirItem.stored(
            player.getInventory()
                .getItem(slot))
            >= amount;
    }

    protected void consume(Player player, int slot, int amount) {
        if (player.getAbilities().instabuild) return;
        ItemStack cell = player.getInventory()
            .getItem(slot);
        player.getInventory()
            .setItem(slot, ((ParticleReservoirItem) cell.getItem()).consume(cell, amount));
        player.getInventory()
            .setChanged();
    }

    public static HitResult trace(Player player, double range, boolean blocks) {
        Vec3 start = player.getEyePosition();
        Vec3 end = start.add(
            player.getLookAngle()
                .scale(range));
        HitResult nearest = blocks ? player.level()
            .clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player)) : null;
        double distance = nearest == null ? range * range : start.distanceToSqr(nearest.getLocation());
        for (Entity entity : player.level()
            .getEntities(
                player,
                player.getBoundingBox()
                    .expandTowards(end.subtract(start))
                    .inflate(1),
                entity -> entity.isPickable() && !entity.isSpectator())) {
            if (entity instanceof Player target && !player.canHarmPlayer(target)) continue;
            var box = entity.getBoundingBox()
                .inflate(entity.getPickRadius());
            var hit = box.clip(start, end);
            if (!box.contains(start) && hit.isEmpty()) continue;
            Vec3 point = box.contains(start) ? start : hit.orElseThrow();
            double candidate = start.distanceToSqr(point);
            if (candidate < distance) {
                nearest = new EntityHitResult(entity, point);
                distance = candidate;
            }
        }
        return nearest == null ? net.minecraft.world.phys.BlockHitResult.miss(
            end,
            net.minecraft.core.Direction.getNearest(player.getLookAngle()),
            net.minecraft.core.BlockPos.containing(end)) : nearest;
    }

    public static void irradiate(Entity target, double dose) {
        if (!(target instanceof LivingEntity living)) return;
        var rads = RadiationHelper.getEntityRadiation(living);
        if (rads == null) return;
        rads.setRadiationLevel(RadiationHelper.addRadsToEntity(rads, living, dose, false, false, 1));
        if (rads.isFatal() && !rads.isImmune()) {
            living.hurt(
                living.damageSources()
                    .source(DamageTypeRegistration.FATAL_RADS),
                Float.MAX_VALUE);
        }
    }
}
