package lach_01298.qmd.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public final class LeptonBeamEntity extends WeaponBeamEntity {

    public LeptonBeamEntity(EntityType<? extends LeptonBeamEntity> type, Level level) {
        super(type, level, 4);
    }
}
