package lach_01298.qmd.multiblock;

import com.nred.nuclearcraft.block.turbine.TurbineRotorBladeBlock;
import com.nred.nuclearcraft.multiblock.turbine.ITurbinePartType;
import com.nred.nuclearcraft.multiblock.turbine.Turbine;
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartTypeProperties;
import lach_01298.qmd.registry.QMDBlockEntities;

public enum QMDTurbinePartType implements ITurbinePartType {

    ROTOR_BLADE;

    private final MultiblockPartTypeProperties<Turbine, ITurbinePartType> properties = new MultiblockPartTypeProperties<>(
        () -> QMDBlockEntities.SUPER_ALLOY_TURBINE_BLADE::get,
        TurbineRotorBladeBlock::new,
        "",
        p -> p,
        p -> p);

    @Override
    public MultiblockPartTypeProperties<Turbine, ITurbinePartType> getPartTypeProperties() {
        return properties;
    }

    @Override
    public String getSerializedName() {
        return "super_alloy_rotor_blade";
    }
}
