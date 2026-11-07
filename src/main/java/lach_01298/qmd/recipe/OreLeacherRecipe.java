package lach_01298.qmd.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.nred.nuclearcraft.recipe.ProcessorRecipe;
import com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient;
import com.nred.nuclearcraft.recipe.SizedChanceItemIngredient;
import lach_01298.qmd.registration.QMDMachines;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;

/** One ore and three acid inputs, with up to three independently rolled products. */
public final class OreLeacherRecipe extends ProcessorRecipe {
    private static final Codec<Double> POSITIVE_FINITE = Codec.DOUBLE.validate(value ->
            Double.isFinite(value) && value > 0 ? DataResult.success(value)
                    : DataResult.error(() -> "Ore leacher time modifier must be finite and positive"));
    private static final Codec<Double> NON_NEGATIVE_FINITE = Codec.DOUBLE.validate(value ->
            Double.isFinite(value) && value >= 0 ? DataResult.success(value)
                    : DataResult.error(() -> "Ore leacher power/radiation must be finite and nonnegative"));
    private static final Codec<SizedChanceItemIngredient> INPUT_ITEM_CODEC =
            SizedChanceItemIngredient.FLAT_CODEC.validate(OreLeacherRecipe::validateItemInput);
    private static final Codec<SizedChanceItemIngredient> PRODUCT_CODEC =
            SizedChanceItemIngredient.FLAT_CODEC.validate(OreLeacherRecipe::validateProduct);
    private static final Codec<List<SizedChanceItemIngredient>> PRODUCTS_CODEC =
            PRODUCT_CODEC.listOf(1, 3).validate(products -> products.getFirst().isEmpty()
                    ? DataResult.error(() -> "Ore leacher primary product must not be empty")
                    : DataResult.success(products));
    private static final Codec<SizedChanceFluidIngredient> FLUID_INPUT_CODEC =
            SizedChanceFluidIngredient.FLAT_CODEC.validate(OreLeacherRecipe::validateFluidInput);

