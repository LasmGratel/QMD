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

public final class AcceleratorSourceRecipe extends AbstractAcceleratorRecipe {

    private static final Serializer<AcceleratorSourceRecipe> SERIALIZER = new Serializer<>(
        AcceleratorSourceRecipe::new);

    public AcceleratorSourceRecipe(Ingredient item, int itemCount, FluidStack fluid, ParticleBeam particle,
        List<SizedChanceItemIngredient> items, List<SizedChanceFluidIngredient> fluids, int heat, int temperature,
        double time) {
        super(AcceleratorRecipeKind.SOURCE, item, itemCount, fluid, particle, items, fluids, heat, temperature, time);
    }

    public static Serializer<AcceleratorSourceRecipe> serializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeSerializer<AcceleratorSourceRecipe> getSerializer() {
        return QMDRecipes.ACCELERATOR_SOURCE_SERIALIZER.get();
    }

    @Override
    public RecipeType<AcceleratorSourceRecipe> getType() {
        return QMDRecipes.ACCELERATOR_SOURCE_TYPE.get();
    }
}
