package lach_01298.qmd.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class QMDSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, "qmd");
    public static final DeferredHolder<SoundEvent, SoundEvent> LEPTON = sound("neutral.qmd.lepton_cannon");
    public static final DeferredHolder<SoundEvent, SoundEvent> GLUON = sound("neutral.qmd.gluon_gun");
    public static final DeferredHolder<SoundEvent, SoundEvent> GLUON_START = sound("neutral.qmd.gluon_gun_start");
    public static final DeferredHolder<SoundEvent, SoundEvent> GLUON_STOP = sound("neutral.qmd.gluon_gun_stop");

    private static DeferredHolder<SoundEvent, SoundEvent> sound(String name) {
        return SOUNDS.register(
            name,
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("qmd", name)));
    }

    private QMDSounds() {}
}
