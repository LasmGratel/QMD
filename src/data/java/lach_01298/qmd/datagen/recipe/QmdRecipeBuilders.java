package lach_01298.qmd.datagen.recipe;

import com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient;
import com.nred.nuclearcraft.recipe.SizedChanceItemIngredient;
import lach_01298.qmd.particle.ParticleBeam;
import lach_01298.qmd.recipe.accelerator.AbstractAcceleratorRecipe;
import lach_01298.qmd.recipe.chamber.AbstractChamberRecipe;
import lach_01298.qmd.recipe.CollectorRecipe;
import lach_01298.qmd.recipe.IrradiatorRecipe;
import lach_01298.qmd.recipe.LiquefierRecipe;
import lach_01298.qmd.recipe.OreLeacherRecipe;
import lach_01298.qmd.recipe.WorldCondition;
import lach_01298.qmd.recipe.ingredient.ParticleIngredient;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Typed custom recipe builders: each family supplies its runtime recipe constructor as a factory. */
final class QmdRecipeBuilders {
    static <R extends AbstractChamberRecipe> Chamber<R> chamber(AbstractChamberRecipe.Factory<R> factory) { return new Chamber<>(factory); }
    static <R extends AbstractAcceleratorRecipe> Accelerator<R> accelerator(AbstractAcceleratorRecipe.Factory<R> factory) { return new Accelerator<>(factory); }
    static Collector collector(CollectorRecipe.Kind kind) { return new Collector(kind, CollectorRecipe::new); }
    static Liquefier liquefier(LiquefierRecipe.Kind kind) { return new Liquefier(kind, LiquefierRecipe::new); }
    static Irradiator irradiator(IrradiatorRecipe.Kind kind) { return new Irradiator(kind, IrradiatorRecipe::new); }
    static OreLeacher oreLeacher() { return new OreLeacher(OreLeacherRecipe::new); }

    abstract static class Builder<B extends Builder<B>> implements RecipeBuilder {
        private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
        private final List<ICondition> conditions = new ArrayList<>();

        protected abstract Recipe<?> build();

        @SuppressWarnings("unchecked")
        protected final B self() { return (B) this; }

        @Override
        public final B unlockedBy(String name, Criterion<?> criterion) {
            criteria.put(name, criterion);
            return self();
        }

        /** Machine recipes have no recipe-book groups. */
        @Override
        public final B group(String group) { return self(); }

        @Override
        public Item getResult() { return Items.AIR; }

        final B conditions(ICondition... values) {
            conditions.addAll(List.of(values));
            return self();
        }

        @Override
        public final void save(RecipeOutput output, ResourceLocation id) {
            var conditioned = output.withConditions(conditions.toArray(ICondition[]::new));
            if (criteria.isEmpty()) {
                conditioned.accept(id, build(), null);
            } else {
                Advancement.Builder advancement = conditioned.advancement()
                        .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                        .rewards(AdvancementRewards.Builder.recipe(id))
                        .requirements(AdvancementRequirements.Strategy.OR);
                criteria.forEach(advancement::addCriterion);
                conditioned.accept(id, build(), advancement.build(id.withPrefix("recipes/")));
            }
        }
    }

    static final class Chamber<R extends AbstractChamberRecipe> extends Builder<Chamber<R>> {
        private final AbstractChamberRecipe.Factory<R> factory;
        private final List<SizedChanceItemIngredient> items = new ArrayList<>();
        private final List<SizedChanceFluidIngredient> fluids = new ArrayList<>();
        private final List<ParticleIngredient> particles = new ArrayList<>();
        private final List<SizedChanceItemIngredient> itemProducts = new ArrayList<>();
        private final List<SizedChanceFluidIngredient> fluidProducts = new ArrayList<>();
        private final List<ParticleBeam> particleProducts = new ArrayList<>();
        private double crossSection = 1, radiation, heat, temperature;
        private long energyReleased;

