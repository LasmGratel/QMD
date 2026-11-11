package lach_01298.qmd.multiblock.accelerator;

/** Controller-selected beam topology; routing is determined from physical ports. */
public enum AcceleratorKind {

    LINEAR("linear_accelerator", new LinearAcceleratorLogic()),
    RING("ring_accelerator", new RingAcceleratorLogic()),
    DECELERATOR("decelerator", new DeceleratorLogic()),
    DIVERTER("beam_diverter", new BeamDiverterLogic()),
    SPLITTER("beam_splitter", new BeamSplitterLogic()),
    SPECTROMETER("mass_spectrometer", new MassSpectrometerLogic());

    public final String id;
    public final AbstractAcceleratorLogic logic;

    AcceleratorKind(String id, AbstractAcceleratorLogic logic) {
        this.id = id;
        this.logic = logic;
    }
}
