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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

/** Processing recipes and source-speed recipes are both authoritative, reloadable server data. */
public final class IrradiatorRecipe implements Recipe<SingleRecipeInput> {

    private static final Codec<Double> POSITIVE = Codec.DOUBLE.validate(
        value -> Double.isFinite(value) && value > 0 ? DataResult.success(value)
            : DataResult.error(() -> "Expected positive finite multiplier"));
    private static final Codec<Double> NON_NEGATIVE = Codec.DOUBLE.validate(
        value -> Double.isFinite(value) && value >= 0 ? DataResult.success(value)
            : DataResult.error(() -> "Expected nonnegative finite radiation"));

    public enum Kind {
        PROCESS,
        FUEL
    }

    private final Kind kind;
    private final Ingredient ingredient;
    private final ItemStack output;
    private final double multiplier, radiation;

    public IrradiatorRecipe(Kind kind, Ingredient ingredient, ItemStack output, double multiplier, double radiation) {
        if (!Double.isFinite(multiplier) || multiplier <= 0
            || !Double.isFinite(radiation)
            || radiation < 0
            || kind == Kind.PROCESS && output.isEmpty()
            || kind == Kind.FUEL && !output.isEmpty()) throw new IllegalArgumentException("Invalid irradiator recipe");
        this.kind = kind;
        this.ingredient = ingredient;
        this.output = output.copy();
        this.multiplier = multiplier;
        this.radiation = radiation;
    }

    public static MapCodec<IrradiatorRecipe> codec(Kind kind) {
        return RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient")
                    .forGetter(IrradiatorRecipe::ingredient),
                ItemStack.OPTIONAL_CODEC.optionalFieldOf("output", ItemStack.EMPTY)
                    .forGetter(IrradiatorRecipe::output),
                POSITIVE.optionalFieldOf("multiplier", 1D)
                    .forGetter(IrradiatorRecipe::multiplier),
                NON_NEGATIVE.optionalFieldOf("radiation", 0D)
                    .forGetter(IrradiatorRecipe::radiation))
                .apply(
                    instance,
                    (ingredient, output, multiplier,
                        radiation) -> new IrradiatorRecipe(kind, ingredient, output, multiplier, radiation)));
    }

    public Kind kind() {
        return kind;
    }

    public Ingredient ingredient() {
        return ingredient;
    }

    public ItemStack output() {
        return output.copy();
    }

    public double multiplier() {
        return multiplier;
    }

    public double radiation() {
        return radiation;
    }

    public boolean accepts(ItemStack stack) {
        return !stack.isEmpty() && ingredient.test(stack);
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return accepts(input.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return output();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return output();
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
    public RecipeSerializer<?> getSerializer() {
        return kind == Kind.PROCESS ? QMDRecipes.IRRADIATOR_SERIALIZER.get()
            : QMDRecipes.IRRADIATOR_FUEL_SERIALIZER.get();
    }

    @Override
    public RecipeType<IrradiatorRecipe> getType() {
        return kind == Kind.PROCESS ? QMDRecipes.IRRADIATOR_TYPE.get() : QMDRecipes.IRRADIATOR_FUEL_TYPE.get();
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(QMDBlocks.IRRADIATOR.get());
    }

    public static final class Serializer implements RecipeSerializer<IrradiatorRecipe> {

        private final MapCodec<IrradiatorRecipe> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, IrradiatorRecipe> stream;

        public Serializer(Kind kind) {
            codec = IrradiatorRecipe.codec(kind);
            stream = ByteBufCodecs.fromCodecWithRegistries(codec.codec());
        }

        @Override
        public MapCodec<IrradiatorRecipe> codec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, IrradiatorRecipe> streamCodec() {
            return stream;
        }
    }
}
