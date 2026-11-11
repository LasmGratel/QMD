package lach_01298.qmd.block.chamber;

import lach_01298.qmd.multiblock.ChamberPart;

/** Physical chamber casing, detector or port; controller blocks have their own classes. */
public final class ChamberPartBlock extends AbstractChamberBlock {

    public ChamberPartBlock(ChamberPart role) {
        this(role, defaultProperties(role));
    }

    ChamberPartBlock(ChamberPart role, Properties properties) {
        super(role, properties);
    }
}
