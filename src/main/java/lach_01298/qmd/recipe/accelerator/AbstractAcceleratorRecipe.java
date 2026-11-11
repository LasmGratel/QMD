package lach_01298.qmd.recipe.accelerator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient;
import com.nred.nuclearcraft.recipe.SizedChanceItemIngredient;
import lach_01298.qmd.particle.ParticleBeam;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

/** Reloadable ion-source, cryogenic cooling and mass-separation recipes. */
public abstract class AbstractAcceleratorRecipe implements Recipe<AbstractAcceleratorRecipe.Input> {

    private final AcceleratorRecipeKind mode;
    private final Ingredient item;
    private final int itemCount;
    private final FluidStack fluid;
    private final ParticleBeam particle;
    private final List<SizedChanceItemIngredient> itemProducts;
    private final List<SizedChanceFluidIngredient> fluidProducts;
    private final int heat, temperature;
    private final double time;

    protected AbstractAcceleratorRecipe(AcceleratorRecipeKind mode, Ingredient item, int itemCount, FluidStack fluid,
        ParticleBeam particle, List<SizedChanceItemIngredient> itemProducts,
        List<SizedChanceFluidIngredient> fluidProducts, int heat, int temperature, double time) {
        if (itemCount < 1 || heat < 0
            || temperature < 0
            || !Double.isFinite(time)
            || time <= 0
            || itemProducts.size() > 4
            || fluidProducts.size() > 4) throw new IllegalArgumentException("Invalid accelerator recipe");
        this.mode = mode;
        this.item = item;
        this.itemCount = itemCount;
        this.fluid = fluid.copy();
        this.particle = particle;
        this.itemProducts = List.copyOf(itemProducts);
        this.fluidProducts = List.copyOf(fluidProducts);
        this.heat = heat;
        this.temperature = temperature;
        this.time = time;
        if (itemProducts.stream()
            .anyMatch(p -> p.count() < 0 || p.minStackSize() > p.count() || p.chancePercent() > 100)
            || fluidProducts.stream()
                .anyMatch(p -> p.amount() < 0 || p.minStackSize() > p.amount() || p.chancePercent() > 100))
            throw new IllegalArgumentException("Invalid accelerator product bounds");
    }

    private static <R extends AbstractAcceleratorRecipe> MapCodec<R> codec(Factory<R> factory) {
        return RecordCodecBuilder.<R>mapCodec(
            i -> i.group(
                Ingredient.CODEC.optionalFieldOf("item", Ingredient.EMPTY)
                    .forGetter(AbstractAcceleratorRecipe::item),
                Codec.intRange(1, 64)
                    .optionalFieldOf("item_count", 1)
                    .forGetter(AbstractAcceleratorRecipe::itemCount),
                FluidStack.OPTIONAL_CODEC.optionalFieldOf("fluid", FluidStack.EMPTY)
                    .forGetter(AbstractAcceleratorRecipe::fluid),
                ParticleBeam.CODEC.optionalFieldOf("particle", ParticleBeam.EMPTY)
                    .forGetter(AbstractAcceleratorRecipe::particle),
                SizedChanceItemIngredient.FLAT_CODEC.listOf()
                    .optionalFieldOf("items", List.of())
                    .forGetter(AbstractAcceleratorRecipe::itemProducts),
                SizedChanceFluidIngredient.FLAT_CODEC.listOf()
                    .optionalFieldOf("fluids", List.of())
                    .forGetter(AbstractAcceleratorRecipe::fluidProducts),
                Codec.intRange(0, Integer.MAX_VALUE)
                    .optionalFieldOf("heat", 0)
                    .forGetter(AbstractAcceleratorRecipe::heat),
                Codec.intRange(0, 400)
                    .optionalFieldOf("temperature", 293)
                    .forGetter(AbstractAcceleratorRecipe::temperature),
                Codec.DOUBLE.optionalFieldOf("time", 1D)
                    .forGetter(AbstractAcceleratorRecipe::time))
                .apply(
                    i,
                    (item, count, fluid, particle, items, fluids, heat, temperature, time) -> factory
                        .create(item, count, fluid, particle, items, fluids, heat, temperature, time)));
    }

    public final AcceleratorRecipeKind mode() {
        return mode;
    }

    public final Ingredient item() {
        return item;
    }

    public final int itemCount() {
        return itemCount;
    }

    public final ParticleBeam particle() {
        return particle;
    }

    public final List<SizedChanceItemIngredient> itemProducts() {
        return itemProducts;
    }

    public final List<SizedChanceFluidIngredient> fluidProducts() {
        return fluidProducts;
    }

    public final int heat() {
        return heat;
    }

    public final int temperature() {
        return temperature;
    }

    public final double time() {
        return time;
    }

    public final FluidStack fluid() {
        return fluid.copy();
    }

    public List<ItemStack> items() {
        return itemProducts.stream()
            .map(SizedChanceItemIngredient::getStackRaw)
            .toList();
    }

    public List<FluidStack> fluids() {
        return fluidProducts.stream()
            .map(
                p -> p.getStackRaw()
                    .copy())
            .toList();
    }

    @Override
    public boolean matches(Input input, Level level) {
        return !isIncomplete() && (item.isEmpty() || item.test(input.item) && input.item.getCount() >= itemCount)
            && (fluid.isEmpty() || FluidStack.isSameFluidSameComponents(fluid, input.fluid)
                && input.fluid.getAmount() >= fluid.getAmount());
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
        return itemProducts.isEmpty() ? ItemStack.EMPTY
            : itemProducts.getFirst()
                .getStackRaw();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean isIncomplete() {
        return itemProducts.stream()
            .anyMatch(p -> !p.isEmpty() && p.getItemsRaw().length == 0)
            || fluidProducts.stream()
                .anyMatch(p -> !p.isEmpty() && p.getFluidsRaw().length == 0);
    }

    @Override
    public abstract RecipeSerializer<? extends AbstractAcceleratorRecipe> getSerializer();

    @Override
    public abstract RecipeType<? extends AbstractAcceleratorRecipe> getType();

    public record Input(ItemStack item, FluidStack fluid) implements RecipeInput {

        @Override
        public ItemStack getItem(int index) {
            if (index != 0) throw new IndexOutOfBoundsException(index);
            return item;
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return item.isEmpty() && fluid.isEmpty();
        }
    }

    @FunctionalInterface
    public interface Factory<R extends AbstractAcceleratorRecipe> {

        R create(Ingredient item, int itemCount, FluidStack fluid, ParticleBeam particle,
            List<SizedChanceItemIngredient> items, List<SizedChanceFluidIngredient> fluids, int heat, int temperature,
            double time);
    }

    public static final class Serializer<R extends AbstractAcceleratorRecipe> implements RecipeSerializer<R> {

        private final MapCodec<R> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;

        public Serializer(Factory<R> factory) {
            codec = AbstractAcceleratorRecipe.codec(factory);
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
