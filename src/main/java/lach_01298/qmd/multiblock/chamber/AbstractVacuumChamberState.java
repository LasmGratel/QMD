package lach_01298.qmd.multiblock.chamber;

import lach_01298.qmd.block.entity.chamber.AbstractChamberBlockEntity;
import lach_01298.qmd.config.AcceleratorConfig;
import lach_01298.qmd.particle.ParticleStack;
import lach_01298.qmd.physics.ParticlePhysics;
import lach_01298.qmd.recipe.chamber.AbstractChamberRecipe;
import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.*;

/** Persistent cryogenic containment, cell exchange and hot-casing nucleosynthesis simulation. */
public abstract class AbstractVacuumChamberState {

    protected final AbstractChamberBlockEntity owner;
    protected long heatCapacity = 1, heat, heating, casingCapacity = 1, casingHeat, casingCooling;
    protected long work0, work1, required0 = 1, required1;
    protected int maximumTemperature = 110, surface;
    protected boolean initialized, plasmaOn;
    protected double coolingRemainder, casingRemainder, heatRemainder, casingHeating;
    protected String recipeId = "";
    protected AbstractChamberRecipe previousRecipe;
    protected List<BlockPos> plasma = List.of();

    protected AbstractVacuumChamberState(AbstractChamberBlockEntity owner) {
        this.owner = owner;
    }

    public abstract int tankCount();

    public abstract boolean inputTank(int index);

    public abstract boolean neutral();

    protected abstract RecipeType<? extends AbstractChamberRecipe> recipeType();

    protected abstract boolean hasFuel();

    protected abstract boolean failsWhenBroken();

    protected abstract boolean failsWhenInactive(boolean wasActive, boolean hasFuel);

    protected abstract void prepareProcessing();

    protected abstract void accumulateWork(AbstractChamberRecipe recipe, List<ParticleStack> ordered);

    protected abstract int fluidOrder(AbstractChamberRecipe recipe);

    protected abstract int outputTank(int index);

    protected void consumeInputs(AbstractChamberRecipe recipe) {}

    protected void finishCooling() {}

    protected abstract void failure();

    public double temperature() {
        return 400D * heat / Math.max(1, heatCapacity);
    }

    public double casingTemperature() {
        return 293D + 2400D * casingHeat / Math.max(1, casingCapacity);
    }

    public long work(int index) {
        return index == 0 ? work0 : work1;
    }

    public void clearMaterial() {
        work0 = 0;
        work1 = 0;
        recipeId = "";
    }

    public long required(int index) {
        return index == 0 ? required0 : required1;
    }

    public long heating() {
        return heating;
    }

    public long casingCooling() {
        return casingCooling;
    }

    public boolean plasmaOn() {
        return plasmaOn;
    }

    public void formed(int volume, long heating, long cooling, int maximum, List<BlockPos> plasma) {
        this.heating = heating;
        this.casingCooling = cooling;
        this.maximumTemperature = maximum;
        heatCapacity = Math.max(1, (long) AcceleratorConfig.HEAT_CAPACITY.get() * volume);
        casingCapacity = heatCapacity > Long.MAX_VALUE / 1000 ? Long.MAX_VALUE : heatCapacity * 1000;
        if (!initialized) {
            heat = heatCapacity * 293 / 400;
            initialized = true;
        }
        heat = Math.min(heat, heatCapacity);
        this.plasma = List.copyOf(plasma);
        var machine = owner.getMultiblockController()
            .orElse(null);
        surface = machine == null ? 0
            : 2 * (machine.getExteriorLengthX() * machine.getExteriorLengthY()
                + machine.getExteriorLengthY() * machine.getExteriorLengthZ()
                + machine.getExteriorLengthZ() * machine.getExteriorLengthX());
    }

    public void broken() {
        if (owner.getLevel() == null || owner.getLevel().isClientSide) return;
        if (failsWhenBroken()) failure();
        setPlasma(false);
    }

