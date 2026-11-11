package lach_01298.qmd.compat.jei;

import lach_01298.qmd.config.MachineViewerStats;
import lach_01298.qmd.network.MachineViewerStatsPayload;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import lach_01298.qmd.config.QMDConfig;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MachineViewerStatsTest {

    @BeforeEach
    @AfterEach
    void resetClientSnapshot() {
        MachineViewerStats.resetClient();
    }

    @Test
    void snapshotsRejectInvalidTimeAndPowerAndKeepFullIntegerPrecision() {
        assertThrows(IllegalArgumentException.class, () -> new MachineViewerStats(0, 0, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new MachineViewerStats(-1, 0, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new MachineViewerStats(1, -1, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new MachineViewerStats(1, 0, -1, 0));
        assertThrows(IllegalArgumentException.class, () -> new MachineViewerStats(1, 0, 0, -1));
        var maximum = new MachineViewerStats(
            Integer.MAX_VALUE,
            Integer.MAX_VALUE,
            Integer.MAX_VALUE,
            Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, maximum.oreLeacherTime());
        assertEquals(Integer.MAX_VALUE, maximum.oreLeacherPower());
        assertEquals(Integer.MAX_VALUE, maximum.liquidCollectorPower());
        assertEquals(Integer.MAX_VALUE, maximum.atmosphereCollectorPower());
        assertEquals(0, new MachineViewerStats(1, 0, 0, 0).oreLeacherPower());
    }

    @Test
    void configuredFactoryReadsEachAuthoritativeLocalSetting() {
        var configured = MachineViewerStats.configured();
        assertEquals(
            QMDConfig.ORE_LEACHER_TIME.get()
                .intValue(),
            configured.oreLeacherTime());
        assertEquals(
            QMDConfig.ORE_LEACHER_POWER.get()
                .intValue(),
            configured.oreLeacherPower());
        assertEquals(
            QMDConfig.LIQUID_COLLECTOR_POWER.get()
                .intValue(),
            configured.liquidCollectorPower());
        assertEquals(
            QMDConfig.ATMOSPHERE_COLLECTOR_POWER.get()
                .intValue(),
            configured.atmosphereCollectorPower());
        assertEquals(
            lach_01298.qmd.config.IrradiatorConfig.PROCESS_TIME.get()
                .intValue(),
            configured.irradiatorTime());
        assertEquals(
            lach_01298.qmd.config.IrradiatorConfig.FUEL_USAGE.get()
                .intValue(),
            configured.irradiatorFuelUsage());
        assertEquals(
            lach_01298.qmd.config.AcceleratorConfig.SOURCES[0].multiplier()
                .get()
                .intValue(),
            configured.basicSourceMultiplier());
        assertEquals(
            lach_01298.qmd.config.AcceleratorConfig.SOURCES[1].multiplier()
                .get()
                .intValue(),
            configured.laserSourceMultiplier());
        assertEquals(
            lach_01298.qmd.config.ChamberConfig.TARGET_SCALE.get()
                .intValue(),
            configured.targetChamberScale());
        assertEquals(
            lach_01298.qmd.config.ChamberConfig.NUCLEOSYNTHESIS_SCALE.get()
                .intValue(),
            configured.nucleosynthesisScale());
        assertEquals(
            configured,
            MachineViewerStatsPayload.configured()
                .stats());
    }

    @Test
    void consecutivePayloadsRoundTripMaximumValuesAndZeroPowerWithoutTrailingData() {
        var first = new MachineViewerStatsPayload(
            new MachineViewerStats(
                Integer.MAX_VALUE,
                Integer.MAX_VALUE,
                Integer.MAX_VALUE,
                Integer.MAX_VALUE,
                Integer.MAX_VALUE,
                Integer.MAX_VALUE,
                7,
                19,
                Integer.MAX_VALUE,
                513));
        var second = new MachineViewerStatsPayload(new MachineViewerStats(1, 0, 0, 0, 1, 0, 1, 1));
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            MachineViewerStatsPayload.STREAM_CODEC.encode(buffer, first);
            MachineViewerStatsPayload.STREAM_CODEC.encode(buffer, second);
            assertEquals(first, MachineViewerStatsPayload.STREAM_CODEC.decode(buffer));
            assertTrue(buffer.isReadable(), "The first decode must leave the complete next payload");
            assertEquals(second, MachineViewerStatsPayload.STREAM_CODEC.decode(buffer));
            assertFalse(buffer.isReadable(), "Both decodes must consume exactly the two payloads");
            assertSame(MachineViewerStatsPayload.TYPE, first.type());
        } finally {
            buffer.release();
        }
    }

    @Test
    void malformedPayloadsRejectZeroTimeAndNegativePowerBeforeApplication() {
        for (int[] values : new int[][] { { 0, 0, 0, 0 }, { -1, 0, 0, 0 }, { 1, -1, 0, 0 }, { 1, 0, -1, 0 },
            { 1, 0, 0, -1 } }) {
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            try {
                for (int value : values) buffer.writeVarInt(value);
                for (int value : new int[] { 200, 10, 1, 2, 100, 100 }) buffer.writeVarInt(value);
                assertThrows(DecoderException.class, () -> MachineViewerStatsPayload.STREAM_CODEC.decode(buffer));
                assertEquals(MachineViewerStats.configured(), MachineViewerStats.client());
            } finally {
                buffer.release();
            }
        }
    }

    @Test
    void expandedSettingsRejectInvalidWorkAndSourceMultipliers() {
        assertThrows(IllegalArgumentException.class, () -> new MachineViewerStats(1, 0, 0, 0, 0, 10, 1, 2));
        assertThrows(IllegalArgumentException.class, () -> new MachineViewerStats(1, 0, 0, 0, 200, -1, 1, 2));
        assertThrows(IllegalArgumentException.class, () -> new MachineViewerStats(1, 0, 0, 0, 200, 10, 0, 2));
        assertThrows(IllegalArgumentException.class, () -> new MachineViewerStats(1, 0, 0, 0, 200, 10, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> new MachineViewerStats(1, 0, 0, 0, 200, 10, 1, 2, 0, 100));
        assertThrows(IllegalArgumentException.class, () -> new MachineViewerStats(1, 0, 0, 0, 200, 10, 1, 2, 100, 0));
    }

    @Test
    void receivedValuesReplaceClientFallbackWithoutChangingAuthoritativeSettingsAndResetOnDisconnect() {
        var configured = MachineViewerStats.configured();
        assertEquals(configured, MachineViewerStats.client());
        var first = new MachineViewerStats(
            different(configured.oreLeacherTime()),
            different(configured.oreLeacherPower()),
            different(configured.liquidCollectorPower()),
            different(configured.atmosphereCollectorPower()));
        MachineViewerStats.receiveClient(first);
        assertEquals(first, MachineViewerStats.client());
        assertNotEquals(configured, MachineViewerStats.client());
        assertEquals(configured, MachineViewerStats.configured());
        assertEquals(
            configured,
            MachineViewerStatsPayload.configured()
                .stats());

        var replacement = new MachineViewerStats(different(first.oreLeacherTime()), 0, 0, 0);
        MachineViewerStats.receiveClient(replacement);
        assertEquals(replacement, MachineViewerStats.client());
        assertNotEquals(first, MachineViewerStats.client());
        assertEquals(configured, MachineViewerStats.configured());

        MachineViewerStats.resetClient();
        assertEquals(configured, MachineViewerStats.client());
    }

    private static int different(int value) {
        return value == Integer.MAX_VALUE ? 1 : value + 1;
    }
}
