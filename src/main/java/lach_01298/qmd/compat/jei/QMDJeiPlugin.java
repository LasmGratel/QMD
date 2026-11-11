package lach_01298.qmd.compat.jei;

import lach_01298.qmd.client.particle.ParticleStackRenderer;
import lach_01298.qmd.client.screen.CreativeParticleSourceScreen;
import lach_01298.qmd.client.screen.OreLeacherScreen;
import lach_01298.qmd.compat.jei.category.CollectorCategory;
import lach_01298.qmd.compat.jei.category.OreLeacherCategory;
import lach_01298.qmd.compat.jei.category.ParticleInfoCategory;
import lach_01298.qmd.compat.jei.ingredient.ParticleStackHelper;
import lach_01298.qmd.compat.jei.ingredient.ParticleStackListFactory;
import lach_01298.qmd.compat.jei.ingredient.ParticleType;
import lach_01298.qmd.compat.jei.recipe.ParticleInfoRecipe;
import lach_01298.qmd.recipe.CollectorRecipe;
import lach_01298.qmd.registry.QMDBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/** Optional client integration: JEI discovers this class; common mod setup never loads it. */
@JeiPlugin
public final class QMDJeiPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.parse("qmd:jei");
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        registration.register(
            ParticleType.PARTICLE,
            ParticleStackListFactory.create(),
            new ParticleStackHelper(),
            new ParticleStackRenderer(),
            ParticleType.CODEC);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        ChamberJei.categories(registration);
        QMDAcceleratorJei.categories(registration);
        IrradiatorJei.categories(registration);
        var gui = registration.getJeiHelpers()
            .getGuiHelper();
        registration.addRecipeCategories(
            new OreLeacherCategory(gui),
            new CollectorCategory(gui, CollectorRecipe.Kind.LIQUID),
            new CollectorCategory(gui, CollectorRecipe.Kind.ATMOSPHERE),
            new ParticleInfoCategory(gui));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        var connection = Minecraft.getInstance()
            .getConnection();
        if (connection == null)
            throw new IllegalStateException("QMD JEI recipes require the connected server's recipe manager");
        // JEI 19.70 rebuilds registrations after RecipesUpdatedEvent, including an empty sync.
        ChamberJei.recipes(registration, connection.getRecipeManager());
        QMDAcceleratorJei.recipes(registration);
        IrradiatorJei.recipes(registration);
        var recipes = QMDJeiRecipes.from(connection.getRecipeManager());
        registration.addRecipes(QMDJeiRecipeTypes.ORE_LEACHER, recipes.oreLeacher());
        registration.addRecipes(QMDJeiRecipeTypes.LIQUID_COLLECTOR, recipes.liquidCollector());
        registration.addRecipes(QMDJeiRecipeTypes.ATMOSPHERE_COLLECTOR, recipes.atmosphereCollector());
        registration.addRecipes(QMDJeiRecipeTypes.PARTICLE_INFO, ParticleInfoRecipe.createAll());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        ChamberJei.catalysts(registration);
        QMDAcceleratorJei.catalysts(registration);
        IrradiatorJei.catalysts(registration);
        registration.addRecipeCatalyst(new ItemStack(QMDBlocks.ORE_LEACHER.get()), QMDJeiRecipeTypes.ORE_LEACHER);
        registration
            .addRecipeCatalyst(new ItemStack(QMDBlocks.LIQUID_COLLECTOR.get()), QMDJeiRecipeTypes.LIQUID_COLLECTOR);
        registration.addRecipeCatalyst(
            new ItemStack(QMDBlocks.ATMOSPHERE_COLLECTOR.get()),
            QMDJeiRecipeTypes.ATMOSPHERE_COLLECTOR);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        QMDAcceleratorJei.transfers(registration);
        IrradiatorJei.transfers(registration);
        ChamberJei.transfers(registration);
        registration.addRecipeTransferHandler(
            new OreLeacherTransferHandler(registration.getTransferHelper()),
            QMDJeiRecipeTypes.ORE_LEACHER);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        QMDAcceleratorJei.gui(registration);
        IrradiatorJei.gui(registration);
        ChamberJei.gui(registration);
        registration.addRecipeClickArea(OreLeacherScreen.class, 40, 29, 48, 11, QMDJeiRecipeTypes.ORE_LEACHER);
        registration.addRecipeClickArea(OreLeacherScreen.class, 94, 46, 16, 8, QMDJeiRecipeTypes.ORE_LEACHER);
        registration.addGuiContainerHandler(OreLeacherScreen.class, new OreLeacherGuiHandler());
        registration.addGuiContainerHandler(CreativeParticleSourceScreen.class, new ParticleSourceGuiHandler());
    }
}
