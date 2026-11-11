package lach_01298.qmd.multiblock;

import lach_01298.qmd.block.entity.chamber.AbstractChamberBlockEntity;
import lach_01298.qmd.config.VacuumConfig;
import lach_01298.qmd.recipe.chamber.ChamberKind;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import java.util.*;

/** Exact cold-apparatus geometry, independent of controller persistence and recipe execution. */
public final class VacuumGeometry {

    private final ChamberMultiblock machine;
    private boolean alongX, positiveInput;
    private List<BlockPos> plasma = List.of();

    public VacuumGeometry(ChamberMultiblock machine) {
        this.machine = machine;
    }

    private List<AbstractChamberBlockEntity> parts(ChamberPart role) {
        return machine.parts(role);
    }

    private boolean has(BlockPos position, ChamberPart role) {
        return machine.getWorld()
            .getBlockEntity(position) instanceof AbstractChamberBlockEntity part && part.role() == role;
    }

    private boolean fail(String error, BlockPos position) {
        machine.setLastError(
            position,
            "qmd.multiblock_validation.vacuum." + error,
            position.getX(),
            position.getY(),
            position.getZ());
        return false;
    }

    public boolean validate() {
        var controller = machine.controller();
        for (var part : machine.getParts(AbstractChamberBlockEntity.class)) if (!part.role()
            .vacuum()) return fail("foreign_part", part.getBlockPos());
        if (parts(ChamberPart.VACUUM_ENERGY_PORT).isEmpty()) return fail("energy", controller.getBlockPos());
        if (!ports(ChamberPart.COOLANT_VENT)) return fail("cooling_vents", controller.getBlockPos());
        if (controller.role().kind == ChamberKind.NEUTRAL_CONTAINMENT) return neutral();
        return nucleosynthesis();
    }

    private boolean ports(ChamberPart role) {
        return parts(role).stream()
            .anyMatch(AbstractChamberBlockEntity::output)
            && parts(role).stream()
                .anyMatch(p -> !p.output());
    }

    private boolean neutral() {
        int x = machine.getExteriorLengthX(), y = machine.getExteriorLengthY(), z = machine.getExteriorLengthZ();
        int min = VacuumConfig.MIN_SIZE.get() + 2, max = Math.max(min, VacuumConfig.MAX_SIZE.get() + 2);
        if (x != z || x % 2 == 0 || x < min || x > max || y < min || y > max)
            return fail("neutral_size", machine.getMiddleCoord());
        if (!parts(ChamberPart.VACUUM_FLUID_PORT).isEmpty() || !parts(ChamberPart.HEATER_VENT).isEmpty())
            return fail("neutral_ports", machine.getMiddleCoord());
        var lasers = parts(ChamberPart.VACUUM_LASER);
        var beams = parts(ChamberPart.VACUUM_BEAM_PORT);
        if (lasers.size() != 2 || beams.size() != 2
            || beams.stream()
                .anyMatch(AbstractChamberBlockEntity::output))
            return fail("neutral_beams", machine.getMiddleCoord());
        Set<BlockPos> laserExpected = new HashSet<>(), beamExpected = new HashSet<>();
        boolean laserX = lasers.getFirst()
            .getBlockPos()
            .getX() == machine.getMinX()
            || lasers.getFirst()
                .getBlockPos()
                .getX() == machine.getMaxX();
        for (Direction d : Direction.Plane.HORIZONTAL) {
            BlockPos position = machine.getMiddleCoord()
                .relative(d, x / 2);
            boolean laser = (d.getAxis() == Direction.Axis.X) == laserX;
            if (!has(position, laser ? ChamberPart.VACUUM_LASER : ChamberPart.VACUUM_BEAM_PORT))
                return fail("neutral_beams", position);
            (laser ? laserExpected : beamExpected).add(position);
        }
        for (int px = machine.getMinX() + 1; px < machine.getMaxX(); px++)
            for (int py = machine.getMinY() + 1; py < machine.getMaxY(); py++)
                for (int pz = machine.getMinZ() + 1; pz < machine.getMaxZ(); pz++) {
                    BlockPos position = new BlockPos(px, py, pz);
                    boolean ring = (py == machine.getMinY() + 1 || py == machine.getMaxY() - 1)
                        && px >= machine.getMinX() + 2
                        && px <= machine.getMaxX() - 2
                        && pz >= machine.getMinZ() + 2
                        && pz <= machine.getMaxZ() - 2
                        && (px == machine.getMinX() + 2 || px == machine.getMaxX() - 2
                            || pz == machine.getMinZ() + 2
                            || pz == machine.getMaxZ() - 2);
                    if (ring ? !has(position, ChamberPart.VACUUM_COIL)
                        : !machine.getWorld()
                            .getBlockState(position)
                            .isAir())
                        return fail("neutral_coils", position);
                }
        plasma = List.of();
        return true;
    }

