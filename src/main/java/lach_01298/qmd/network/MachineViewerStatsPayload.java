package lach_01298.qmd.network;

import lach_01298.qmd.config.MachineViewerStats;

import io.netty.handler.codec.DecoderException;
import lach_01298.qmd.config.QMDConfig;
import lach_01298.qmd.config.AcceleratorConfig;
import lach_01298.qmd.config.IrradiatorConfig;
import lach_01298.qmd.config.ChamberConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.Objects;

/** Authoritative machine settings, sent before recipe synchronization and independently of JEI installation. */
public record MachineViewerStatsPayload(MachineViewerStats stats) implements CustomPacketPayload {

    public static final Type<MachineViewerStatsPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath("qmd", "machine_viewer_stats"));
    public static final StreamCodec<FriendlyByteBuf, MachineViewerStatsPayload> STREAM_CODEC = new StreamCodec<>() {

        @Override
        public MachineViewerStatsPayload decode(FriendlyByteBuf buffer) {
            try {
                return new MachineViewerStatsPayload(
                    new MachineViewerStats(
                        buffer.readVarInt(),
                        buffer.readVarInt(),
                        buffer.readVarInt(),
                        buffer.readVarInt(),
                        buffer.readVarInt(),
                        buffer.readVarInt(),
                        buffer.readVarInt(),
                        buffer.readVarInt(),
                        buffer.readVarInt(),
                        buffer.readVarInt()));
            } catch (IllegalArgumentException exception) {
                throw new DecoderException("Invalid QMD machine viewer settings", exception);
            }
        }

        @Override
        public void encode(FriendlyByteBuf buffer, MachineViewerStatsPayload payload) {
            buffer.writeVarInt(payload.stats.oreLeacherTime());
            buffer.writeVarInt(payload.stats.oreLeacherPower());
            buffer.writeVarInt(payload.stats.liquidCollectorPower());
            buffer.writeVarInt(payload.stats.atmosphereCollectorPower());
            buffer.writeVarInt(payload.stats.irradiatorTime());
            buffer.writeVarInt(payload.stats.irradiatorFuelUsage());
            buffer.writeVarInt(payload.stats.basicSourceMultiplier());
            buffer.writeVarInt(payload.stats.laserSourceMultiplier());
            buffer.writeVarInt(payload.stats.targetChamberScale());
            buffer.writeVarInt(payload.stats.nucleosynthesisScale());
        }
    };

    public MachineViewerStatsPayload {
        Objects.requireNonNull(stats);
    }

    @Override
    public Type<MachineViewerStatsPayload> type() {
        return TYPE;
    }

    public static MachineViewerStatsPayload configured() {
        return new MachineViewerStatsPayload(MachineViewerStats.configured());
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        // The registrar's default MAIN handler preserves ordering with the following vanilla recipe packet.
        event.registrar("2")
            .playToClient(TYPE, STREAM_CODEC, (payload, context) -> MachineViewerStats.receiveClient(payload.stats));
    }

    public static void onDatapackSync(OnDatapackSyncEvent event) {
        // NeoForge 21.1.265 PlayerList posts this before recipe packets on both login and resource reload.
        MachineViewerStatsPayload payload = configured();
        event.getRelevantPlayers()
            .forEach(player -> PacketDistributor.sendToPlayer(player, payload));
    }

    public static void onConfigReload(ModConfigEvent.Reloading event) {
        var spec = event.getConfig()
            .getSpec();
        if (spec != QMDConfig.SPEC && spec != AcceleratorConfig.SPEC
            && spec != IrradiatorConfig.SPEC
            && spec != ChamberConfig.SPEC) return;
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        // File watching can fire off-thread. Read and broadcast the current server values on its main thread.
        server.execute(() -> {
            if (ServerLifecycleHooks.getCurrentServer() == server) {
                MachineViewerStatsPayload payload = configured();
                server.getPlayerList()
                    .getPlayers()
                    .forEach(player -> PacketDistributor.sendToPlayer(player, payload));
            }
        });
    }
}
