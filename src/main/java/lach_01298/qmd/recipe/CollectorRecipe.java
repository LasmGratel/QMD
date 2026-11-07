package lach_01298.qmd.recipe;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lach_01298.qmd.registration.QMDMachines;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Objects;

/** A world-conditioned fluid output, expressed as a full-efficiency amount per tick. */
public final class CollectorRecipe implements Recipe<CollectorRecipe.Input> {
    private static final Codec<FluidStack> OUTPUT_CODEC = FluidStack.CODEC.validate(output ->
            !output.isEmpty() && output.getAmount() > 0
                    ? DataResult.success(output)
                    : DataResult.error(() -> "Collector output amount must be positive"));

    private final Kind kind;
    private final WorldCondition world;
    private final FluidStack output;
    private final int priority;

    public CollectorRecipe(Kind kind, WorldCondition world, FluidStack output, int priority) {
        this.kind = Objects.requireNonNull(kind);
        this.world = Objects.requireNonNull(world);
        if (output.isEmpty() || output.getAmount() <= 0) {
            throw new IllegalArgumentException("Collector output amount must be positive");
        }
        this.output = output.copy();
        this.priority = priority;
    }

    public static MapCodec<CollectorRecipe> codec(Kind kind) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                WorldCondition.CODEC.optionalFieldOf("world", WorldCondition.ANY).forGetter(CollectorRecipe::world),
                OUTPUT_CODEC.fieldOf("output").forGetter(CollectorRecipe::output),
                Codec.INT.optionalFieldOf("priority", 0).forGetter(CollectorRecipe::priority)
        ).apply(instance, (world, output, priority) -> new CollectorRecipe(kind, world, output, priority)));
    }

    public Kind kind() {
        return kind;
    }

    public WorldCondition world() {
        return world;
    }

    public FluidStack output() {
        return output.copy();
    }

    public int priority() {
        return priority;
    }

    @Override
    public boolean matches(Input input, Level level) {
        return world.matches(input.block(), input.biome(), input.dimension());
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
    public RecipeSerializer<?> getSerializer() {
        return kind == Kind.LIQUID ? QMDMachineRecipes.LIQUID_COLLECTOR_SERIALIZER.get()
                : QMDMachineRecipes.ATMOSPHERE_COLLECTOR_SERIALIZER.get();
    }

    @Override
    public RecipeType<CollectorRecipe> getType() {
        return kind == Kind.LIQUID ? QMDMachineRecipes.LIQUID_COLLECTOR_TYPE.get()
                : QMDMachineRecipes.ATMOSPHERE_COLLECTOR_TYPE.get();
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(kind == Kind.LIQUID ? QMDMachines.LIQUID_COLLECTOR.get()
                : QMDMachines.ATMOSPHERE_COLLECTOR.get());
    }

    public enum Kind {
        LIQUID, ATMOSPHERE
    }

    public record Input(BlockState block, Holder<Biome> biome, ResourceKey<Level> dimension) implements RecipeInput {
        @Override
        public ItemStack getItem(int index) {
            throw new IndexOutOfBoundsException("Collectors do not have item inputs");
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }

    public static final class Serializer implements RecipeSerializer<CollectorRecipe> {
        private final MapCodec<CollectorRecipe> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, CollectorRecipe> streamCodec;

        public Serializer(Kind kind) {
            codec = CollectorRecipe.codec(kind);
            streamCodec = ByteBufCodecs.fromCodecWithRegistries(codec.codec());
        }

        @Override
        public MapCodec<CollectorRecipe> codec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CollectorRecipe> streamCodec() {
            return streamCodec;
        }
    }
}
