package lach_01298.qmd.compat.jei;

import lach_01298.qmd.config.MachineViewerStats;
import lach_01298.qmd.network.MachineViewerStatsPayload;

import io.netty.buffer.Unpooled;
import lach_01298.qmd.config.QMDConfig;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Server configuration snapshots must remain authoritative even with a divergent integrated-client cache. */
@GameTestHolder("qmd")
@PrefixGameTestTemplate(false)
public final class MachineViewerStatsGameTests {
    private MachineViewerStatsGameTests() {}

    @GameTest(template = "empty", batch = "qmd.jei.stats")
    public static void synchronizationSnapshotsServerConfigWithoutChangingClientCache(GameTestHelper helper) {
        MachineViewerStats original = MachineViewerStats.configured();
        MachineViewerStats localClient = new MachineViewerStats(17, 0, 3, 5, 17, 2, 3, 5, 2, 3);
        MachineViewerStats serverValues = new MachineViewerStats(811, 103, 79, 157, 340, 19, 7, 13, 217, 413);
        MachineViewerStats.receiveClient(localClient);
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            setConfig(serverValues);
            MachineViewerStatsPayload snapshot = MachineViewerStatsPayload.configured();
            helper.assertTrue(snapshot.stats().equals(serverValues) && MachineViewerStats.client().equals(localClient),
                    "Server synchronization must snapshot actual COMMON values rather than client-cache values");
            MachineViewerStatsPayload.onDatapackSync(new OnDatapackSyncEvent(helper.getLevel().getServer().getPlayerList(), null));
            helper.assertTrue(MachineViewerStats.client().equals(localClient),
                    "Server datapack sync must never assign an integrated client's cache directly");
            setConfig(original);
            MachineViewerStatsPayload.STREAM_CODEC.encode(buffer, snapshot);
            var decoded = MachineViewerStatsPayload.STREAM_CODEC.decode(buffer);
            helper.assertTrue(decoded.stats().equals(serverValues) && buffer.readableBytes() == 0,
                    "A pending packet must retain its complete server snapshot after configuration changes");
            MachineViewerStats.receiveClient(decoded.stats());
            helper.assertTrue(MachineViewerStats.client().equals(serverValues)
                            && MachineViewerStats.configured().equals(original),
                    "Client receive must override viewer settings without changing gameplay configuration");
            MachineViewerStats.resetClient();
            helper.assertTrue(MachineViewerStats.client().equals(original),
                    "Disconnect reset must discard the previous server's values");
        } finally {
            setConfig(original);
            MachineViewerStats.resetClient();
            buffer.release();
        }
        helper.succeed();
    }

    private static void setConfig(MachineViewerStats stats) {
        QMDConfig.ORE_LEACHER_TIME.set(stats.oreLeacherTime());
        QMDConfig.ORE_LEACHER_POWER.set(stats.oreLeacherPower());
        QMDConfig.LIQUID_COLLECTOR_POWER.set(stats.liquidCollectorPower());
        QMDConfig.ATMOSPHERE_COLLECTOR_POWER.set(stats.atmosphereCollectorPower());
        lach_01298.qmd.config.IrradiatorConfig.PROCESS_TIME.set(stats.irradiatorTime());
        lach_01298.qmd.config.IrradiatorConfig.FUEL_USAGE.set(stats.irradiatorFuelUsage());
        lach_01298.qmd.config.AcceleratorConfig.SOURCES[0].multiplier().set(stats.basicSourceMultiplier());
        lach_01298.qmd.config.AcceleratorConfig.SOURCES[1].multiplier().set(stats.laserSourceMultiplier());
        lach_01298.qmd.config.ChamberConfig.TARGET_SCALE.set(stats.targetChamberScale());
        lach_01298.qmd.config.ChamberConfig.NUCLEOSYNTHESIS_SCALE.set(stats.nucleosynthesisScale());
    }
}
