package lach_01298.qmd.item.equipment;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Set;

/** Server-authoritative drill mining and legacy HEV suit bonuses. */
public final class EquipmentEvents {

    public static final int ARMOR_DAMAGE_COST = 100;
    public static final int JUMP_COST = 100;
    public static final int LONG_JUMP_COST = 250;
    public static final int FALL_COST = 100;
    public static final int EFFECT_COST = 1_000;

    private static final Set<String> PROTECTED_DAMAGE_TYPES = Set.of(
        "radiation",
        "fatal_rads",
        "sulphuric_acid",
        "sulfuric_acid",
        "acid_burn",
        "corium_burn",
        "hot_coolant_burn");
    private static final ThreadLocal<Boolean> MINING_DRILL_PLANE = ThreadLocal.withInitial(() -> false);

    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (MINING_DRILL_PLANE.get() || !(event.getPlayer() instanceof ServerPlayer player)) return;
        if (!(player.getMainHandItem()
            .getItem() instanceof DrillItem)) return;
        HitResult target = player.pick(player.blockInteractionRange(), 0.0F, false);
        if (target instanceof BlockHitResult hit) {
            mineAdditionalBlocks(player, event.getPos(), hit.getDirection());
        }
    }

    static void mineAdditionalBlocks(ServerPlayer player, BlockPos origin, Direction hitFace) {
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof DrillItem drill) || drill.radius() < 1
            || !EquipmentEnergy.has(stack, DrillItem.ENERGY_PER_BLOCK)) return;

        BlockState originState = player.level()
            .getBlockState(origin);
        float originHardness = originState.getDestroySpeed(player.level(), origin);
        MINING_DRILL_PLANE.set(true);
        try {
            for (BlockPos pos : drill.additionalBlocks(origin, hitFace)) {
                BlockState state = player.level()
                    .getBlockState(pos);
                if (state.isAir()) continue;
                float hardness = state.getDestroySpeed(player.level(), pos);
                if (hardness == 0.0F || hardness < 0.0F || hardness > originHardness * 2.0F) continue;
                if (!state.canHarvestBlock(player.level(), pos, player)) continue;
                player.gameMode.destroyBlock(pos);
            }
        } finally {
            MINING_DRILL_PLANE.set(false);
        }
    }

    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) return;

        ItemStack legs = hevPiece(entity, EquipmentSlot.LEGS);
        if (!legs.isEmpty() && EquipmentEnergy.consume(legs, JUMP_COST)) {
            entity.setDeltaMovement(
                entity.getDeltaMovement()
                    .add(0.0D, 0.2D, 0.0D));
        }

        ItemStack chest = hevPiece(entity, EquipmentSlot.CHEST);
        if (entity.isSprinting() && !chest.isEmpty() && EquipmentEnergy.consume(chest, LONG_JUMP_COST)) {
            float angle = entity.getYRot() * Mth.DEG_TO_RAD;
            entity.setDeltaMovement(
                entity.getDeltaMovement()
                    .add(-Mth.sin(angle), 0.0D, Mth.cos(angle)));
        }
    }

    public static void onLivingFall(LivingFallEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) return;
        ItemStack boots = hevPiece(entity, EquipmentSlot.FEET);
        if (boots.isEmpty() || !EquipmentEnergy.has(boots, FALL_COST)) return;
        if (event.getDistance() > 3.0F) EquipmentEnergy.extract(boots, FALL_COST);
        event.setDistance(event.getDistance() - 4.0F);
        event.setDamageMultiplier(0.5F);
    }

    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide || !event.getEffectInstance()
            .is(MobEffects.POISON)) return;
        ItemStack helmet = hevPiece(entity, EquipmentSlot.HEAD);
        if (!helmet.isEmpty() && EquipmentEnergy.consume(helmet, EFFECT_COST)) {
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }
    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;
        ItemStack helmet = hevPiece(player, EquipmentSlot.HEAD);
        if (helmet.isEmpty() || !EquipmentEnergy.has(helmet, EFFECT_COST)) return;
        MobEffectInstance wither = player.getEffect(MobEffects.WITHER);
        if (wither != null && (wither.getAmplifier() > 0 || wither.getDuration() > 80)
            && EquipmentEnergy.consume(helmet, EFFECT_COST)) {
            player.removeEffect(MobEffects.WITHER);
            player.addEffect(new MobEffectInstance(MobEffects.WITHER, 80, 0));
        }
    }

    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!PROTECTED_DAMAGE_TYPES.contains(
            event.getSource()
                .getMsgId()))
            return;
        double protectedFraction = 0.0D;
        for (ItemStack armor : event.getEntity()
            .getArmorSlots()) {
            if (armor.getItem() instanceof HevArmorItem hev) protectedFraction += hev.specialProtection();
        }
        if (protectedFraction > 0.0D) {
            event.setAmount(event.getAmount() * (float) Math.max(0.0D, 1.0D - protectedFraction));
        }
    }

    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        if (event.getEntity()
            .level().isClientSide) return;
        int cost = (int) (ARMOR_DAMAGE_COST * event.getOriginalDamage());
        if (cost <= 0) return;
        for (ItemStack armor : event.getEntity()
            .getArmorSlots()) {
            if (armor.getItem() instanceof HevArmorItem && EquipmentEnergy.stored(armor) > 0) {
                EquipmentEnergy.extract(armor, cost);
            }
        }
    }

    private static ItemStack hevPiece(LivingEntity entity, EquipmentSlot slot) {
        ItemStack stack = entity.getItemBySlot(slot);
        return stack.getItem() instanceof HevArmorItem hev && hev.getType()
            .getSlot() == slot ? stack : ItemStack.EMPTY;
    }

    private EquipmentEvents() {}
}
