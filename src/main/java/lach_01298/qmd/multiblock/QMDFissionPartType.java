package lach_01298.qmd.multiblock;

import com.nred.nuclearcraft.block.fission.FissionShieldBlock;
import com.nred.nuclearcraft.multiblock.fisson.FissionReactor;
import com.nred.nuclearcraft.multiblock.fisson.IFissionPartType;
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartTypeProperties;
import lach_01298.qmd.registry.QMDBlockEntities;

public enum QMDFissionPartType implements IFissionPartType {

    SHIELD;

    private final MultiblockPartTypeProperties<FissionReactor, IFissionPartType> properties = new MultiblockPartTypeProperties<>(
        () -> QMDBlockEntities.HAFNIUM_SHIELD::get,
        FissionShieldBlock::new,
        "",
        p -> p,
        p -> p);

    @Override
    public MultiblockPartTypeProperties<FissionReactor, IFissionPartType> getPartTypeProperties() {
        return properties;
    }

    @Override
    public String getSerializedName() {
        return "hafnium_shield";
    }
}
