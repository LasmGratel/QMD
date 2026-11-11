package lach_01298.qmd.recipe.chamber;

import com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient;
import com.nred.nuclearcraft.recipe.SizedChanceItemIngredient;
import lach_01298.qmd.particle.ParticleBeam;
import lach_01298.qmd.recipe.ingredient.ParticleIngredient;
import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

/** The registered vacuum chamber heating reaction and its fixed physical channels. */
public final class VacuumChamberHeatingRecipe extends AbstractChamberRecipe {

    private static final Serializer<VacuumChamberHeatingRecipe> SERIALIZER = new Serializer<>(
        ChamberKind.VACUUM_CHAMBER_HEATING,
        VacuumChamberHeatingRecipe::new);

    public VacuumChamberHeatingRecipe(List<SizedChanceItemIngredient> items, List<SizedChanceFluidIngredient> fluids,
        List<ParticleIngredient> particles, List<SizedChanceItemIngredient> itemProducts,
        List<SizedChanceFluidIngredient> fluidProducts, List<ParticleBeam> particleProducts, double crossSection,
        long energyReleased, double radiation, double heat, double temperature) {
        super(
            ChamberKind.VACUUM_CHAMBER_HEATING,
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

    public static Serializer<VacuumChamberHeatingRecipe> serializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeSerializer<VacuumChamberHeatingRecipe> getSerializer() {
        return QMDRecipes.VACUUM_CHAMBER_HEATING_SERIALIZER.get();
    }

    @Override
    public RecipeType<VacuumChamberHeatingRecipe> getType() {
        return QMDRecipes.VACUUM_CHAMBER_HEATING_TYPE.get();
    }
}
