package lach_01298.qmd.multiblock.chamber;

import lach_01298.qmd.block.entity.chamber.AbstractChamberBlockEntity;
import lach_01298.qmd.config.VacuumConfig;
import lach_01298.qmd.entity.GammaFlashEntity;
import lach_01298.qmd.multiblock.CellExchange;
import lach_01298.qmd.particle.ParticleStack;
import lach_01298.qmd.physics.ParticlePhysics;
import lach_01298.qmd.recipe.chamber.AbstractChamberRecipe;
import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Comparator;
import java.util.List;

/** Two-beam neutral material containment and inventory cell filling. */
public final class NeutralContainmentState extends AbstractVacuumChamberState {

    public NeutralContainmentState(AbstractChamberBlockEntity owner) {
        super(owner);
    }

    @Override
    public int tankCount() {
        return 3;
    }

    @Override
    public boolean inputTank(int index) {
        return index == 0;
    }

    @Override
    public boolean neutral() {
        return true;
    }

    @Override
    protected RecipeType<? extends AbstractChamberRecipe> recipeType() {
        return QMDRecipes.NEUTRAL_CONTAINMENT_TYPE.get();
    }

    @Override
    protected boolean hasFuel() {
        return true;
    }

    @Override
    protected boolean failsWhenBroken() {
        return !owner.tank(2)
            .isEmpty();
    }

    @Override
    protected boolean failsWhenInactive(boolean wasActive, boolean hasFuel) {
        return wasActive;
    }

    @Override
    protected void prepareProcessing() {
        processCell();
    }

    @Override
    protected void accumulateWork(AbstractChamberRecipe recipe, List<ParticleStack> ordered) {
        required0 = recipe.particleInputs()
            .getFirst()
            .amount();
        required1 = recipe.particleInputs()
            .get(1)
            .amount();
        work0 = ParticlePhysics.saturatedAdd(
            work0,
            ordered.getFirst() == null ? 0
                : ordered.getFirst()
                    .getAmount());
        work1 = ParticlePhysics.saturatedAdd(
            work1,
            ordered.get(1) == null ? 0
                : ordered.get(1)
                    .getAmount());
    }

    @Override
    protected int fluidOrder(AbstractChamberRecipe recipe) {
        return 0;
    }

    @Override
    protected int outputTank(int index) {
        return 2;
    }

    private void processCell() {
        for (var holder : owner.getLevel()
            .getRecipeManager()
            .getAllRecipesFor(QMDRecipes.CELL_FILLING_TYPE.get())
            .stream()
            .sorted(
                Comparator.comparing(
                    h -> h.id()
                        .toString()))
            .toList()) if (CellExchange.process(owner, holder.value())) return;
    }

    @Override
    protected void failure() {
        var level = owner.getLevel();
        if (level == null || level.isClientSide) return;
        plasmaOn = false;
        FluidStack fluid = owner.tank(2)
            .getFluid()
            .copy();
        owner.tank(2)
            .setFluid(FluidStack.EMPTY);
        if (fluid.isEmpty()) return;
        String name = net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(fluid.getFluid())
            .getPath();
        double mass = switch (name) {
            case "antideuterium" -> 2;
            case "antitritium", "antihelium3" -> 3;
            case "antihelium" -> 4;
            case "positronium" -> .00054;
            case "muonium" -> .11;
            case "tauonium" -> 1.9;
            case "glueballs" -> 1.8;
            default -> 1;
        };
        double size = mass * fluid.getAmount() / 1000D;
        if (VacuumConfig.GAMMA_FLASH.get()) GammaFlashEntity.create(
            level,
            owner.getBlockPos()
                .getCenter(),
            size,
            VacuumConfig.EXPLOSIONS.get() ? (float) (size * VacuumConfig.EXPLOSION_SIZE.get()) : 0,
            size * VacuumConfig.RADIATION.get());
        else if (VacuumConfig.EXPLOSIONS.get()) level.explode(
            null,
            owner.getBlockPos()
                .getX() + .5,
            owner.getBlockPos()
                .getY() + .5,
            owner.getBlockPos()
                .getZ() + .5,
            (float) (size * VacuumConfig.EXPLOSION_SIZE.get()),
            Level.ExplosionInteraction.BLOCK);
        if (!VacuumConfig.EXPLOSIONS.get()) level.destroyBlock(owner.getBlockPos(), false);
        owner.setActive(false);
        owner.setChanged();
    }
}
