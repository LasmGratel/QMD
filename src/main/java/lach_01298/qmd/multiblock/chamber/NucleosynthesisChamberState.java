package lach_01298.qmd.multiblock.chamber;

import lach_01298.qmd.block.entity.chamber.AbstractChamberBlockEntity;
import lach_01298.qmd.config.ChamberConfig;
import lach_01298.qmd.config.VacuumConfig;
import lach_01298.qmd.multiblock.ChamberPart;
import lach_01298.qmd.particle.ParticleStack;
import lach_01298.qmd.physics.ParticlePhysics;
import lach_01298.qmd.recipe.chamber.AbstractChamberRecipe;
import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

/** Fuel-fed plasma synthesis with a separately heated and cooled casing. */
public final class NucleosynthesisChamberState extends AbstractVacuumChamberState {

    public NucleosynthesisChamberState(AbstractChamberBlockEntity owner) {
        super(owner);
    }

    @Override
    public int tankCount() {
        return 8;
    }

    @Override
    public boolean inputTank(int index) {
        return index == 0 || index == 2 || index == 4 || index == 5;
    }

    @Override
    public boolean neutral() {
        return false;
    }

    @Override
    protected RecipeType<? extends AbstractChamberRecipe> recipeType() {
        return QMDRecipes.NUCLEOSYNTHESIS_CHAMBER_TYPE.get();
    }

    @Override
    protected boolean hasFuel() {
        return !owner.tank(4)
            .isEmpty()
            || !owner.tank(5)
                .isEmpty();
    }

    @Override
    protected boolean failsWhenBroken() {
        return plasmaOn;
    }

    @Override
    protected boolean failsWhenInactive(boolean wasActive, boolean hasFuel) {
        return hasFuel && plasmaOn;
    }

    @Override
    protected void prepareProcessing() {
        setPlasma(true);
    }

    @Override
    protected void accumulateWork(AbstractChamberRecipe recipe, List<ParticleStack> ordered) {
        required0 = ChamberConfig.scaledWork(
            recipe.kind(),
            recipe.particleInputs()
                .getFirst()
                .amount());
        required1 = 0;
        int incoming = ordered.getFirst() == null ? 0
            : ordered.getFirst()
                .getAmount();
        work0 = ParticlePhysics.saturatedAdd(work0, incoming);
        casingHeating = required0 == 0 ? 0 : recipe.heat() * incoming / required0;
        heatRemainder += casingHeating;
        long whole = (long) Math.min(Long.MAX_VALUE, heatRemainder);
        heatRemainder -= whole;
        casingHeat = Math.min(casingCapacity, ParticlePhysics.saturatedAdd(casingHeat, whole));
    }

    @Override
    protected int outputTank(int index) {
        return 6 + index;
    }

    @Override
    protected void consumeInputs(AbstractChamberRecipe recipe) {
        int order = fluidOrder(recipe);
        for (int i = 0; i < 2; i++) owner.tank(4 + (i ^ order))
            .drain(
                recipe.getFluidIngredients()
                    .get(i)
                    .amount(),
                IFluidHandler.FluidAction.EXECUTE);
    }

    @Override
    protected void finishCooling() {
        coolCasing();
        if (casingHeat >= casingCapacity) failure();
    }

    @Override
    protected int fluidOrder(AbstractChamberRecipe recipe) {
        for (int order = 0; order < 2; order++) {
            boolean match = true;
            for (int i = 0; i < 2; i++) {
                var ingredient = recipe.getFluidIngredients()
                    .get(i);
                var fluid = owner.tank(4 + (i ^ order))
                    .getFluid();
                if (ingredient.isEmpty() ? !fluid.isEmpty()
                    : !ingredient.test(fluid) || fluid.getAmount() < ingredient.amount()) match = false;
            }
            if (match) return order;
        }
        return -1;
    }

    private void coolCasing() {
        if (casingCooling <= 0 || casingHeat <= 0) return;
        for (var holder : owner.getLevel()
            .getRecipeManager()
            .getAllRecipesFor(QMDRecipes.VACUUM_CHAMBER_HEATING_TYPE.get())) {
            var r = holder.value();
            var in = r.getFluidIngredients()
                .getFirst();
            var out = r.getFluidProducts()
                .getFirst()
                .getStackRaw();
            if (r.heat() <= 0 || in.isEmpty()
                || out.isEmpty()
                || !in.test(
                    owner.tank(2)
                        .getFluid()))
                continue;
            double efficiency = Math
                .min(1, (casingHeating + 20000D) / (casingHeating + Math.abs(casingHeating - casingCooling)));
            if (!Double.isFinite(efficiency) || efficiency <= 0) efficiency = 1;
            casingRemainder += Math.min(casingCooling * efficiency / r.heat(), casingHeat * efficiency / r.heat());
            int cycles = (int) Math.min(Integer.MAX_VALUE, Math.floor(casingRemainder));
            cycles = Math.min(
                cycles,
                owner.tank(2)
                    .getFluidAmount() / in.amount());
            cycles = Math.min(
                cycles,
                owner.tank(3)
                    .fill(out.copyWithAmount(Integer.MAX_VALUE), IFluidHandler.FluidAction.SIMULATE) / out.getAmount());
            if (cycles > 0) {
                owner.tank(2)
                    .drain(cycles * in.amount(), IFluidHandler.FluidAction.EXECUTE);
                owner.tank(3)
                    .fill(out.copyWithAmount(cycles * out.getAmount()), IFluidHandler.FluidAction.EXECUTE);
                casingHeat = Math.max(0, casingHeat - (long) (cycles * r.heat() / efficiency));
                casingRemainder -= cycles;
            }
            casingRemainder = Math.min(1, casingRemainder);
            return;
        }
    }

    @Override
    protected void failure() {
        var level = owner.getLevel();
        if (level == null || level.isClientSide) return;
        boolean hadPlasma = plasmaOn;
        plasmaOn = false;
        owner.tank(4)
            .setFluid(FluidStack.EMPTY);
        owner.tank(5)
            .setFluid(FluidStack.EMPTY);
        casingHeat = 0;
        if (hadPlasma && VacuumConfig.NUCLEOSYNTHESIS_EXPLOSIONS.get()) {
            var parts = owner.getMultiblockController()
                .map(
                    m -> m.getParts(AbstractChamberBlockEntity.class)
                        .stream()
                        .filter(
                            p -> p.role() == ChamberPart.VACUUM_BEAM || p.role() == ChamberPart.PLASMA_GLASS
                                || p.role() == ChamberPart.PLASMA_NOZZLE)
                        .map(AbstractChamberBlockEntity::getBlockPos)
                        .toList())
                .orElse(List.of());
            var mutable = new ArrayList<>(parts);
            int count = 1 + level.random.nextInt(1 + mutable.size() / 8);
            while (count-- > 0 && !mutable.isEmpty()) {
                BlockPos pos = mutable.remove(level.random.nextInt(mutable.size()));
                level.explode(
                    null,
                    pos.getX() + .5,
                    pos.getY() + .5,
                    pos.getZ() + .5,
                    4,
                    Level.ExplosionInteraction.BLOCK);
                level.setBlock(
                    pos,
                    com.nred.nuclearcraft.registration.FluidRegistration.CUSTOM_FLUID_MAP.get("plasma").still.get()
                        .defaultFluidState()
                        .createLegacyBlock(),
                    3);
            }
        } else level.destroyBlock(owner.getBlockPos(), false);
        owner.setActive(false);
        owner.setChanged();
    }
}
