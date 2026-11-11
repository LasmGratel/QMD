package lach_01298.qmd.menu;

import lach_01298.qmd.item.player.BeamMeterReading;
import lach_01298.qmd.item.player.BeamMeterStatePayload;
import lach_01298.qmd.registry.QMDItems;
import lach_01298.qmd.registry.QMDMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Objects;

/** Read-only, no-slot menu that remains owned by the exact hand and hotbar slot used to measure. */
public final class BeamMeterMenu extends AbstractContainerMenu {

    public static final int OFFHAND_SLOT = -1;

    private final Player owner;
    private final InteractionHand hand;
    private final int slot;
    private BeamMeterReading reading;

    public BeamMeterMenu(int containerId, Inventory inventory, InteractionHand hand, int slot,
        BeamMeterReading reading) {
        super(QMDMenus.BEAM_METER.get(), containerId);
        this.owner = inventory.player;
        this.hand = Objects.requireNonNull(hand, "hand");
        this.slot = slot;
        this.reading = Objects.requireNonNull(reading, "reading");
        if (hand == InteractionHand.MAIN_HAND && (slot < 0 || slot >= Inventory.getSelectionSize())) {
            throw new IllegalArgumentException("Invalid beam-meter hotbar slot " + slot);
        }
        if (hand == InteractionHand.OFF_HAND && slot != OFFHAND_SLOT) {
            throw new IllegalArgumentException("Offhand beam meters must use the offhand slot marker");
        }
    }

    public BeamMeterMenu(int containerId, Inventory inventory, FriendlyByteBuf data) {
        this(
            containerId,
            inventory,
            data.readEnum(InteractionHand.class),
            data.readVarInt(),
            BeamMeterReading.STREAM_CODEC.decode(data));
    }

    public static void writeOpeningData(FriendlyByteBuf buffer, InteractionHand hand, int slot,
        BeamMeterReading reading) {
        buffer.writeEnum(hand);
        buffer.writeVarInt(slot);
        BeamMeterReading.STREAM_CODEC.encode(buffer, reading);
    }

    public InteractionHand hand() {
        return hand;
    }

    public int heldSlot() {
        return slot;
    }

    public BeamMeterReading reading() {
        return reading;
    }

    public void receiveReading(BeamMeterReading reading) {
        this.reading = Objects.requireNonNull(reading, "reading");
    }

    @Override
    public boolean stillValid(Player player) {
        if (player != owner || !player.isAlive()) return false;
        if (hand == InteractionHand.MAIN_HAND && player.getInventory().selected != slot) return false;
        return player.getItemInHand(hand)
            .is(QMDItems.BEAM_METER.get());
    }

    @Override
    public void sendAllDataToRemote() {
        super.sendAllDataToRemote();
        if (owner instanceof ServerPlayer player && player.containerMenu == this && stillValid(player)) {
            PacketDistributor.sendToPlayer(player, new BeamMeterStatePayload(containerId, reading));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
