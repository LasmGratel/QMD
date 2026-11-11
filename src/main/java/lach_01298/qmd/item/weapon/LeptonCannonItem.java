package lach_01298.qmd.item.weapon;

import lach_01298.qmd.config.WeaponConfig;
import lach_01298.qmd.entity.LeptonBeamEntity;
import lach_01298.qmd.registry.QMDDamageTypes;
import lach_01298.qmd.registry.QMDEntities;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public final class LeptonCannonItem extends ParticleWeaponItem {

    private static final int[] COLORS = { 0xABABAB, 0x6357FF, 0xFF7E00 };

    public LeptonCannonItem() {
        super("positronium", "muonium", "tauonium");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack gun = player.getItemInHand(hand);
        int slot = ammunitionSlot(player);
        int usage = WeaponConfig.LEPTON_USAGE.get();
        if (!hasAmmunition(player, slot, usage)) return InteractionResultHolder.fail(gun);
        if (!level.isClientSide) {
            int kind = ammunitionKind(
                player.getInventory()
                    .getItem(slot));
            var hit = trace(player, WeaponConfig.LEPTON_RANGE[kind].get(), false);
            LeptonBeamEntity beam = new LeptonBeamEntity(QMDEntities.LEPTON_BEAM.get(), level);
            beam.configure(
                player,
                hand,
                player.getEyePosition()
                    .distanceTo(hit.getLocation()),
                COLORS[kind]);
            if (hit instanceof EntityHitResult target) {
                target.getEntity()
                    .hurt(
                        QMDDamageTypes.source(QMDDamageTypes.LEPTON, beam, player),
                        WeaponConfig.LEPTON_DAMAGE[kind].get()
                            .floatValue());
                irradiate(target.getEntity(), WeaponConfig.LEPTON_RADIATION[kind].get());
            }
            level.addFreshEntity(beam);
            level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                lach_01298.qmd.registry.QMDSounds.LEPTON.get(),
                net.minecraft.sounds.SoundSource.NEUTRAL,
                0.2F,
                1F);
            consume(player, slot, usage);
            player.getCooldowns()
                .addCooldown(this, WeaponConfig.LEPTON_COOLDOWN.get());
        }
        return InteractionResultHolder.sidedSuccess(gun, level.isClientSide);
    }
}
