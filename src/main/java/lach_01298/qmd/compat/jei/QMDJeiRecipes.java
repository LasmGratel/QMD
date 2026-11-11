package lach_01298.qmd.compat.jei;

import lach_01298.qmd.recipe.CollectorRecipe;
import lach_01298.qmd.recipe.OreLeacherRecipe;
import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Comparator;
import java.util.List;

/** Fresh authoritative recipe snapshots; never retains a manager across reloads or worlds. */
public record QMDJeiRecipes(List<RecipeHolder<OreLeacherRecipe>> oreLeacher,
    List<RecipeHolder<CollectorRecipe>> liquidCollector, List<RecipeHolder<CollectorRecipe>> atmosphereCollector) {

    public QMDJeiRecipes {
        oreLeacher = List.copyOf(oreLeacher);
        liquidCollector = List.copyOf(liquidCollector);
        atmosphereCollector = List.copyOf(atmosphereCollector);
    }

    public static QMDJeiRecipes from(RecipeManager manager) {
        return new QMDJeiRecipes(
            sorted(manager, QMDRecipes.ORE_LEACHER_TYPE.get()),
            sorted(manager, QMDRecipes.LIQUID_COLLECTOR_TYPE.get()),
            sorted(manager, QMDRecipes.ATMOSPHERE_COLLECTOR_TYPE.get()));
    }

    private static <I extends RecipeInput, R extends Recipe<I>> List<RecipeHolder<R>> sorted(RecipeManager manager,
        RecipeType<R> type) {
        return manager.getAllRecipesFor(type)
            .stream()
            .sorted(
                Comparator.comparing(
                    holder -> holder.id()
                        .toString()))
            .toList();
    }
}
