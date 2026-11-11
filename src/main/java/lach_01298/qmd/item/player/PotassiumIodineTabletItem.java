package lach_01298.qmd.item.player;

import com.nred.nuclearcraft.radiation.RadiationHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class PotassiumIodineTabletItem extends Item {

    private static final FoodProperties FOOD = new FoodProperties.Builder().nutrition(0)
        .saturationModifier(0F)
        .alwaysEdible()
        .build();

    public PotassiumIodineTabletItem(Properties properties) {
        super(properties.food(FOOD));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        ItemStack remainder = super.finishUsingItem(stack, level, livingEntity);
        if (!level.isClientSide && livingEntity instanceof Player player) {
            applyDose(player, PlayerContentConfig.KI_TIME.get());
        }
        return remainder;
    }

    public static double nextImmunityTime(double currentTime, int lifetime) {
        return currentTime > lifetime ? currentTime + (double) lifetime * lifetime / currentTime
            : currentTime + lifetime;
    }

    public static boolean applyDose(Player player, int lifetime) {
        var radiation = RadiationHelper.getEntityRadiation(player);
        if (radiation == null) return false;

        double currentTime = radiation.getRadiationImmunityTime();
        double newTime = nextImmunityTime(currentTime, lifetime);
        radiation.setRadiationImmunityTime(newTime);
        applyDoseEffects(player, currentTime, lifetime, newTime);
        return true;
    }

    public static void applyDoseEffects(Player player, double currentTime, int lifetime, double newTime) {
        int duration = effectDuration(newTime);
        addEffect(player, MobEffects.WEAKNESS, duration, 0);

        if (currentTime > lifetime * 1.5D) {
            addEffect(player, MobEffects.HUNGER, duration, 0);
        }
        if (currentTime > lifetime * 2D) {
            int doubledDuration = doubledDuration(duration);
            addEffect(player, MobEffects.HUNGER, doubledDuration, 0);
            addEffect(player, MobEffects.WEAKNESS, doubledDuration, 0);
            addEffect(player, MobEffects.MOVEMENT_SLOWDOWN, doubledDuration, 0);
        }
        if (currentTime > lifetime * 3D) {
            int doubledDuration = doubledDuration(duration);
            addEffect(player, MobEffects.HUNGER, doubledDuration, 1);
            addEffect(player, MobEffects.WEAKNESS, doubledDuration, 1);
            addEffect(player, MobEffects.MOVEMENT_SLOWDOWN, doubledDuration, 1);
            addEffect(player, MobEffects.POISON, Math.max(1, duration / 2), 0);
        }
        if (currentTime > lifetime * 4D) {
            addEffect(player, MobEffects.CONFUSION, duration, 0);
            player.hurt(
                player.damageSources()
                    .magic(),
                10F);
        }
        if (currentTime > lifetime * 4.5D) {
            player.hurt(
                player.damageSources()
                    .magic(),
                player.getHealth());
        }
    }

    private static int effectDuration(double duration) {
        if (!Double.isFinite(duration) || duration >= Integer.MAX_VALUE) return Integer.MAX_VALUE;
        return Math.max(1, (int) duration);
    }

    private static int doubledDuration(int duration) {
        return duration > Integer.MAX_VALUE / 2 ? Integer.MAX_VALUE : duration * 2;
    }

    private static void addEffect(Player player, net.minecraft.core.Holder<MobEffect> effect, int duration,
        int amplifier) {
        player.addEffect(new MobEffectInstance(effect, duration, amplifier));
    }
}
