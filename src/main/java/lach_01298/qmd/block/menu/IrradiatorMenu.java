package lach_01298.qmd.block.menu;

import lach_01298.qmd.block.entity.IrradiatorBlockEntity;
import lach_01298.qmd.config.IrradiatorConfig;
import lach_01298.qmd.registry.QMDMenus;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public final class IrradiatorMenu extends AbstractContainerMenu {

    public static final int PLAYER_START = 3;
    private final IrradiatorBlockEntity tile;
    private final Player owner;
    private final ContainerData data;

    public IrradiatorMenu(int id, Inventory inventory, IrradiatorBlockEntity tile) {
        super(QMDMenus.IRRADIATOR.get(), id);
        this.tile = tile;
        owner = inventory.player;
        data = owner.level().isClientSide ? new SimpleContainerData(25) : new ContainerData() {

            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> tile.target() <= 0 ? 0
                        : (int) Math.clamp(tile.progress() / tile.target() * 10000, 0, 10000);
                    case 1 -> tile.processing() ? 1 : 0;
                    case 2 -> tile.redstoneControl() ? 1 : 0;
                    case 3 -> tile.outputMode()
                        .ordinal();
                    case 22 -> (int) Math.min(32767, Math.ceil(tile.target() / Math.max(1E-12, tile.speed())));
                    case 23 -> Math.min(32767, IrradiatorConfig.FUEL_USAGE.get());
                    case 24 -> (int) Math.min(32767, tile.speed() * 100);
                    default -> tile.sorption(Direction.values()[(index - 4) / 3], (index - 4) % 3)
                        .ordinal();
                };
            }

            @Override
            public void set(int index, int value) {}

            @Override
            public int getCount() {
                return 25;
            }
        };
        addDataSlots(data);
        addSlot(new SlotItemHandler(tile.inventory(), 0, 44, 54));
        addSlot(new SlotItemHandler(tile.inventory(), 1, 80, 21));
        addSlot(new SlotItemHandler(tile.inventory(), 2, 116, 54) {

            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        for (int row = 0; row < 3; row++) for (int column = 0; column < 9; column++)
            addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
        for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column, 8 + column * 18, 142));
    }

    public IrradiatorMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        this(id, inventory, requireTile(inventory, buffer));
    }

    private static IrradiatorBlockEntity requireTile(Inventory inventory, FriendlyByteBuf buffer) {
        var pos = buffer.readBlockPos();
        if (inventory.player.level()
            .hasChunkAt(pos)
            && inventory.player.level()
                .getBlockEntity(pos) instanceof IrradiatorBlockEntity tile)
            return tile;
        throw new IllegalStateException("Missing irradiator at " + pos);
    }

    public IrradiatorBlockEntity tile() {
        return tile;
    }

    public int progressScaled(int width) {
        return data.get(0) * width / 10000;
    }

    public int progressPercent() {
        return data.get(0) / 100;
    }

    public boolean processing() {
        return data.get(1) == 1;
    }

    public boolean redstoneControl() {
        return data.get(2) == 1;
    }

    public IrradiatorBlockEntity.OutputMode outputMode() {
        return IrradiatorBlockEntity.OutputMode.values()[Math.clamp(data.get(3), 0, 2)];
    }

    public IrradiatorBlockEntity.Sorption sorption(Direction face, int slot) {
        return IrradiatorBlockEntity.Sorption.values()[Math.clamp(data.get(4 + face.ordinal() * 3 + slot), 0, 3)];
    }

    public int processTicks() {
        return data.get(22);
    }

    public int fuelUsage() {
        return data.get(23);
    }

    public double speed() {
        return data.get(24) / 100D;
    }

    @Override
    public boolean stillValid(Player player) {
        return player == owner && !tile.isRemoved()
            && tile.getLevel() == player.level()
            && player.level()
                .hasChunkAt(tile.getBlockPos())
            && player.level()
                .getBlockEntity(tile.getBlockPos()) == tile
            && player.distanceToSqr(
                tile.getBlockPos()
                    .getCenter())
                <= 64;
    }

    @Override
    public boolean clickMenuButton(Player player, int button) {
        if (player.level().isClientSide || player.containerMenu != this || !stillValid(player)) return false;
        if (button == 0) tile.toggleRedstoneControl();
        else if (button == 1) tile.cycleOutputMode();
        else if (button >= 10 && button < 28)
            tile.cycleSorption(Direction.values()[(button - 10) / 3], (button - 10) % 3);
        else return false;
        broadcastChanges();
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (!stillValid(player) || index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        Slot source = slots.get(index);
        if (!source.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = source.getItem(), original = stack.copy();
        if (index < PLAYER_START) {
            if (!moveItemStackTo(stack, PLAYER_START, slots.size(), true)) return ItemStack.EMPTY;
        } else if (tile.acceptsFuel(stack)) {
            if (!moveItemStackTo(stack, 1, 2, false)) return ItemStack.EMPTY;
        } else if (tile.acceptsInput(stack)) {
            if (!moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY;
        } else if (index < 30) {
            if (!moveItemStackTo(stack, 30, slots.size(), false)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stack, PLAYER_START, 30, false)) return ItemStack.EMPTY;
        if (stack.isEmpty()) source.setByPlayer(ItemStack.EMPTY);
        else source.setChanged();
        if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
        source.onTake(player, stack);
        return original;
    }
}
