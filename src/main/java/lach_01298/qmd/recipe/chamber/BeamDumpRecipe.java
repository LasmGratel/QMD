package lach_01298.qmd.recipe.chamber;

import com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient;
import com.nred.nuclearcraft.recipe.SizedChanceItemIngredient;
import lach_01298.qmd.particle.ParticleBeam;
import lach_01298.qmd.recipe.ingredient.ParticleIngredient;
import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

/** The registered beam dump reaction and its fixed physical channels. */
public final class BeamDumpRecipe extends AbstractChamberRecipe {

    private static final Serializer<BeamDumpRecipe> SERIALIZER = new Serializer<>(
        ChamberKind.BEAM_DUMP,
        BeamDumpRecipe::new);

    public BeamDumpRecipe(List<SizedChanceItemIngredient> items, List<SizedChanceFluidIngredient> fluids,
        List<ParticleIngredient> particles, List<SizedChanceItemIngredient> itemProducts,
        List<SizedChanceFluidIngredient> fluidProducts, List<ParticleBeam> particleProducts, double crossSection,
        long energyReleased, double radiation, double heat, double temperature) {
        super(
            ChamberKind.BEAM_DUMP,
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

    public static Serializer<BeamDumpRecipe> serializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeSerializer<BeamDumpRecipe> getSerializer() {
        return QMDRecipes.BEAM_DUMP_SERIALIZER.get();
    }

    @Override
    public RecipeType<BeamDumpRecipe> getType() {
        return QMDRecipes.BEAM_DUMP_TYPE.get();
    }
}
