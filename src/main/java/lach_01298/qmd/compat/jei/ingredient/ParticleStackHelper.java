package lach_01298.qmd.compat.jei.ingredient;

import lach_01298.qmd.particle.ParticleStack;
import lach_01298.qmd.particle.Particles;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/** Recipe lookup matches species, as legacy did, without conflating a beam's count/energy/focus with its ID. */
public final class ParticleStackHelper implements IIngredientHelper<ParticleStack> {

    @Override
    public IIngredientType<ParticleStack> getIngredientType() {
        return ParticleType.PARTICLE;
    }

    @Override
    public String getDisplayName(ParticleStack ingredient) {
        return Component.translatable(
            ingredient.getParticle()
                .getUnlocalizedName())
            .getString();
    }

    @Override
    @SuppressWarnings("removal")
    public String getUniqueId(ParticleStack ingredient, UidContext context) {
        return getResourceLocation(ingredient).toString();
    }

    @Override
    public Object getUid(ParticleStack ingredient, UidContext context) {
        return getResourceLocation(ingredient);
    }

    @Override
    public Object getGroupingUid(ParticleStack ingredient) {
        return getResourceLocation(ingredient);
    }

    @Override
    public ResourceLocation getResourceLocation(ParticleStack ingredient) {
        return ResourceLocation.fromNamespaceAndPath(
            "qmd",
            ingredient.getParticle()
                .getName());
    }

    @Override
    public long getAmount(ParticleStack ingredient) {
        return ingredient.getAmount();
    }

    @Override
    public ParticleStack copyIngredient(ParticleStack ingredient) {
        return ingredient.copy();
    }

    @Override
    public ParticleStack copyWithAmount(ParticleStack ingredient, long amount) {
        ParticleStack result = ingredient.copy();
        result.setAmount((int) Math.clamp(amount, 0, Integer.MAX_VALUE));
        return result;
    }

    @Override
    public ParticleStack normalizeIngredient(ParticleStack ingredient) {
        return new ParticleStack(ingredient.getParticle());
    }

    @Override
    public boolean isValidIngredient(ParticleStack ingredient) {
        return ingredient != null && !ingredient.isEmpty()
            && Particles.getParticleFromName(
                ingredient.getParticle()
                    .getName())
                == ingredient.getParticle();
    }

    @Override
    public String getErrorInfo(@Nullable ParticleStack ingredient) {
        if (ingredient == null) return "null";
        return "ParticleStack[particle=" + (ingredient.getParticle() == null ? "null"
            : ingredient.getParticle()
                .getName())
            + ", amount="
            + ingredient.getAmount()
            + ", energy="
            + ingredient.getMeanEnergy()
            + ", focus="
            + ingredient.getFocus()
            + "]";
    }
}
