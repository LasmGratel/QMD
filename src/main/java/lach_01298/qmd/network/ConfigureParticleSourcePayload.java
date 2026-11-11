package lach_01298.qmd.network;

import lach_01298.qmd.block.menu.CreativeParticleSourceMenu;

import lach_01298.qmd.particle.ParticleBeam;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public record ConfigureParticleSourcePayload(int containerId, BlockPos pos, ParticleBeam beam)
    implements CustomPacketPayload {

    public static final Type<ConfigureParticleSourcePayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath("qmd", "configure_particle_source"));
    public static final StreamCodec<FriendlyByteBuf, ConfigureParticleSourcePayload> STREAM_CODEC = new StreamCodec<>() {

        @Override
        public ConfigureParticleSourcePayload decode(FriendlyByteBuf buffer) {
            return new ConfigureParticleSourcePayload(
                buffer.readVarInt(),
                buffer.readBlockPos(),
                ParticleBeam.STREAM_CODEC.decode(buffer));
        }

        @Override
        public void encode(FriendlyByteBuf buffer, ConfigureParticleSourcePayload payload) {
            buffer.writeVarInt(payload.containerId);
            buffer.writeBlockPos(payload.pos);
            ParticleBeam.STREAM_CODEC.encode(buffer, payload.beam);
        }
    };

    public ConfigureParticleSourcePayload {
        pos = pos.immutable();
    }

    @Override
    public Type<ConfigureParticleSourcePayload> type() {
        return TYPE;
    }

    public static boolean apply(Player player, ConfigureParticleSourcePayload payload) {
        return player.containerMenu instanceof CreativeParticleSourceMenu menu
            && menu.containerId == payload.containerId
            && menu.getBlockPos()
                .equals(payload.pos)
            && menu.applySettings(player, payload.beam);
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.playToServer(TYPE, STREAM_CODEC, (payload, context) -> apply(context.player(), payload));
        registrar.playToClient(
            ParticleSourceStatePayload.TYPE,
            ParticleSourceStatePayload.STREAM_CODEC,
            (payload, context) -> ParticleSourceStatePayload.apply(context.player(), payload));
    }
}
