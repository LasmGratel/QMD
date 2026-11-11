package lach_01298.qmd.config;

import lach_01298.qmd.multiblock.ChamberPart;
import lach_01298.qmd.recipe.chamber.ChamberKind;
import net.neoforged.neoforge.common.ModConfigSpec;
import java.util.*;

/** Server-owned particle chamber power, capacities and detector balance. */
public final class ChamberConfig {

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.IntValue ENERGY_CAPACITY, INPUT_CAPACITY, OUTPUT_CAPACITY, TARGET_SCALE,
        NUCLEOSYNTHESIS_SCALE;
    public static final Map<ChamberKind, ModConfigSpec.IntValue> POWER = new EnumMap<>(ChamberKind.class);
    public static final Map<ChamberPart, Detector> DETECTORS = new EnumMap<>(ChamberPart.class);

    public record Detector(ModConfigSpec.IntValue power, ModConfigSpec.DoubleValue efficiency) {}

    static {
        var b = new ModConfigSpec.Builder();
        b.push("particle_chamber");
        ENERGY_CAPACITY = b.defineInRange("base_energy_capacity", 40000, 1, Integer.MAX_VALUE);
        INPUT_CAPACITY = b.defineInRange("base_input_tank_capacity", 16000, 1, Integer.MAX_VALUE);
        OUTPUT_CAPACITY = b.defineInRange("base_output_tank_capacity", 1000, 1, Integer.MAX_VALUE);
        for (var kind : List.of(
            ChamberKind.TARGET_CHAMBER,
            ChamberKind.DECAY_CHAMBER,
            ChamberKind.COLLISION_CHAMBER,
            ChamberKind.BEAM_DUMP)) POWER.put(kind, b.defineInRange(kind.id() + "_power", 5000, 0, Integer.MAX_VALUE));
        for (var part : ChamberPart.values()) if (part.detector()) {
            b.push(part.id);
            DETECTORS.put(
                part,
                new Detector(
                    b.defineInRange("power", part.power, 0, Integer.MAX_VALUE),
                    b.defineInRange("efficiency", part.efficiency, 0, 100)));
            b.pop();
        }
        b.pop();
        b.push("recipes");
        TARGET_SCALE = b.defineInRange("rsf_target_chamber", 100, 1, Integer.MAX_VALUE);
        NUCLEOSYNTHESIS_SCALE = b.defineInRange("rsf_nucleosynthesis", 100, 1, Integer.MAX_VALUE);
        b.pop();
        SPEC = b.build();
    }

    public static long scaledWork(ChamberKind kind, long amount) {
        int scale = kind == ChamberKind.TARGET_CHAMBER ? TARGET_SCALE.get()
            : kind == ChamberKind.NUCLEOSYNTHESIS_CHAMBER ? NUCLEOSYNTHESIS_SCALE.get() : 100;
        return Math.max(
            1,
            java.math.BigInteger.valueOf(amount)
                .multiply(java.math.BigInteger.valueOf(scale))
                .divide(java.math.BigInteger.valueOf(100))
                .min(java.math.BigInteger.valueOf(Long.MAX_VALUE))
                .longValue());
    }

    private ChamberConfig() {}
}
