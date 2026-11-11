package lach_01298.qmd.compat.jei;

import lach_01298.qmd.client.screen.LiquefierScreen;
import lach_01298.qmd.compat.jei.category.LiquefierCategory;
import lach_01298.qmd.recipe.LiquefierRecipe;
import lach_01298.qmd.registry.QMDBlocks;
import lach_01298.qmd.registry.QMDRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

@JeiPlugin
public final class LiquefierJeiPlugin implements IModPlugin {

    public static final RecipeType<RecipeHolder<LiquefierRecipe>> GAS = RecipeType
        .createRecipeHolderType(ResourceLocation.parse("qmd:liquefier"));
    public static final RecipeType<RecipeHolder<LiquefierRecipe>> COOLANT = RecipeType
        .createRecipeHolderType(ResourceLocation.parse("qmd:liquefier_coolant"));

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.parse("qmd:liquefier_jei");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var gui = registration.getJeiHelpers()
            .getGuiHelper();
        registration.addRecipeCategories(
            new LiquefierCategory(gui, LiquefierRecipe.Kind.GAS),
            new LiquefierCategory(gui, LiquefierRecipe.Kind.COOLANT));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        var connection = Minecraft.getInstance()
            .getConnection();
        if (connection == null)
            throw new IllegalStateException("Liquefier JEI requires the connected server recipe manager");
        registration.addRecipes(
            GAS,
            connection.getRecipeManager()
                .getAllRecipesFor(QMDRecipes.LIQUEFIER_GAS_TYPE.get()));
        registration.addRecipes(
            COOLANT,
            connection.getRecipeManager()
                .getAllRecipesFor(QMDRecipes.LIQUEFIER_COOLANT_TYPE.get()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        for (var block : QMDBlocks.liquefierBlocks())
            registration.addRecipeCatalyst(new ItemStack(block), GAS, COOLANT);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(LiquefierScreen.class, 7, 25, 162, 10, GAS);
        registration.addRecipeClickArea(LiquefierScreen.class, 7, 37, 162, 10, COOLANT);
    }
}