    private BlockPos pos(int u, int y, int v) {
        return alongX ? new BlockPos(machine.getMinX() + u, machine.getMinY() + y, machine.getMinZ() + v)
            : new BlockPos(machine.getMinX() + v, machine.getMinY() + y, machine.getMinZ() + u);
    }

    private boolean nucleosynthesis() {
        alongX = machine.getExteriorLengthX() == 11;
        if (machine.getExteriorLengthY() != 7 || (alongX ? machine.getExteriorLengthZ() != 5
            : machine.getExteriorLengthZ() != 11 || machine.getExteriorLengthX() != 5))
            return fail("nucleosynthesis_size", machine.getMiddleCoord());
        if (!parts(ChamberPart.VACUUM_ITEM_PORT).isEmpty() || !parts(ChamberPart.VACUUM_LASER).isEmpty()
            || !parts(ChamberPart.VACUUM_COIL).isEmpty())
            return fail("nucleosynthesis_ports", machine.getMiddleCoord());
        if (!ports(ChamberPart.HEATER_VENT)) return fail("heater_vents", machine.getMiddleCoord());
        var beams = parts(ChamberPart.VACUUM_BEAM_PORT);
        if (beams.size() != 1 || beams.getFirst()
            .output()) return fail("nucleosynthesis_beam", machine.getMiddleCoord());
        positiveInput = beams.getFirst()
            .getBlockPos()
            .equals(pos(10, 2, 2));
        if (!positiveInput && !beams.getFirst()
            .getBlockPos()
            .equals(pos(0, 2, 2))) return fail(
                "nucleosynthesis_beam",
                beams.getFirst()
                    .getBlockPos());
        if (parts(ChamberPart.VACUUM_FLUID_PORT).size() != 4)
            return fail("nucleosynthesis_ports", machine.getMiddleCoord());
        for (int end : new int[] { 0, 10 }) for (int h : new int[] { 3, 5 }) {
            BlockPos position = pos(end, h, 2);
            if (!has(position, ChamberPart.VACUUM_FLUID_PORT)) return fail("nucleosynthesis_ports", position);
            var part = (AbstractChamberBlockEntity) machine.getWorld()
                .getBlockEntity(position);
            boolean input = (end == 10) == positiveInput;
            if (part.output() == input) return fail("fluid_direction", position);
            part.assignChannel((input ? 4 : 6) + (h == 5 ? 0 : 1));
        }
        var plasma = new ArrayList<BlockPos>();
        for (int u = 1; u <= 9; u++) for (int y = 1; y <= 5; y++) for (int v = 1; v <= 3; v++) {
            BlockPos p = pos(u, y, v);
            ChamberPart expected = null;
            if (v == 2 && (y == 5 || (u == 1 || u == 9) && y >= 2)) expected = ChamberPart.VACUUM_BEAM;
            else if (v == 2 && y == 2 && (u == 2 || u == 8)) expected = ChamberPart.PLASMA_NOZZLE;
            else if (u >= 3 && u <= 7 && (v == 2 && (y == 1 || y == 3) || y == 2 && (v == 1 || v == 3)))
                expected = ChamberPart.PLASMA_GLASS;
            else if (u >= 3 && u <= 7 && y == 2 && v == 2) {
                plasma.add(p);
                if (!machine.getWorld()
                    .getBlockState(p)
                    .isAir()
                    && !machine.getWorld()
                        .getFluidState(p)
                        .is(
                            com.nred.nuclearcraft.registration.FluidRegistration.CUSTOM_FLUID_MAP.get("plasma").still
                                .get()))
                    return fail("plasma_space", p);
                continue;
            }
            if (expected != null) {
                if (!has(p, expected)) return fail("apparatus", p);
            } else if (!machine.getWorld()
                .getBlockState(p)
                .isAir()
                && !(machine.getWorld()
                    .getBlockEntity(p) instanceof AbstractChamberBlockEntity part && part.role()
                        .heater()))
                return fail("apparatus", p);
        }
        this.plasma = List.copyOf(plasma);
        return true;
    }

