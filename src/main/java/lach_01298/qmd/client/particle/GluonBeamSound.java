package lach_01298.qmd.client.particle;

import lach_01298.qmd.entity.GluonBeamEntity;
import lach_01298.qmd.registry.QMDSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

/** Moving continuous sound; start and stop stay tied to this beam instance. */
public final class GluonBeamSound extends AbstractTickableSoundInstance {

    private final GluonBeamEntity beam;

    private GluonBeamSound(GluonBeamEntity beam) {
        super(QMDSounds.GLUON.get(), SoundSource.NEUTRAL, RandomSource.create());
        this.beam = beam;
        looping = true;
        volume = 0.1F;
        x = beam.getX();
        y = beam.getY();
        z = beam.getZ();
    }

    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide && event.getEntity() instanceof GluonBeamEntity beam) {
            var manager = Minecraft.getInstance()
                .getSoundManager();
            manager.play(
                new SimpleSoundInstance(
                    QMDSounds.GLUON_START.get(),
                    SoundSource.NEUTRAL,
                    0.1F,
                    1F,
                    RandomSource.create(),
                    beam.blockPosition()));
            manager.play(new GluonBeamSound(beam));
        }
    }

    @Override
    public void tick() {
        x = beam.getX();
        y = beam.getY();
        z = beam.getZ();
        if (beam.isRemoved()) {
            stop();
            Minecraft.getInstance()
                .getSoundManager()
                .play(
                    new SimpleSoundInstance(
                        QMDSounds.GLUON_STOP.get(),
                        SoundSource.NEUTRAL,
                        0.1F,
                        1F,
                        RandomSource.create(),
                        beam.blockPosition()));
        }
    }
}
