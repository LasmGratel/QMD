package lach_01298.qmd.compat.jei;

import lach_01298.qmd.block.menu.accelerator.*;
import lach_01298.qmd.client.screen.accelerator.*;
import lach_01298.qmd.compat.jei.category.accelerator.*;
import lach_01298.qmd.multiblock.accelerator.AcceleratorPartType;
import lach_01298.qmd.recipe.accelerator.*;
import lach_01298.qmd.registry.QMDBlocks;
import lach_01298.qmd.registry.QMDMenus;
import lach_01298.qmd.registry.QMDRecipes;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.*;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import java.util.*;

/** Typed accelerator recipes, concrete menu transfers and authored instrument click areas. */
public final class QMDAcceleratorJei {

    public static final RecipeType<RecipeHolder<AcceleratorSourceRecipe>> SOURCE = type("accelerator_source");
    public static final RecipeType<RecipeHolder<AcceleratorCoolingRecipe>> COOLING = type("accelerator_cooling");
    public static final RecipeType<RecipeHolder<MassSpectrometerRecipe>> SPECTROMETER = type("mass_spectrometer");
    private static final Map<AcceleratorRecipeKind, RecipeType<?>> TYPES = Map.of(
        AcceleratorRecipeKind.SOURCE,
        SOURCE,
        AcceleratorRecipeKind.COOLING,
        COOLING,
        AcceleratorRecipeKind.SPECTROMETER,
        SPECTROMETER);

    private static <R extends AbstractAcceleratorRecipe> RecipeType<RecipeHolder<R>> type(String id) {
        return RecipeType.createRecipeHolderType(ResourceLocation.fromNamespaceAndPath("qmd", id));
    }

    public static void categories(IRecipeCategoryRegistration registration) {
        var gui = registration.getJeiHelpers()
            .getGuiHelper();
        registration.addRecipeCategories(
            new AcceleratorSourceCategory(gui),
            new AcceleratorCoolingCategory(gui),
            new MassSpectrometerCategory(gui));
    }

    public static void recipes(IRecipeRegistration registration) {
        var connection = Minecraft.getInstance()
            .getConnection();
        if (connection == null) return;
        var manager = connection.getRecipeManager();
        registration.addRecipes(SOURCE, manager.getAllRecipesFor(QMDRecipes.ACCELERATOR_SOURCE_TYPE.get()));
        registration.addRecipes(COOLING, manager.getAllRecipesFor(QMDRecipes.ACCELERATOR_COOLING_TYPE.get()));
        registration.addRecipes(SPECTROMETER, manager.getAllRecipesFor(QMDRecipes.MASS_SPECTROMETER_TYPE.get()));
    }

    public static void catalysts(IRecipeCatalystRegistration registration) {
        for (String id : List
            .of("accelerator_ion_source_basic", "accelerator_ion_source_laser", "linear_accelerator_controller"))
            registration.addRecipeCatalyst(
                new ItemStack(
                    QMDBlocks.ACCELERATOR_BLOCKS.get(id)
                        .get()),
                SOURCE);
        registration.addRecipeCatalyst(
            new ItemStack(
                QMDBlocks.ACCELERATOR_BLOCKS.get("mass_spectrometer_controller")
                    .get()),
            SPECTROMETER);
        for (var entry : QMDBlocks.ACCELERATOR_PARTS.entrySet())
            if (entry.getValue().role == AcceleratorPartType.Role.CONTROLLER
                || entry.getValue().role == AcceleratorPartType.Role.COOLER)
                registration.addRecipeCatalyst(
                    new ItemStack(
                        QMDBlocks.ACCELERATOR_BLOCKS.get(entry.getKey())
                            .get()),
                    COOLING);
    }

    public static void gui(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(LinearAcceleratorScreen.class, new GuiHandler<>());
        registration.addGuiContainerHandler(RingAcceleratorScreen.class, new GuiHandler<>());
        registration.addGuiContainerHandler(DeceleratorScreen.class, new GuiHandler<>());
        registration.addGuiContainerHandler(BeamDiverterScreen.class, new GuiHandler<>());
        registration.addGuiContainerHandler(BeamSplitterScreen.class, new GuiHandler<>());
        registration.addGuiContainerHandler(MassSpectrometerScreen.class, new GuiHandler<>());
        registration.addGuiContainerHandler(AcceleratorSourceScreen.class, new GuiHandler<>());
        registration.addGuiContainerHandler(AcceleratorPartScreen.class, new GuiHandler<>());
    }

