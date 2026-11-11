package lach_01298.qmd.compat.jei;

import lach_01298.qmd.block.menu.IrradiatorMenu;
import lach_01298.qmd.client.screen.IrradiatorScreen;
import lach_01298.qmd.compat.jei.category.IrradiatorCategory;
import lach_01298.qmd.recipe.IrradiatorRecipe;
import lach_01298.qmd.registry.QMDBlocks;
import lach_01298.qmd.registry.QMDMenus;
import lach_01298.qmd.registry.QMDRecipes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

/** Optional JEI hooks. Recipe managers and ingredients are refreshed for each server recipe sync. */
public final class IrradiatorJei {

    public static final RecipeType<RecipeHolder<IrradiatorRecipe>> PROCESS = RecipeType
        .createRecipeHolderType(ResourceLocation.parse("qmd:irradiator"));
    public static final RecipeType<RecipeHolder<IrradiatorRecipe>> FUEL = RecipeType
        .createRecipeHolderType(ResourceLocation.parse("qmd:irradiator_fuel"));

    public static void categories(IRecipeCategoryRegistration registration) {
        var gui = registration.getJeiHelpers()
            .getGuiHelper();
        registration.addRecipeCategories(
            new IrradiatorCategory(gui, IrradiatorRecipe.Kind.PROCESS),
            new IrradiatorCategory(gui, IrradiatorRecipe.Kind.FUEL));
    }

    public static void recipes(IRecipeRegistration registration) {
        var connection = Minecraft.getInstance()
            .getConnection();
        if (connection == null) return;
        registration.addRecipes(
            PROCESS,
            connection.getRecipeManager()
                .getAllRecipesFor(QMDRecipes.IRRADIATOR_TYPE.get()));
        registration.addRecipes(
            FUEL,
            connection.getRecipeManager()
                .getAllRecipesFor(QMDRecipes.IRRADIATOR_FUEL_TYPE.get()));
    }

    public static void catalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(QMDBlocks.IRRADIATOR.get()), PROCESS, FUEL);
    }

    public static void gui(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(IrradiatorScreen.class, 62, 38, 52, 29, PROCESS);
        registration.addRecipeClickArea(IrradiatorScreen.class, 79, 20, 18, 18, FUEL);
    }

    public static void transfers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(
            IrradiatorMenu.class,
            QMDMenus.IRRADIATOR.get(),
            PROCESS,
            0,
            1,
            IrradiatorMenu.PLAYER_START,
            36);
        registration.addRecipeTransferHandler(
            IrradiatorMenu.class,
            QMDMenus.IRRADIATOR.get(),
            FUEL,
            1,
            1,
            IrradiatorMenu.PLAYER_START,
            36);
    }

    private IrradiatorJei() {}
}
