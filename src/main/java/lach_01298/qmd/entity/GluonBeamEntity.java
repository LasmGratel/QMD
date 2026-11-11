package lach_01298.qmd.entity;

import lach_01298.qmd.item.weapon.GluonGunItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public final class GluonBeamEntity extends WeaponBeamEntity {

    public GluonBeamEntity(EntityType<? extends GluonBeamEntity> type, Level level) {
        super(type, level, 100);
    }

    @Override
    public void tick() {
        super.tick();
        var player = owner();
        if (player == null) return;
        if (!player.isAlive() || !player.isUsingItem()
            || player.getUsedItemHand() != hand()
            || !(player.getUseItem()
                .getItem() instanceof GluonGunItem gun)) {
            if (!level().isClientSide) discard();
            return;
        }
        lifetime = 100;
        follow(player);
        if (!level().isClientSide) gun.fire(player, hand(), this);
    }
}
