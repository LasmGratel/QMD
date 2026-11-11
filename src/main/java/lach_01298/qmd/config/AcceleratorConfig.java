package lach_01298.qmd.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import lach_01298.qmd.multiblock.accelerator.AcceleratorMaterial;
import lach_01298.qmd.multiblock.accelerator.AcceleratorCoolers;
import java.util.*;

/** Accelerator limits and thermal policy, retaining the legacy defaults. */
public final class AcceleratorConfig {

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.IntValue LINEAR_MIN, LINEAR_MAX, RING_MIN, RING_MAX, ENERGY_CAPACITY,
        HEAT_CAPACITY, INPUT_CAPACITY, OUTPUT_CAPACITY, RING_MIN_ENERGY, DIVERTER_RADIUS, SPECTROMETER_TIME;
    public static final ModConfigSpec.DoubleValue CONDUCTIVITY;
    public static final ModConfigSpec.BooleanValue EXPLOSIONS;
    public static final ModConfigSpec.IntValue SPECTROMETER_POWER, SPECTROMETER_HEAT;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SPECTROMETER_MAGNETS, SPECTROMETER_SOURCES;
    public static final Map<AcceleratorMaterial, MaterialSettings> MATERIALS = new EnumMap<>(AcceleratorMaterial.class);
    public static final Map<String, CoolerSettings> COOLERS = new LinkedHashMap<>();
    public static final SourceSettings[] SOURCES = new SourceSettings[2];

    public record MaterialSettings(ModConfigSpec.IntValue voltage, ModConfigSpec.DoubleValue strength,
        ModConfigSpec.DoubleValue cavityEfficiency, ModConfigSpec.DoubleValue magnetEfficiency,
        ModConfigSpec.IntValue cavityHeat, ModConfigSpec.IntValue magnetHeat, ModConfigSpec.IntValue cavityPower,
        ModConfigSpec.IntValue magnetPower, ModConfigSpec.IntValue cavityTemperature,
        ModConfigSpec.IntValue magnetTemperature) {}

    public record CoolerSettings(ModConfigSpec.IntValue cooling, ModConfigSpec.ConfigValue<String> rule) {}

    public record SourceSettings(ModConfigSpec.IntValue power, ModConfigSpec.IntValue multiplier,
        ModConfigSpec.DoubleValue focus) {}

    static {
        var b = new ModConfigSpec.Builder();
        b.push("accelerator");
        LINEAR_MIN = b.defineInRange("linear_min_size", 6, 6, 255);
        LINEAR_MAX = b.defineInRange("linear_max_size", 100, 6, 255);
        RING_MIN = b.defineInRange("ring_min_size", 11, 11, 255);
        RING_MAX = b.defineInRange("ring_max_size", 100, 11, 255);
        ENERGY_CAPACITY = b.defineInRange("base_energy_capacity", 40000, 1, Integer.MAX_VALUE);
        HEAT_CAPACITY = b.defineInRange("base_heat_capacity", 25000, 1, Integer.MAX_VALUE);
        INPUT_CAPACITY = b.defineInRange("base_input_tank_capacity", 10, 1, Integer.MAX_VALUE);
        OUTPUT_CAPACITY = b.defineInRange("base_output_tank_capacity", 3200, 1, Integer.MAX_VALUE);
        RING_MIN_ENERGY = b.defineInRange("minimum_ring_input_energy", 5000, 0, Integer.MAX_VALUE);
        DIVERTER_RADIUS = b.defineInRange("beam_diverter_radius", 160, 1, 1000);
        SPECTROMETER_TIME = b.defineInRange("mass_spectrometer_process_time", 1000, 1, Integer.MAX_VALUE);
        SPECTROMETER_POWER = b.defineInRange("mass_spectrometer_power_usage", 12000, 0, Integer.MAX_VALUE);
        SPECTROMETER_HEAT = b.defineInRange("mass_spectrometer_heat", 3000, 0, Integer.MAX_VALUE);
        SPECTROMETER_MAGNETS = b.defineList(
            "mass_spectrometer_valid_magnets",
            List.of("bscco", "ybco", "hbcco"),
            v -> v instanceof String s && Arrays.stream(AcceleratorMaterial.values())
                .anyMatch(m -> m.id.equals(s)));
        SPECTROMETER_SOURCES = b.defineList(
            "mass_spectrometer_valid_sources",
            List.of("basic", "laser"),
            v -> v instanceof String s && List.of("basic", "laser")
                .contains(s));
        CONDUCTIVITY = b.defineInRange("thermal_conductivity", 0.0025, 0, Double.MAX_VALUE);
        EXPLOSIONS = b.define("quench_explosions", true);
        for (var m : AcceleratorMaterial.values()) {
            b.push("materials")
                .push(m.id);
            MATERIALS.put(
                m,
                new MaterialSettings(
                    b.defineInRange("voltage", m.voltage, 0, Integer.MAX_VALUE),
                    b.defineInRange("strength", m.strength, 0, 100),
                    b.defineInRange("cavity_efficiency", m.efficiency, .01, 100),
                    b.defineInRange("magnet_efficiency", m.efficiency, .01, 100),
                    b.defineInRange("cavity_heat", m.heat, 0, Integer.MAX_VALUE),
                    b.defineInRange("magnet_heat", m.heat, 0, Integer.MAX_VALUE),
                    b.defineInRange("cavity_power", m.cavityPower, 0, Integer.MAX_VALUE),
                    b.defineInRange("magnet_power", m.magnetPower, 0, Integer.MAX_VALUE),
                    b.defineInRange("cavity_max_temperature", m.maxTemperature, 0, 400),
                    b.defineInRange("magnet_max_temperature", m.maxTemperature, 0, 400)));
            b.pop(2);
        }
        for (int n = 0; n < AcceleratorCoolers.NAMES.length; n++) {
            b.push("coolers")
                .push(AcceleratorCoolers.NAMES[n]);
            COOLERS.put(
                AcceleratorCoolers.NAMES[n],
                new CoolerSettings(
                    b.defineInRange("cooling", AcceleratorCoolers.COOLING[n], 0, Integer.MAX_VALUE),
                    b.define(
                        "rule",
                        AcceleratorCoolers.RULES[n],
                        value -> value instanceof String rule && AcceleratorCoolers.validRule(rule))));
            b.pop(2);
        }
        for (int n = 0; n < SOURCES.length; n++) {
            b.push(n == 0 ? "basic_ion_source" : "laser_ion_source");
            SOURCES[n] = new SourceSettings(
                b.defineInRange("power", n == 0 ? 500 : 2000, 0, Integer.MAX_VALUE),
                b.defineInRange("output_multiplier", n + 1, 1, 100000),
                b.defineInRange("focus", n == 0 ? .4 : .2, 0, Double.MAX_VALUE));
            b.pop();
        }
        b.pop();
        SPEC = b.build();
    }

    private AcceleratorConfig() {}
}
