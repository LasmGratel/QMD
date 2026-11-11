package lach_01298.qmd.item.player;

import lach_01298.qmd.block.entity.accelerator.AbstractAcceleratorBlockEntity;
import lach_01298.qmd.block.entity.BeamlineBlockEntity;
import lach_01298.qmd.block.entity.chamber.AbstractChamberBlockEntity;
import lach_01298.qmd.capability.QMDParticleCapabilities;
import lach_01298.qmd.multiblock.accelerator.AcceleratorPartType;
import lach_01298.qmd.particle.IParticleStackHandler;
import lach_01298.qmd.particle.Particle;
import lach_01298.qmd.particle.ParticleStack;
import lach_01298.qmd.particle.ParticleStorageBeamline;
import lach_01298.qmd.particle.Particles;
import lach_01298.qmd.physics.ParticlePhysics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/** Immutable, bounded snapshot of the server-side target inspected by a beam meter. */
public record BeamMeterReading(Kind kind, String particleName, int amount, long meanEnergy, double focus,
    double focusLossPerBlock, double travelDistance, int beamlineLength, PortMode portMode, int outputChannel) {

    public static final StreamCodec<FriendlyByteBuf, BeamMeterReading> STREAM_CODEC = new StreamCodec<>() {

        @Override
        public BeamMeterReading decode(FriendlyByteBuf buffer) {
            return new BeamMeterReading(
                buffer.readEnum(Kind.class),
                buffer.readUtf(64),
                buffer.readVarInt(),
                buffer.readLong(),
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readVarInt(),
                buffer.readEnum(PortMode.class),
                buffer.readVarInt());
        }

        @Override
        public void encode(FriendlyByteBuf buffer, BeamMeterReading reading) {
            buffer.writeEnum(reading.kind);
            buffer.writeUtf(reading.particleName, 64);
            buffer.writeVarInt(reading.amount);
            buffer.writeLong(reading.meanEnergy);
            buffer.writeDouble(reading.focus);
            buffer.writeDouble(reading.focusLossPerBlock);
            buffer.writeDouble(reading.travelDistance);
            buffer.writeVarInt(reading.beamlineLength);
            buffer.writeEnum(reading.portMode);
            buffer.writeVarInt(reading.outputChannel);
        }
    };

    public BeamMeterReading {
        kind = Objects.requireNonNull(kind, "kind");
        particleName = Objects.requireNonNull(particleName, "particleName");
        portMode = Objects.requireNonNull(portMode, "portMode");
        if (particleName.length() > 64) throw new IllegalArgumentException("Particle name is too long");
        if (amount < 0 || meanEnergy < 0 || focus < 0 || focusLossPerBlock < 0 || beamlineLength < 0) {
            throw new IllegalArgumentException("Beam meter values must be non-negative");
        }
        if (!Double.isFinite(focus) || !Double.isFinite(focusLossPerBlock)) {
            throw new IllegalArgumentException("Beam meter focus values must be finite");
        }
        if (Double.isNaN(travelDistance) || travelDistance < 0) {
            throw new IllegalArgumentException("Beam meter travel distance must be non-negative");
        }
        if (kind == Kind.BEAM && (particleName.isBlank() || amount == 0)) {
            throw new IllegalArgumentException("A beam reading requires a particle and a positive amount");
        }
        if (kind != Kind.BEAM && (!particleName.isEmpty() || amount != 0
            || meanEnergy != 0
            || focus != 0
            || focusLossPerBlock != 0
            || travelDistance != 0)) {
            throw new IllegalArgumentException("Only beam readings may contain beam values");
        }
        if (kind != Kind.ACCELERATOR_PORT && portMode != PortMode.NONE) {
            throw new IllegalArgumentException("Only accelerator-port readings may contain a port mode");
        }
        if (kind == Kind.ACCELERATOR_PORT && portMode == PortMode.NONE) {
            throw new IllegalArgumentException("An accelerator-port reading requires a port mode");
        }
        if (kind != Kind.CHAMBER_PORT && outputChannel != -1) {
            throw new IllegalArgumentException("Only chamber-port readings may contain an output channel");
        }
        if (kind == Kind.CHAMBER_PORT && outputChannel < 0) {
            throw new IllegalArgumentException("A chamber-port reading requires a non-negative output channel");
        }
    }

    public static BeamMeterReading empty(int beamlineLength) {
        return new BeamMeterReading(Kind.EMPTY, "", 0, 0, 0, 0, 0, Math.max(0, beamlineLength), PortMode.NONE, -1);
    }

    public static BeamMeterReading acceleratorPort(PortMode mode) {
        if (mode == PortMode.NONE) throw new IllegalArgumentException("An accelerator port must be input or output");
        return new BeamMeterReading(Kind.ACCELERATOR_PORT, "", 0, 0, 0, 0, 0, 0, mode, -1);
    }

    public static BeamMeterReading chamberPort(int outputChannel) {
        if (outputChannel < 0) throw new IllegalArgumentException("Output channel must be non-negative");
        return new BeamMeterReading(Kind.CHAMBER_PORT, "", 0, 0, 0, 0, 0, 0, PortMode.NONE, outputChannel);
    }

    public static BeamMeterReading beam(ParticleStack source, int beamlineLength) {
        Objects.requireNonNull(source, "source");
        if (source.isEmpty() || source.getParticle() == null) return empty(beamlineLength);

        ParticleStack measured = source.copy();
        measured.addFocus(-ParticlePhysics.focusLoss(Math.max(0, beamlineLength), measured));
        double lossPerBlock = ParticlePhysics.focusLoss(1, measured);
        double distance = lossPerBlock == 0 ? Double.POSITIVE_INFINITY : measured.getFocus() / lossPerBlock;
        return new BeamMeterReading(
            Kind.BEAM,
            measured.getParticle()
                .getName(),
            measured.getAmount(),
            measured.getMeanEnergy(),
            measured.getFocus(),
            lossPerBlock,
            distance,
            Math.max(0, beamlineLength),
            PortMode.NONE,
            -1);
    }

    /**
     * Measures the same capability and port state used by the legacy item. A null result means the
     * target is not a particle endpoint and normal block interaction should continue.
     */
    @Nullable
    public static BeamMeterReading measure(Level level, BlockPos pos, boolean inspectPortSetting) {
        if (!level.hasChunkAt(pos)) return null;
        if (inspectPortSetting) {
            Object blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof AbstractChamberBlockEntity chamber && chamber.role()
                .beamPort() && chamber.output()) {
                return chamberPort(chamber.channel());
            }
            if (blockEntity instanceof AbstractAcceleratorBlockEntity accelerator) {
                AcceleratorPartType.Role role = accelerator.partType().role;
                if (role == AcceleratorPartType.Role.BEAM_PORT || role == AcceleratorPartType.Role.SYNCHROTRON_PORT) {
                    return acceleratorPort(
                        role == AcceleratorPartType.Role.SYNCHROTRON_PORT || accelerator.output() ? PortMode.OUTPUT
                            : PortMode.INPUT);
                }
            }
            return null;
        }

        for (Direction face : Direction.values()) {
            IParticleStackHandler handler = level.getCapability(QMDParticleCapabilities.BLOCK, pos, face);
            if (handler == null) continue;
            int length = beamlineLength(level, pos);
            ParticleStack stack = handler.getParticle();
            return stack == null || stack.isEmpty() ? empty(length) : beam(stack, length);
        }
        return null;
    }

    private static int beamlineLength(Level level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof BeamlineBlockEntity beamline)) return 0;
        return beamline.getParticleBeams()
            .stream()
            .filter(ParticleStorageBeamline.class::isInstance)
            .map(ParticleStorageBeamline.class::cast)
            .mapToInt(ParticleStorageBeamline::getLength)
            .findFirst()
            .orElse(0);
    }

    @Nullable
    public Particle particle() {
        return kind == Kind.BEAM ? Particles.getParticleFromName(particleName) : null;
    }

    public boolean hasBeam() {
        return kind == Kind.BEAM;
    }

    public enum Kind {
        EMPTY,
        BEAM,
        ACCELERATOR_PORT,
        CHAMBER_PORT
    }

    public enum PortMode {
        NONE,
        INPUT,
        OUTPUT
    }
}
