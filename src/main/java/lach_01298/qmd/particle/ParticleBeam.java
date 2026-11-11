package lach_01298.qmd.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

/** Immutable, validated particle state for persistence and network snapshots. */
public record ParticleBeam(String particleName, int amount, long meanEnergy, double focus) {

    public static final ParticleBeam EMPTY = new ParticleBeam("", 0, 0, 0);

    private record Fields(String particle, int amount, long energy, double focus) {}
    public static final Codec<ParticleBeam> CODEC = RecordCodecBuilder
        .<Fields>create(
            instance -> instance.group(
                Codec.string(0, 128)
                    .fieldOf("particle")
                    .forGetter(Fields::particle),
                Codec.INT.fieldOf("amount")
                    .forGetter(Fields::amount),
                Codec.LONG.fieldOf("meanEnergy")
                    .forGetter(Fields::energy),
                Codec.DOUBLE.fieldOf("focus")
                    .forGetter(Fields::focus))
                .apply(instance, Fields::new))
        .flatXmap(fields -> {
            try {
                return DataResult
                    .success(new ParticleBeam(fields.particle, fields.amount, fields.energy, fields.focus));
            } catch (IllegalArgumentException exception) {
                return DataResult.error(exception::getMessage);
            }
        }, beam -> DataResult.success(new Fields(beam.particleName, beam.amount, beam.meanEnergy, beam.focus)));

    public static final StreamCodec<FriendlyByteBuf, ParticleBeam> STREAM_CODEC = new StreamCodec<>() {

        @Override
        public ParticleBeam decode(FriendlyByteBuf buffer) {
            String name = buffer.readUtf(128);
            int amount = buffer.readVarInt();
            long energy = buffer.readLong();
            double focus = buffer.readDouble();
            try {
                return new ParticleBeam(name, amount, energy, focus);
            } catch (IllegalArgumentException exception) {
                throw new DecoderException("Invalid QMD particle beam", exception);
            }
        }

        @Override
        public void encode(FriendlyByteBuf buffer, ParticleBeam beam) {
            buffer.writeUtf(beam.particleName, 128);
            buffer.writeVarInt(beam.amount);
            buffer.writeLong(beam.meanEnergy);
            buffer.writeDouble(beam.focus);
        }
    };

    public ParticleBeam {
        if (particleName == null || particleName.length() > 128
            || amount < 0
            || meanEnergy < 0
            || !Double.isFinite(focus)
            || focus < 0) {
            throw new IllegalArgumentException("Invalid particle name, amount, energy or focus");
        }
        if (particleName.isEmpty()) {
            if (amount != 0 || meanEnergy != 0 || focus != 0) {
                throw new IllegalArgumentException("An empty beam must have zero amount, energy and focus");
            }
        } else {
            Particles.initialize();
            Particle particle = Particles.getParticleFromName(particleName);
            if (particle == null) throw new IllegalArgumentException("Unknown QMD particle: " + particleName);
            particleName = particle.getName();
        }
    }

    public static ParticleBeam fromStack(@Nullable ParticleStack stack) {
        return stack == null || stack.isEmpty() ? EMPTY
            : new ParticleBeam(
                stack.getParticle()
                    .getName(),
                stack.getAmount(),
                stack.getMeanEnergy(),
                stack.getFocus());
    }

    @Nullable
    public ParticleStack toStack() {
        return particleName.isEmpty() || amount == 0 ? null
            : new ParticleStack(Particles.getParticleFromName(particleName), amount, meanEnergy, focus);
    }
}
