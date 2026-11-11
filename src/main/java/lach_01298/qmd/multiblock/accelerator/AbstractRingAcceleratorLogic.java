package lach_01298.qmd.multiblock.accelerator;

import lach_01298.qmd.block.entity.accelerator.AbstractAcceleratorBlockEntity;
import lach_01298.qmd.config.AcceleratorConfig;
import lach_01298.qmd.particle.ParticleStack;
import static lach_01298.qmd.multiblock.accelerator.AcceleratorPartType.Role.*;

public abstract class AbstractRingAcceleratorLogic extends AbstractAcceleratorLogic {

    @Override
    protected boolean gainsFocus() {
        return true;
    }

    @Override
    protected boolean acceptsInput(Accelerator machine, ParticleStack input, AbstractAcceleratorBlockEntity port,
        long maximum) {
        if (input.getMeanEnergy() < AcceleratorConfig.RING_MIN_ENERGY.get()) {
            machine.error = 4;
            return false;
        }
        if (input.getMeanEnergy() > maximum) {
            machine.error = 5;
            return false;
        }
        return true;
    }
}
