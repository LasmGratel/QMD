package lach_01298.qmd.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Legacy weapon tuning, in particle-cell units and ticks. */
public final class WeaponConfig {

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue[] LEPTON_DAMAGE = new ModConfigSpec.DoubleValue[3];
    public static final ModConfigSpec.DoubleValue[] LEPTON_RADIATION = new ModConfigSpec.DoubleValue[3];
    public static final ModConfigSpec.DoubleValue[] LEPTON_RANGE = new ModConfigSpec.DoubleValue[3];
    public static final ModConfigSpec.IntValue LEPTON_USAGE, LEPTON_COOLDOWN, GLUON_USAGE, ANTIMATTER_USAGE,
        ANTIMATTER_COOLDOWN;
    public static final ModConfigSpec.DoubleValue GLUON_DAMAGE, GLUON_RADIATION, GLUON_RANGE, ANTIMATTER_DAMAGE;
    public static final ModConfigSpec.DoubleValue ANTIMATTER_EXPLOSION;
    static {
        var builder = new ModConfigSpec.Builder();
        String[] names = { "positronium", "muonium", "tauonium" };
        for (int i = 0; i < names.length; i++) {
            builder.push(names[i]);
            LEPTON_DAMAGE[i] = builder.defineInRange("damage", 7D * (1 << i), 0D, Double.MAX_VALUE);
            LEPTON_RADIATION[i] = builder.defineInRange("radiation", 10D * (1 << i), 0D, Double.MAX_VALUE);
            LEPTON_RANGE[i] = builder.defineInRange("range", 30D * (i + 1), 0D, 1024D);
            builder.pop();
        }
        LEPTON_USAGE = builder.defineInRange("lepton_cannon_use", 500, 1, 100_000);
        LEPTON_COOLDOWN = builder.defineInRange("lepton_cannon_cool_down", 8, 0, 72000);
        GLUON_USAGE = builder.defineInRange("gluon_gun_use", 10, 1, 100_000);
        GLUON_DAMAGE = builder.defineInRange("gluon_gun_damage", 10D, 0D, Double.MAX_VALUE);
        GLUON_RADIATION = builder.defineInRange("gluon_gun_radiation", 10D, 0D, Double.MAX_VALUE);
        GLUON_RANGE = builder.defineInRange("gluon_gun_range", 40D, 0D, 1024D);
        ANTIMATTER_USAGE = builder.defineInRange("antimatter_launcher_use", 5000, 1, 100_000);
        ANTIMATTER_COOLDOWN = builder.defineInRange("antimatter_launcher_cool_down", 30, 0, 72000);
        ANTIMATTER_DAMAGE = builder.defineInRange("antimatter_launcher_damage", 20D, 0D, Double.MAX_VALUE);
        ANTIMATTER_EXPLOSION = builder.defineInRange("antimatter_launcher_explosion_size", 2.5D, 0D, 1000D);
        SPEC = builder.build();
    }

    private WeaponConfig() {}
}
