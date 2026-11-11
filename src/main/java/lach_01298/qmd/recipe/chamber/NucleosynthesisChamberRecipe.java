package lach_01298.qmd.recipe.chamber;

import com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient;
import com.nred.nuclearcraft.recipe.SizedChanceItemIngredient;
import lach_01298.qmd.particle.ParticleBeam;
import lach_01298.qmd.recipe.ingredient.ParticleIngredient;
import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

/** The registered nucleosynthesis chamber reaction and its fixed physical channels. */
public final class NucleosynthesisChamberRecipe extends AbstractChamberRecipe {

    private static final Serializer<NucleosynthesisChamberRecipe> SERIALIZER = new Serializer<>(
        ChamberKind.NUCLEOSYNTHESIS_CHAMBER,
        NucleosynthesisChamberRecipe::new);

    public NucleosynthesisChamberRecipe(List<SizedChanceItemIngredient> items, List<SizedChanceFluidIngredient> fluids,
        List<ParticleIngredient> particles, List<SizedChanceItemIngredient> itemProducts,
        List<SizedChanceFluidIngredient> fluidProducts, List<ParticleBeam> particleProducts, double crossSection,
        long energyReleased, double radiation, double heat, double temperature) {
        super(
            ChamberKind.NUCLEOSYNTHESIS_CHAMBER,
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

    public static Serializer<NucleosynthesisChamberRecipe> serializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeSerializer<NucleosynthesisChamberRecipe> getSerializer() {
        return QMDRecipes.NUCLEOSYNTHESIS_CHAMBER_SERIALIZER.get();
    }

    @Override
    public RecipeType<NucleosynthesisChamberRecipe> getType() {
        return QMDRecipes.NUCLEOSYNTHESIS_CHAMBER_TYPE.get();
    }
}
