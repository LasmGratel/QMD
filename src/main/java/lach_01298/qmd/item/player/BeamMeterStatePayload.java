package lach_01298.qmd.item.player;

import lach_01298.qmd.menu.BeamMeterMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Objects;

/** Server-authored meter snapshot, accepted only by the matching open menu. */
public record BeamMeterStatePayload(int containerId, BeamMeterReading reading) implements CustomPacketPayload {

    public static final Type<BeamMeterStatePayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath("qmd", "beam_meter_state"));
    public static final StreamCodec<FriendlyByteBuf, BeamMeterStatePayload> STREAM_CODEC = new StreamCodec<>() {

        @Override
        public BeamMeterStatePayload decode(FriendlyByteBuf buffer) {
            return new BeamMeterStatePayload(buffer.readVarInt(), BeamMeterReading.STREAM_CODEC.decode(buffer));
        }

        @Override
        public void encode(FriendlyByteBuf buffer, BeamMeterStatePayload payload) {
            buffer.writeVarInt(payload.containerId);
            BeamMeterReading.STREAM_CODEC.encode(buffer, payload.reading);
        }
    };

    public BeamMeterStatePayload {
        reading = Objects.requireNonNull(reading, "reading");
    }

    @Override
    public Type<BeamMeterStatePayload> type() {
        return TYPE;
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1")
            .playToClient(TYPE, STREAM_CODEC, BeamMeterStatePayload::handle);
    }

    public static void handle(BeamMeterStatePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> apply(context.player(), payload));
    }

    public static boolean apply(Player player, BeamMeterStatePayload payload) {
        if (!(player.containerMenu instanceof BeamMeterMenu menu) || menu.containerId != payload.containerId
            || !menu.stillValid(player)) return false;
        menu.receiveReading(payload.reading);
        return true;
    }
}
