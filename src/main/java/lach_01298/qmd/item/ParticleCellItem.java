package lach_01298.qmd.item;

import lach_01298.qmd.config.ParticleCellConfig;
import lach_01298.qmd.entity.GammaFlashEntity;
import lach_01298.qmd.registry.QMDItems;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public final class ParticleCellItem extends ParticleReservoirItem {

    private final String species;
    private final double mass;

    public ParticleCellItem(String species, double mass) {
        super(new Properties().stacksTo(1), 100_000, () -> new ItemStack(QMDItems.EMPTY_CELL.get()));
        this.species = species;
        this.mass = mass;
    }

    public String species() {
        return species;
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (!entity.level().isClientSide && ParticleCellConfig.LIFETIME.get() > 0
            && (entity.getAge() > ParticleCellConfig.LIFETIME.get() || entity.isInLava() || entity.isOnFire())) {
            detonate(entity);
            entity.discard();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyed(ItemEntity entity, DamageSource damage) {
        if (!entity.level().isClientSide && ParticleCellConfig.LIFETIME.get() > 0) detonate(entity);
    }

    private void detonate(ItemEntity entity) {
        ItemStack stack = entity.getItem();
        double size = mass * stored(stack) / capacity() * stack.getCount();
        // Clear before creating the explosion, which can damage nearby item entities recursively.
        stack.setCount(0);
        if (size > 0) GammaFlashEntity.create(
            entity.level(),
            entity.position(),
            size,
            (float) (size * ParticleCellConfig.EXPLOSION_SIZE.get()),
            size * ParticleCellConfig.RADIATION.get());
    }
}
