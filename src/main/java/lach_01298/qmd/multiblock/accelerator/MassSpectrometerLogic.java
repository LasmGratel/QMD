package lach_01298.qmd.multiblock.accelerator;

import lach_01298.qmd.config.AcceleratorConfig;
import lach_01298.qmd.physics.ParticlePhysics;
import static lach_01298.qmd.multiblock.accelerator.AcceleratorPartType.Role.*;

import lach_01298.qmd.recipe.accelerator.AbstractAcceleratorRecipe;
import lach_01298.qmd.recipe.accelerator.MassSpectrometerRecipe;
import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

public final class MassSpectrometerLogic extends AbstractAcceleratorLogic {

    @Override
    public void operate(Accelerator machine, double control) {
        MassSpectrometerRecipe recipe = null;
        for (var candidate : machine.recipes(QMDRecipes.MASS_SPECTROMETER_TYPE.get())) if (candidate.matches(
            new AbstractAcceleratorRecipe.Input(
                machine.controller.inventory.getStackInSlot(0),
                machine.processFluids[0].getFluid()),
            machine.getWorld())) {
                recipe = candidate;
                break;
            }
        if (recipe == null) {
            machine.work = 0;
            return;
        }
        var items = recipe.items();
        var fluids = recipe.fluids();
        for (int n = 0; n < items.size(); n++) {
            ItemStack present = machine.controller.inventory.getStackInSlot(n + 2), output = items.get(n);
            if (!output.isEmpty() && (!present.isEmpty() && !ItemStack.isSameItemSameComponents(present, output)
                || present.getCount() + output.getCount() > output.getMaxStackSize())) return;
        }
        for (int n = 0; n < fluids.size(); n++) if (!fluids.get(n)
            .isEmpty() && machine.processFluids[n + 1].fill(fluids.get(n), FluidAction.SIMULATE)
                != fluids.get(n)
                    .getAmount())
            return;
        if (!machine.energy.use(machine.requiredEnergy)) {
            machine.error = 2;
            return;
        }
        machine.operating = true;
        machine.heat = Math.min(machine.heatCapacity, ParticlePhysics.saturatedAdd(machine.heat, machine.rawHeating));
        machine.currentHeating += machine.rawHeating;
        machine.work += machine.parts(SOURCE)
            .stream()
            .mapToInt(
                p -> Accelerator.sourceSettings(p)
                    .multiplier()
                    .get())
            .sum();
        machine.recipeWork = AcceleratorConfig.SPECTROMETER_TIME.get() * recipe.time();
        if (machine.work < machine.recipeWork) return;
        for (int n = 0; n < items.size(); n++) if (!items.get(n)
            .isEmpty()) {
                ItemStack present = machine.controller.inventory.getStackInSlot(n + 2);
                ItemStack result = recipe.itemProducts()
                    .get(n)
                    .getStack();
                if (!result.isEmpty()) {
                    result.grow(present.getCount());
                    machine.controller.inventory.setStackInSlot(n + 2, result);
                }
            }
        for (int n = 0; n < fluids.size(); n++) machine.processFluids[n + 1].fill(
            recipe.fluidProducts()
                .get(n)
                .getStack(),
            FluidAction.EXECUTE);
        machine.controller.inventory.extractItem(
            0,
            recipe.item()
                .isEmpty() ? 0 : recipe.itemCount(),
            false);
        machine.processFluids[0].drain(
            recipe.fluid()
                .amount(),
            FluidAction.EXECUTE);
        machine.work = Math.max(0, machine.work - machine.recipeWork);
    }
}
