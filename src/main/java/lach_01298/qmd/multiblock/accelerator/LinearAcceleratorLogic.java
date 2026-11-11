package lach_01298.qmd.multiblock.accelerator;

import lach_01298.qmd.particle.ParticleStack;
import lach_01298.qmd.physics.ParticlePhysics;
import static lach_01298.qmd.multiblock.accelerator.AcceleratorPartType.Role.*;

public final class LinearAcceleratorLogic extends AbstractAcceleratorLogic {

    @Override
    protected boolean gainsFocus() {
        return true;
    }

    @Override
    protected void changeEnergy(Accelerator machine, ParticleStack input, ParticleStack output, double control,
        long maximum) {
        output.setMeanEnergy(
            ParticlePhysics.saturatedAdd(
                input.getMeanEnergy(),
                (long) (ParticlePhysics.linacEnergyGain(machine.voltage, input) * control)));
    }

    @Override
    public int beamLength(Accelerator machine) {
        return Math.max(machine.getExteriorLengthX(), machine.getExteriorLengthZ());
    }
}
