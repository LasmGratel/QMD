package lach_01298.qmd.machine;

import com.nred.nuclearcraft.block_entity.processor.info.builder.ProcessorMenuInfoBuilderImpl.BasicUpgradableProcessorMenuInfoBuilder;
import com.nred.nuclearcraft.handler.BlockEntityInfoHandler;
import com.nred.nuclearcraft.payload.processor.EnergyProcessorUpdatePacket;
import com.nred.nuclearcraft.recipe.ProcessorRecipe;
import lach_01298.qmd.Config;
import lach_01298.qmd.recipe.QMDMachineRecipes;
import net.minecraft.resources.ResourceLocation;

import static com.nred.nuclearcraft.util.ContainerInfoHelper.standardSlot;

/** Shared server and client layout used by Neohaul's processor framework. */
public final class OreLeacherMachineInfo {
    public static final String KEY = "qmd:ore_leacher";
    private static boolean initialized;

    private OreLeacherMachineInfo() {
    }

    /** Must run before the processor block is constructed by its registry supplier. */
    public static synchronized void initialize() {
        if (initialized) return;
        QMDMachineRecipes.initialize();
        BlockEntityInfoHandler.registerProcessorInfo(
                new BasicUpgradableProcessorMenuInfoBuilder<OreLeacherBlockEntity, EnergyProcessorUpdatePacket, ProcessorRecipe>(
                        KEY, OreLeacherBlockEntity.class, OreLeacherBlockEntity::new, OreLeacherMenu::new)
                        .setRecipeHandlerName(KEY)
                        .setScreenTexture("qmd:screen/ore_leacher")
                        .setRecipeViewerTexture("qmd:textures/gui/sprites/screen/ore_leacher.png")
                        .setFrontTexture(ResourceLocation.parse("qmd:block/machine/ore_leacher_front"))
                        .setDefaultProcessTime(Config.ORE_LEACHER_TIME::get)
                        .setDefaultProcessPower(Config.ORE_LEACHER_POWER::get)
                        .setLosesProgress(true)
                        .setInputTankCapacity(16000)
                        .setItemInputSlots(standardSlot(36, 11))
                        .setFluidInputSlots(standardSlot(36, 42), standardSlot(56, 42), standardSlot(76, 42))
                        .setItemOutputSlots(standardSlot(112, 42), standardSlot(132, 42), standardSlot(152, 42))
                        .setProgressBarGuiXYWHUV(40, 29, 48, 11, 176, 0)
                        .setSpeedUpgradeSlot(132, 64, 16, 16)
                        .setEnergyUpgradeSlot(152, 64, 16, 16));
        initialized = true;
    }
}
