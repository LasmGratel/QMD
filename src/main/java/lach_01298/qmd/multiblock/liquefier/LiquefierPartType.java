package lach_01298.qmd.multiblock.liquefier;

import com.nred.nuclearcraft.multiblock.hx.HeatExchanger;
import com.nred.nuclearcraft.multiblock.hx.IHeatExchangerPartType;
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartTypeProperties;
import lach_01298.qmd.block.LiquefierPartBlock;
import lach_01298.qmd.registry.QMDBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public enum LiquefierPartType implements IHeatExchangerPartType {

    CONTROLLER(() -> QMDBlockEntities.LIQUEFIER_CONTROLLER.get()),
    COMPRESSOR(() -> QMDBlockEntities.LIQUEFIER_COMPRESSOR.get()),
    NOZZLE(() -> QMDBlockEntities.LIQUEFIER_NOZZLE.get()),
    FLUID_PORT(() -> QMDBlockEntities.LIQUEFIER_FLUID_PORT.get()),
    ENERGY_PORT(() -> QMDBlockEntities.LIQUEFIER_ENERGY_PORT.get());

    private final MultiblockPartTypeProperties<HeatExchanger, IHeatExchangerPartType> properties;

    LiquefierPartType(Supplier<BlockEntityType<?>> entity) {
        properties = new MultiblockPartTypeProperties<>(() -> entity, LiquefierPartBlock::new, "", p -> p, p -> p);
    }

    @Override
    public MultiblockPartTypeProperties<HeatExchanger, IHeatExchangerPartType> getPartTypeProperties() {
        return properties;
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(java.util.Locale.ROOT);
    }
}
