package lach_01298.qmd.compat.jei;

import lach_01298.qmd.config.MachineViewerStats;

import com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient;
import com.nred.nuclearcraft.recipe.SizedChanceItemIngredient;
import lach_01298.qmd.recipe.OreLeacherRecipe;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.List;

/** Deterministic recipe-viewer values; never roll Neohaul's chance products while building a display. */
public final class OreLeacherDisplay {

    private OreLeacherDisplay() {}

    public static List<ItemStack> itemAlternatives(SizedChanceItemIngredient ingredient) {
        return Arrays.stream(ingredient.getItemsRaw())
            .filter(stack -> !stack.isEmpty())
            .map(stack -> stack.copyWithCount(ingredient.count()))
            .toList();
    }

    public static List<FluidStack> fluidAlternatives(SizedChanceFluidIngredient ingredient) {
        return Arrays.stream(ingredient.getFluidsRaw())
            .filter(stack -> !stack.isEmpty())
            .map(stack -> stack.copyWithAmount(ingredient.amount()))
            .toList();
    }

    public static double meanOutput(SizedChanceItemIngredient product) {
        return product.minStackSize()
            + (product.count() - (double) product.minStackSize()) * product.chancePercent() / 100D;
    }

    public static ProcessStats processStats(OreLeacherRecipe recipe) {
        MachineViewerStats settings = MachineViewerStats.client();
        double time = recipe.getBaseProcessTime(settings.oreLeacherTime());
        double power = recipe.getBaseProcessPower(settings.oreLeacherPower());
        return new ProcessStats(time, power, time * power, recipe.getBaseProcessRadiation());
    }

    public record ProcessStats(double timeTicks, double powerPerTick, double totalEnergy, double radiation) {}
}
