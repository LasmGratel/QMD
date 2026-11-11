package lach_01298.qmd.recipe.chamber;

import java.util.Locale;

/** Stable recipe IDs and physical channel counts for chamber reactions. */
public enum ChamberKind {

    TARGET_CHAMBER(1, 1, 1, 1, 1, 3),
    DECAY_CHAMBER(0, 0, 1, 0, 0, 3),
    COLLISION_CHAMBER(0, 0, 2, 0, 0, 4),
    BEAM_DUMP(0, 0, 1, 0, 1, 0),
    NEUTRAL_CONTAINMENT(0, 0, 2, 0, 1, 0),
    CELL_FILLING(1, 1, 0, 1, 1, 0),
    NUCLEOSYNTHESIS_CHAMBER(0, 2, 1, 0, 2, 0),
    VACUUM_CHAMBER_HEATING(0, 1, 0, 0, 1, 0);

    public final int itemInputs, fluidInputs, particleInputs, itemOutputs, fluidOutputs, particleOutputs;

    ChamberKind(int itemInputs, int fluidInputs, int particleInputs, int itemOutputs, int fluidOutputs,
        int particleOutputs) {
        this.itemInputs = itemInputs;
        this.fluidInputs = fluidInputs;
        this.particleInputs = particleInputs;
        this.itemOutputs = itemOutputs;
        this.fluidOutputs = fluidOutputs;
        this.particleOutputs = particleOutputs;
    }

    public String id() {
        return name().toLowerCase(Locale.ROOT);
    }
}
