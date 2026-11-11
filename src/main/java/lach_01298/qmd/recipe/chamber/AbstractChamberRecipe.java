package lach_01298.qmd.recipe.chamber;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.nred.nuclearcraft.recipe.BasicRecipe;
import com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient;
import com.nred.nuclearcraft.recipe.SizedChanceItemIngredient;
import lach_01298.qmd.particle.ParticleBeam;
import lach_01298.qmd.particle.ParticleStack;
import lach_01298.qmd.recipe.ingredient.ParticleIngredient;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

/** Data-pack reaction shared by chamber simulation, automation and recipe viewers. */
public abstract class AbstractChamberRecipe extends BasicRecipe {

    private static final Codec<Double> NONNEGATIVE = Codec.DOUBLE.validate(
        value -> Double.isFinite(value) && value >= 0 ? DataResult.success(value)
            : DataResult.error(() -> "Expected a finite nonnegative reaction value"));
    private final ChamberKind kind;
    private final List<ParticleIngredient> particleInputs;
    private final List<ParticleBeam> particleOutputs;
    private final double crossSection, radiation, heat;
    private final long energyReleased;
    private final double temperature;

    protected AbstractChamberRecipe(ChamberKind kind, List<SizedChanceItemIngredient> items,
        List<SizedChanceFluidIngredient> fluids, List<ParticleIngredient> particles,
        List<SizedChanceItemIngredient> itemProducts, List<SizedChanceFluidIngredient> fluidProducts,
        List<ParticleBeam> particleProducts, double crossSection, long energyReleased, double radiation, double heat,
        double temperature) {
        super(List.copyOf(items), List.copyOf(fluids), List.copyOf(itemProducts), List.copyOf(fluidProducts));
        this.kind = kind;
        this.particleInputs = List.copyOf(particles);
        this.particleOutputs = List.copyOf(particleProducts);
        this.crossSection = crossSection;
        this.energyReleased = energyReleased;
        this.radiation = radiation;
        this.heat = heat;
        this.temperature = temperature;
        if (items.size() != kind.itemInputs || fluids.size() != kind.fluidInputs
            || particles.size() != kind.particleInputs
            || itemProducts.size() != kind.itemOutputs
            || fluidProducts.size() != kind.fluidOutputs
            || particleProducts.size() != kind.particleOutputs) {
            throw new IllegalArgumentException("Wrong ingredient/output channel count for " + kind.id());
        }
        if (!Double.isFinite(crossSection) || crossSection < 0
            || !Double.isFinite(radiation)
            || radiation < 0
            || !Double.isFinite(heat)
            || heat < 0
            || !Double.isFinite(temperature)
            || temperature < 0) {
            throw new IllegalArgumentException("Invalid reaction cross section, radiation, heat or temperature");
        }
        for (SizedChanceItemIngredient input : items) {
            if (input.chancePercent() != 100 || input.minStackSize() != 0
                || input.count() < 0
                || input.isEmpty() != (input.count() == 0))
                throw new IllegalArgumentException("Invalid reaction item input");
        }
        for (SizedChanceFluidIngredient input : fluids) {
            if (input.chancePercent() != 100 || input.minStackSize() != 0
                || input.increment() != 1
                || input.amount() < 0
                || input.isEmpty() != (input.amount() == 0))
                throw new IllegalArgumentException("Invalid reaction fluid input");
        }
        for (SizedChanceItemIngredient output : itemProducts) {
            if (output.chancePercent() <= 0 || output.chancePercent() > 100
                || output.minStackSize() < 0
                || output.minStackSize() > output.count()
                || output.isEmpty() != (output.count() == 0))
                throw new IllegalArgumentException("Invalid reaction item output");
        }
        for (SizedChanceFluidIngredient output : fluidProducts) {
            if (output.chancePercent() <= 0 || output.chancePercent() > 100
                || output.minStackSize() < 0
                || output.minStackSize() > output.amount()
                || output.increment() < 1
                || output.isEmpty() != (output.amount() == 0))
                throw new IllegalArgumentException("Invalid reaction fluid output");
        }
    }

    public ChamberKind kind() {
        return kind;
    }

    public List<ParticleIngredient> particleInputs() {
        return particleInputs;
    }

    public List<ParticleBeam> particleOutputs() {
        return particleOutputs;
    }

    public double crossSection() {
        return crossSection;
    }

    public long energyReleased() {
        return energyReleased;
    }

    public double radiation() {
        return radiation;
    }

    public double heat() {
        return heat;
    }

    public double temperature() {
        return temperature;
    }

