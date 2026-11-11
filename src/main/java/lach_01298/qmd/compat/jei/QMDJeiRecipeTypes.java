package lach_01298.qmd.compat.jei;

import lach_01298.qmd.compat.jei.recipe.ParticleInfoRecipe;
import lach_01298.qmd.recipe.CollectorRecipe;
import lach_01298.qmd.recipe.OreLeacherRecipe;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

/** JEI uses the same namespaced IDs as QMD's data-driven recipe types. */
public final class QMDJeiRecipeTypes {

    public static final RecipeType<RecipeHolder<OreLeacherRecipe>> ORE_LEACHER = RecipeType
        .createRecipeHolderType(ResourceLocation.parse("qmd:ore_leacher"));
    public static final RecipeType<RecipeHolder<CollectorRecipe>> LIQUID_COLLECTOR = RecipeType
        .createRecipeHolderType(ResourceLocation.parse("qmd:liquid_collector"));
    public static final RecipeType<RecipeHolder<CollectorRecipe>> ATMOSPHERE_COLLECTOR = RecipeType
        .createRecipeHolderType(ResourceLocation.parse("qmd:atmosphere_collector"));
    public static final RecipeType<ParticleInfoRecipe> PARTICLE_INFO = RecipeType
        .create("qmd", "particle_info", ParticleInfoRecipe.class);

    private QMDJeiRecipeTypes() {}
}
