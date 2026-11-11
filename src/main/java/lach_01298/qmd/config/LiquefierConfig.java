package lach_01298.qmd.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Server settings retained from the original liquefier. */
public final class LiquefierConfig {

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.IntValue ENERGY_DENSITY, GAS_DENSITY, LIQUID_DENSITY;
    public static final ModConfigSpec.DoubleValue NOZZLE_SPEED;
    public static final ModConfigSpec.DoubleValue[] ENERGY_EFFICIENCY = new ModConfigSpec.DoubleValue[3];
    public static final ModConfigSpec.DoubleValue[] HEAT_EFFICIENCY = new ModConfigSpec.DoubleValue[3];

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        ENERGY_DENSITY = builder.defineInRange("energy_capacity_per_exterior_block", 1000, 1, 1_000_000);
        GAS_DENSITY = builder.defineInRange("gas_input_capacity_per_exterior_block", 640, 1, 1_000_000);
        LIQUID_DENSITY = builder.defineInRange("liquid_output_capacity_per_exterior_block", 10, 1, 1_000_000);
        NOZZLE_SPEED = builder.defineInRange("nozzle_speed", 0.4D, 0.000001D, 1_000_000D);
        String[] materials = { "copper", "neodymium", "samarium_cobalt" };
        double[] energy = { 0.9, 0.95, 0.85 }, heat = { 0.9, 0.85, 0.95 };
        for (int i = 0; i < materials.length; i++) {
            builder.push(materials[i]);
            ENERGY_EFFICIENCY[i] = builder.defineInRange("energy_efficiency", energy[i], 0.000001D, 1D);
            HEAT_EFFICIENCY[i] = builder.defineInRange("heat_efficiency", heat[i], 0.000001D, 1D);
            builder.pop();
        }
        SPEC = builder.build();
    }

    private LiquefierConfig() {}
}
