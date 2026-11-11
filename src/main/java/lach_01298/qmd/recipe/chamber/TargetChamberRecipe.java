package lach_01298.qmd.recipe.chamber;

import com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient;
import com.nred.nuclearcraft.recipe.SizedChanceItemIngredient;
import lach_01298.qmd.particle.ParticleBeam;
import lach_01298.qmd.recipe.ingredient.ParticleIngredient;
import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

/** The registered target chamber reaction and its fixed physical channels. */
public final class TargetChamberRecipe extends AbstractChamberRecipe {

    private static final Serializer<TargetChamberRecipe> SERIALIZER = new Serializer<>(
        ChamberKind.TARGET_CHAMBER,
        TargetChamberRecipe::new);

    public TargetChamberRecipe(List<SizedChanceItemIngredient> items, List<SizedChanceFluidIngredient> fluids,
        List<ParticleIngredient> particles, List<SizedChanceItemIngredient> itemProducts,
        List<SizedChanceFluidIngredient> fluidProducts, List<ParticleBeam> particleProducts, double crossSection,
        long energyReleased, double radiation, double heat, double temperature) {
        super(
            ChamberKind.TARGET_CHAMBER,
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

    public static Serializer<TargetChamberRecipe> serializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeSerializer<TargetChamberRecipe> getSerializer() {
        return QMDRecipes.TARGET_CHAMBER_SERIALIZER.get();
    }

    @Override
    public RecipeType<TargetChamberRecipe> getType() {
        return QMDRecipes.TARGET_CHAMBER_TYPE.get();
    }
}
