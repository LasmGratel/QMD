package lach_01298.qmd.multiblock;

import lach_01298.qmd.recipe.chamber.ChamberKind;
import org.jetbrains.annotations.Nullable;

/** Stable physical roles shared by controller subclasses, parts, registration and multiblock validation. */
public enum ChamberPart {

    CASING("particle_chamber_casing", false),
    GLASS("particle_chamber_glass", false),
    BEAM("particle_chamber_beam", true),
    CHAMBER("particle_chamber", true),
    ENERGY_PORT("particle_chamber_energy_port", false),
    ITEM_PORT("particle_chamber_port", false),
    FLUID_PORT("particle_chamber_fluid_port", false),
    BEAM_PORT("particle_chamber_beam_port", false),
    BUBBLE_CHAMBER("bubble_chamber", true, .075, 200, 2),
    SILICON_TRACKER("silicon_tracker", true, .15, 2000, 1),
    WIRE_CHAMBER("wire_chamber", true, .1, 1000, 2),
    EM_CALORIMETER("em_calorimeter", true, .05, 200, 3),
    HADRON_CALORIMETER("hadron_calorimeter", true, .025, 100, 5),
    TARGET_CONTROLLER("target_chamber_controller", ChamberKind.TARGET_CHAMBER),
    DECAY_CONTROLLER("decay_chamber_controller", ChamberKind.DECAY_CHAMBER),
    COLLISION_CONTROLLER("collision_chamber_controller", ChamberKind.COLLISION_CHAMBER),
    BEAM_DUMP_CONTROLLER("beam_dump_controller", ChamberKind.BEAM_DUMP),
    VACUUM_CASING("vacuum_chamber_casing", false),
    VACUUM_GLASS("vacuum_chamber_glass", false),
    VACUUM_BEAM("vacuum_chamber_beam", true),
    VACUUM_COIL("vacuum_chamber_coil", true),
    PLASMA_GLASS("vacuum_chamber_plasma_glass", true),
    PLASMA_NOZZLE("vacuum_chamber_plasma_nozzle", true),
    VACUUM_LASER("vacuum_chamber_laser", false),
    VACUUM_ENERGY_PORT("vacuum_chamber_energy_port", false),
    VACUUM_ITEM_PORT("vacuum_chamber_port", false),
    VACUUM_FLUID_PORT("vacuum_chamber_fluid_port", false),
    VACUUM_BEAM_PORT("vacuum_chamber_beam_port", false),
    COOLANT_VENT("vacuum_chamber_vent", false),
    HEATER_VENT("vacuum_chamber_heater_vent", false),
    REDSTONE_PORT("vacuum_chamber_redstone_port", false),
    HEATER_IRON("vacuum_chamber_heater_iron", true),
    HEATER_REDSTONE("vacuum_chamber_heater_redstone", true),
    HEATER_QUARTZ("vacuum_chamber_heater_quartz", true),
    HEATER_OBSIDIAN("vacuum_chamber_heater_obsidian", true),
    HEATER_GLOWSTONE("vacuum_chamber_heater_glowstone", true),
    HEATER_LAPIS("vacuum_chamber_heater_lapis", true),
    HEATER_GOLD("vacuum_chamber_heater_gold", true),
    HEATER_DIAMOND("vacuum_chamber_heater_diamond", true),
    CONTAINMENT_CONTROLLER("neutral_containment_controller", ChamberKind.NEUTRAL_CONTAINMENT),
    NUCLEOSYNTHESIS_CONTROLLER("nucleosynthesis_chamber_controller", ChamberKind.NUCLEOSYNTHESIS_CHAMBER);

    public final String id;
    public final boolean interior;
    public final double efficiency;
    public final int power, distance;
    @Nullable
    public final ChamberKind kind;

    ChamberPart(String id, boolean interior) {
        this(id, interior, 0, 0, 0);
    }

    ChamberPart(String id, boolean interior, double efficiency, int power, int distance) {
        this.id = id;
        this.interior = interior;
        this.efficiency = efficiency;
        this.power = power;
        this.distance = distance;
        this.kind = null;
    }

    ChamberPart(String id, ChamberKind kind) {
        this.id = id;
        this.kind = kind;
        this.interior = false;
        this.efficiency = 0;
        this.power = 0;
        this.distance = 0;
    }

    public boolean controller() {
        return kind != null;
    }

    public boolean detector() {
        return efficiency > 0;
    }

    public boolean vacuum() {
        return ordinal() >= VACUUM_CASING.ordinal();
    }

    public boolean heater() {
        return ordinal() >= HEATER_IRON.ordinal() && ordinal() <= HEATER_DIAMOND.ordinal();
    }

    public boolean casing() {
        return this == CASING || this == VACUUM_CASING;
    }

    public boolean beamPort() {
        return this == BEAM_PORT || this == VACUUM_BEAM_PORT;
    }

    public boolean energyPort() {
        return this == ENERGY_PORT || this == VACUUM_ENERGY_PORT;
    }

    public boolean itemPort() {
        return this == ITEM_PORT || this == VACUUM_ITEM_PORT;
    }

    public boolean fluidPort() {
        return this == FLUID_PORT || this == VACUUM_FLUID_PORT || this == COOLANT_VENT || this == HEATER_VENT;
    }
}
