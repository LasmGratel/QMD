package lach_01298.qmd.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import lach_01298.qmd.Config;
import net.neoforged.neoforge.common.conditions.ICondition;

/** Select exactly one cooling policy when server recipes are loaded or reloaded. */
public record OverrideNCRecipesCondition(boolean enabled) implements ICondition {
    public static final MapCodec<OverrideNCRecipesCondition> CODEC = Codec.BOOL.fieldOf("enabled")
            .xmap(OverrideNCRecipesCondition::new, OverrideNCRecipesCondition::enabled);

    @Override
    public boolean test(IContext context) {
        return Config.OVERRIDE_NC_RECIPES.get() == enabled;
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
