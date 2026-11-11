package lach_01298.qmd.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ParticleCellConfig {

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.IntValue LIFETIME;
    public static final ModConfigSpec.DoubleValue RADIATION;
    public static final ModConfigSpec.DoubleValue EXPLOSION_SIZE;
    static {
        var builder = new ModConfigSpec.Builder();
        LIFETIME = builder.comment("Dropped exotic cell lifetime in ticks. Zero disables cell hazards.")
            .defineInRange("cell_lifetime", 200, 0, 6000);
        RADIATION = builder.defineInRange("cell_radiation", 102400D, 0D, Double.MAX_VALUE);
        EXPLOSION_SIZE = builder.defineInRange("cell_explosion_size", 5D, 0D, 1000D);
        SPEC = builder.build();
    }

    private ParticleCellConfig() {}
}
