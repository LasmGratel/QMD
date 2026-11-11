package lach_01298.qmd.recipe.chamber;

import com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient;
import com.nred.nuclearcraft.recipe.SizedChanceItemIngredient;
import lach_01298.qmd.particle.ParticleBeam;
import lach_01298.qmd.recipe.ingredient.ParticleIngredient;
import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

/** The registered neutral containment reaction and its fixed physical channels. */
public final class NeutralContainmentRecipe extends AbstractChamberRecipe {

    private static final Serializer<NeutralContainmentRecipe> SERIALIZER = new Serializer<>(
        ChamberKind.NEUTRAL_CONTAINMENT,
        NeutralContainmentRecipe::new);

    public NeutralContainmentRecipe(List<SizedChanceItemIngredient> items, List<SizedChanceFluidIngredient> fluids,
        List<ParticleIngredient> particles, List<SizedChanceItemIngredient> itemProducts,
        List<SizedChanceFluidIngredient> fluidProducts, List<ParticleBeam> particleProducts, double crossSection,
        long energyReleased, double radiation, double heat, double temperature) {
        super(
            ChamberKind.NEUTRAL_CONTAINMENT,
            items,
            fluids,
            particles,
            itemProducts,
            fluidProducts,
            particleProducts,
            crossSection,
            energyReleased,
            radiation,
            heat,
            temperature);
    }

    public static Serializer<NeutralContainmentRecipe> serializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeSerializer<NeutralContainmentRecipe> getSerializer() {
        return QMDRecipes.NEUTRAL_CONTAINMENT_SERIALIZER.get();
    }

    @Override
    public RecipeType<NeutralContainmentRecipe> getType() {
        return QMDRecipes.NEUTRAL_CONTAINMENT_TYPE.get();
    }
}
