package lach_01298.qmd.capability;

import lach_01298.qmd.item.ParticleCellItem;
import lach_01298.qmd.item.ParticleReservoirItem;
import lach_01298.qmd.particle.CellFluid;
import lach_01298.qmd.registry.QMDDataComponents;
import lach_01298.qmd.registry.QMDFluids;
import lach_01298.qmd.registry.QMDItems;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

/** Exact exchange at 1 mB = 1,000 particles, including sub-mB particle remainders. */
public final class ParticleCellFluidHandler implements IFluidHandlerItem {

    public static final int PARTICLES_PER_MB = 1000;
    private ItemStack container;

    public ParticleCellFluidHandler(ItemStack container) {
        this.container = container;
    }

    @Override
    public ItemStack getContainer() {
        return container;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public int getTankCapacity(int tank) {
        return tank == 0 ? 100 : 0;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        if (tank != 0 || !(container.getItem() instanceof ParticleCellItem cell)) return FluidStack.EMPTY;
        int amount = ParticleReservoirItem.stored(container) / PARTICLES_PER_MB;
        if (amount == 0) return FluidStack.EMPTY;
        CellFluid template = container.get(QMDDataComponents.CELL_FLUID);
        var fluid = QMDFluids.EXOTIC.get(cell.species()).still.get();
        return template != null && template.fluid()
            .value() == fluid ? template.stack(amount) : new FluidStack(fluid, amount);
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack fluid) {
        if (tank != 0 || fluid.isEmpty()) return false;
        if (container.getItem() instanceof ParticleCellItem cell) {
            if (fluid.getFluid() != QMDFluids.EXOTIC.get(cell.species()).still.get()) return false;
            CellFluid template = container.get(QMDDataComponents.CELL_FLUID);
            return template == null ? fluid.getComponentsPatch()
                .isEmpty() : template.equals(CellFluid.of(fluid));
        }
        return container.is(QMDItems.EMPTY_CELL.get()) && QMDFluids.EXOTIC.values()
            .stream()
            .anyMatch(entry -> entry.still.get() == fluid.getFluid());
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (container.getCount() != 1 || !isFluidValid(0, resource)) return 0;
        int stored = ParticleReservoirItem.stored(container);
        int fill = Math.min(resource.getAmount(), (100_000 - stored) / PARTICLES_PER_MB);
        if (fill <= 0) return 0;
        if (action.execute()) {
            if (container.is(QMDItems.EMPTY_CELL.get())) {
                String species = QMDFluids.EXOTIC.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().still.get() == resource.getFluid())
                    .findFirst()
                    .orElseThrow()
                    .getKey();
                container = container.transmuteCopy(
                    QMDItems.PARTICLE_CELLS.get(species)
                        .get(),
                    1);
            }
            ParticleReservoirItem.setStored(container, stored + fill * PARTICLES_PER_MB);
            container.set(QMDDataComponents.CELL_FLUID, CellFluid.of(resource));
        }
        return fill;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        FluidStack fluid = getFluidInTank(0);
        return FluidStack.isSameFluidSameComponents(fluid, resource) ? drain(resource.getAmount(), action)
            : FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (container.getCount() != 1 || maxDrain <= 0) return FluidStack.EMPTY;
        FluidStack fluid = getFluidInTank(0);
        if (fluid.isEmpty()) return FluidStack.EMPTY;
        int drain = Math.min(maxDrain, fluid.getAmount());
        if (action.execute()) {
            int remaining = ParticleReservoirItem.stored(container) - drain * PARTICLES_PER_MB;
            if (remaining == 0) {
                ParticleReservoirItem.setStored(container, 0);
                container = container.transmuteCopy(QMDItems.EMPTY_CELL.get(), 1);
                container.remove(QMDDataComponents.PARTICLE_AMOUNT);
                container.remove(QMDDataComponents.CELL_FLUID);
            } else ParticleReservoirItem.setStored(container, remaining);
        }
        return fluid.copyWithAmount(drain);
    }
}