    public final void tick(List<AbstractChamberBlockEntity> inputs) {
        Level level = owner.getLevel();
        if (level == null || level.isClientSide) return;
        List<ParticleStack> incoming = new ArrayList<>();
        inputs.forEach(port -> incoming.add(port.pullBeam()));
        owner.setInputBeams(incoming);
        boolean wasActive = owner.active();
        boolean hasFuel = hasFuel();
        boolean powered = owner.enabled() && !level.hasNeighborSignal(owner.getBlockPos())
            && owner.energy() >= owner.requiredPower
            && hasFuel;
        if (powered) {
            owner.consumeEnergy(owner.requiredPower);
            heat = Math.min(heatCapacity, ParticlePhysics.saturatedAdd(heat, heating));
        }
        boolean operational = powered && temperature() <= maximumTemperature;
        owner.setActive(operational);
        if (!hasFuel) setPlasma(false);
        if (!operational && failsWhenInactive(wasActive, hasFuel)) failure();
        if (operational) {
            prepareProcessing();
            RecipeHolder<? extends AbstractChamberRecipe> holder = level.getRecipeManager()
                .getAllRecipesFor(recipeType())
                .stream()
                .sorted(
                    Comparator.comparing(
                        value -> value.id()
                            .toString()))
                .filter(
                    value -> value.value()
                        .matchesParticles(incoming) && fluidOrder(value.value()) >= 0)
                .findFirst()
                .orElse(null);
            if (holder != null && canFit(holder.value())) {
                var recipe = holder.value();
                if (!recipeId.equals(
                    holder.id()
                        .toString())
                    || previousRecipe != null && previousRecipe != recipe) work0 = work1 = 0;
                recipeId = holder.id()
                    .toString();
                previousRecipe = recipe;
                accumulateWork(recipe, recipe.orderedParticles(incoming));
                int cycles = 0;
                while (required0 > 0 && work0 >= required0
                    && work1 >= required1
                    && fluidOrder(recipe) >= 0
                    && canFit(recipe)
                    && cycles++ < 10000) {
                    produce(recipe);
                    work0 -= required0;
                    work1 -= required1;
                }
                // Retain at most one output buffer's worth of work when automation blocks.
                work0 = Math.min(work0, required0 > Long.MAX_VALUE / 10000 ? Long.MAX_VALUE : required0 * 10000);
                work1 = Math.min(work1, required1 > Long.MAX_VALUE / 10000 ? Long.MAX_VALUE : required1 * 10000);
                owner.getRadiationSource()
                    .setRadiationLevel(recipe.radiation());
            } else {
                casingHeating = 0;
                owner.getRadiationSource()
                    .setRadiationLevel(0);
            }
        }
        if (!hasFuel) setPlasma(false);
        externalHeat();
        coolApparatus();
        finishCooling();
        owner.updateRedstone();
        owner.setChanged();
        if (level.getGameTime() % 10 == 0) {
            owner.syncOpenMenus();
            owner.syncVisuals();
        }
    }

    private void externalHeat() {
        double change = (293 - temperature()) * AcceleratorConfig.CONDUCTIVITY.get() * surface;
        heat = Math.clamp(heat + (long) change, 0, heatCapacity);
        if (casingHeating == 0) casingHeat = Math
            .max(0, casingHeat + (long) ((293 - casingTemperature()) * AcceleratorConfig.CONDUCTIVITY.get() * surface));
    }

    private void coolApparatus() {
        var level = owner.getLevel();
        for (var holder : level.getRecipeManager()
            .getAllRecipesFor(QMDRecipes.ACCELERATOR_COOLING_TYPE.get())) {
            var recipe = holder.value();
            FluidStack input = recipe.fluid();
            if (input.isEmpty() || recipe.heat() <= 0
                || recipe.fluids()
                    .isEmpty()
                || temperature() <= recipe.temperature()
                || !FluidStack.isSameFluidSameComponents(
                    input,
                    owner.tank(0)
                        .getFluid()))
                continue;
            FluidStack output = recipe.fluids()
                .getFirst();
            if (output.isEmpty()) continue;
            double available = Math.min(
                2D * heating / recipe.heat(),
                Math.max(0, (heat - heatCapacity * recipe.temperature() / 400D) / recipe.heat()));
            coolingRemainder += available;
            int cycles = (int) Math.min(Integer.MAX_VALUE, Math.floor(coolingRemainder));
            cycles = Math.min(
                cycles,
                owner.tank(0)
                    .getFluidAmount() / input.getAmount());
            cycles = Math.min(
                cycles,
                owner.tank(1)
                    .fill(output.copyWithAmount(Integer.MAX_VALUE), IFluidHandler.FluidAction.SIMULATE)
                    / output.getAmount());
            if (cycles > 0) {
                owner.tank(0)
                    .drain(cycles * input.getAmount(), IFluidHandler.FluidAction.EXECUTE);
                owner.tank(1)
                    .fill(output.copyWithAmount(cycles * output.getAmount()), IFluidHandler.FluidAction.EXECUTE);
                heat = Math.max(0, heat - (long) cycles * recipe.heat());
                coolingRemainder -= cycles;
            }
            coolingRemainder = Math.min(1, coolingRemainder);
            return;
        }
    }