    /** Input ports are shapeless; product channels retain their recipe order. */
    public boolean matchesParticles(List<ParticleStack> beams) {
        return orderedParticles(beams) != null;
    }

    public List<ParticleStack> orderedParticles(List<ParticleStack> beams) {
        if (beams.size() != particleInputs.size()) return null;
        if (matchesOrdered(beams)) return beams;
        if (beams.size() == 2) {
            List<ParticleStack> reversed = java.util.Arrays.asList(beams.get(1), beams.get(0));
            if (matchesOrdered(reversed)) return reversed;
        }
        return null;
    }

    private boolean matchesOrdered(List<ParticleStack> beams) {
        for (int i = 0; i < beams.size(); i++) if (!particleInputs.get(i)
            .matches(beams.get(i), false)) return false;
        return true;
    }

    @Override
    public abstract RecipeSerializer<? extends AbstractChamberRecipe> getSerializer();

    @Override
    public abstract RecipeType<? extends AbstractChamberRecipe> getType();

    private static <T> MapCodec<List<T>> channels(Codec<T> codec, int count, String name) {
        Codec<List<T>> list = codec.listOf(count, count);
        return count == 0 ? list.optionalFieldOf(name, List.of()) : list.fieldOf(name);
    }

    private static <R extends AbstractChamberRecipe> MapCodec<R> codec(ChamberKind kind, Factory<R> factory) {
        return RecordCodecBuilder.<R>mapCodec(
            instance -> instance
                .group(
                    channels(SizedChanceItemIngredient.FLAT_CODEC, kind.itemInputs, "itemIngredients")
                        .forGetter(AbstractChamberRecipe::getItemIngredients),
                    channels(SizedChanceFluidIngredient.FLAT_CODEC, kind.fluidInputs, "fluidIngredients")
                        .forGetter(AbstractChamberRecipe::getFluidIngredients),
                    channels(ParticleIngredient.CODEC, kind.particleInputs, "particleIngredients")
                        .forGetter(AbstractChamberRecipe::particleInputs),
                    channels(SizedChanceItemIngredient.FLAT_CODEC, kind.itemOutputs, "itemProducts")
                        .forGetter(AbstractChamberRecipe::getItemProducts),
                    channels(SizedChanceFluidIngredient.FLAT_CODEC, kind.fluidOutputs, "fluidProducts")
                        .forGetter(AbstractChamberRecipe::getFluidProducts),
                    channels(ParticleBeam.CODEC, kind.particleOutputs, "particleProducts")
                        .forGetter(AbstractChamberRecipe::particleOutputs),
                    NONNEGATIVE.optionalFieldOf("crossSection", 1D)
                        .forGetter(AbstractChamberRecipe::crossSection),
                    Codec.LONG.optionalFieldOf("energyReleased", 0L)
                        .forGetter(AbstractChamberRecipe::energyReleased),
                    NONNEGATIVE.optionalFieldOf("radiation", 0D)
                        .forGetter(AbstractChamberRecipe::radiation),
                    NONNEGATIVE.optionalFieldOf("heat", 0D)
                        .forGetter(AbstractChamberRecipe::heat),
                    NONNEGATIVE.optionalFieldOf("temperature", 0D)
                        .forGetter(AbstractChamberRecipe::temperature))
                .apply(
                    instance,
                    (items, fluids, particles, itemProducts, fluidProducts, particleProducts, crossSection,
                        energyReleased, radiation, heat, temperature) -> factory.create(
                            items,
                            fluids,
                            particles,
                            itemProducts,
                            fluidProducts,
                            particleProducts,
                            crossSection,
                            energyReleased,
                            radiation,
                            heat,
                            temperature)));
    }

    @FunctionalInterface
    public interface Factory<R extends AbstractChamberRecipe> {

        R create(List<SizedChanceItemIngredient> items, List<SizedChanceFluidIngredient> fluids,
            List<ParticleIngredient> particles, List<SizedChanceItemIngredient> itemProducts,
            List<SizedChanceFluidIngredient> fluidProducts, List<ParticleBeam> particleProducts, double crossSection,
            long energyReleased, double radiation, double heat, double temperature);
    }

    public static final class Serializer<R extends AbstractChamberRecipe> implements RecipeSerializer<R> {

        private final MapCodec<R> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;

        public Serializer(ChamberKind kind, Factory<R> factory) {
            codec = AbstractChamberRecipe.codec(kind, factory);
            // Registry-aware codec preserves component-sensitive item and fluid alternatives.
            streamCodec = ByteBufCodecs.fromCodecWithRegistries(codec.codec());
        }

        @Override
        public MapCodec<R> codec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, R> streamCodec() {
            return streamCodec;
        }
    }
}
