package lach_01298.qmd.multiblock.accelerator;

import lach_01298.qmd.block.entity.accelerator.AbstractAcceleratorBlockEntity;
import net.minecraft.core.Direction;
import static lach_01298.qmd.multiblock.accelerator.AcceleratorPartType.Role.*;

public final class BeamDiverterLogic extends AbstractRoutingAcceleratorLogic {

    @Override
    protected boolean acceptsNeutral(Accelerator machine, AbstractAcceleratorBlockEntity inputPort) {
        Direction inward = inputPort == null ? null : machine.outward(inputPort.getBlockPos());
        return inward != null && machine.parts(BEAM_PORT)
            .stream()
            .filter(AbstractAcceleratorBlockEntity::output)
            .anyMatch(port -> machine.outward(port.getBlockPos()) == inward.getOpposite());
    }
}
