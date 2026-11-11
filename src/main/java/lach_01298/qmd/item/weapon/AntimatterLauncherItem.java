package lach_01298.qmd.item.weapon;

import lach_01298.qmd.config.WeaponConfig;
import lach_01298.qmd.entity.AntimatterProjectileEntity;
import lach_01298.qmd.registry.QMDEntities;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class AntimatterLauncherItem extends ParticleWeaponItem {

    private static final int[] MASSES = { 1, 2, 3, 3, 4 };
    private static final int[] COLORS = { 0xB37AC4, 0x9E6FEF, 0x5DBBD6, 0xCBBB67, 0xC57B81 };

    public AntimatterLauncherItem() {
        super("antihydrogen", "antideuterium", "antitritium", "antihelium3", "antihelium");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack gun = player.getItemInHand(hand);
        int slot = ammunitionSlot(player);
        int usage = WeaponConfig.ANTIMATTER_USAGE.get();
        if (!hasAmmunition(player, slot, usage)) return InteractionResultHolder.fail(gun);
        if (!level.isClientSide) {
            int kind = ammunitionKind(
                player.getInventory()
                    .getItem(slot));
            AntimatterProjectileEntity projectile = new AntimatterProjectileEntity(
                QMDEntities.ANTIMATTER_PROJECTILE.get(),
                level);
            projectile.configure(player, MASSES[kind], COLORS[kind]);
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 3, 1);
            level.addFreshEntity(projectile);
            consume(player, slot, usage);
            player.getCooldowns()
                .addCooldown(this, WeaponConfig.ANTIMATTER_COOLDOWN.get());
        }
        return InteractionResultHolder.sidedSuccess(gun, level.isClientSide);
    }
}
