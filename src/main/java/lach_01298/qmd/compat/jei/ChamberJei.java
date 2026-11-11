package lach_01298.qmd.compat.jei;

import lach_01298.qmd.block.menu.chamber.AbstractChamberMenu;
import lach_01298.qmd.block.menu.chamber.NeutralContainmentMenu;
import lach_01298.qmd.block.menu.chamber.TargetChamberMenu;
import lach_01298.qmd.client.screen.chamber.AbstractChamberScreen;
import lach_01298.qmd.client.screen.chamber.BeamDumpScreen;
import lach_01298.qmd.client.screen.chamber.CollisionChamberScreen;
import lach_01298.qmd.client.screen.chamber.DecayChamberScreen;
import lach_01298.qmd.client.screen.chamber.NeutralContainmentScreen;
import lach_01298.qmd.client.screen.chamber.NucleosynthesisChamberScreen;
import lach_01298.qmd.client.screen.chamber.TargetChamberScreen;
import lach_01298.qmd.compat.jei.category.chamber.BeamDumpCategory;
import lach_01298.qmd.compat.jei.category.chamber.CellFillingCategory;
import lach_01298.qmd.compat.jei.category.chamber.CollisionChamberCategory;
import lach_01298.qmd.compat.jei.category.chamber.DecayChamberCategory;
import lach_01298.qmd.compat.jei.category.chamber.NeutralContainmentCategory;
import lach_01298.qmd.compat.jei.category.chamber.NucleosynthesisChamberCategory;
import lach_01298.qmd.compat.jei.category.chamber.TargetChamberCategory;
import lach_01298.qmd.compat.jei.category.chamber.VacuumChamberHeatingCategory;
import lach_01298.qmd.multiblock.ChamberPart;
import lach_01298.qmd.recipe.chamber.AbstractChamberRecipe;
import lach_01298.qmd.recipe.chamber.BeamDumpRecipe;
import lach_01298.qmd.recipe.chamber.CellFillingRecipe;
import lach_01298.qmd.recipe.chamber.ChamberKind;
import lach_01298.qmd.recipe.chamber.CollisionChamberRecipe;
import lach_01298.qmd.recipe.chamber.DecayChamberRecipe;
import lach_01298.qmd.recipe.chamber.NeutralContainmentRecipe;
import lach_01298.qmd.recipe.chamber.NucleosynthesisChamberRecipe;
import lach_01298.qmd.recipe.chamber.TargetChamberRecipe;
import lach_01298.qmd.recipe.chamber.VacuumChamberHeatingRecipe;
import lach_01298.qmd.registry.QMDBlocks;
import lach_01298.qmd.registry.QMDMenus;
import lach_01298.qmd.registry.QMDRecipes;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferContext;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Optional client-side registration of each concrete chamber recipe and screen. */
public final class ChamberJei {

    public static final RecipeType<RecipeHolder<TargetChamberRecipe>> TARGET_CHAMBER = type(ChamberKind.TARGET_CHAMBER);
    public static final RecipeType<RecipeHolder<DecayChamberRecipe>> DECAY_CHAMBER = type(ChamberKind.DECAY_CHAMBER);
    public static final RecipeType<RecipeHolder<CollisionChamberRecipe>> COLLISION_CHAMBER = type(
        ChamberKind.COLLISION_CHAMBER);
    public static final RecipeType<RecipeHolder<BeamDumpRecipe>> BEAM_DUMP = type(ChamberKind.BEAM_DUMP);
    public static final RecipeType<RecipeHolder<NeutralContainmentRecipe>> NEUTRAL_CONTAINMENT = type(
        ChamberKind.NEUTRAL_CONTAINMENT);
    public static final RecipeType<RecipeHolder<CellFillingRecipe>> CELL_FILLING = type(ChamberKind.CELL_FILLING);
    public static final RecipeType<RecipeHolder<NucleosynthesisChamberRecipe>> NUCLEOSYNTHESIS_CHAMBER = type(
        ChamberKind.NUCLEOSYNTHESIS_CHAMBER);
    public static final RecipeType<RecipeHolder<VacuumChamberHeatingRecipe>> VACUUM_CHAMBER_HEATING = type(
        ChamberKind.VACUUM_CHAMBER_HEATING);

