package lach_01298.qmd.multiblock.accelerator;

import lach_01298.qmd.block.entity.accelerator.AbstractAcceleratorBlockEntity;
import lach_01298.qmd.particle.ParticleStack;
import lach_01298.qmd.particle.Particles;
import lach_01298.qmd.physics.ParticlePhysics;
import static lach_01298.qmd.multiblock.accelerator.AcceleratorPartType.Role.*;

public final class RingAcceleratorLogic extends AbstractRingAcceleratorLogic {

    @Override
    protected long maximumEnergy(Accelerator machine, ParticleStack input) {
        return ParticlePhysics.ringEnergyMaxEnergy(machine.dipoleStrength, machine.voltage, machine.radius(), input);
    }

    @Override
    protected void changeEnergy(Accelerator machine, ParticleStack input, ParticleStack output, double control,
        long maximum) {
        output.setMeanEnergy((long) (maximum * control));
    }

    @Override
    protected void publish(Accelerator machine, ParticleStack input, ParticleStack output,
        AbstractAcceleratorBlockEntity inputPort) {
        super.publish(machine, input, output, inputPort);
        var photons = new ParticleStack(
            Particles.photon,
            output.getAmount(),
            ParticlePhysics.synchrotronRadiationEnergy(machine.radius(), output),
            output.getFocus());
        machine.beams[2].setParticleStack(photons);
        for (var port : machine.parts(SYNCHROTRON_PORT)) machine.emit(port, photons);
    }
}
