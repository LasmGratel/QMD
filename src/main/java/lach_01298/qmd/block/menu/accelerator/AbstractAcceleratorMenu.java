package lach_01298.qmd.block.menu.accelerator;

import lach_01298.qmd.block.entity.accelerator.AbstractAcceleratorBlockEntity;
import lach_01298.qmd.multiblock.accelerator.AcceleratorPartType;
import lach_01298.qmd.network.AcceleratorStatePayload;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;

public abstract class AbstractAcceleratorMenu extends AbstractContainerMenu {

    private final AbstractAcceleratorBlockEntity part;
    private final Player owner;
    private final int machineSlots;
    private final IItemHandlerModifiable machineInventory;
    private final int inventoryX, inventoryY;
    private CompoundTag snapshot = new CompoundTag(), lastSent;

    protected static AbstractAcceleratorBlockEntity require(Player player, FriendlyByteBuf buffer) {
        BlockPos pos = buffer.readBlockPos();
        if (player.level()
            .hasChunkAt(pos)
            && player.level()
                .getBlockEntity(pos) instanceof AbstractAcceleratorBlockEntity part)
            return part;
        throw new IllegalStateException("Missing accelerator part at " + pos);
    }

    protected AbstractAcceleratorMenu(MenuType<?> type, int id, Inventory inventory,
        AbstractAcceleratorBlockEntity part, int machineSlots, int inventoryX, int inventoryY) {
        super(type, id);
        this.part = part;
        owner = inventory.player;
        machineInventory = part.menuInventory();
        this.machineSlots = machineSlots;
        this.inventoryX = inventoryX;
        this.inventoryY = inventoryY;
        addMachineSlots();
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++)
            addSlot(new Slot(inventory, col + row * 9 + 9, inventoryX() + col * 18, inventoryY() + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, inventoryX() + col * 18, inventoryY() + 58));
    }

    public boolean source() {
        return false;
    }

    public boolean spectrometer() {
        return false;
    }

    public final int inventoryX() {
        return inventoryX;
    }

    public final int inventoryY() {
        return inventoryY;
    }

    protected void addMachineSlots() {}

    protected boolean handlePartButton(int button) {
        return false;
    }

    protected final void machineSlot(int index, int x, int y, boolean input, boolean visible) {
        addSlot(new SlotItemHandler(machineInventory, index, x, y) {

            @Override
            public boolean mayPlace(ItemStack stack) {
                return input && part.menuInventory()
                    .isItemValid(index, stack);
            }

            @Override
            public boolean isActive() {
                return visible;
            }
        });
    }

    public BlockPos pos() {
        return part.getBlockPos();
    }

    public AbstractAcceleratorBlockEntity part() {
        return part;
    }

    public CompoundTag snapshot() {
        return owner.level().isClientSide ? snapshot.copy() : currentSnapshot();
    }

    private CompoundTag currentSnapshot() {
        var acc = part.controller();
        CompoundTag tag = acc == null ? new CompoundTag() : acc.menuSnapshot(owner.registryAccess());
        tag.put(
            "sourceTank",
            part.sourceFluid()
                .writeToNBT(owner.registryAccess(), new CompoundTag()));
        tag.putInt(
            "sourceTankCapacity",
            part.sourceFluid()
                .getCapacity());
        return tag;
    }

    public void receiveSnapshot(CompoundTag tag) {
        snapshot = tag.copy();
    }

    @Override
    public boolean stillValid(Player player) {
        return player == owner && !part.isRemoved()
            && part.menuInventory() == machineInventory
            && part.getLevel() == player.level()
            && player.level()
                .hasChunkAt(pos())
            && player.level()
                .getBlockEntity(pos()) == part
            && player.distanceToSqr(pos().getCenter()) <= 64;
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (owner instanceof ServerPlayer player && player.containerMenu == this && stillValid(player)) {
            CompoundTag tag = currentSnapshot();
            if (!tag.equals(lastSent)) {
                PacketDistributor.sendToPlayer(player, new AcceleratorStatePayload(containerId, pos(), tag));
                lastSent = tag;
            }
        }
    }

    @Override
    public boolean clickMenuButton(Player player, int button) {
        if (player != owner || player.containerMenu != this || !stillValid(player) || player.level().isClientSide)
            return false;
        if (handlePartButton(button)) {
            broadcastChanges();
            return true;
        }
        var acc = part.controller();
        if (acc == null) return false;
        switch (button) {
            case 0 -> acc.percentage = -1;
            case 1 -> acc.percentage = Math.max(0, acc.percentage - 5);
            case 2 -> acc.percentage = Math.min(100, acc.percentage < 0 ? 5 : acc.percentage + 5);
            case 3 -> {
                for (var port : acc.parts(AcceleratorPartType.Role.BEAM_PORT)) {
                    player.level()
                        .setBlock(
                            port.getBlockPos(),
                            port.getBlockState()
                                .cycle(lach_01298.qmd.block.accelerator.AbstractAcceleratorBlock.OUTPUT),
                            3);
                    port.invalidateCapabilities();
                }
                acc.checkIfMachineIsWhole();
            }
            case 4 -> acc.clearAllMaterial();
            case 10 -> acc.coolantInput.setFluid(net.neoforged.neoforge.fluids.FluidStack.EMPTY);
            case 11 -> acc.coolantOutput.setFluid(net.neoforged.neoforge.fluids.FluidStack.EMPTY);
            case 12, 13, 14, 15, 16 -> acc.processFluids[button - 12]
                .setFluid(net.neoforged.neoforge.fluids.FluidStack.EMPTY);
            default -> {
                return false;
            }
        }
        acc.markReferenceCoordForUpdate();
        broadcastChanges();
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size() || !stillValid(player)) return ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack present = slot.getItem(), original = present.copy();
        if (index < machineSlots) {
            if (!moveItemStackTo(present, machineSlots, slots.size(), true)) return ItemStack.EMPTY;
        } else if (machineSlots == 0 || !moveItemStackTo(present, 0, Math.min(2, machineSlots), false))
            return ItemStack.EMPTY;
        if (present.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
        else slot.setChanged();
        slot.onTake(player, present);
        return original;
    }
}
