package lach_01298.qmd.multiblock.accelerator;

import lach_01298.qmd.block.entity.accelerator.AbstractAcceleratorBlockEntity;
import lach_01298.qmd.config.AcceleratorConfig;
import lach_01298.qmd.particle.ParticleStack;
import lach_01298.qmd.physics.ParticlePhysics;
import static lach_01298.qmd.multiblock.accelerator.AcceleratorPartType.Role.*;

public abstract class AbstractRoutingAcceleratorLogic extends AbstractAcceleratorLogic {

    protected abstract boolean acceptsNeutral(Accelerator machine, AbstractAcceleratorBlockEntity inputPort);

    protected void divideBeam(ParticleStack output, ParticleStack straight) {}

    @Override
    protected boolean acceptsInput(Accelerator machine, ParticleStack input, AbstractAcceleratorBlockEntity port,
        long maximum) {
        if (input.getParticle()
            .getCharge() != 0 && input.getMeanEnergy() > maximum
            || input.getParticle()
                .getCharge() == 0 && !acceptsNeutral(machine, port)) {
            machine.error = 5;
            return false;
        }
        return true;
    }

    @Override
    protected void publish(Accelerator machine, ParticleStack input, ParticleStack output,
        AbstractAcceleratorBlockEntity inputPort) {
        ParticleStack straight = output.copy();
        output.setMeanEnergy(
            Math.max(0, input.getMeanEnergy() - ParticlePhysics.cornerEnergyLoss(input, machine.radius())));
        divideBeam(output, straight);
        machine.beams[1].setParticleStack(output);
        machine.beams[2].setParticleStack(straight);
        for (var port : machine.parts(BEAM_PORT)) if (port.output()) {
            boolean straightPort = inputPort != null
                && machine.outward(port.getBlockPos()) == machine.outward(inputPort.getBlockPos())
                    .getOpposite();
            machine.emit(port, straightPort ? straight : output);
        }
    }

    @Override
    public double radius(Accelerator machine) {
        return AcceleratorConfig.DIVERTER_RADIUS.get();
    }

    @Override
    public int beamLength(Accelerator machine) {
        return 5;
    }
}
