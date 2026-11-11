package lach_01298.qmd.multiblock;

import com.nred.nuclearcraft.multiblock.Multiblock;
import lach_01298.qmd.block.entity.chamber.AbstractChamberBlockEntity;
import lach_01298.qmd.recipe.chamber.ChamberKind;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Neohaul/ZeroCore owns assembly, chunk suspension and connectivity; this owns QMD geometry. */
public final class ChamberMultiblock extends Multiblock<ChamberMultiblock> {

    @Nullable
    private AbstractChamberBlockEntity controller;
    private List<AbstractChamberBlockEntity> inputs = List.of(), outputs = List.of();
    private boolean collisionAlongX;
    private final VacuumGeometry vacuum = new VacuumGeometry(this);

    public ChamberMultiblock(Level level) {
        super(level);
    }

    @Nullable
    public AbstractChamberBlockEntity controller() {
        return controller;
    }

    @Override
    public int getMinimumInteriorLength() {
        return 1;
    }

    @Override
    public int getMaximumInteriorLength() {
        return controller == null ? 15
            : controller.role()
                .vacuum() ? Math.max(9, lach_01298.qmd.config.VacuumConfig.MAX_SIZE.get())
                    : controller.role().kind == ChamberKind.BEAM_DUMP ? 1
                        : controller.role().kind == ChamberKind.COLLISION_CHAMBER ? 15 : 7;
    }

    public List<AbstractChamberBlockEntity> parts(ChamberPart role) {
        return getParts(AbstractChamberBlockEntity.class).stream()
            .filter(part -> part.role() == role)
            .toList();
    }

    @Override
    protected boolean isMachineWhole(IMultiblockValidator validator) {
        var controllers = getParts(AbstractChamberBlockEntity.class).stream()
            .filter(
                part -> part.role()
                    .controller())
            .toList();
        if (controllers.size() != 1) return fail("controllers", getMinimumCoord().orElse(BlockPos.ZERO));
        controller = controllers.getFirst();
        if (!super.isMachineWhole(validator)) return false;
        if (controller.role()
            .vacuum()) return vacuum.validate();
        for (var part : getParts(AbstractChamberBlockEntity.class)) if (part.role()
            .vacuum()) return fail("foreign_part", part.getBlockPos());
        if (parts(ChamberPart.ENERGY_PORT).isEmpty()) return fail("need_energy_ports", controller.getBlockPos());
        var kind = controller.role().kind;
        if ((kind == ChamberKind.DECAY_CHAMBER || kind == ChamberKind.COLLISION_CHAMBER)
            && (!parts(ChamberPart.ITEM_PORT).isEmpty() || !parts(ChamberPart.FLUID_PORT).isEmpty()))
            return fail("no_material_ports", controller.getBlockPos());
        if (kind == ChamberKind.BEAM_DUMP && (parts(ChamberPart.FLUID_PORT).stream()
            .noneMatch(AbstractChamberBlockEntity::output) || !parts(ChamberPart.ITEM_PORT).isEmpty()))
            return fail("dump_ports", controller.getBlockPos());
        return kind == ChamberKind.COLLISION_CHAMBER ? validateCollision() : validateRadial();
    }

