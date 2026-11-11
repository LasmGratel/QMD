package lach_01298.qmd.recipe.chamber;

import com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient;
import com.nred.nuclearcraft.recipe.SizedChanceItemIngredient;
import lach_01298.qmd.particle.ParticleBeam;
import lach_01298.qmd.recipe.ingredient.ParticleIngredient;
import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

/** The registered cell filling reaction and its fixed physical channels. */
public final class CellFillingRecipe extends AbstractChamberRecipe {

    private static final Serializer<CellFillingRecipe> SERIALIZER = new Serializer<>(
        ChamberKind.CELL_FILLING,
        CellFillingRecipe::new);

    public CellFillingRecipe(List<SizedChanceItemIngredient> items, List<SizedChanceFluidIngredient> fluids,
        List<ParticleIngredient> particles, List<SizedChanceItemIngredient> itemProducts,
        List<SizedChanceFluidIngredient> fluidProducts, List<ParticleBeam> particleProducts, double crossSection,
        long energyReleased, double radiation, double heat, double temperature) {
        super(
            ChamberKind.CELL_FILLING,
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

    public static Serializer<CellFillingRecipe> serializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeSerializer<CellFillingRecipe> getSerializer() {
        return QMDRecipes.CELL_FILLING_SERIALIZER.get();
    }

    @Override
    public RecipeType<CellFillingRecipe> getType() {
        return QMDRecipes.CELL_FILLING_TYPE.get();
    }
}
