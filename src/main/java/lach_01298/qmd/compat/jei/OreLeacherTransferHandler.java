package lach_01298.qmd.compat.jei;

import lach_01298.qmd.block.menu.OreLeacherMenu;
import lach_01298.qmd.recipe.OreLeacherRecipe;
import lach_01298.qmd.registry.QMDMenus;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferContext;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/** JEI moves ore items only. Acid tanks remain real fluid inputs, never inventory slots. */
public final class OreLeacherTransferHandler
    implements IRecipeTransferHandler<OreLeacherMenu, RecipeHolder<OreLeacherRecipe>> {

    public static final int INPUT_SLOT = 0;
    public static final int PLAYER_SLOT_START = 6;
    public static final int PLAYER_SLOT_COUNT = 36;
    private final IRecipeTransferHandlerHelper helper;
    private final IRecipeTransferHandler<OreLeacherMenu, RecipeHolder<OreLeacherRecipe>> delegate;

    public OreLeacherTransferHandler(IRecipeTransferHandlerHelper helper) {
        this.helper = helper;
        delegate = helper.createUnregisteredRecipeTransferHandler(
            helper.createBasicRecipeTransferInfo(
                OreLeacherMenu.class,
                QMDMenus.ORE_LEACHER.get(),
                QMDJeiRecipeTypes.ORE_LEACHER,
                INPUT_SLOT,
                1,
                PLAYER_SLOT_START,
                PLAYER_SLOT_COUNT));
    }

    @Override
    public Class<OreLeacherMenu> getContainerClass() {
        return OreLeacherMenu.class;
    }

    @Override
    public Optional<MenuType<OreLeacherMenu>> getMenuType() {
        return Optional.of(QMDMenus.ORE_LEACHER.get());
    }

    @Override
    public RecipeType<RecipeHolder<OreLeacherRecipe>> getRecipeType() {
        return QMDJeiRecipeTypes.ORE_LEACHER;
    }

    public static List<IRecipeSlotView> itemInputSlots(IRecipeSlotsView slots) {
        return slots.getSlotViews(RecipeIngredientRole.INPUT)
            .stream()
            .filter(
                slot -> slot.getItemStacks()
                    .findAny()
                    .isPresent())
            .toList();
    }

    @Override
    @Nullable
    public IRecipeTransferError transferRecipe(
        IRecipeTransferContext<RecipeHolder<OreLeacherRecipe>, OreLeacherMenu> context, boolean doTransfer) {
        IRecipeSlotsView itemSlots = helper.createRecipeSlotsView(itemInputSlots(context.getRecipeSlots()));
        return helper.transferRecipeWithInputAlternatives(delegate, context, List.of(itemSlots), doTransfer);
    }

    @Override
    @SuppressWarnings("removal")
    @Nullable
    public IRecipeTransferError transferRecipe(OreLeacherMenu menu, RecipeHolder<OreLeacherRecipe> recipe,
        IRecipeSlotsView slots, Player player, boolean maxTransfer, boolean doTransfer) {
        return delegate.transferRecipe(
            menu,
            recipe,
            helper.createRecipeSlotsView(itemInputSlots(slots)),
            player,
            maxTransfer,
            doTransfer);
    }
}