    private static final Map<ChamberKind, RecipeType<?>> TYPES = Map.of(
        ChamberKind.TARGET_CHAMBER,
        TARGET_CHAMBER,
        ChamberKind.DECAY_CHAMBER,
        DECAY_CHAMBER,
        ChamberKind.COLLISION_CHAMBER,
        COLLISION_CHAMBER,
        ChamberKind.BEAM_DUMP,
        BEAM_DUMP,
        ChamberKind.NEUTRAL_CONTAINMENT,
        NEUTRAL_CONTAINMENT,
        ChamberKind.CELL_FILLING,
        CELL_FILLING,
        ChamberKind.NUCLEOSYNTHESIS_CHAMBER,
        NUCLEOSYNTHESIS_CHAMBER,
        ChamberKind.VACUUM_CHAMBER_HEATING,
        VACUUM_CHAMBER_HEATING);

    private static <R extends AbstractChamberRecipe> RecipeType<RecipeHolder<R>> type(ChamberKind kind) {
        return RecipeType.createRecipeHolderType(ResourceLocation.fromNamespaceAndPath("qmd", kind.id()));
    }

    public static void categories(IRecipeCategoryRegistration registration) {
        var gui = registration.getJeiHelpers()
            .getGuiHelper();
        registration.addRecipeCategories(
            new TargetChamberCategory(gui),
            new DecayChamberCategory(gui),
            new CollisionChamberCategory(gui),
            new BeamDumpCategory(gui),
            new NeutralContainmentCategory(gui),
            new CellFillingCategory(gui),
            new NucleosynthesisChamberCategory(gui),
            new VacuumChamberHeatingCategory(gui));
    }

    public static void recipes(IRecipeRegistration registration, RecipeManager manager) {
        recipes(registration, manager, TARGET_CHAMBER, QMDRecipes.TARGET_CHAMBER_TYPE.get());
        recipes(registration, manager, DECAY_CHAMBER, QMDRecipes.DECAY_CHAMBER_TYPE.get());
        recipes(registration, manager, COLLISION_CHAMBER, QMDRecipes.COLLISION_CHAMBER_TYPE.get());
        recipes(registration, manager, BEAM_DUMP, QMDRecipes.BEAM_DUMP_TYPE.get());
        recipes(registration, manager, NEUTRAL_CONTAINMENT, QMDRecipes.NEUTRAL_CONTAINMENT_TYPE.get());
        recipes(registration, manager, CELL_FILLING, QMDRecipes.CELL_FILLING_TYPE.get());
        recipes(registration, manager, NUCLEOSYNTHESIS_CHAMBER, QMDRecipes.NUCLEOSYNTHESIS_CHAMBER_TYPE.get());
        recipes(registration, manager, VACUUM_CHAMBER_HEATING, QMDRecipes.VACUUM_CHAMBER_HEATING_TYPE.get());
    }

    private static <R extends AbstractChamberRecipe> void recipes(IRecipeRegistration registration,
        RecipeManager manager, RecipeType<RecipeHolder<R>> jeiType,
        net.minecraft.world.item.crafting.RecipeType<R> type) {
        registration.addRecipes(
            jeiType,
            manager.getAllRecipesFor(type)
                .stream()
                .sorted(
                    Comparator.comparing(
                        holder -> holder.id()
                            .toString()))
                .toList());
    }

