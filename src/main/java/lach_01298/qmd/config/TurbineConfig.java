package lach_01298.qmd.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class TurbineConfig {

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue BLADE_EFFICIENCY;
    public static final ModConfigSpec.DoubleValue BLADE_EXPANSION;
    static {
        var builder = new ModConfigSpec.Builder();
        BLADE_EFFICIENCY = builder.defineInRange("super_alloy_blade_efficiency", 1.1D, 0.01D, 15D);
        BLADE_EXPANSION = builder.defineInRange("super_alloy_blade_expansion", 1.3D, 1D, 15D);
        SPEC = builder.build();
    }

    private TurbineConfig() {}
}
