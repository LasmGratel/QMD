package lach_01298.qmd.block.menu;

import lach_01298.qmd.block.entity.liquefier.LiquefierControllerBlockEntity;
import lach_01298.qmd.network.LiquefierStatePayload.State;
import lach_01298.qmd.network.LiquefierStatePayload;
import lach_01298.qmd.registry.QMDMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

/** Configuration-only controller menu, matching the slot-free legacy controller. */
public final class LiquefierMenu extends AbstractContainerMenu {

    private final LiquefierControllerBlockEntity controller;
    private final Player owner;
    private State state = State.empty();

    public LiquefierMenu(int id, Inventory inventory, LiquefierControllerBlockEntity controller) {
        super(QMDMenus.LIQUEFIER.get(), id);
        this.controller = controller;
        owner = inventory.player;
    }

    public LiquefierMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
        this(id, inventory, requireController(inventory.player, buf.readBlockPos()));
    }

    private static LiquefierControllerBlockEntity requireController(Player player, BlockPos pos) {
        if (player.level()
            .hasChunkAt(pos)
            && player.level()
                .getBlockEntity(pos) instanceof LiquefierControllerBlockEntity controller)
            return controller;
        throw new IllegalStateException("Missing liquefier controller at " + pos);
    }

    public BlockPos getBlockPos() {
        return controller.getBlockPos();
    }

    public State state() {
        return owner.level().isClientSide ? state
            : controller.liquefier() == null ? State.empty() : State.from(controller.liquefier());
    }

    public void receiveState(State state) {
        this.state = state;
    }

    @Override
    public boolean stillValid(Player player) {
        return player == owner && !controller.isRemoved()
            && controller.getLevel() == player.level()
            && player.level()
                .hasChunkAt(getBlockPos())
            && player.level()
                .getBlockEntity(getBlockPos()) == controller
            && controller.isMachineAssembled()
            && player.distanceToSqr(getBlockPos().getCenter()) <= 64;
    }

    @Override
    public boolean clickMenuButton(Player player, int button) {
        if (button != 0 || player.level().isClientSide || player.containerMenu != this || !stillValid(player))
            return false;
        controller.getMultiblockController()
            .ifPresent(multiblock -> {
                multiblock.clearAllMaterial();
                controller.markTileDirty();
            });
        broadcastChanges();
        return true;
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        sendState();
    }

    @Override
    public void sendAllDataToRemote() {
        super.sendAllDataToRemote();
        sendState();
    }

    private void sendState() {
        if (owner instanceof ServerPlayer player && player.containerMenu == this && stillValid(player))
            PacketDistributor.sendToPlayer(player, new LiquefierStatePayload(containerId, getBlockPos(), state()));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
