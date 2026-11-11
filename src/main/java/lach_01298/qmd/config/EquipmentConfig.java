package lach_01298.qmd.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Registration-era equipment switches retained from the legacy tools configuration. */
public final class EquipmentConfig {

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue ENABLE_TUNGSTEN_CARBIDE_TOOLS;
    public static final ModConfigSpec.BooleanValue ENABLE_DRILLS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("equipment");
        ENABLE_TUNGSTEN_CARBIDE_TOOLS = builder
            .comment("Enable tungsten-carbide tool recipes and creative-tab entries. Legacy default: true.")
            .gameRestart()
            .define("enable_tungsten_carbide_tools", true);
        ENABLE_DRILLS = builder
            .comment("Enable basic and advanced drill recipes and creative-tab entries. Legacy default: true.")
            .gameRestart()
            .define("enable_drills", true);
        builder.pop();
        SPEC = builder.build();
    }

    public static boolean toolsEnabled() {
        return SPEC.isLoaded() ? ENABLE_TUNGSTEN_CARBIDE_TOOLS.get() : ENABLE_TUNGSTEN_CARBIDE_TOOLS.getDefault();
    }

    public static boolean drillsEnabled() {
        return SPEC.isLoaded() ? ENABLE_DRILLS.get() : ENABLE_DRILLS.getDefault();
    }

    private EquipmentConfig() {}
}
