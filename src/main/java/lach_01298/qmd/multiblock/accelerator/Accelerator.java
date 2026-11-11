package lach_01298.qmd.multiblock.accelerator;

import com.nred.nuclearcraft.multiblock.Multiblock;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import lach_01298.qmd.block.accelerator.AbstractAcceleratorBlock;
import lach_01298.qmd.block.entity.accelerator.AbstractAcceleratorBlockEntity;
import lach_01298.qmd.capability.QMDParticleCapabilities;
import lach_01298.qmd.config.AcceleratorConfig;
import lach_01298.qmd.item.ParticleReservoirItem;
import lach_01298.qmd.multiblock.CuboidBounds;
import lach_01298.qmd.particle.*;
import lach_01298.qmd.physics.ParticlePhysics;
import lach_01298.qmd.recipe.accelerator.AbstractAcceleratorRecipe;
import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.*;

import static lach_01298.qmd.multiblock.accelerator.AcceleratorPartType.Role.*;

/**
 * ZeroCore owns attachment, merging, splitting, chunk suspension and save delegation.
 * QMD validates the five-block-thick beam envelope, including a toroid's inner wall.
 */
public final class Accelerator extends Multiblock<Accelerator> {

    public final AcceleratorEnergy energy = new AcceleratorEnergy();
    public final FluidTank coolantInput = new FluidTank(1000), coolantOutput = new FluidTank(320000);
    public final FluidTank[] processFluids = { new FluidTank(16000), new FluidTank(16000), new FluidTank(16000),
        new FluidTank(16000), new FluidTank(16000) };
    public final ParticleStorageAccelerator[] beams = { new ParticleStorageAccelerator(),
        new ParticleStorageAccelerator(), new ParticleStorageAccelerator() };
    public AcceleratorKind kind = AcceleratorKind.LINEAR;
    public AbstractAcceleratorBlockEntity controller;
    public long heat, heatCapacity = 25000, requiredEnergy, rawHeating, cooling, currentHeating, voltage;
    public double quadrupoleStrength, dipoleStrength, efficiency = 1, maxTemperature = 400, work, recipeWork;
    public int cavities, quadrupoles, dipoles, percentage = -1, error;
    public boolean operating, initialized;
    private CuboidBounds bounds;
    private Direction.Axis axis;
    private Map<BlockPos, AbstractAcceleratorBlockEntity> parts = Map.of();
    private Set<BlockPos> path = Set.of(), components = Set.of();
    private double coolingRemainder;
    private String validationMessage = "Unassembled";

    public Accelerator(Level level) {
        super(level);
    }

    @Override
    public int getMinimumInteriorLength() {
        return 3;
    }

    @Override
    public int getMaximumInteriorLength() {
        return Math.max(AcceleratorConfig.LINEAR_MAX.get(), AcceleratorConfig.RING_MAX.get());
    }

    @Override
    protected boolean isBlockGoodForInterior(Level level, int x, int y, int z, IMultiblockValidator validator) {
        BlockPos pos = new BlockPos(x, y, z);
        return level.getBlockState(pos)
            .isAir() || level.getBlockEntity(pos) instanceof AbstractAcceleratorBlockEntity;
    }

    @Override
    protected int getMinimumNumberOfPartsForAssembledMachine() {
        return 50;
    }

    public String validationMessage() {
        return validationMessage;
    }

    private boolean fail(String message, BlockPos pos) {
        validationMessage = message + (pos == null ? "" : " at " + pos.toShortString());
        setLastError(
            "qmd.multiblock.accelerator.invalid",
            pos == null ? List.of() : List.of(pos.asLong()),
            validationMessage);
        return false;
    }

    public int temperature() {
        return (int) Math.clamp(Math.round(400D * heat / Math.max(1, heatCapacity)), 0, 400);
    }

    public List<AbstractAcceleratorBlockEntity> parts(AcceleratorPartType.Role role) {
        return parts.values()
            .stream()
            .filter(p -> p.partType().role == role)
            .toList();
    }

