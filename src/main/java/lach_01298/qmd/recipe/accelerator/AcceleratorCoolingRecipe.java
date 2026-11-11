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

public final class AcceleratorCoolingRecipe extends AbstractAcceleratorRecipe {

    private static final Serializer<AcceleratorCoolingRecipe> SERIALIZER = new Serializer<>(
        AcceleratorCoolingRecipe::new);

    public AcceleratorCoolingRecipe(Ingredient item, int itemCount, FluidStack fluid, ParticleBeam particle,
        List<SizedChanceItemIngredient> items, List<SizedChanceFluidIngredient> fluids, int heat, int temperature,
        double time) {
        super(AcceleratorRecipeKind.COOLING, item, itemCount, fluid, particle, items, fluids, heat, temperature, time);
    }

    public static Serializer<AcceleratorCoolingRecipe> serializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeSerializer<AcceleratorCoolingRecipe> getSerializer() {
        return QMDRecipes.ACCELERATOR_COOLING_SERIALIZER.get();
    }

    @Override
    public RecipeType<AcceleratorCoolingRecipe> getType() {
        return QMDRecipes.ACCELERATOR_COOLING_TYPE.get();
    }
}
