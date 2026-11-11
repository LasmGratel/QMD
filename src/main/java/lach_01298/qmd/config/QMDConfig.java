package lach_01298.qmd.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class QMDConfig {

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue BEAM_ATTENUATION_RATE;
    public static final ModConfigSpec.IntValue BEAM_SCALING;
    public static final ModConfigSpec.IntValue ORE_LEACHER_TIME;
    public static final ModConfigSpec.IntValue ORE_LEACHER_POWER;
    public static final ModConfigSpec.IntValue LIQUID_COLLECTOR_POWER;
    public static final ModConfigSpec.IntValue ATMOSPHERE_COLLECTOR_POWER;
    public static final ModConfigSpec.BooleanValue OVERRIDE_NC_RECIPES;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("accelerator");
        BEAM_ATTENUATION_RATE = builder.comment("Beam focus lost per block. Legacy default: 0.02.")
            .defineInRange("beam_attenuation_rate", 0.02D, 0.0D, 1000.0D);
        builder.pop();
        builder.push("other");
        BEAM_SCALING = builder.comment("Particle count scale used by beam physics. Legacy default: 10000.")
            .defineInRange("beam_scaling", 10000, 1, Integer.MAX_VALUE);
        builder.pop();
        builder.push("processors");
        ORE_LEACHER_TIME = builder.comment("Base ore leacher processing time in ticks.")
            .defineInRange("ore_leacher_time", 400, 1, Integer.MAX_VALUE);
        ORE_LEACHER_POWER = builder.comment("Base ore leacher energy use in FE per tick.")
            .defineInRange("ore_leacher_power", 50, 0, Integer.MAX_VALUE);
        LIQUID_COLLECTOR_POWER = builder.comment("Liquid collector energy use in FE per productive tick.")
            .defineInRange("liquid_collector_power", 50, 0, Integer.MAX_VALUE);
        ATMOSPHERE_COLLECTOR_POWER = builder.comment("Atmosphere collector energy use in FE per productive tick.")
            .defineInRange("atmosphere_collector_power", 100, 0, Integer.MAX_VALUE);
        builder.pop();
        builder.push("recipes");
        OVERRIDE_NC_RECIPES = builder
            .comment(
                "Use legacy QMD cooling ratios for the migrated gas recipes. Takes effect on server resource reload.")
            .define("override_nc_recipes", true);
        builder.pop();
        SPEC = builder.build();
    }

    private QMDConfig() {}
}
