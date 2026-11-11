package lach_01298.qmd.multiblock.accelerator;

import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockPartType2;
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartTypeProperties;
import lach_01298.qmd.block.accelerator.AbstractAcceleratorBlock;
import lach_01298.qmd.registry.QMDBlockEntities;

/** Flat modern registry IDs describe the part rather than encoding old metadata variants. */
public final class AcceleratorPartType implements IMultiblockPartType2<Accelerator, AcceleratorPartType> {

    public enum Role {
        CONTROLLER,
        CASING,
        GLASS,
        BEAM,
        YOKE,
        CAVITY,
        MAGNET,
        COOLER,
        SOURCE,
        COLLECTOR,
        BEAM_PORT,
        SYNCHROTRON_PORT,
        ENERGY_PORT,
        COOLANT_INLET,
        COOLANT_OUTLET,
        REDSTONE_PORT,
        PROCESS_PORT,
        COMPUTER_PORT
    }

    public final String id;
    public final Role role;
    public final AcceleratorKind kind;
    public final AcceleratorMaterial material;
    public final int variant;
    private final MultiblockPartTypeProperties<Accelerator, AcceleratorPartType> properties;

    public AcceleratorPartType(String id, Role role, AcceleratorKind kind, AcceleratorMaterial material, int variant) {
        this.id = id;
        this.role = role;
        this.kind = kind;
        this.material = material;
        this.variant = variant;
        properties = new MultiblockPartTypeProperties<>(
            () -> QMDBlockEntities.ACCELERATOR_PART::get,
            p -> AbstractAcceleratorBlock.create(p, this),
            "",
            p -> role == Role.GLASS ? p.strength(2F, 12F)
                .noOcclusion() : p.strength(3.5F, 15F),
            p -> p);
    }

    @Override
    public MultiblockPartTypeProperties<Accelerator, AcceleratorPartType> getPartTypeProperties() {
        return properties;
    }

    @Override
    public String getSerializedName() {
        return id;
    }

    public boolean isInterior() {
        return role == Role.BEAM || role == Role.YOKE
            || role == Role.CAVITY
            || role == Role.MAGNET
            || role == Role.COOLER;
    }
}
