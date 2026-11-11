package lach_01298.qmd.recipe.accelerator;

import com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient;
import com.nred.nuclearcraft.recipe.SizedChanceItemIngredient;
import lach_01298.qmd.particle.ParticleBeam;
import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStack;
import java.util.List;

public final class MassSpectrometerRecipe extends AbstractAcceleratorRecipe {

    private static final Serializer<MassSpectrometerRecipe> SERIALIZER = new Serializer<>(MassSpectrometerRecipe::new);

    public MassSpectrometerRecipe(Ingredient item, int itemCount, FluidStack fluid, ParticleBeam particle,
        List<SizedChanceItemIngredient> items, List<SizedChanceFluidIngredient> fluids, int heat, int temperature,
        double time) {
        super(
            AcceleratorRecipeKind.SPECTROMETER,
            item,
            itemCount,
            fluid,
            particle,
            items,
            fluids,
            heat,
            temperature,
            time);
    }

    public static Serializer<MassSpectrometerRecipe> serializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeSerializer<MassSpectrometerRecipe> getSerializer() {
        return QMDRecipes.MASS_SPECTROMETER_SERIALIZER.get();
    }

    @Override
    public RecipeType<MassSpectrometerRecipe> getType() {
        return QMDRecipes.MASS_SPECTROMETER_TYPE.get();
    }
}
