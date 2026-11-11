package lach_01298.qmd.multiblock.accelerator;

import lach_01298.qmd.block.entity.accelerator.AbstractAcceleratorBlockEntity;
import lach_01298.qmd.particle.ParticleStack;
import static lach_01298.qmd.multiblock.accelerator.AcceleratorPartType.Role.*;

public final class BeamSplitterLogic extends AbstractRoutingAcceleratorLogic {

    @Override
    protected boolean acceptsNeutral(Accelerator machine, AbstractAcceleratorBlockEntity inputPort) {
        return false;
    }

    @Override
    protected void divideBeam(ParticleStack output, ParticleStack straight) {
        output.setAmount(output.getAmount() / 2);
        straight.setAmount(straight.getAmount() / 2);
    }
}
