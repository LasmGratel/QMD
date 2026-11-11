package lach_01298.qmd.config;

import java.util.Objects;

/** Machine base settings shared with recipe viewers without requiring JEI on either side. */
public record MachineViewerStats(int oreLeacherTime, int oreLeacherPower, int liquidCollectorPower,
    int atmosphereCollectorPower, int irradiatorTime, int irradiatorFuelUsage, int basicSourceMultiplier,
    int laserSourceMultiplier, int targetChamberScale, int nucleosynthesisScale) {

    private static volatile MachineViewerStats clientStats;

    public MachineViewerStats {
        if (oreLeacherTime <= 0 || oreLeacherPower < 0
            || liquidCollectorPower < 0
            || atmosphereCollectorPower < 0
            || irradiatorTime <= 0
            || irradiatorFuelUsage < 0
            || basicSourceMultiplier <= 0
            || laserSourceMultiplier <= 0
            || targetChamberScale <= 0
            || nucleosynthesisScale <= 0) {
            throw new IllegalArgumentException("Viewer processing time must be positive and power values nonnegative");
        }
    }

    public MachineViewerStats(int oreLeacherTime, int oreLeacherPower, int liquidCollectorPower,
        int atmosphereCollectorPower, int irradiatorTime, int irradiatorFuelUsage, int basicSourceMultiplier,
        int laserSourceMultiplier) {
        this(
            oreLeacherTime,
            oreLeacherPower,
            liquidCollectorPower,
            atmosphereCollectorPower,
            irradiatorTime,
            irradiatorFuelUsage,
            basicSourceMultiplier,
            laserSourceMultiplier,
            ChamberConfig.TARGET_SCALE.get(),
            ChamberConfig.NUCLEOSYNTHESIS_SCALE.get());
    }

    public MachineViewerStats(int oreLeacherTime, int oreLeacherPower, int liquidCollectorPower,
        int atmosphereCollectorPower) {
        this(
            oreLeacherTime,
            oreLeacherPower,
            liquidCollectorPower,
            atmosphereCollectorPower,
            IrradiatorConfig.PROCESS_TIME.get(),
            IrradiatorConfig.FUEL_USAGE.get(),
            AcceleratorConfig.SOURCES[0].multiplier()
                .get(),
            AcceleratorConfig.SOURCES[1].multiplier()
                .get());
    }

    /** Always reads this process's configuration, never the remote-server client cache. */
    public static MachineViewerStats configured() {
        return new MachineViewerStats(
            QMDConfig.ORE_LEACHER_TIME.get(),
            QMDConfig.ORE_LEACHER_POWER.get(),
            QMDConfig.LIQUID_COLLECTOR_POWER.get(),
            QMDConfig.ATMOSPHERE_COLLECTOR_POWER.get());
    }

    public static MachineViewerStats client() {
        MachineViewerStats received = clientStats;
        return received == null ? configured() : received;
    }

    /** Called only by the clientbound payload handler; server/integrated-server sync never mutates this cache. */
    public static void receiveClient(MachineViewerStats stats) {
        clientStats = Objects.requireNonNull(stats);
    }

    /** Prevent a previous server's settings from leaking into the next connection. */
    public static void resetClient() {
        clientStats = null;
    }
}