    @Override
    protected boolean isMachineWhole(IMultiblockValidator validator) {
        var found = new HashMap<BlockPos, AbstractAcceleratorBlockEntity>();
        for (var part : getParts(AbstractAcceleratorBlockEntity.class)) found.put(part.getBlockPos(), part);
        parts = Map.copyOf(found);
        var controllers = parts(CONTROLLER);
        if (controllers.size() != 1) return fail("Exactly one accelerator controller is required", null);
        controller = controllers.getFirst();
        kind = controller.partType().kind;
        bounds = new CuboidBounds(getMinimumCoord().orElseThrow(), getMaximumCoord().orElseThrow());
        if (!bounds.chunksLoaded(getWorld()))
            return fail("All accelerator chunks must be loaded", controller.getBlockPos());
        int x = bounds.sizeX(), y = bounds.sizeY(), z = bounds.sizeZ();
        if (kind == AcceleratorKind.SPECTROMETER) {
            axis = x != 7 ? Direction.Axis.X
                : z != 7 ? Direction.Axis.Z
                    : parts(SOURCE).isEmpty() || parts(SOURCE).getFirst()
                        .facing()
                        .getAxis() == Direction.Axis.Z ? Direction.Axis.X : Direction.Axis.Z;
            int width = axis == Direction.Axis.X ? z : x, length = axis == Direction.Axis.X ? x : z;
            if (y != 7 || width != 7 || length < 5 || length > AcceleratorConfig.LINEAR_MAX.get() || length % 2 != 1)
                return fail(
                    "Mass spectrometers require height 7, width 7 and odd length 5 or greater",
                    controller.getBlockPos());
        } else if (kind == AcceleratorKind.RING || kind == AcceleratorKind.DECELERATOR) {
            axis = Direction.Axis.X;
            if (y != 5 || x != z || x < AcceleratorConfig.RING_MIN.get() || x > AcceleratorConfig.RING_MAX.get())
                return fail(
                    "Ring accelerators require a square five-block-high toroid within configured size limits",
                    controller.getBlockPos());
        } else if (kind == AcceleratorKind.LINEAR) {
            axis = x > z ? Direction.Axis.X : Direction.Axis.Z;
            int width = axis == Direction.Axis.X ? z : x, length = axis == Direction.Axis.X ? x : z;
            if (y != 5 || width != 5
                || length < AcceleratorConfig.LINEAR_MIN.get()
                || length > AcceleratorConfig.LINEAR_MAX.get())
                return fail(
                    "Linear accelerators require height 5, width 5 and configured length",
                    controller.getBlockPos());
        } else {
            axis = Direction.Axis.X;
            if (x != 5 || y != 5 || z != 5)
                return fail("Beam routing machines require a 5 × 5 × 5 envelope", controller.getBlockPos());
        }
        for (BlockPos mutable : bounds.positions()) {
            BlockPos pos = mutable.immutable();
            boolean shell = shell(pos);
            var part = parts.get(pos);
            if (shell && (part == null || part.partType()
                .isInterior())) return fail("Missing accelerator wall", pos);
            if (shell && bounds.isEdge(pos) && part.partType().role != CASING)
                return fail("Frame blocks must be accelerator casing", pos);
            if (!shell && footprint(pos)
                && !getWorld().getBlockState(pos)
                    .isAir()
                && (part == null || !part.partType()
                    .isInterior()))
                return fail("Invalid interior block", pos);
            if (part != null && !footprint(pos)) return fail("Accelerator part occupies the toroid hole", pos);
        }
        if (parts(ENERGY_PORT).isEmpty() || parts(COOLANT_INLET).isEmpty() || parts(COOLANT_OUTLET).isEmpty())
            return fail("An energy port, coolant inlet and coolant outlet are required", controller.getBlockPos());
        if (kind == AcceleratorKind.SPECTROMETER) return validateSpectrometer();
        Set<BlockPos> beamPath = new HashSet<>();
        int cy = bounds.min()
            .getY() + 2,
            cx = bounds.min()
                .getX() + 2,
            cz = bounds.min()
                .getZ() + 2;
        if (kind == AcceleratorKind.LINEAR) {
            for (int i = (axis == Direction.Axis.X ? bounds.min()
                .getX()
                : bounds.min()
                    .getZ())
                + 1; i
                    < (axis == Direction.Axis.X ? bounds.max()
                        .getX()
                        : bounds.max()
                            .getZ()); i++)
                beamPath.add(axis == Direction.Axis.X ? new BlockPos(i, cy, cz) : new BlockPos(cx, cy, i));
        } else if (kind == AcceleratorKind.RING || kind == AcceleratorKind.DECELERATOR) {
            for (int i = bounds.min()
                .getX() + 2; i <= bounds.max()
                    .getX() - 2; i++) {
                beamPath.add(
                    new BlockPos(
                        i,
                        cy,
                        bounds.min()
                            .getZ() + 2));
                beamPath.add(
                    new BlockPos(
                        i,
                        cy,
                        bounds.max()
                            .getZ() - 2));
                beamPath.add(
                    new BlockPos(
                        bounds.min()
                            .getX() + 2,
                        cy,
                        bounds.min()
                            .getZ() + i
                            - bounds.min()
                                .getX()));
                beamPath.add(
                    new BlockPos(
                        bounds.max()
                            .getX() - 2,
                        cy,
                        bounds.min()
                            .getZ() + i
                            - bounds.min()
                                .getX()));
            }
        } else {
            // A routing dipole has four horizontal beam/yoke sockets in its 3³ interior.
            BlockPos center = new BlockPos(cx, cy, cz);
            beamPath.add(center);
            for (var port : parts(BEAM_PORT)) {
                Direction inward = outward(port.getBlockPos());
                if (inward == null) return fail("Beam port must occupy a wall", port.getBlockPos());
                beamPath.add(
                    port.getBlockPos()
                        .relative(inward.getOpposite()));
            }
        }
        for (BlockPos pos : beamPath)
            if (!isRole(pos, BEAM)) return fail("Continuous accelerator beam is required", pos);
        path = Set.copyOf(beamPath);
        var sources = parts(SOURCE);
        var ports = parts(BEAM_PORT);
        int inputs = 0, outputs = 0;
        if (!parts(COLLECTOR).isEmpty()) return fail(
            "Ion collectors belong to a mass spectrometer",
            parts(COLLECTOR).getFirst()
                .getBlockPos());
        for (var port : parts(SYNCHROTRON_PORT)) {
            Direction out = outward(port.getBlockPos());
            if (kind != AcceleratorKind.RING || out == null
                || out.getAxis() == Direction.Axis.Y
                || port.getBlockPos()
                    .getY() != cy
                || !path.contains(
                    port.getBlockPos()
                        .relative(out.getOpposite(), 2)))
                return fail("Synchrotron ports must connect to a ring beam centre", port.getBlockPos());
        }
        for (var port : ports) {
            Direction out = outward(port.getBlockPos());
            if (out == null || out.getAxis() == Direction.Axis.Y
                || port.getBlockPos()
                    .getY() != cy
                || !path.contains(
                    port.getBlockPos()
                        .relative(out.getOpposite(), 2)))
                return fail("Beam port must connect to the beam centre", port.getBlockPos());
            if (port.output()) outputs++;
            else inputs++;
        }
        for (var source : sources) {
            Direction out = outward(source.getBlockPos());
            if (kind != AcceleratorKind.LINEAR || out == null
                || source.facing() != out.getOpposite()
                || !path.contains(
                    source.getBlockPos()
                        .relative(out.getOpposite())))
                return fail("Ion source must face inward at the linear beam end", source.getBlockPos());
        }
        int expectedOutputs = kind == AcceleratorKind.SPLITTER ? 2 : 1;
        if (sources.size() + inputs != 1 || outputs != expectedOutputs || (!sources.isEmpty() && ports.size() != 1))
            return fail(
                "Exactly one source/input and the required output ports must connect",
                controller.getBlockPos());
        Set<BlockPos> valid = new HashSet<>();
        cavities = quadrupoles = dipoles = 0;
        voltage = 0;
        quadrupoleStrength = dipoleStrength = 0;
        requiredEnergy = rawHeating = 0;
        maxTemperature = 400;
        double totalEfficiency = 0;
        int assemblies = 0;
        for (BlockPos pos : path) {
            var above = parts.get(pos.above());
            if (above == null || above.partType().material == null) continue;
            var material = above.partType().material;
            Direction.Axis beamAxis = beamAxis(pos);
            Set<BlockPos> assembled = dipole(pos, Direction.Axis.Y);
            if (assembled.isEmpty()) assembled = dipole(pos, Direction.Axis.X);
            if (assembled.isEmpty()) assembled = dipole(pos, Direction.Axis.Z);
            boolean magnet = false;
            int assemblyKind;
            if (!assembled.isEmpty()) {
                assemblyKind = 0;
                magnet = true;
            } else if (beamAxis != null && above.partType().role == CAVITY
                && !(assembled = plane(pos, beamAxis, CAVITY, material, false)).isEmpty()) {
                    Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, beamAxis);
                    if (isRole(
                        pos.above()
                            .relative(direction),
                        CAVITY)
                        || isRole(
                            pos.above()
                                .relative(direction.getOpposite()),
                            CAVITY))
                        continue;
                    assemblyKind = 1;
                } else if (beamAxis != null && above.partType().role == MAGNET
                    && !(assembled = plane(pos, beamAxis, MAGNET, material, true)).isEmpty()) {
                        assemblyKind = 2;
                        magnet = true;
                    } else continue;
            if (!Collections.disjoint(valid, assembled)) continue;
            var settings = material.settings();
            if (assemblyKind == 0) {
                dipoles++;
                dipoleStrength += settings.strength()
                    .get();
            } else if (assemblyKind == 1) {
                cavities++;
                voltage = ParticlePhysics.saturatedAdd(
                    voltage,
                    settings.voltage()
                        .get());
            } else {
                quadrupoles++;
                quadrupoleStrength += settings.strength()
                    .get();
            }
            valid.addAll(assembled);
            assemblies++;
            totalEfficiency += (magnet ? settings.magnetEfficiency() : settings.cavityEfficiency()).get();
            rawHeating += (magnet ? settings.magnetHeat() : settings.cavityHeat()).get();
            requiredEnergy += (magnet ? settings.magnetPower() : settings.cavityPower()).get();
            maxTemperature = Math
                .min(maxTemperature, (magnet ? settings.magnetTemperature() : settings.cavityTemperature()).get());
        }
        for (var source : sources) requiredEnergy += sourceSettings(source).power()
            .get();
        efficiency = assemblies == 0 ? 1 : totalEfficiency / assemblies;
        requiredEnergy = (long) (requiredEnergy / efficiency);
        for (var part : parts.values())
            if ((part.partType().role == CAVITY || part.partType().role == MAGNET || part.partType().role == YOKE)
                && !valid.contains(part.getBlockPos()))
                return fail("Incomplete or overlapping accelerator component", part.getBlockPos());
        if (kind == AcceleratorKind.LINEAR && cavities == 0)
            return fail("A complete RF cavity is required", controller.getBlockPos());
        if ((kind == AcceleratorKind.RING || kind == AcceleratorKind.DECELERATOR) && dipoles < 4)
            return fail("Each toroid corner needs a complete dipole", controller.getBlockPos());
        if (kind == AcceleratorKind.RING || kind == AcceleratorKind.DECELERATOR)
            for (int cornerX : new int[] { getMinX() + 2, getMaxX() - 2 })
                for (int cornerZ : new int[] { getMinZ() + 2, getMaxZ() - 2 }) {
                    BlockPos corner = new BlockPos(cornerX, cy, cornerZ);
                    if (dipole(corner, Direction.Axis.Y).isEmpty())
                        return fail("Each toroid corner needs a complete vertical dipole", corner);
                }
        if ((kind == AcceleratorKind.DIVERTER || kind == AcceleratorKind.SPLITTER) && dipoles != 1)
            return fail("A complete central dipole is required", controller.getBlockPos());
        components = Set.copyOf(valid);
        cooling = AcceleratorCoolers.active(parts, components)
            .stream()
            .map(parts::get)
            .mapToLong(p -> AcceleratorCoolers.cooling(p.partType().variant))
            .sum();
        if (kind == AcceleratorKind.DIVERTER || kind == AcceleratorKind.SPLITTER)
            cooling = 2 * (rawHeating + maximumExternalHeating());
        validationMessage = "Assembled";
        return true;
    }

    private boolean validateSpectrometer() {
        if (!parts(BEAM_PORT).isEmpty() || !parts(SYNCHROTRON_PORT).isEmpty() || !parts(CAVITY).isEmpty())
            return fail("Mass spectrometers cannot contain RF cavities or beam ports", controller.getBlockPos());
        Set<Integer> sourceLayers = new HashSet<>();
        int sourceMultiplier = 0;
        for (var source : parts(SOURCE)) {
            BlockPos pos = source.getBlockPos();
            Direction out = outward(pos);
            if (!AcceleratorConfig.SPECTROMETER_SOURCES.get()
                .contains(source.partType().variant == 0 ? "basic" : "laser"))
                return fail("Ion source is disabled for spectrometers by configuration", pos);
            int layer = (axis == Direction.Axis.X ? pos.getX() - getMinInteriorX() : pos.getZ() - getMinInteriorZ());
            if (out == null || out.getAxis() == axis
                || out.getAxis() == Direction.Axis.Y
                || source.facing() != out.getOpposite()
                || pos.getY() != getMinInteriorY()
                || layer % 2 != 1
                || !sourceLayers.add(layer)) return fail("Invalid spectrometer ion-source layer or facing", pos);
            for (int n = 1; n <= 4; n++) if (!isRole(pos.above(n), COLLECTOR) || parts.get(pos.above(n))
                .facing() != out.getOpposite())
                return fail("Four inward-facing collectors must be stacked over each ion source", pos.above(n));
            sourceMultiplier += sourceSettings(source).multiplier()
                .get();
        }
        int expectedSources = ((axis == Direction.Axis.X ? bounds.sizeX() : bounds.sizeZ()) - 2) / 2;
        if (sourceLayers.size() != expectedSources || parts(COLLECTOR).size() != sourceLayers.size() * 4) return fail(
            "Every spectrometer separation layer requires an ion source and four collectors",
            controller.getBlockPos());
        Set<BlockPos> valid = new HashSet<>();
        Map<Integer, AcceleratorMaterial> layerMaterials = new HashMap<>();
        int magnets = 0;
        double sumEfficiency = 0;
        maxTemperature = 400;
        for (BlockPos mutable : bounds.interior()
            .positions()) {
            BlockPos pos = mutable.immutable();
            int layer = axis == Direction.Axis.X ? pos.getX() - getMinInteriorX() : pos.getZ() - getMinInteriorZ();
            if (layer % 2 == 1) {
                if (!isRole(pos, BEAM)) return fail("Spectrometer separation layers must be beam blocks", pos);
            } else {
                int cross = axis == Direction.Axis.X ? pos.getZ() - getMinInteriorZ() : pos.getX() - getMinInteriorX();
                boolean rim = pos.getY() == getMinInteriorY() || pos.getY() == getMaxInteriorY()
                    || cross == 0
                    || cross == 4;
                if (!isRole(pos, rim ? MAGNET : YOKE))
                    return fail("Spectrometer magnet layers require a magnet rim around a yoke core", pos);
                var part = parts.get(pos);
                valid.add(pos);
                if (rim) {
                    var m = part.partType().material;
                    if (!AcceleratorConfig.SPECTROMETER_MAGNETS.get()
                        .contains(m.id))
                        return fail("Magnet material is disabled for spectrometers by configuration", pos);
                    if (layerMaterials.putIfAbsent(layer, m) != null && layerMaterials.get(layer) != m)
                        return fail("All magnets within one spectrometer layer must use the same material", pos);
                    magnets++;
                    sumEfficiency += m.settings()
                        .magnetEfficiency()
                        .get();
                    maxTemperature = Math.min(
                        maxTemperature,
                        m.settings()
                            .magnetTemperature()
                            .get());
                }
            }
        }
        components = Set.copyOf(valid);
        efficiency = magnets == 0 ? 1 : sumEfficiency / magnets;
        requiredEnergy = (long) ((magnets * (long) (AcceleratorConfig.SPECTROMETER_POWER.get() / 16)
            + parts(SOURCE).stream()
                .mapToLong(
                    p -> sourceSettings(p).power()
                        .get())
                .sum())
            / efficiency);
        rawHeating = magnets * (long) (AcceleratorConfig.SPECTROMETER_HEAT.get() / 16);
        cooling = 2 * (rawHeating + maximumExternalHeating());
        validationMessage = "Assembled";
        return true;
    }

    private boolean footprint(BlockPos pos) {
        if (kind != AcceleratorKind.RING && kind != AcceleratorKind.DECELERATOR) return bounds.contains(pos);
        return pos.getX() <= getMinX() + 4 || pos.getX() >= getMaxX() - 4
            || pos.getZ() <= getMinZ() + 4
            || pos.getZ() >= getMaxZ() - 4;
    }

    private boolean shell(BlockPos pos) {
        if (!footprint(pos)) return false;
        if (bounds.isShell(pos)) return true;
        if (kind != AcceleratorKind.RING && kind != AcceleratorKind.DECELERATOR) return false;
        return pos.getX() == getMinX() + 4 && pos.getZ() >= getMinZ() + 4 && pos.getZ() <= getMaxZ() - 4
            || pos.getX() == getMaxX() - 4 && pos.getZ() >= getMinZ() + 4 && pos.getZ() <= getMaxZ() - 4
            || pos.getZ() == getMinZ() + 4 && pos.getX() >= getMinX() + 4 && pos.getX() <= getMaxX() - 4
            || pos.getZ() == getMaxZ() - 4 && pos.getX() >= getMinX() + 4 && pos.getX() <= getMaxX() - 4;
    }

    public Direction outward(BlockPos pos) {
        for (Direction d : Direction.values())
            if (!footprint(pos.relative(d)) || !bounds.contains(pos.relative(d))) return d;
        return null;
    }

    private boolean isRole(BlockPos pos, AcceleratorPartType.Role role) {
        var part = parts.get(pos);
        return part != null && part.partType().role == role;
    }

    private Direction.Axis beamAxis(BlockPos pos) {
        boolean x = path.contains(pos.east()) || path.contains(pos.west()),
            z = path.contains(pos.north()) || path.contains(pos.south());
        return x == z ? null : x ? Direction.Axis.X : Direction.Axis.Z;
    }

    private Set<BlockPos> plane(BlockPos center, Direction.Axis normal, AcceleratorPartType.Role role,
        AcceleratorMaterial material, boolean plus) {
        var positions = new HashSet<BlockPos>();
        for (int u = -1; u <= 1; u++) for (int v = -1; v <= 1; v++) {
            if (u == 0 && v == 0 || plus && u != 0 && v != 0) continue;
            BlockPos pos = normal == Direction.Axis.X ? center.offset(0, u, v) : center.offset(u, v, 0);
            var part = parts.get(pos);
            if (part == null || part.partType().role != role || part.partType().material != material) return Set.of();
            positions.add(pos);
        }
        return positions;
    }

    private Set<BlockPos> dipole(BlockPos center, Direction.Axis normal) {
        Direction d = Direction.get(Direction.AxisDirection.POSITIVE, normal);
        var first = parts.get(center.relative(d));
        if (first == null || first.partType().role != MAGNET) return Set.of();
        var other = parts.get(center.relative(d.getOpposite()));
        if (other == null || other.partType() != first.partType()) return Set.of();
        Set<BlockPos> positions = new HashSet<>();
        for (BlockPos mutable : BlockPos.betweenClosed(center.offset(-1, -1, -1), center.offset(1, 1, 1))) {
            BlockPos pos = mutable.immutable();
            if (pos.equals(center)) continue;
            int dx = Math.abs(pos.getX() - center.getX()), dy = Math.abs(pos.getY() - center.getY()),
                dz = Math.abs(pos.getZ() - center.getZ());
            if (dx + dy + dz == 1) {
                if (pos.equals(center.relative(d)) || pos.equals(center.relative(d.getOpposite()))) {
                    positions.add(pos);
                    continue;
                }
                if (isRole(pos, BEAM)) continue;
            }
            if (!isRole(pos, YOKE)) return Set.of();
            positions.add(pos);
        }
        return positions;
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
        long volume = interiorVolume();
        energy.capacity = volume * AcceleratorConfig.ENERGY_CAPACITY.get();
        energy.stored = Math.min(energy.stored, energy.capacity);
        heatCapacity = volume * AcceleratorConfig.HEAT_CAPACITY.get();
        coolantInput.setCapacity((int) Math.min(Integer.MAX_VALUE, volume * AcceleratorConfig.INPUT_CAPACITY.get()));
        coolantOutput.setCapacity((int) Math.min(Integer.MAX_VALUE, volume * AcceleratorConfig.OUTPUT_CAPACITY.get()));
        int processCapacity = (int) Math.min(Integer.MAX_VALUE, 1000L * AcceleratorConfig.INPUT_CAPACITY.get());
        for (var fluid : processFluids) fluid.setCapacity(processCapacity);
        for (var source : parts(SOURCE)) source.sourceTank.setCapacity(processCapacity);
        if (!initialized) {
            heat = (long) (heatCapacity * 293D / 400);
            initialized = true;
        }
        parts.values()
            .forEach(p -> p.invalidateCapabilities());
        markReferenceCoordForUpdate();
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
        operating = false;
        for (var beam : beams) beam.setParticleStack(null);
        parts.values()
            .forEach(p -> {
                p.inputBeam.setParticleStack(null);
                p.outputBeam.setParticleStack(null);
                p.setRedstoneLevel(0);
                p.invalidateCapabilities();
            });
    }

    @Override
    protected void onAssimilate(IMultiblockController<Accelerator> other) {
        if (other instanceof Accelerator acc) {
            energy.stored = ParticlePhysics.saturatedAdd(energy.stored, acc.energy.stored);
            heat = ParticlePhysics.saturatedAdd(heat, acc.heat);
        }
    }

    @Override
    protected void updateClient() {}

    public long interiorVolume() {
        long x = Math.max(1, bounds.sizeX() - 2), y = Math.max(1, bounds.sizeY() - 2),
            z = Math.max(1, bounds.sizeZ() - 2);
        return x * y * z;
    }

    public long surfaceArea() {
        long x = bounds.sizeX(), y = bounds.sizeY(), z = bounds.sizeZ();
        return 2 * (x * y + x * z + y * z);
    }

    private long maximumExternalHeating() {
        return (long) (293 * AcceleratorConfig.CONDUCTIVITY.get() * surfaceArea());
    }

    @Override
    protected boolean updateServer() {
        if (!isAssembled() || bounds == null || !bounds.chunksLoaded(getWorld()) || controller == null) {
            operating = false;
            for (var beam : beams) beam.setParticleStack(null);
            parts.values()
                .forEach(p -> p.outputBeam.setParticleStack(null));
            return false;
        }
        super.updateServer();
        error = 0;
        currentHeating = 0;
        boolean wasOperating = operating;
        operating = false;
        for (var beam : beams) beam.setParticleStack(null);
        for (var part : parts.values()) part.outputBeam.setParticleStack(null);
        long external = (long) ((293 - temperature()) * AcceleratorConfig.CONDUCTIVITY.get() * surfaceArea());
        heat = Math.clamp(heat + external, 0, heatCapacity);
        currentHeating = external;
        cool();
        int signal = getWorld().getBestNeighborSignal(controller.getBlockPos());
        for (var port : parts(REDSTONE_PORT))
            if (!port.output()) signal = Math.max(signal, getWorld().getBestNeighborSignal(port.getBlockPos()));
        double control = percentage >= 0 ? percentage / 100D : signal / 15D;
        if (control > 0) {
            if (temperature() > maxTemperature) {
                error = 1;
                if (wasOperating && AcceleratorConfig.EXPLOSIONS.get()) quench();
            } else kind.logic.operate(this, control);
        } else {
            work = 0;
            parts.values()
                .forEach(p -> p.inputBeam.setParticleStack(null));
        }
        if (controller.getBlockState()
            .getValue(AbstractAcceleratorBlock.ACTIVE) != operating)
            getWorld().setBlock(
                controller.getBlockPos(),
                controller.getBlockState()
                    .setValue(AbstractAcceleratorBlock.ACTIVE, operating),
                Block.UPDATE_CLIENTS);
        for (var port : parts(REDSTONE_PORT))
            port.setRedstoneLevel(port.output() ? (int) (15D * temperature() / Math.max(1, maxTemperature)) : 0);
        if (getWorld().getGameTime() % 20 == 0) markReferenceCoordForUpdate();
        return true;
    }

    private void quench() {
        var hot = components.stream()
            .map(parts::get)
            .filter(Objects::nonNull)
            .filter(
                p -> p.partType().material != null && (p.partType().role == MAGNET ? p.partType().material.settings()
                    .magnetTemperature()
                    : p.partType().material.settings()
                        .cavityTemperature()).get()
                    < temperature())
            .toList();
        if (!hot.isEmpty()) {
            var pos = hot.get(getWorld().random.nextInt(hot.size()))
                .getBlockPos();
            getWorld()
                .explode(null, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, 2F, Level.ExplosionInteraction.BLOCK);
        }
    }

    <R extends AbstractAcceleratorRecipe> List<R> recipes(net.minecraft.world.item.crafting.RecipeType<R> type) {
        return getWorld().getRecipeManager()
            .getAllRecipesFor(type)
            .stream()
            .map(holder -> holder.value())
            .toList();
    }

    static AcceleratorConfig.SourceSettings sourceSettings(AbstractAcceleratorBlockEntity source) {
        return AcceleratorConfig.SOURCES[source.partType().variant];
    }

    private void cool() {
        for (var recipe : recipes(QMDRecipes.ACCELERATOR_COOLING_TYPE.get())) {
            if (recipe.heat() <= 0 || recipe.fluids()
                .isEmpty()
                || !recipe
                    .matches(new AbstractAcceleratorRecipe.Input(ItemStack.EMPTY, coolantInput.getFluid()), getWorld())
                || temperature() <= recipe.temperature()) continue;
            var input = recipe.fluid();
            FluidStack output = recipe.fluids()
                .getFirst();
            if (input.isEmpty() || output.isEmpty()) continue;
            long thermalFloor = (long) (recipe.temperature() / 400D * heatCapacity);
            double possible = Math
                .min(cooling / (double) recipe.heat(), (heat - thermalFloor) / (double) recipe.heat());
            possible = Math.min(possible, coolantInput.getFluidAmount() / (double) input.amount());
            int outputRoom = coolantOutput.fill(output.copyWithAmount(Integer.MAX_VALUE), FluidAction.SIMULATE);
            possible = Math.min(possible, outputRoom / (double) output.getAmount());
            coolingRemainder += Math.max(0, possible);
            int count = (int) Math.min(Integer.MAX_VALUE, Math.floor(coolingRemainder));
            count = Math
                .min(count, Math.min(coolantInput.getFluidAmount() / input.amount(), outputRoom / output.getAmount()));
            count = (int) Math.min(count, Math.max(0, heat - thermalFloor) / recipe.heat());
            if (count > 0) {
                coolantInput.drain(count * input.amount(), FluidAction.EXECUTE);
                coolantOutput.fill(output.copyWithAmount(count * output.getAmount()), FluidAction.EXECUTE);
                heat -= (long) count * recipe.heat();
                coolingRemainder -= count;
            }
            return;
        }
    }

    void operateBeam(double control, AbstractAcceleratorLogic logic) {
        ParticleStack input = null;
        AbstractAcceleratorBlockEntity inputPort = null, source = null;
        lach_01298.qmd.recipe.accelerator.AcceleratorSourceRecipe sourceRecipe = null;
        if (!parts(SOURCE).isEmpty()) {
            source = parts(SOURCE).getFirst();
            for (var recipe : recipes(QMDRecipes.ACCELERATOR_SOURCE_TYPE.get())) if (recipe.matches(
                new AbstractAcceleratorRecipe.Input(source.inventory.getStackInSlot(0), source.sourceTank.getFluid()),
                getWorld())
                && (recipe.fluid()
                    .isEmpty()
                    || source.sourceTank.getFluidAmount() >= (long) recipe.fluid()
                        .amount() * sourceSettings(source).multiplier()
                            .get())
                && (recipe.item()
                    .isEmpty()
                    || source.inventory.getStackInSlot(0)
                        .getItem() instanceof ParticleReservoirItem
                    || source.inventory.getStackInSlot(0)
                        .getCount()
                        >= (long) recipe.itemCount() * sourceSettings(source).multiplier()
                            .get())) {
                                sourceRecipe = recipe;
                                input = recipe.particle()
                                    .toStack();
                                break;
                            }
            if (input != null) {
                int count = ParticlePhysics.saturatedCount(
                    (long) input.getAmount() * sourceSettings(source).multiplier()
                        .get());
                ItemStack stored = source.inventory.getStackInSlot(0);
                if (stored.getItem() instanceof ParticleReservoirItem && stored.getCount() != 1) return;
                if (stored.getItem() instanceof ParticleReservoirItem)
                    count = Math.min(count, ParticleReservoirItem.stored(stored));
                input.setAmount(count);
                input.addFocus(
                    sourceSettings(source).focus()
                        .get());
            }
        } else for (var port : parts(BEAM_PORT)) if (!port.output()) {
            inputPort = port;
            input = port.consumeInputBeam();
            if (input == null) {
                Direction out = outward(port.getBlockPos());
                BlockPos neighbor = port.getBlockPos()
                    .relative(out);
                if (getWorld().hasChunkAt(neighbor)) {
                    var handler = getWorld().getCapability(QMDParticleCapabilities.BLOCK, neighbor, out.getOpposite());
                    if (handler != null) input = handler.extractParticle(out.getOpposite());
                }
            }
            break;
        }
        if (input == null || input.isEmpty()) return;
        beams[0].setParticleStack(input);
        long max = logic.maximumEnergy(this, input);
        if (!logic.acceptsInput(this, input, inputPort, max)) return;
        if (!energy.use(requiredEnergy)) {
            error = 2;
            return;
        }
        heat = Math.min(heatCapacity, ParticlePhysics.saturatedAdd(heat, rawHeating));
        currentHeating += rawHeating;
        operating = true;
        if (source != null && sourceRecipe != null) consumeSource(source, sourceRecipe, input.getAmount());
        ParticleStack output = logic.transform(this, input, control, max);
        if (output.getFocus() <= 0) {
            error = 3;
            return;
        }
        logic.publish(this, input, output, inputPort);
    }

    private void consumeSource(AbstractAcceleratorBlockEntity source,
        lach_01298.qmd.recipe.accelerator.AcceleratorSourceRecipe recipe, int count) {
        ItemStack stack = source.inventory.getStackInSlot(0);
        if (stack.getItem() instanceof ParticleReservoirItem item) {
            source.inventory.setStackInSlot(0, item.consume(stack, count));
            if (ParticleReservoirItem.stored(source.inventory.getStackInSlot(0)) == 0
                && !source.inventory.getStackInSlot(1)
                    .isEmpty()) {
                ItemStack exhausted = source.inventory.getStackInSlot(0);
                source.inventory.setStackInSlot(0, source.inventory.getStackInSlot(1));
                source.inventory.setStackInSlot(1, exhausted);
            }
        } else if (!recipe.item()
            .isEmpty())
            source.inventory.extractItem(
                0,
                recipe.itemCount() * sourceSettings(source).multiplier()
                    .get(),
                false);
        if (!recipe.fluid()
            .isEmpty())
            source.sourceTank.drain(
                recipe.fluid()
                    .amount()
                    * sourceSettings(source).multiplier()
                        .get(),
                FluidAction.EXECUTE);
    }

    void emit(AbstractAcceleratorBlockEntity port, ParticleStack stack) {
        port.outputBeam.setParticleStack(stack);
        Direction out = outward(port.getBlockPos());
        if (out == null) return;
        BlockPos neighbor = port.getBlockPos()
            .relative(out);
        if (!getWorld().hasChunkAt(neighbor)) return;
        var handler = getWorld().getCapability(QMDParticleCapabilities.BLOCK, neighbor, out.getOpposite());
        if (handler != null) handler.receiveParticle(out.getOpposite(), stack.copy());
    }

    public double radius() {
        return kind.logic.radius(this);
    }

    public int beamLength() {
        return kind.logic.beamLength(this);
    }

    @Override
    public CompoundTag syncDataTo(CompoundTag tag, HolderLookup.Provider registries, SyncReason reason) {
        tag.putLong("energy", energy.stored);
        tag.putLong("energyCapacity", energy.capacity);
        tag.putLong("heat", heat);
        tag.putLong("heatCapacity", heatCapacity);
        tag.putBoolean("initialized", initialized);
        tag.putInt("percentage", percentage);
        tag.putBoolean("operating", operating);
        tag.putInt("error", error);
        tag.putLong("requiredEnergy", requiredEnergy);
        tag.putLong("rawHeating", rawHeating);
        tag.putLong("cooling", cooling);
        tag.putLong("voltage", voltage);
        tag.putInt("cavities", cavities);
        tag.putInt("quadrupoles", quadrupoles);
        tag.putInt("dipoles", dipoles);
        tag.putDouble("quadrupoleStrength", quadrupoleStrength);
        tag.putDouble("dipoleStrength", dipoleStrength);
        tag.putDouble("efficiency", efficiency);
        tag.putDouble("maxTemperature", maxTemperature);
        tag.putDouble("work", work);
        tag.putDouble("recipeWork", recipeWork);
        tag.putDouble("coolingRemainder", coolingRemainder);
        tag.put("coolantInput", coolantInput.writeToNBT(registries, new CompoundTag()));
        tag.put("coolantOutput", coolantOutput.writeToNBT(registries, new CompoundTag()));
        tag.putInt("coolantInputCapacity", coolantInput.getCapacity());
        tag.putInt("coolantOutputCapacity", coolantOutput.getCapacity());
        for (int n = 0; n < processFluids.length; n++)
            tag.putInt("processFluid" + n + "Capacity", processFluids[n].getCapacity());
        for (int n = 0; n < processFluids.length; n++)
            tag.put("processFluid" + n, processFluids[n].writeToNBT(registries, new CompoundTag()));
        for (int n = 0; n < beams.length; n++) beams[n].writeToNBT(tag, n);
        return tag;
    }

    @Override
    public void syncDataFrom(CompoundTag tag, HolderLookup.Provider registries, SyncReason reason) {
        energy.capacity = Math.max(1, tag.getLong("energyCapacity"));
        energy.stored = Math.clamp(tag.getLong("energy"), 0, energy.capacity);
        heatCapacity = Math.max(1, tag.getLong("heatCapacity"));
        heat = Math.clamp(tag.getLong("heat"), 0, heatCapacity);
        initialized = tag.getBoolean("initialized");
        percentage = tag.contains("percentage") ? Math.clamp(tag.getInt("percentage"), -1, 100) : -1;
        operating = tag.getBoolean("operating");
        error = tag.getInt("error");
        work = Math.max(0, tag.getDouble("work"));
        recipeWork = Math.max(1, tag.getDouble("recipeWork"));
        coolingRemainder = Math.clamp(tag.getDouble("coolingRemainder"), 0, 1);
        coolantInput.readFromNBT(registries, tag.getCompound("coolantInput"));
        coolantOutput.readFromNBT(registries, tag.getCompound("coolantOutput"));
        for (int n = 0; n < processFluids.length; n++)
            processFluids[n].readFromNBT(registries, tag.getCompound("processFluid" + n));
        for (int n = 0; n < beams.length; n++) {
            beams[n].setParticleStack(null);
            beams[n].readFromNBT(tag, n);
        }
    }

    public void clearAllMaterial() {
        coolantInput.setFluid(FluidStack.EMPTY);
        coolantOutput.setFluid(FluidStack.EMPTY);
        for (var tank : processFluids) tank.setFluid(FluidStack.EMPTY);
        for (var beam : beams) beam.setParticleStack(null);
        for (var part : parts.values()) {
            part.sourceTank.setFluid(FluidStack.EMPTY);
            for (int i = 0; i < part.inventory.getSlots(); i++) part.inventory.setStackInSlot(i, ItemStack.EMPTY);
            part.inputBeam.setParticleStack(null);
            part.outputBeam.setParticleStack(null);
            part.setChanged();
        }
        work = 0;
        markReferenceCoordForUpdate();
    }

    public CompoundTag menuSnapshot(HolderLookup.Provider registries) {
        CompoundTag tag = syncDataTo(new CompoundTag(), registries, SyncReason.FullSync);
        tag.putBoolean("assembled", isAssembled());
        tag.putString("validation", validationMessage);
        tag.putString("kind", kind.id);
        if (isAssembled()) {
            tag.putInt("beamLength", beamLength());
            tag.putDouble("radius", radius());
        }
        return tag;
    }
}
