package lach_01298.qmd.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lach_01298.qmd.registry.QMDBlocks;
import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

/** Gas liquefaction and shell coolant recipes share a validated, data-driven fluid conversion. */
public final class LiquefierRecipe implements Recipe<LiquefierRecipe.Input> {

    private static final Codec<Double> POSITIVE = Codec.DOUBLE.validate(
        value -> Double.isFinite(value) && value > 0 ? DataResult.success(value)
            : DataResult.error(() -> "Expected a finite positive value"));
    private static final Codec<Double> NON_NEGATIVE = Codec.DOUBLE.validate(
        value -> Double.isFinite(value) && value >= 0 ? DataResult.success(value)
            : DataResult.error(() -> "Expected a finite nonnegative value"));
    private final Kind kind;
    private final FluidIngredient ingredient;
    private final int amount;
    private final FluidStack output;
    private final double heat, energy, pressureCoefficient;
    private final int inputTemperature, outputTemperature;

    public LiquefierRecipe(Kind kind, FluidIngredient ingredient, int amount, FluidStack output, double heat,
        double energy, int inputTemperature, int outputTemperature, double pressureCoefficient) {
        if (amount <= 0 || output.isEmpty()
            || !Double.isFinite(heat)
            || heat <= 0
            || !Double.isFinite(energy)
            || energy < 0
            || inputTemperature < 0
            || outputTemperature < 0
            || !Double.isFinite(pressureCoefficient)
            || pressureCoefficient < 0
            || kind == Kind.GAS && pressureCoefficient == 0)
            throw new IllegalArgumentException("Invalid liquefier recipe");
        this.kind = kind;
        this.ingredient = ingredient;
        this.amount = amount;
        this.output = output.copy();
        this.heat = heat;
        this.energy = energy;
        this.inputTemperature = inputTemperature;
        this.outputTemperature = outputTemperature;
        this.pressureCoefficient = pressureCoefficient;
    }

    public static MapCodec<LiquefierRecipe> codec(Kind kind) {
        return RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                FluidIngredient.CODEC.fieldOf("ingredient")
                    .forGetter(LiquefierRecipe::ingredient),
                Codec.intRange(1, Integer.MAX_VALUE)
                    .fieldOf("amount")
                    .forGetter(LiquefierRecipe::amount),
                FluidStack.CODEC.fieldOf("output")
                    .forGetter(LiquefierRecipe::output),
                POSITIVE.fieldOf("heat")
                    .forGetter(LiquefierRecipe::heat),
                NON_NEGATIVE.optionalFieldOf("energy", 0D)
                    .forGetter(LiquefierRecipe::energy),
                Codec.intRange(0, Integer.MAX_VALUE)
                    .fieldOf("input_temperature")
                    .forGetter(LiquefierRecipe::inputTemperature),
                Codec.intRange(0, Integer.MAX_VALUE)
                    .fieldOf("output_temperature")
                    .forGetter(LiquefierRecipe::outputTemperature),
                (kind == Kind.GAS ? POSITIVE : NON_NEGATIVE)
                    .optionalFieldOf("pressure_coefficient", kind == Kind.GAS ? 0.25D : 0D)
                    .forGetter(LiquefierRecipe::pressureCoefficient))
                .apply(
                    instance,
                    (ingredient, amount, output, heat, energy, in, out, coefficient) -> new LiquefierRecipe(
                        kind,
                        ingredient,
                        amount,
                        output,
                        heat,
                        energy,
                        in,
                        out,
                        coefficient)));
    }

    public Kind kind() {
        return kind;
    }

    public FluidIngredient ingredient() {
        return ingredient;
    }

    public int amount() {
        return amount;
    }

    public FluidStack output() {
        return output.copy();
    }

    public double heat() {
        return heat;
    }

    public double energy() {
        return energy;
    }

    /** Gas recipe: inversion temperature; coolant recipe: input temperature. */
    public int inputTemperature() {
        return inputTemperature;
    }

    /** Gas recipe: compressed gas temperature; coolant recipe: output temperature. */
    public int outputTemperature() {
        return outputTemperature;
    }

    public double pressureCoefficient() {
        return pressureCoefficient;
    }

    public boolean accepts(FluidStack stack) {
        return !stack.isEmpty() && ingredient.test(stack);
    }

    @Override
    public boolean matches(Input input, Level level) {
        return accepts(input.fluid()) && input.fluid()
            .getAmount() >= amount;
    }

    @Override
    public ItemStack assemble(Input input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean isIncomplete() {
        return false;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(QMDBlocks.LIQUEFIER_CONTROLLER.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return kind == Kind.GAS ? QMDRecipes.LIQUEFIER_GAS_SERIALIZER.get()
            : QMDRecipes.LIQUEFIER_COOLANT_SERIALIZER.get();
    }

    @Override
    public RecipeType<LiquefierRecipe> getType() {
        return kind == Kind.GAS ? QMDRecipes.LIQUEFIER_GAS_TYPE.get() : QMDRecipes.LIQUEFIER_COOLANT_TYPE.get();
    }

    public enum Kind {
        GAS,
        COOLANT
    }

    public record Input(FluidStack fluid) implements RecipeInput {

        @Override
        public ItemStack getItem(int index) {
            throw new IndexOutOfBoundsException(index);
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return fluid.isEmpty();
        }
    }

    public static final class Serializer implements RecipeSerializer<LiquefierRecipe> {

        private final MapCodec<LiquefierRecipe> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, LiquefierRecipe> stream;

        public Serializer(Kind kind) {
            codec = LiquefierRecipe.codec(kind);
            stream = ByteBufCodecs.fromCodecWithRegistries(codec.codec());
        }

        @Override
        public MapCodec<LiquefierRecipe> codec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, LiquefierRecipe> streamCodec() {
            return stream;
        }
    }
}
