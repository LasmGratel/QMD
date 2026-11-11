package lach_01298.qmd.multiblock;

import com.nred.nuclearcraft.block.rtg.RTGBlock;
import com.nred.nuclearcraft.multiblock.rtg.IRTGPartType;
import com.nred.nuclearcraft.multiblock.rtg.RTGMultiblock;
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartTypeProperties;
import lach_01298.qmd.registry.QMDBlockEntities;

public enum QMDRtgPartType implements IRTGPartType {

    STRONTIUM;

    private final MultiblockPartTypeProperties<RTGMultiblock, IRTGPartType> properties = new MultiblockPartTypeProperties<>(
        () -> QMDBlockEntities.STRONTIUM_RTG::get,
        RTGBlock::new,
        "",
        p -> p,
        p -> p);

    @Override
    public MultiblockPartTypeProperties<RTGMultiblock, IRTGPartType> getPartTypeProperties() {
        return properties;
    }

    @Override
    public String getSerializedName() {
        return "strontium_rtg";
    }
}