        Chamber(AbstractChamberRecipe.Factory<R> factory) { this.factory = factory; }
        Chamber<R> itemInput(SizedChanceItemIngredient value) { items.add(value); return this; }
        Chamber<R> fluidInput(SizedChanceFluidIngredient value) { fluids.add(value); return this; }
        Chamber<R> beamInput(ParticleIngredient value) { particles.add(value); return this; }
        Chamber<R> itemOutput(SizedChanceItemIngredient value) { itemProducts.add(value); return this; }
        Chamber<R> fluidOutput(SizedChanceFluidIngredient value) { fluidProducts.add(value); return this; }
        Chamber<R> beamOutput(ParticleBeam value) { particleProducts.add(value); return this; }
        Chamber<R> crossSection(double value) { crossSection = value; return this; }
        Chamber<R> energyReleased(long value) { energyReleased = value; return this; }
        Chamber<R> radiation(double value) { radiation = value; return this; }
        Chamber<R> heat(double value) { heat = value; return this; }
        Chamber<R> temperature(double value) { temperature = value; return this; }

        @Override
        public Item getResult() { return itemProducts.isEmpty() ? Items.AIR : itemProducts.getFirst().getStackRaw().getItem(); }

        @Override
        protected R build() {
            return factory.create(items, fluids, particles, itemProducts, fluidProducts, particleProducts,
                    crossSection, energyReleased, radiation, heat, temperature);
        }
    }

    static final class Accelerator<R extends AbstractAcceleratorRecipe> extends Builder<Accelerator<R>> {
        private final AbstractAcceleratorRecipe.Factory<R> factory;
        private Ingredient item = Ingredient.EMPTY;
        private int count = 1;
        private SizedChanceFluidIngredient fluid = SizedChanceFluidIngredient.EMPTY;
        private ParticleBeam particle = ParticleBeam.EMPTY;
        private final List<SizedChanceItemIngredient> items = new ArrayList<>();
        private final List<SizedChanceFluidIngredient> fluids = new ArrayList<>();
        private int heat, temperature = 293;
        private double time = 1;

        Accelerator(AbstractAcceleratorRecipe.Factory<R> factory) { this.factory = factory; }
        Accelerator<R> item(Ingredient value, int amount) { item = value; count = amount; return this; }
        Accelerator<R> fluid(SizedChanceFluidIngredient value) { fluid = value; return this; }
        Accelerator<R> particle(ParticleBeam value) { particle = value; return this; }
        Accelerator<R> itemOutput(SizedChanceItemIngredient value) { items.add(value); return this; }
        Accelerator<R> fluidOutput(SizedChanceFluidIngredient value) { fluids.add(value); return this; }
        Accelerator<R> heat(int value) { heat = value; return this; }
        Accelerator<R> temperature(int value) { temperature = value; return this; }
        Accelerator<R> time(double value) { time = value; return this; }

        @Override
        public Item getResult() { return items.isEmpty() ? Items.AIR : items.getFirst().getStackRaw().getItem(); }

        @Override
        protected R build() {
            return factory.create(item, count, fluid, particle, items, fluids, heat, temperature, time);
        }
    }

    @FunctionalInterface
    interface CollectorFactory {
        CollectorRecipe create(CollectorRecipe.Kind kind, WorldCondition world, FluidStack output, int priority);
    }

    static final class Collector extends Builder<Collector> {
        private final CollectorRecipe.Kind kind;
        private final CollectorFactory factory;
        private WorldCondition world = WorldCondition.ANY;
        private FluidStack output = FluidStack.EMPTY;
        private int priority;

        Collector(CollectorRecipe.Kind kind, CollectorFactory factory) { this.kind = kind; this.factory = factory; }
        Collector world(WorldCondition value) { world = value; return this; }
        Collector output(FluidStack value) { output = value; return this; }
        Collector priority(int value) { priority = value; return this; }

        @Override
        protected CollectorRecipe build() { return factory.create(kind, world, output, priority); }
    }

    @FunctionalInterface
    interface LiquefierFactory {
        LiquefierRecipe create(LiquefierRecipe.Kind kind, FluidIngredient ingredient, int amount, FluidStack output,
                               double heat, double energy, int inputTemperature, int outputTemperature, double pressureCoefficient);
    }

