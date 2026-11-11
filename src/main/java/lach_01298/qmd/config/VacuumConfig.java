package lach_01298.qmd.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

/** Cold apparatus and containment hazards, preserving the original defaults. */
public final class VacuumConfig {

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.IntValue MIN_SIZE, MAX_SIZE, ENERGY_CAPACITY, INPUT_CAPACITY, OUTPUT_CAPACITY;
    public static final ModConfigSpec.BooleanValue EXPLOSIONS, GAMMA_FLASH, NUCLEOSYNTHESIS_EXPLOSIONS;
    public static final ModConfigSpec.DoubleValue RADIATION, EXPLOSION_SIZE;
    public static final ModConfigSpec.ConfigValue<List<? extends Integer>> POWER, HEAT, MAX_TEMPERATURE, HEATER_COOLING;
    public static final Map<String, ModConfigSpec.ConfigValue<String>> HEATER_RULES = new LinkedHashMap<>();
    static {
        var b = new ModConfigSpec.Builder();
        b.push("vacuum_chamber");
        MIN_SIZE = b.defineInRange("minimum_interior_size", 5, 5, 7);
        MAX_SIZE = b.defineInRange("maximum_interior_size", 7, 5, 31);
        ENERGY_CAPACITY = b.defineInRange("base_energy_capacity", 40000, 1, Integer.MAX_VALUE);
        INPUT_CAPACITY = b.defineInRange("base_input_tank_capacity", 1000, 1, Integer.MAX_VALUE);
        OUTPUT_CAPACITY = b.defineInRange("base_output_tank_capacity", 1000, 1, Integer.MAX_VALUE);
        POWER = b.defineList("part_power", List.of(400, 500, 500, 500, 1000), v -> v instanceof Integer n && n >= 0);
        HEAT = b.defineList("part_heat", List.of(200, 500, 100, 100, 500), v -> v instanceof Integer n && n >= 0);
        MAX_TEMPERATURE = b.defineList(
            "part_maximum_temperature",
            List.of(110, 110, 110, 110, 110),
            v -> v instanceof Integer n && n >= 0 && n <= 400);
        HEATER_COOLING = b.defineList(
            "heater_heat_removed",
            List.of(5000, 10000, 20000, 40000, 80000, 160000, 320000, 640000),
            v -> v instanceof Integer n && n >= 0);
        b.push("heater_rules");
        int index = 0;
        for (String name : List.of("iron", "redstone", "quartz", "obsidian", "glowstone", "lapis", "gold", "diamond"))
            HEATER_RULES.put(
                name,
                b.define(
                    name,
                    lach_01298.qmd.multiblock.VacuumHeaters.RULES[index++],
                    v -> v instanceof String rule && lach_01298.qmd.multiblock.VacuumHeaters.valid(rule)));
        b.pop();
        EXPLOSIONS = b.define("exotic_containment_explosion", true);
        GAMMA_FLASH = b.define("exotic_containment_gamma_flash", true);
        NUCLEOSYNTHESIS_EXPLOSIONS = b.define("nucleosynthesis_chamber_explosion", true);
        RADIATION = b.defineInRange("exotic_containment_radiation", 1024000D, 0, Double.MAX_VALUE);
        EXPLOSION_SIZE = b.defineInRange("exotic_containment_explosion_size", 50D, 0, 1000D);
        b.pop();
        SPEC = b.build();
    }

    public static int componentIndex(lach_01298.qmd.multiblock.ChamberPart role) {
        return switch (role) {
            case VACUUM_COIL -> 0;
            case VACUUM_BEAM -> 1;
            case PLASMA_GLASS -> 2;
            case PLASMA_NOZZLE -> 3;
            case VACUUM_LASER -> 4;
            default -> -1;
        };
    }

    public static int value(List<? extends Integer> values, int index, int fallback) {
        return index >= 0 && index < values.size() ? values.get(index) : fallback;
    }

    private VacuumConfig() {}
}
