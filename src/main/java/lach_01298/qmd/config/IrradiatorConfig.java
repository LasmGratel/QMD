package lach_01298.qmd.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class IrradiatorConfig {

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.IntValue PROCESS_TIME;
    public static final ModConfigSpec.IntValue FUEL_USAGE;
    public static final ModConfigSpec.DoubleValue RADIATION_RESISTANCE;
    static {
        var builder = new ModConfigSpec.Builder();
        PROCESS_TIME = builder.defineInRange("process_time", 200, 1, Integer.MAX_VALUE);
        FUEL_USAGE = builder.comment("Particle units consumed per unit of irradiation work.")
            .defineInRange("fuel_usage", 10, 0, Integer.MAX_VALUE);
        RADIATION_RESISTANCE = builder.defineInRange("radiation_resistance", 10000D, 0D, Double.MAX_VALUE);
        SPEC = builder.build();
    }

    private IrradiatorConfig() {}
}
