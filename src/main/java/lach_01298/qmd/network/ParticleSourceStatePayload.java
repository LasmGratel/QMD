package lach_01298.qmd.network;

import lach_01298.qmd.block.menu.CreativeParticleSourceMenu;

import lach_01298.qmd.particle.ParticleBeam;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/** One immutable beam, rather than mutable storage objects or independently truncated data slots. */
public record ParticleSourceStatePayload(int containerId, BlockPos pos, ParticleBeam beam)
    implements CustomPacketPayload {

    public static final Type<ParticleSourceStatePayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath("qmd", "particle_source_state"));
    public static final StreamCodec<FriendlyByteBuf, ParticleSourceStatePayload> STREAM_CODEC = new StreamCodec<>() {

        @Override
        public ParticleSourceStatePayload decode(FriendlyByteBuf buffer) {
            return new ParticleSourceStatePayload(
                buffer.readVarInt(),
                buffer.readBlockPos(),
                ParticleBeam.STREAM_CODEC.decode(buffer));
        }

        @Override
        public void encode(FriendlyByteBuf buffer, ParticleSourceStatePayload payload) {
            buffer.writeVarInt(payload.containerId);
            buffer.writeBlockPos(payload.pos);
            ParticleBeam.STREAM_CODEC.encode(buffer, payload.beam);
        }
    };

    public ParticleSourceStatePayload {
        pos = pos.immutable();
    }

    @Override
    public Type<ParticleSourceStatePayload> type() {
        return TYPE;
    }

    public static boolean apply(Player player, ParticleSourceStatePayload payload) {
        if (player.containerMenu instanceof CreativeParticleSourceMenu menu && menu.containerId == payload.containerId
            && menu.getBlockPos()
                .equals(payload.pos)) {
            menu.receiveBeam(payload.beam);
            return true;
        }
        return false;
    }
}
