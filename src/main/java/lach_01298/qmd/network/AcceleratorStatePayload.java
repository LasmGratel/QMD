package lach_01298.qmd.network;

import lach_01298.qmd.block.menu.accelerator.AbstractAcceleratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Bounded by an open menu; full NBT preserves long energy values and exact beam snapshots. */
public record AcceleratorStatePayload(int containerId, BlockPos pos, CompoundTag snapshot)
    implements CustomPacketPayload {

    public static final Type<AcceleratorStatePayload> TYPE = new Type<>(
        ResourceLocation.parse("qmd:accelerator_state"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AcceleratorStatePayload> STREAM_CODEC = new StreamCodec<>() {

        @Override
        public AcceleratorStatePayload decode(RegistryFriendlyByteBuf b) {
            return new AcceleratorStatePayload(
                b.readVarInt(),
                b.readBlockPos(),
                java.util.Objects.requireNonNull(b.readNbt()));
        }

        @Override
        public void encode(RegistryFriendlyByteBuf b, AcceleratorStatePayload p) {
            b.writeVarInt(p.containerId);
            b.writeBlockPos(p.pos);
            b.writeNbt(p.snapshot);
        }
    };

    public AcceleratorStatePayload {
        pos = pos.immutable();
        snapshot = snapshot.copy();
    }

    @Override
    public Type<AcceleratorStatePayload> type() {
        return TYPE;
    }

    public static void handle(AcceleratorStatePayload p, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().containerMenu instanceof AbstractAcceleratorMenu menu
                && menu.containerId == p.containerId
                && menu.pos()
                    .equals(p.pos))
                menu.receiveSnapshot(p.snapshot);
        });
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1")
            .playToClient(
                AcceleratorStatePayload.TYPE,
                AcceleratorStatePayload.STREAM_CODEC,
                AcceleratorStatePayload::handle);
    }
}
