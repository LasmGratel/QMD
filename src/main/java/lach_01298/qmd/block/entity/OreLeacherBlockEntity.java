package lach_01298.qmd.block.entity;

import com.nred.nuclearcraft.block_entity.processor.ProcessorEntityImpl.BasicUpgradableEnergyProcessorEntity;
import com.nred.nuclearcraft.recipe.ProcessorRecipe;
import lach_01298.qmd.registry.QMDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

/** Neohaul supplies processing, persistence, upgrades, automation and GUI synchronization. */
public final class OreLeacherBlockEntity
    extends BasicUpgradableEnergyProcessorEntity<ProcessorRecipe, OreLeacherBlockEntity> {

    public OreLeacherBlockEntity(BlockPos pos, BlockState state) {
        super(QMDBlockEntities.ORE_LEACHER.get(), pos, state, OreLeacherMachineInfo.KEY);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.qmd.ore_leacher");
    }
}
