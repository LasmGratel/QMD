package lach_01298.qmd.recipe.chamber;

import com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient;
import com.nred.nuclearcraft.recipe.SizedChanceItemIngredient;
import lach_01298.qmd.particle.ParticleBeam;
import lach_01298.qmd.recipe.ingredient.ParticleIngredient;
import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

/** The registered decay chamber reaction and its fixed physical channels. */
public final class DecayChamberRecipe extends AbstractChamberRecipe {

    private static final Serializer<DecayChamberRecipe> SERIALIZER = new Serializer<>(
        ChamberKind.DECAY_CHAMBER,
        DecayChamberRecipe::new);

    public DecayChamberRecipe(List<SizedChanceItemIngredient> items, List<SizedChanceFluidIngredient> fluids,
        List<ParticleIngredient> particles, List<SizedChanceItemIngredient> itemProducts,
        List<SizedChanceFluidIngredient> fluidProducts, List<ParticleBeam> particleProducts, double crossSection,
        long energyReleased, double radiation, double heat, double temperature) {
        super(
            ChamberKind.DECAY_CHAMBER,
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

    public static Serializer<DecayChamberRecipe> serializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeSerializer<DecayChamberRecipe> getSerializer() {
        return QMDRecipes.DECAY_CHAMBER_SERIALIZER.get();
    }

    @Override
    public RecipeType<DecayChamberRecipe> getType() {
        return QMDRecipes.DECAY_CHAMBER_TYPE.get();
    }
}
