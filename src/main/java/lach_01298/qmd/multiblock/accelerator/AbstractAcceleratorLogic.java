package lach_01298.qmd.multiblock.accelerator;

import lach_01298.qmd.block.entity.accelerator.AbstractAcceleratorBlockEntity;
import lach_01298.qmd.particle.ParticleStack;
import lach_01298.qmd.physics.ParticlePhysics;
import static lach_01298.qmd.multiblock.accelerator.AcceleratorPartType.Role.*;

/** Shared beam protocol; concrete machines own limits, transformations and routing. */
public abstract class AbstractAcceleratorLogic {

    public void operate(Accelerator machine, double control) {
        machine.operateBeam(control, this);
    }

    protected long maximumEnergy(Accelerator machine, ParticleStack input) {
        return ParticlePhysics.ringEnergyMaxEnergyFromDipole(
            machine.dipoleStrength,
            machine.radius(),
            input.getParticle()
                .getCharge(),
            input.getParticle()
                .getMass());
    }

    protected boolean acceptsInput(Accelerator machine, ParticleStack input, AbstractAcceleratorBlockEntity port,
        long maximum) {
        return true;
    }

    protected void changeEnergy(Accelerator machine, ParticleStack input, ParticleStack output, double control,
        long maximum) {}

    protected boolean gainsFocus() {
        return false;
    }

    protected boolean losesFocus() {
        return true;
    }

    protected final ParticleStack transform(Accelerator machine, ParticleStack input, double control, long maximum) {
        ParticleStack output = input.copy();
        changeEnergy(machine, input, output, control, maximum);
        if (gainsFocus()) output.addFocus(ParticlePhysics.focusGain(machine.quadrupoleStrength, input));
        if (losesFocus()) output.addFocus(-ParticlePhysics.focusLoss(machine.beamLength(), input));
        return output;
    }

    protected void publish(Accelerator machine, ParticleStack input, ParticleStack output,
        AbstractAcceleratorBlockEntity inputPort) {
        machine.beams[1].setParticleStack(output);
        machine.beams[2].setParticleStack(output.copy());
        for (var port : machine.parts(BEAM_PORT)) if (port.output()) machine.emit(port, output);
    }

    public double radius(Accelerator machine) {
        return Math.max(1, (machine.getExteriorLengthX() - 4) / 2D);
    }

    public int beamLength(Accelerator machine) {
        return 4 * (machine.getExteriorLengthX() - 4);
    }
}