    public void formed() {
        var controller = machine.controller();
        if (controller == null || controller.vacuum() == null) return;
        int power = 0, maximum = 400;
        long heat = 0;
        for (var part : machine.getParts(AbstractChamberBlockEntity.class)) {
            int i = VacuumConfig.componentIndex(part.role());
            if (i < 0) continue;
            power = (int) Math
                .min(Integer.MAX_VALUE, (long) power + VacuumConfig.value(VacuumConfig.POWER.get(), i, 0));
            heat += VacuumConfig.value(VacuumConfig.HEAT.get(), i, 0);
            maximum = Math.min(maximum, VacuumConfig.value(VacuumConfig.MAX_TEMPERATURE.get(), i, 110));
        }
        long cooling = heaterCooling();
        int volume = machine.getExteriorLengthX() * machine.getExteriorLengthY() * machine.getExteriorLengthZ();
        controller
            .formed((int) Math.min(Integer.MAX_VALUE, (long) volume * VacuumConfig.ENERGY_CAPACITY.get()), power, 1);
        controller.vacuum()
            .formed(volume, heat, cooling, maximum, plasma);
        boolean laserX = !parts(ChamberPart.VACUUM_LASER).isEmpty() && (parts(ChamberPart.VACUUM_LASER).getFirst()
            .getBlockPos()
            .getX() == machine.getMinX()
            || parts(ChamberPart.VACUUM_LASER).getFirst()
                .getBlockPos()
                .getX() == machine.getMaxX());
        controller.visualBounds(
            new BlockPos(machine.getMinX(), machine.getMinY(), machine.getMinZ()),
            new BlockPos(machine.getMaxX(), machine.getMaxY(), machine.getMaxZ()),
            laserX);
        controller.syncVisuals();
        int storedCapacity = (int) Math.min(Integer.MAX_VALUE, Math.pow(machine.getInteriorLengthX() - 4, 3) * 8000);
        for (int i = 0; i < 8; i++) controller.tank(i)
            .setCapacity(
                i == 2 && controller.role().kind == ChamberKind.NEUTRAL_CONTAINMENT ? storedCapacity
                    : (int) Math.min(
                        Integer.MAX_VALUE,
                        (long) (i < 2 ? volume : i < 4 ? 100 : 10)
                            * (i % 2 == 0 ? VacuumConfig.INPUT_CAPACITY.get() : VacuumConfig.OUTPUT_CAPACITY.get())));
    }

    private long heaterCooling() {
        Set<BlockPos> valid = new HashSet<>();
        boolean changed;
        do {
            changed = false;
            for (var part : machine.getParts(AbstractChamberBlockEntity.class)) if (part.role()
                .heater() && !valid.contains(part.getBlockPos())
                && heaterValid(part, valid)) {
                    valid.add(part.getBlockPos());
                    changed = true;
                }
        } while (changed);
        long cooling = 0;
        for (var part : machine.getParts(AbstractChamberBlockEntity.class))
            if (valid.contains(part.getBlockPos())) cooling += VacuumConfig.value(
                VacuumConfig.HEATER_COOLING.get(),
                part.role()
                    .ordinal() - ChamberPart.HEATER_IRON.ordinal(),
                0);
        return cooling;
    }

    private boolean heaterValid(AbstractChamberBlockEntity part, Set<BlockPos> valid) {
        var rule = VacuumConfig.HEATER_RULES.get(part.role().id.substring("vacuum_chamber_heater_".length()));
        return rule != null && VacuumHeaters.test(rule.get(), part, valid);
    }
}