    public static void catalysts(IRecipeCatalystRegistration registration) {
        for (ChamberPart part : ChamberPart.values()) if (part.controller()) registration.addRecipeCatalyst(
            new ItemStack(
                QMDBlocks.CHAMBER_PARTS.get(part)
                    .get()),
            TYPES.get(part.kind));
        registration.addRecipeCatalyst(
            new ItemStack(
                QMDBlocks.CHAMBER_PARTS.get(ChamberPart.CONTAINMENT_CONTROLLER)
                    .get()),
            CELL_FILLING);
        registration.addRecipeCatalyst(
            new ItemStack(
                QMDBlocks.CHAMBER_PARTS.get(ChamberPart.NUCLEOSYNTHESIS_CONTROLLER)
                    .get()),
            VACUUM_CHAMBER_HEATING);
        registration.addRecipeCatalyst(
            new ItemStack(
                QMDBlocks.CHAMBER_PARTS.get(ChamberPart.HEATER_VENT)
                    .get()),
            VACUUM_CHAMBER_HEATING);
    }

    public static void gui(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(TargetChamberScreen.class, new GuiHandler<>());
        registration.addGuiContainerHandler(DecayChamberScreen.class, new GuiHandler<>());
        registration.addGuiContainerHandler(CollisionChamberScreen.class, new GuiHandler<>());
        registration.addGuiContainerHandler(BeamDumpScreen.class, new GuiHandler<>());
        registration.addGuiContainerHandler(NeutralContainmentScreen.class, new GuiHandler<>());
        registration.addGuiContainerHandler(NucleosynthesisChamberScreen.class, new GuiHandler<>());
    }

    public static void transfers(IRecipeTransferRegistration registration) {
        var helper = registration.getTransferHelper();
        registration.addRecipeTransferHandler(new TargetTransfer(helper), TARGET_CHAMBER);
        registration.addRecipeTransferHandler(new CellTransfer(helper), CELL_FILLING);
    }

    private static final class GuiHandler<S extends AbstractChamberScreen<?>> extends MachineGuiHandler<S> {

        GuiHandler() {
            super(AbstractChamberScreen::instruments);
        }

        @Override
        public Collection<IGuiClickableArea> getGuiClickableAreas(S screen, double mouseX, double mouseY) {
            var area = screen.recipeArea();
            return List.of(
                IGuiClickableArea.createBasic(
                    area.left(),
                    area.top(),
                    area.width(),
                    area.height(),
                    TYPES.get(
                        screen.getMenu()
                            .kind())));
        }
    }

    /** Only item inputs transfer here; fluid and beam channels use their machine capabilities. */
    private abstract static class ItemTransfer<M extends AbstractChamberMenu, R extends AbstractChamberRecipe>
        implements IRecipeTransferHandler<M, RecipeHolder<R>> {

        private final IRecipeTransferHandlerHelper helper;
        private final IRecipeTransferHandler<M, RecipeHolder<R>> delegate;
        private final Class<M> menuClass;
        private final MenuType<M> menuType;
        private final RecipeType<RecipeHolder<R>> recipeType;

        ItemTransfer(IRecipeTransferHandlerHelper helper, Class<M> menuClass, MenuType<M> menuType,
            RecipeType<RecipeHolder<R>> recipeType) {
            this.helper = helper;
            this.menuClass = menuClass;
            this.menuType = menuType;
            this.recipeType = recipeType;
            delegate = helper.createUnregisteredRecipeTransferHandler(
                helper.createBasicRecipeTransferInfo(menuClass, menuType, recipeType, 0, 1, 2, 36));
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

    private static final class TargetTransfer extends ItemTransfer<TargetChamberMenu, TargetChamberRecipe> {

        TargetTransfer(IRecipeTransferHandlerHelper helper) {
            super(helper, TargetChamberMenu.class, QMDMenus.TARGET_CHAMBER.get(), TARGET_CHAMBER);
        }
    }

    private static final class CellTransfer extends ItemTransfer<NeutralContainmentMenu, CellFillingRecipe> {

        CellTransfer(IRecipeTransferHandlerHelper helper) {
            super(helper, NeutralContainmentMenu.class, QMDMenus.NEUTRAL_CONTAINMENT.get(), CELL_FILLING);
        }
    }

    private ChamberJei() {}
}
