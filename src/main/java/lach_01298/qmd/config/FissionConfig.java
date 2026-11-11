package lach_01298.qmd.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class FissionConfig {

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue SHIELD_HEAT;
    public static final ModConfigSpec.DoubleValue SHIELD_EFFICIENCY;
    public static final ModConfigSpec.IntValue RTG_POWER;
    static {
        var builder = new ModConfigSpec.Builder();
        SHIELD_HEAT = builder.defineInRange("shield_heat_per_flux", 15D, 0D, 32767D);
        SHIELD_EFFICIENCY = builder.defineInRange("shield_efficiency", 1D, 0D, 255D);
        RTG_POWER = builder.defineInRange("strontium_rtg_power", 50, 0, Integer.MAX_VALUE);
        SPEC = builder.build();
    }

    private FissionConfig() {}
}