    private boolean canFit(AbstractChamberRecipe recipe) {
        for (int i = 0; i < recipe.getFluidProducts()
            .size(); i++) {
            FluidStack output = recipe.getFluidProducts()
                .get(i)
                .getStackRaw();
            if (!output.isEmpty() && owner.tank(outputTank(i))
                .fill(output, IFluidHandler.FluidAction.SIMULATE) != output.getAmount()) return false;
        }
        return true;
    }

    private void produce(AbstractChamberRecipe recipe) {
        consumeInputs(recipe);
        for (int i = 0; i < recipe.getFluidProducts()
            .size(); i++)
            owner.tank(outputTank(i))
                .fill(
                    recipe.getFluidProducts()
                        .get(i)
                        .getStack()
                        .copy(),
                    IFluidHandler.FluidAction.EXECUTE);
    }

    protected final void setPlasma(boolean active) {
        if (plasmaOn == active) return;
        plasmaOn = active;
        var state = active
            ? com.nred.nuclearcraft.registration.FluidRegistration.CUSTOM_FLUID_MAP.get("plasma").still.get()
                .defaultFluidState()
                .createLegacyBlock()
            : Blocks.AIR.defaultBlockState();
        for (BlockPos p : plasma) if (owner.getLevel()
            .hasChunkAt(p))
            owner.getLevel()
                .setBlock(p, state, 3);
    }

    public CompoundTag save() {
        var tag = new CompoundTag();
        tag.putLong("heat", heat);
        tag.putLong("heatCapacity", heatCapacity);
        tag.putLong("casingHeat", casingHeat);
        tag.putLong("casingCapacity", casingCapacity);
        tag.putLong("heating", heating);
        tag.putLong("casingCooling", casingCooling);
        tag.putDouble("casingHeating", casingHeating);
        tag.putDouble("coolingRemainder", coolingRemainder);
        tag.putDouble("casingRemainder", casingRemainder);
        tag.putDouble("heatRemainder", heatRemainder);
        tag.putLong("work0", work0);
        tag.putLong("work1", work1);
        tag.putLong("required0", required0);
        tag.putLong("required1", required1);
        tag.putBoolean("initialized", initialized);
        tag.putBoolean("plasma", plasmaOn);
        tag.putString("recipe", recipeId);
        tag.putInt("maximumTemperature", maximumTemperature);
        tag.putInt("surface", surface);
        tag.putLongArray(
            "plasmaPositions",
            plasma.stream()
                .mapToLong(BlockPos::asLong)
                .toArray());
        return tag;
    }

    public void load(CompoundTag tag) {
        heatCapacity = Math.max(1, tag.getLong("heatCapacity"));
        heat = Math.clamp(tag.getLong("heat"), 0, heatCapacity);
        casingCapacity = Math.max(1, tag.getLong("casingCapacity"));
        casingHeat = Math.clamp(tag.getLong("casingHeat"), 0, casingCapacity);
        heating = Math.max(0, tag.getLong("heating"));
        casingCooling = Math.max(0, tag.getLong("casingCooling"));
        casingHeating = finite(tag.getDouble("casingHeating"));
        coolingRemainder = finite(tag.getDouble("coolingRemainder"));
        casingRemainder = finite(tag.getDouble("casingRemainder"));
        heatRemainder = finite(tag.getDouble("heatRemainder"));
        work0 = Math.max(0, tag.getLong("work0"));
        work1 = Math.max(0, tag.getLong("work1"));
        required0 = Math.max(1, tag.getLong("required0"));
        required1 = Math.max(0, tag.getLong("required1"));
        initialized = tag.getBoolean("initialized");
        plasmaOn = tag.getBoolean("plasma");
        recipeId = tag.getString("recipe");
        maximumTemperature = Math.clamp(tag.getInt("maximumTemperature"), 0, 400);
        surface = Math.max(0, tag.getInt("surface"));
        plasma = Arrays.stream(tag.getLongArray("plasmaPositions"))
            .mapToObj(BlockPos::of)
            .limit(5)
            .toList();
    }

    private static double finite(double value) {
        return Double.isFinite(value) && value >= 0 ? value : 0;
    }
}
