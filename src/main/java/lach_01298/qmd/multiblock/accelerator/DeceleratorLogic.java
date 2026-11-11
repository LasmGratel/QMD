package lach_01298.qmd.multiblock.accelerator;

import lach_01298.qmd.particle.ParticleStack;
import static lach_01298.qmd.multiblock.accelerator.AcceleratorPartType.Role.*;

public final class DeceleratorLogic extends AbstractRingAcceleratorLogic {

    @Override
    protected boolean losesFocus() {
        return false;
    }

    @Override
    protected void changeEnergy(Accelerator machine, ParticleStack input, ParticleStack output, double control,
        long maximum) {
        output.setMeanEnergy(Math.min(input.getMeanEnergy(), (long) (maximum * (1 - control))));
    }
}