    public static final MapCodec<OreLeacherRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    INPUT_ITEM_CODEC.listOf(1, 1).fieldOf("itemIngredients").forGetter(OreLeacherRecipe::getItemIngredients),
                    PRODUCTS_CODEC.fieldOf("itemProducts").forGetter(OreLeacherRecipe::getItemProducts),
                    FLUID_INPUT_CODEC.listOf(3, 3).fieldOf("fluidIngredients").forGetter(OreLeacherRecipe::getFluidIngredients),
                    SizedChanceFluidIngredient.FLAT_CODEC.listOf(0, 0).optionalFieldOf("fluidProducts", List.of()).forGetter(OreLeacherRecipe::getFluidProducts),
                    POSITIVE_FINITE.optionalFieldOf("timeModifier", 1D).forGetter(OreLeacherRecipe::getProcessTimeMultiplier),
                    NON_NEGATIVE_FINITE.optionalFieldOf("powerModifier", 1D).forGetter(OreLeacherRecipe::getProcessPowerMultiplier),
                    NON_NEGATIVE_FINITE.optionalFieldOf("radiation", 0D).forGetter(OreLeacherRecipe::getBaseProcessRadiation)
            ).apply(instance, OreLeacherRecipe::new));

    public OreLeacherRecipe(List<SizedChanceItemIngredient> itemInputs,
                           List<SizedChanceItemIngredient> itemResults,
                           List<SizedChanceFluidIngredient> fluidInputs,
                           List<SizedChanceFluidIngredient> fluidResults,
                           double timeModifier, double powerModifier, double radiation) {
        super(List.copyOf(itemInputs), padProducts(itemResults), List.copyOf(fluidInputs), List.copyOf(fluidResults),
                timeModifier, powerModifier, radiation);
        if (itemInputs.size() != 1 || fluidInputs.size() != 3 || !fluidResults.isEmpty()) {
            throw new IllegalArgumentException("Ore leacher requires one item input, three fluid inputs and no fluid products");
        }
        itemInputs.forEach(input -> validateItemInput(input).getOrThrow(IllegalArgumentException::new));
        itemResults.forEach(product -> validateProduct(product).getOrThrow(IllegalArgumentException::new));
        fluidInputs.forEach(input -> validateFluidInput(input).getOrThrow(IllegalArgumentException::new));
        if (!Double.isFinite(timeModifier) || timeModifier <= 0
                || !Double.isFinite(powerModifier) || powerModifier < 0
                || !Double.isFinite(radiation) || radiation < 0) {
            throw new IllegalArgumentException("Invalid ore leacher process modifiers");
        }
    }

    private static List<SizedChanceItemIngredient> padProducts(List<SizedChanceItemIngredient> products) {
        if (products.isEmpty() || products.size() > 3 || products.getFirst().isEmpty() || products.getFirst().count() <= 0) {
            throw new IllegalArgumentException("Ore leacher requires one to three item products");
        }
        List<SizedChanceItemIngredient> padded = new ArrayList<>(products);
        while (padded.size() < 3) {
            padded.add(SizedChanceItemIngredient.EMPTY);
        }
        return List.copyOf(padded);
    }

    private static DataResult<SizedChanceItemIngredient> validateItemInput(SizedChanceItemIngredient input) {
        return input.count() > 0 && !input.isEmpty() && input.chancePercent() == 100 && input.minStackSize() == 0
                ? DataResult.success(input)
                : DataResult.error(() -> "Ore leacher item input must have a positive count and no chance modifier");
    }

    private static DataResult<SizedChanceItemIngredient> validateProduct(SizedChanceItemIngredient product) {
        boolean empty = product.isEmpty() && product.count() == 0 && product.minStackSize() == 0;
        return (empty || product.count() > 0 && !product.isEmpty())
                && product.minStackSize() >= 0 && product.minStackSize() <= product.count()
                && product.chancePercent() > 0 && product.chancePercent() <= 100
                ? DataResult.success(product)
                : DataResult.error(() -> "Invalid ore leacher product count, minimum count or chance");
    }

    private static DataResult<SizedChanceFluidIngredient> validateFluidInput(SizedChanceFluidIngredient input) {
        return input.amount() > 0 && !input.isEmpty() && input.chancePercent() == 100 && input.minStackSize() == 0 && input.increment() == 1
                ? DataResult.success(input)
                : DataResult.error(() -> "Ore leacher fluid input must have a positive amount and no chance modifier");
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return QMDMachineRecipes.ORE_LEACHER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return QMDMachineRecipes.ORE_LEACHER_TYPE.get();
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(QMDMachines.ORE_LEACHER.get());
    }

    public static final class Serializer implements RecipeSerializer<OreLeacherRecipe> {
        private static final StreamCodec<RegistryFriendlyByteBuf, OreLeacherRecipe> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public OreLeacherRecipe decode(RegistryFriendlyByteBuf buffer) {
                List<SizedChanceItemIngredient> inputs = readItems(buffer, 1, 1);
                List<SizedChanceItemIngredient> products = readItems(buffer, 1, 3);
                List<SizedChanceFluidIngredient> fluids = new ArrayList<>(3);
                for (int i = 0; i < 3; i++) {
                    fluids.add(SizedChanceFluidIngredient.STREAM_CODEC.decode(buffer));
                }
                return new OreLeacherRecipe(inputs, products, fluids, List.of(),
                        buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buffer, OreLeacherRecipe recipe) {
                writeItems(buffer, recipe.getItemIngredients());
                writeItems(buffer, recipe.getItemProducts());
                recipe.getFluidIngredients().forEach(input -> SizedChanceFluidIngredient.STREAM_CODEC.encode(buffer, input));
                buffer.writeDouble(recipe.getProcessTimeMultiplier());
                buffer.writeDouble(recipe.getProcessPowerMultiplier());
                buffer.writeDouble(recipe.getBaseProcessRadiation());
            }
        };

        private static List<SizedChanceItemIngredient> readItems(RegistryFriendlyByteBuf buffer, int minimum, int maximum) {
            int count = buffer.readVarInt();
            if (count < minimum || count > maximum) {
                throw new IllegalArgumentException("Invalid ore leacher network ingredient list size: " + count);
            }
            List<SizedChanceItemIngredient> items = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                items.add(SizedChanceItemIngredient.STREAM_CODEC.decode(buffer));
            }
            return items;
        }

        private static void writeItems(RegistryFriendlyByteBuf buffer, List<SizedChanceItemIngredient> items) {
            buffer.writeVarInt(items.size());
            items.forEach(input -> SizedChanceItemIngredient.STREAM_CODEC.encode(buffer, input));
        }

        @Override
        public MapCodec<OreLeacherRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, OreLeacherRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
