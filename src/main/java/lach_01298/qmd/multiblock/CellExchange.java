package lach_01298.qmd.multiblock;

import lach_01298.qmd.block.entity.chamber.AbstractChamberBlockEntity;
import lach_01298.qmd.item.ParticleCellItem;
import lach_01298.qmd.item.ParticleReservoirItem;
import lach_01298.qmd.particle.CellFluid;
import lach_01298.qmd.recipe.chamber.CellFillingRecipe;
import lach_01298.qmd.registry.QMDDataComponents;
import lach_01298.qmd.registry.QMDItems;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

/** Transactional live-recipe exchange, with exact partial-cell arithmetic for reservoir products. */
public final class CellExchange {

    public static boolean process(AbstractChamberBlockEntity machine, CellFillingRecipe recipe) {
        ItemStack input = machine.inventory()
            .getStackInSlot(0),
            present = machine.inventory()
                .getStackInSlot(1);
        var itemIn = recipe.getItemIngredients()
            .getFirst();
        var fluidIn = recipe.getFluidIngredients()
            .getFirst();
        var itemOut = recipe.getItemProducts()
            .getFirst();
        var fluidOut = recipe.getFluidProducts()
            .getFirst();
        if (itemIn.isEmpty() ? !input.isEmpty() : !itemIn.test(input) || input.getCount() < itemIn.count())
            return false;
        FluidStack fluid = machine.tank(2)
            .getFluid();
        ItemStack maximumItem = itemOut.getStackRaw();
        FluidStack maximumFluid = fluidOut.getStackRaw();
        if (!fits(present, maximumItem)) return false;
        // A recipe describing a cell and fluid ratio also describes partial refills. Integer quanta retain every
        // particle.
        if (itemIn.count() == 1 && maximumItem.getCount() == 1
            && itemOut.chancePercent() == 100
            && maximumFluid.isEmpty()
            && maximumItem.getItem() instanceof ParticleCellItem cell
            && !fluidIn.isEmpty()
            && fluidIn.test(fluid)
            && (input.is(QMDItems.EMPTY_CELL.get()) || input.getItem() == cell)
            && !maximumItem.has(QMDDataComponents.PARTICLE_AMOUNT.get())) {
            int stored = input.is(QMDItems.EMPTY_CELL.get()) ? 0 : ParticleReservoirItem.stored(input);
            int capacity = cell.capacity(), divisor = gcd(capacity, fluidIn.amount());
            int fluidQuantum = fluidIn.amount() / divisor, particleQuantum = capacity / divisor;
            int quanta = Math.min((capacity - stored) / particleQuantum, fluid.getAmount() / fluidQuantum);
            if (quanta <= 0) return false;
            ItemStack product = input.transmuteCopy(cell, 1);
            product.applyComponents(maximumItem.getComponentsPatch());
            ParticleReservoirItem.setStored(product, stored + quanta * particleQuantum);
            if (fluid.getFluid() == lach_01298.qmd.registry.QMDFluids.EXOTIC.get(cell.species()).still.get())
                product.set(QMDDataComponents.CELL_FLUID, CellFluid.of(fluid));
            else product.remove(QMDDataComponents.CELL_FLUID);
            if (!fits(present, product)) return false;
            machine.tank(2)
                .drain(quanta * fluidQuantum, IFluidHandler.FluidAction.EXECUTE);
            machine.inventory()
                .extractItem(0, 1, false);
            put(machine, present, product);
            return true;
        }
        if (!fluidIn.isEmpty() && (!fluidIn.test(fluid) || fluid.getAmount() < fluidIn.amount())) return false;
        if (fluidIn.isEmpty() && input.getItem() instanceof ParticleCellItem cell
            && maximumItem.is(QMDItems.EMPTY_CELL.get())
            && ParticleReservoirItem.stored(input) < cell.capacity()) return false;
        FluidTank staged = new FluidTank(
            machine.tank(2)
                .getCapacity());
        staged.setFluid(fluid.copy());
        if (!fluidIn.isEmpty()) staged.drain(fluidIn.amount(), IFluidHandler.FluidAction.EXECUTE);
        if (!maximumFluid.isEmpty()
            && staged.fill(maximumFluid, IFluidHandler.FluidAction.SIMULATE) != maximumFluid.getAmount()) return false;
        ItemStack product = itemOut.getStack();
        FluidStack fluidProduct = fluidOut.getStack()
            .copy();
        if (!fluidProduct.isEmpty()) staged.fill(fluidProduct, IFluidHandler.FluidAction.EXECUTE);
        machine.inventory()
            .extractItem(0, itemIn.count(), false);
        machine.tank(2)
            .setFluid(
                staged.getFluid()
                    .copy());
        if (!product.isEmpty()) put(machine, present, product);
        machine.setChanged();
        return true;
    }

    private static boolean fits(ItemStack present, ItemStack product) {
        return product.isEmpty() || ((present.isEmpty() || ItemStack.isSameItemSameComponents(present, product))
            && (long) present.getCount() + product.getCount() <= product.getMaxStackSize());
    }

    private static void put(AbstractChamberBlockEntity machine, ItemStack present, ItemStack product) {
        machine.inventory()
            .setStackInSlot(
                1,
                present.isEmpty() ? product.copy() : present.copyWithCount(present.getCount() + product.getCount()));
    }

    private static int gcd(int a, int b) {
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return a;
    }

    private CellExchange() {}
}