    private boolean validateRadial() {
        if (getExteriorLengthX() != getExteriorLengthZ() || getExteriorLengthX() % 2 != 1)
            return fail("square_odd", getMiddleCoord());
        if (!hasRole(getMiddleCoord(), ChamberPart.CHAMBER)) return fail("center", getMiddleCoord());
        var ports = parts(ChamberPart.BEAM_PORT);
        if (ports.stream()
            .filter(port -> !port.output())
            .count() != 1) return fail("one_input", getMiddleCoord());
        if (controller.role().kind == ChamberKind.BEAM_DUMP && ports.size() != 1)
            return fail("dump_beam", getMiddleCoord());
        int radius = getExteriorLengthX() / 2;
        Set<BlockPos> expected = new HashSet<>();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos position = getMiddleCoord().relative(direction, radius);
            if (!hasRole(position, ChamberPart.BEAM_PORT)) continue;
            expected.add(position);
            for (int distance = 1; distance < radius; distance++)
                if (!hasRole(getMiddleCoord().relative(direction, distance), ChamberPart.BEAM))
                    return fail("beam", getMiddleCoord().relative(direction, distance));
        }
        if (expected.size() != ports.size()) return fail(
            "beam_port_position",
            ports.getFirst()
                .getBlockPos());
        return true;
    }

    private boolean validateCollision() {
        collisionAlongX = getExteriorLengthX() > getExteriorLengthZ();
        int length = collisionAlongX ? getExteriorLengthX() : getExteriorLengthZ();
        int width = collisionAlongX ? getExteriorLengthZ() : getExteriorLengthX();
        if (length != 17 || width != getExteriorLengthY() || width % 2 != 1)
            return fail("collision_size", getMiddleCoord());
        for (int offset = 1; offset < 16; offset++) {
            BlockPos pos = collisionAlongX ? new BlockPos(getMinX() + offset, getMiddleY(), getMiddleZ())
                : new BlockPos(getMiddleX(), getMiddleY(), getMinZ() + offset);
            if (!hasRole(pos, ChamberPart.CHAMBER)) return fail("collision_axis", pos);
        }
        Set<BlockPos> expected = new HashSet<>();
        BlockPos first = collisionAlongX ? new BlockPos(getMinX(), getMiddleY(), getMiddleZ())
            : new BlockPos(getMiddleX(), getMiddleY(), getMinZ());
        BlockPos second = collisionAlongX ? new BlockPos(getMaxX(), getMiddleY(), getMiddleZ())
            : new BlockPos(getMiddleX(), getMiddleY(), getMaxZ());
        for (BlockPos pos : List.of(first, second)) {
            if (!hasRole(pos, ChamberPart.BEAM_PORT)
                || ((AbstractChamberBlockEntity) getWorld().getBlockEntity(pos)).output())
                return fail("collision_input", pos);
            expected.add(pos);
        }
        for (int offset : new int[] { 2, 14 }) {
            for (boolean positive : new boolean[] { false, true }) {
                BlockPos pos = collisionAlongX
                    ? new BlockPos(getMinX() + offset, getMiddleY(), positive ? getMaxZ() : getMinZ())
                    : new BlockPos(positive ? getMaxX() : getMinX(), getMiddleY(), getMinZ() + offset);
                if (!hasRole(pos, ChamberPart.BEAM_PORT)
                    || !((AbstractChamberBlockEntity) getWorld().getBlockEntity(pos)).output())
                    return fail("collision_output", pos);
                expected.add(pos);
            }
            for (int cross = 1; cross < width - 1; cross++) {
                if (cross == width / 2) continue;
                BlockPos pos = collisionAlongX ? new BlockPos(getMinX() + offset, getMiddleY(), getMinZ() + cross)
                    : new BlockPos(getMinX() + cross, getMiddleY(), getMinZ() + offset);
                if (!hasRole(pos, ChamberPart.BEAM)) return fail("beam", pos);
            }
        }
        if (expected.size() != parts(ChamberPart.BEAM_PORT).size()) return fail("beam_port_position", getMiddleCoord());
        return true;
    }

    private boolean hasRole(BlockPos pos, ChamberPart role) {
        return getWorld().getBlockEntity(pos) instanceof AbstractChamberBlockEntity part && part.role() == role;
    }

    private boolean fail(String key, BlockPos pos) {
        setLastError(pos, "qmd.multiblock_validation.chamber." + key, pos.getX(), pos.getY(), pos.getZ());
        return false;
    }

    @Override
    protected boolean isBlockGoodForInterior(Level level, int x, int y, int z, IMultiblockValidator validator) {
        BlockPos pos = new BlockPos(x, y, z);
        return level.getBlockState(pos)
            .isAir()
            || controller != null && controller.role()
                .vacuum()
                && level.getFluidState(pos)
                    .is(
                        com.nred.nuclearcraft.registration.FluidRegistration.CUSTOM_FLUID_MAP.get("plasma").still
                            .get());
    }

    @Override
    protected void onMachineAssembled() {
        formed();
    }

    @Override
    protected void onMachineRestored() {
        formed();
    }

    private void formed() {
        if (controller == null) return;
        if (controller.role()
            .vacuum()) {
            vacuum.formed();
            refreshChannels();
            for (var part : getParts(AbstractChamberBlockEntity.class))
                getWorld().invalidateCapabilities(part.getBlockPos());
            return;
        }
        int power = lach_01298.qmd.config.ChamberConfig.POWER.get(controller.role().kind)
            .get();
        double efficiency = 1;
        for (AbstractChamberBlockEntity part : getParts(AbstractChamberBlockEntity.class)) {
            if (!part.role()
                .detector()) continue;
            var detector = lach_01298.qmd.config.ChamberConfig.DETECTORS.get(part.role());
            power = (int) Math.min(
                Integer.MAX_VALUE,
                (long) power + detector.power()
                    .get());
            BlockPos center = controller.role().kind == ChamberKind.COLLISION_CHAMBER
                ? collisionAlongX ? new BlockPos(
                    part.getBlockPos()
                        .getX(),
                    getMiddleY(),
                    getMiddleZ())
                    : new BlockPos(
                        getMiddleX(),
                        getMiddleY(),
                        part.getBlockPos()
                            .getZ())
                : getMiddleCoord();
            if (center.distManhattan(part.getBlockPos()) <= part.role().distance) efficiency += detector.efficiency()
                .get();
        }
        long volume = (long) getExteriorLengthX() * getExteriorLengthY() * getExteriorLengthZ();
        controller.formed(
            (int) Math.min(Integer.MAX_VALUE, volume * lach_01298.qmd.config.ChamberConfig.ENERGY_CAPACITY.get()),
            power,
            efficiency);
        controller.tank(0)
            .setCapacity(lach_01298.qmd.config.ChamberConfig.INPUT_CAPACITY.get());
        controller.tank(1)
            .setCapacity(
                (int) Math.min(Integer.MAX_VALUE, volume * lach_01298.qmd.config.ChamberConfig.OUTPUT_CAPACITY.get()));
        refreshChannels();
        for (AbstractChamberBlockEntity part : getParts(AbstractChamberBlockEntity.class))
            getWorld().invalidateCapabilities(part.getBlockPos());
    }

    public void refreshChannels() {
        if (controller == null) return;
        var beamPorts = parts(
            controller.role()
                .vacuum() ? ChamberPart.VACUUM_BEAM_PORT : ChamberPart.BEAM_PORT);
        inputs = beamPorts.stream()
            .filter(port -> !port.output())
            .sorted(Comparator.comparing(AbstractChamberBlockEntity::getBlockPos))
            .toList();
        outputs = beamPorts.stream()
            .filter(AbstractChamberBlockEntity::output)
            .toList();
        for (int i = 0; i < inputs.size(); i++) inputs.get(i)
            .assignChannel(i);
        for (AbstractChamberBlockEntity port : beamPorts) port.setOutward(outward(port.getBlockPos()));
        if (controller.role()
            .vacuum()) {
            for (var part : getParts(AbstractChamberBlockEntity.class))
                if (!part.role().interior) part.setOutward(outward(part.getBlockPos()));
            return;
        }
        if (controller.role().kind == ChamberKind.COLLISION_CHAMBER) {
            var sorted = new ArrayList<>(outputs);
            // Clockwise viewed from above, starting on the positive side near the first input.
            sorted.sort(Comparator.comparingInt(port -> collisionPortIndex(port.getBlockPos())));
            outputs = List.copyOf(sorted);
            for (int i = 0; i < outputs.size(); i++) outputs.get(i)
                .assignChannel(controller.outputChannel(i));
        } else if (!inputs.isEmpty()) {
            Direction inputDirection = outward(
                inputs.getFirst()
                    .getBlockPos());
            for (AbstractChamberBlockEntity output : outputs) {
                Direction direction = outward(output.getBlockPos());
                int index = direction == inputDirection.getClockWise() ? 0
                    : direction == inputDirection.getOpposite() ? 1 : 2;
                output.assignChannel(controller.outputChannel(index));
            }
        }
    }

    private int collisionPortIndex(BlockPos position) {
        if (collisionAlongX) {
            boolean near = position.getX() == getMinX() + 2, positive = position.getZ() == getMaxZ();
            return positive ? near ? 0 : 1 : near ? 3 : 2;
        }
        boolean near = position.getZ() == getMinZ() + 2, positive = position.getX() == getMaxX();
        return positive ? near ? 3 : 2 : near ? 0 : 1;
    }

    private Direction outward(BlockPos pos) {
        if (pos.getX() == getMinX()) return Direction.WEST;
        if (pos.getX() == getMaxX()) return Direction.EAST;
        if (pos.getZ() == getMinZ()) return Direction.NORTH;
        if (pos.getZ() == getMaxZ()) return Direction.SOUTH;
        return pos.getY() == getMinY() ? Direction.DOWN : Direction.UP;
    }

    public void requestValidation() {
        forceStructureUpdate(getWorld());
    }

    @Override
    protected void onMachinePaused() {
        broken();
    }

    @Override
    protected void onMachineDisassembled() {
        broken();
    }

    private void broken() {
        for (AbstractChamberBlockEntity part : getParts(AbstractChamberBlockEntity.class)) {
            part.broken();
            getWorld().invalidateCapabilities(part.getBlockPos());
        }
        inputs = List.of();
        outputs = List.of();
    }

    @Override
    protected boolean updateServer() {
        super.updateServer();
        if (controller != null) controller.processTick(inputs, outputs, getExteriorLengthX());
        return controller != null && controller.active();
    }

    @Override
    protected void updateClient() {}

    @Override
    protected void onAssimilate(IMultiblockController<ChamberMultiblock> assimilated) {}

    @Override
    protected void onAssimilated(IMultiblockController<ChamberMultiblock> assimilator) {
        super.onAssimilated(assimilator);
    }

    @Override
    public CompoundTag syncDataTo(CompoundTag tag, HolderLookup.Provider registries, SyncReason reason) {
        return tag;
    }

    @Override
    public void syncDataFrom(CompoundTag tag, HolderLookup.Provider registries, SyncReason reason) {}
}
