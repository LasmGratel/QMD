package lach_01298.qmd.block.menu.chamber;

import lach_01298.qmd.block.entity.chamber.AbstractChamberBlockEntity;
import lach_01298.qmd.recipe.chamber.ChamberKind;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.items.SlotItemHandler;

/** Shared chamber ownership, inventory transfer and server-validated instrument actions. */
public abstract class AbstractChamberMenu extends AbstractContainerMenu {

    private final AbstractChamberBlockEntity controller;
    private final Player owner;
    private final int machineSlots;
    private final int panelHeight;
    private final int inventoryY;

    protected AbstractChamberMenu(MenuType<?> type, int id, Inventory inventory, AbstractChamberBlockEntity controller,
        int panelHeight, int inventoryY) {
        super(type, id);
        this.controller = controller;
        this.owner = inventory.player;
        this.panelHeight = panelHeight;
        this.inventoryY = inventoryY;
        addMachineSlots();
        machineSlots = slots.size();
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++)
            addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, inventoryY + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 8 + col * 18, inventoryY + 58));
        if (!inventory.player.level().isClientSide) controller.syncOpenMenus();
    }

    public abstract ChamberKind kind();

    protected void addMachineSlots() {}

    protected final void addItemSlots(int inputX, int outputX, int y) {
        addSlot(new SlotItemHandler(controller.inventory(), 0, inputX, y));
        addSlot(new SlotItemHandler(controller.inventory(), 1, outputX, y) {

            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
    }

    protected static AbstractChamberBlockEntity requireController(Player player, FriendlyByteBuf buffer,
        ChamberKind kind) {
        var pos = buffer.readBlockPos();
        if (player.level()
            .hasChunkAt(pos)
            && player.level()
                .getBlockEntity(pos) instanceof AbstractChamberBlockEntity part
            && part.role().kind == kind) return part;
        throw new IllegalStateException("Missing " + kind.id() + " controller at " + pos);
    }

    public final AbstractChamberBlockEntity controller() {
        return controller;
    }

    public final int panelHeight() {
        return panelHeight;
    }

    public final int inventoryY() {
        return inventoryY;
    }

    @Override
    public boolean stillValid(Player player) {
        return owner == player && !controller.isRemoved()
            && controller.getLevel() == player.level()
            && player.level()
                .hasChunkAt(controller.getBlockPos())
            && player.level()
                .getBlockEntity(controller.getBlockPos()) == controller
            && player.distanceToSqr(
                controller.getBlockPos()
                    .getCenter())
                <= 64;
    }

    private int tankCount() {
        return controller.vacuum() == null ? 2
            : controller.vacuum()
                .tankCount();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (!stillValid(player) || player.containerMenu != this || player.level().isClientSide) return false;
        if (id == 0) controller.toggleEnabled();
        else if (id == 1) controller.rotateOutputs();
        else if (id == 10) controller.clearAllMaterial();
        else if (id >= 11 && id < 11 + tankCount()) {
            controller.tank(id - 11)
                .setFluid(FluidStack.EMPTY);
            controller.setChanged();
        } else if (id >= 2 && id < 10 && getCarried().getCount() == 1) {
            int tankIndex = id - 2;
            if (tankIndex >= tankCount()) return false;
            boolean input = controller.vacuum() == null ? tankIndex == 0
                : controller.vacuum()
                    .inputTank(tankIndex);
            var result = input
                ? FluidUtil.tryEmptyContainer(getCarried(), controller.tank(tankIndex), Integer.MAX_VALUE, player, true)
                : FluidUtil.tryFillContainer(getCarried(), controller.tank(tankIndex), Integer.MAX_VALUE, player, true);
            if (!result.isSuccess()) return false;
            setCarried(result.getResult());
            controller.setChanged();
        } else return false;
        controller.syncOpenMenus();
        return true;
    }

    @Override
    public void sendAllDataToRemote() {
        super.sendAllDataToRemote();
        controller.syncOpenMenus();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (!stillValid(player) || index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem(), original = stack.copy();
        if (index < machineSlots) {
            if (!moveItemStackTo(stack, machineSlots, slots.size(), true)) return ItemStack.EMPTY;
        } else if (machineSlots > 0) {
            if (!moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY;
        } else if (index < machineSlots + 27) {
            if (!moveItemStackTo(stack, machineSlots + 27, slots.size(), false)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stack, machineSlots, machineSlots + 27, false)) return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
        else slot.setChanged();
        slot.onTake(player, stack);
        return original;
    }
}