    public static void transfers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(new SourceTransfer(registration.getTransferHelper()), SOURCE);
        registration.addRecipeTransferHandler(new SpectrometerTransfer(registration.getTransferHelper()), SPECTROMETER);
    }

    private static final class GuiHandler<S extends AbstractAcceleratorScreen<?>> extends MachineGuiHandler<S> {

        GuiHandler() {
            super(AbstractAcceleratorScreen::instruments);
        }

        @Override
        public Collection<IGuiClickableArea> getGuiClickableAreas(S screen, double mouseX, double mouseY) {
            var area = screen.recipeArea();
            return List.of(
                IGuiClickableArea
                    .createBasic(area.left(), area.top(), area.width(), area.height(), TYPES.get(screen.recipeKind())));
        }
    }

    /** Only item inputs transfer here; fluid and beam channels use their machine capabilities. */
    private abstract static class ItemTransfer<M extends AbstractAcceleratorMenu, R extends AbstractAcceleratorRecipe>
        implements IRecipeTransferHandler<M, RecipeHolder<R>> {

        private final IRecipeTransferHandlerHelper helper;
        private final IRecipeTransferHandler<M, RecipeHolder<R>> delegate;
        private final Class<M> menuClass;
        private final MenuType<M> menuType;
        private final RecipeType<RecipeHolder<R>> recipeType;

        ItemTransfer(IRecipeTransferHandlerHelper helper, Class<M> menuClass, MenuType<M> menuType,
            RecipeType<RecipeHolder<R>> recipeType, int inventoryStart) {
            this.helper = helper;
            this.menuClass = menuClass;
            this.menuType = menuType;
            this.recipeType = recipeType;
            delegate = helper.createUnregisteredRecipeTransferHandler(
                helper.createBasicRecipeTransferInfo(menuClass, menuType, recipeType, 0, 1, inventoryStart, 36));
        }

        @Override
        public Class<M> getContainerClass() {
            return menuClass;
        }

        @Override
        public Optional<MenuType<M>> getMenuType() {
            return Optional.of(menuType);
        }

        @Override
        public RecipeType<RecipeHolder<R>> getRecipeType() {
            return recipeType;
        }

        private List<IRecipeSlotView> inputs(IRecipeSlotsView slots) {
            return slots.getSlotViews(RecipeIngredientRole.INPUT)
                .stream()
                .filter(
                    slot -> slot.getItemStacks()
                        .findAny()
                        .isPresent())
                .toList();
        }

        @Override
        public IRecipeTransferError transferRecipe(IRecipeTransferContext<RecipeHolder<R>, M> context,
            boolean transfer) {
            return helper.transferRecipeWithInputAlternatives(
                delegate,
                context,
                List.of(helper.createRecipeSlotsView(inputs(context.getRecipeSlots()))),
                transfer);
        }

        @Override
        @SuppressWarnings("removal")
        public IRecipeTransferError transferRecipe(M menu, RecipeHolder<R> recipe, IRecipeSlotsView slots,
            Player player, boolean max, boolean transfer) {
            return delegate
                .transferRecipe(menu, recipe, helper.createRecipeSlotsView(inputs(slots)), player, max, transfer);
        }
    }

    private static final class SourceTransfer extends ItemTransfer<AcceleratorSourceMenu, AcceleratorSourceRecipe> {

        SourceTransfer(IRecipeTransferHandlerHelper helper) {
            super(helper, AcceleratorSourceMenu.class, QMDMenus.ACCELERATOR_SOURCE.get(), SOURCE, 2);
        }
    }

    private static final class SpectrometerTransfer extends ItemTransfer<MassSpectrometerMenu, MassSpectrometerRecipe> {

        SpectrometerTransfer(IRecipeTransferHandlerHelper helper) {
            super(helper, MassSpectrometerMenu.class, QMDMenus.MASS_SPECTROMETER.get(), SPECTROMETER, 6);
        }
    }

    private QMDAcceleratorJei() {}
}
