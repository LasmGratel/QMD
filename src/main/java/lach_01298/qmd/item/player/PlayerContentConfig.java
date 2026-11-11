package lach_01298.qmd.item.player;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class PlayerContentConfig {

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.IntValue KI_TIME;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        KI_TIME = builder.comment("Radiation immunity time, in ticks, granted by a potassium iodine tablet.")
            .defineInRange("ki_time", 400, 1, Integer.MAX_VALUE);
        SPEC = builder.build();
    }

    private PlayerContentConfig() {}
}
