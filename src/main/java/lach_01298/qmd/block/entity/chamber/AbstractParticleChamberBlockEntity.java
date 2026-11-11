package lach_01298.qmd.block.entity.chamber;

import lach_01298.qmd.multiblock.ChamberReactions;
import lach_01298.qmd.particle.ParticleBeam;
import lach_01298.qmd.particle.ParticleStack;
import lach_01298.qmd.recipe.chamber.AbstractChamberRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Shared beam acquisition, energy use and output publication for particle chamber controllers. */
public abstract class AbstractParticleChamberBlockEntity extends AbstractChamberBlockEntity {

    protected AbstractParticleChamberBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    protected abstract RecipeType<? extends AbstractChamberRecipe> recipeType();

    protected abstract List<ParticleBeam> products(AbstractChamberRecipe recipe, List<ParticleStack> inputs,
        int length);

    protected long threshold(AbstractChamberRecipe recipe) {
        return ChamberReactions.threshold(recipe);
    }

    protected void processMatter(AbstractChamberRecipe recipe, List<ParticleStack> incoming) {}

    @Override
    protected boolean acceptsFluid(int index, FluidStack stack) {
        return level == null || acceptsRecipeFluid(recipeType(), 0, index == 0, false, stack);
    }

    @Override
    public final void processTick(List<AbstractChamberBlockEntity> inputs, List<AbstractChamberBlockEntity> outputs,
        int length) {
        if (level == null || level.isClientSide) return;
        var incoming = new ArrayList<ParticleStack>();
        inputs.forEach(port -> incoming.add(port.pullBeam()));
        setInputBeams(incoming);
        setOutputBeams(ChamberReactions.emptyProducts(role().kind.particleOutputs));
        boolean processing = false;
        if (enabled() && !level.hasNeighborSignal(worldPosition) && energy() >= requiredPower) {
            RecipeHolder<? extends AbstractChamberRecipe> holder = level.getRecipeManager()
                .getAllRecipesFor(recipeType())
                .stream()
                .sorted(
                    Comparator.comparing(
                        value -> value.id()
                            .toString()))
                .filter(
                    value -> value.value()
                        .matchesParticles(incoming) && matchesMatter(value.value()))
                .findFirst()
                .orElse(null);
            if (holder != null && canFit(holder.value())) {
                AbstractChamberRecipe recipe = holder.value();
                if (!holder.id()
                    .toString()
                    .equals(recipeId) || previousRecipe != null && previousRecipe != recipe) work = 0;
                recipeId = holder.id()
                    .toString();
                previousRecipe = recipe;
                requiredWork = threshold(recipe);
                consumeEnergy(requiredPower);
                processing = true;
                processMatter(recipe, incoming);
                setOutputBeams(products(recipe, recipe.orderedParticles(incoming), length));
                getRadiationSource().setRadiationLevel(recipe.radiation());
            }
        }
        if (!processing) getRadiationSource().setRadiationLevel(0);
        for (AbstractChamberBlockEntity port : outputs) port
            .publish(port.channel() < outputBeams().size() ? outputBeams().get(port.channel()) : ParticleBeam.EMPTY);
        setActive(processing);
        if (active()) setChanged();
        if (level.getGameTime() % 10 == 0) syncOpenMenus();
    }

    protected final boolean matchesMatter(AbstractChamberRecipe recipe) {
        if (!recipe.getItemIngredients()
            .isEmpty()) {
            var input = recipe.getItemIngredients()
                .getFirst();
            ItemStack stack = inventory().getStackInSlot(0);
            if (input.isEmpty() ? !stack.isEmpty() : !input.test(stack) || stack.getCount() < input.count())
                return false;
        }
        if (!recipe.getFluidIngredients()
            .isEmpty()) {
            var input = recipe.getFluidIngredients()
                .getFirst();
            FluidStack stack = tank(0).getFluid();
            if (input.isEmpty() ? !stack.isEmpty() : !input.test(stack) || stack.getAmount() < input.amount())
                return false;
        }
        return true;
    }

    protected final boolean canFit(AbstractChamberRecipe recipe) {
        if (!recipe.getItemProducts()
            .isEmpty()) {
            ItemStack output = recipe.getItemProducts()
                .getFirst()
                .getStackRaw();
            ItemStack present = inventory().getStackInSlot(1);
            if (!output.isEmpty() && (!present.isEmpty() && !ItemStack.isSameItemSameComponents(output, present)
                || (long) present.getCount() + output.getCount() > output.getMaxStackSize())) return false;
        }
        if (!recipe.getFluidProducts()
            .isEmpty()) {
            FluidStack output = recipe.getFluidProducts()
                .getFirst()
                .getStackRaw()
                .copy();
            if (!output.isEmpty() && tank(1).fill(output, IFluidHandler.FluidAction.SIMULATE) != output.getAmount())
                return false;
        }
        return true;
    }

    protected final void produceMatter(AbstractChamberRecipe recipe) {
        if (!recipe.getItemIngredients()
            .isEmpty())
            inventory().extractItem(
                0,
                recipe.getItemIngredients()
                    .getFirst()
                    .count(),
                false);
        if (!recipe.getFluidIngredients()
            .isEmpty())
            tank(0).drain(
                recipe.getFluidIngredients()
                    .getFirst()
                    .amount(),
                IFluidHandler.FluidAction.EXECUTE);
        if (!recipe.getItemProducts()
            .isEmpty()) {
            ItemStack product = recipe.getItemProducts()
                .getFirst()
                .getStack();
            if (!product.isEmpty()) {
                ItemStack present = inventory().getStackInSlot(1);
                inventory().setStackInSlot(
                    1,
                    present.isEmpty() ? product.copy()
                        : present.copyWithCount(present.getCount() + product.getCount()));
            }
        }
        if (!recipe.getFluidProducts()
            .isEmpty())
            tank(1).fill(
                recipe.getFluidProducts()
                    .getFirst()
                    .getStack()
                    .copy(),
                IFluidHandler.FluidAction.EXECUTE);
    }

}
