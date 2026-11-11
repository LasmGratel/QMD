package lach_01298.qmd.item.equipment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import lach_01298.qmd.config.EquipmentConfig;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.Locale;

/** Datapack condition mirroring the two legacy equipment registration switches. */
public record EquipmentEnabledCondition(Kind equipment) implements ICondition {

    private static final Codec<Kind> KIND_CODEC = Codec.STRING.comapFlatMap(Kind::decode, Kind::serializedName);
    public static final MapCodec<EquipmentEnabledCondition> CODEC = KIND_CODEC.fieldOf("equipment")
        .xmap(EquipmentEnabledCondition::new, EquipmentEnabledCondition::equipment);

    @Override
    public boolean test(IContext context) {
        return equipment == Kind.TOOLS ? EquipmentConfig.toolsEnabled() : EquipmentConfig.drillsEnabled();
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }

    public enum Kind {

        TOOLS,
        DRILLS;

        String serializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        static DataResult<Kind> decode(String name) {
            for (Kind kind : values()) {
                if (kind.serializedName()
                    .equals(name)) return DataResult.success(kind);
            }
            return DataResult.error(() -> "Unknown QMD equipment group: " + name);
        }
    }
}