    static final class Liquefier extends Builder<Liquefier> {
        private final LiquefierRecipe.Kind kind;
        private final LiquefierFactory factory;
        private FluidIngredient ingredient = FluidIngredient.empty();
        private int amount = 1, inputTemperature, outputTemperature;
        private FluidStack output = FluidStack.EMPTY;
        private double heat, energy, pressure;

        Liquefier(LiquefierRecipe.Kind kind, LiquefierFactory factory) {
            this.kind = kind;
            this.factory = factory;
            pressure = kind == LiquefierRecipe.Kind.GAS ? 0.25 : 0;
        }
        Liquefier input(FluidIngredient value, int volume) { ingredient = value; amount = volume; return this; }
        Liquefier output(FluidStack value) { output = value; return this; }
        Liquefier heat(double value) { heat = value; return this; }
        Liquefier energy(double value) { energy = value; return this; }
        Liquefier temperatures(int input, int output) { inputTemperature = input; outputTemperature = output; return this; }
        Liquefier pressure(double value) { pressure = value; return this; }

        @Override
        protected LiquefierRecipe build() {
            return factory.create(kind, ingredient, amount, output, heat, energy, inputTemperature, outputTemperature, pressure);
        }
    }

    @FunctionalInterface
    interface IrradiatorFactory {
        IrradiatorRecipe create(IrradiatorRecipe.Kind kind, Ingredient ingredient, ItemStack output, double multiplier, double radiation);
    }

    static final class Irradiator extends Builder<Irradiator> {
        private final IrradiatorRecipe.Kind kind;
        private final IrradiatorFactory factory;
        private Ingredient ingredient = Ingredient.EMPTY;
        private ItemStack output = ItemStack.EMPTY;
        private double multiplier = 1, radiation;

        Irradiator(IrradiatorRecipe.Kind kind, IrradiatorFactory factory) { this.kind = kind; this.factory = factory; }
        Irradiator input(Ingredient value) { ingredient = value; return this; }
        Irradiator output(ItemStack value) { output = value; return this; }
        Irradiator multiplier(double value) { multiplier = value; return this; }
        Irradiator radiation(double value) { radiation = value; return this; }

        @Override
        public Item getResult() { return output.getItem(); }

        @Override
        protected IrradiatorRecipe build() { return factory.create(kind, ingredient, output, multiplier, radiation); }
    }

    @FunctionalInterface
    interface OreLeacherFactory {
        OreLeacherRecipe create(List<SizedChanceItemIngredient> items, List<SizedChanceItemIngredient> itemProducts,
                                List<SizedChanceFluidIngredient> fluids, List<SizedChanceFluidIngredient> fluidProducts,
                                double time, double power, double radiation);
    }

    static final class OreLeacher extends Builder<OreLeacher> {
        private final OreLeacherFactory factory;
        private final List<SizedChanceItemIngredient> items = new ArrayList<>();
        private final List<SizedChanceItemIngredient> itemProducts = new ArrayList<>();
        private final List<SizedChanceFluidIngredient> fluids = new ArrayList<>();
        private final List<SizedChanceFluidIngredient> fluidProducts = new ArrayList<>();
        private double time = 1, power = 1, radiation;

        OreLeacher(OreLeacherFactory factory) { this.factory = factory; }
        OreLeacher itemInput(SizedChanceItemIngredient value) { items.add(value); return this; }
        OreLeacher itemOutput(SizedChanceItemIngredient value) { itemProducts.add(value); return this; }
        OreLeacher fluidInput(SizedChanceFluidIngredient value) { fluids.add(value); return this; }
        OreLeacher fluidOutput(SizedChanceFluidIngredient value) { fluidProducts.add(value); return this; }
        OreLeacher time(double value) { time = value; return this; }
        OreLeacher power(double value) { power = value; return this; }
        OreLeacher radiation(double value) { radiation = value; return this; }

        @Override
        public Item getResult() { return itemProducts.isEmpty() ? Items.AIR : itemProducts.getFirst().getStackRaw().getItem(); }

        @Override
        protected OreLeacherRecipe build() { return factory.create(items, itemProducts, fluids, fluidProducts, time, power, radiation); }
    }

    private QmdRecipeBuilders() {}
}
