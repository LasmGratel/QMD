package lach_01298.qmd.machine.client;

import com.nred.nuclearcraft.block_entity.processor.info.ProcessorMenuInfoImpl.BasicUpgradableProcessorMenuInfo;
import com.nred.nuclearcraft.gui.NCButton;
import com.nred.nuclearcraft.payload.gui.ToggleItemOutputSettingPacket;
import com.nred.nuclearcraft.payload.processor.EnergyProcessorUpdatePacket;
import com.nred.nuclearcraft.recipe.ProcessorRecipe;
import com.nred.nuclearcraft.screen.NCScreen;
import com.nred.nuclearcraft.screen.processor.ItemSorptionsScreen;
import lach_01298.qmd.machine.OreLeacherBlockEntity;
import lach_01298.qmd.machine.OreLeacherMenu;
import net.minecraft.client.Minecraft;

/** Saves output modes on click, including when inventory/Escape navigation returns to the parent. */
public final class OreLeacherItemOutputScreen extends ItemSorptionsScreen.Output<OreLeacherBlockEntity,
        EnergyProcessorUpdatePacket, BasicUpgradableProcessorMenuInfo<OreLeacherBlockEntity, EnergyProcessorUpdatePacket, ProcessorRecipe>, ProcessorRecipe> {
    public OreLeacherItemOutputScreen(NCScreen<OreLeacherMenu> parent, OreLeacherBlockEntity tile, int slot) {
        super(parent, tile, slot);
    }

    @Override
    protected void sorptionPressed(NCButton button, int pressed) {
        if (button.id == 6) {
            if (tile.getTileWorld().isClientSide()) {
                outputSetting = OreLeacherGuiActions.cycleItemOutputSetting(tile, slot, outputSetting, pressed == 1,
                        setting -> new ToggleItemOutputSettingPacket(tile, slot, setting).sendToServer());
            }
            return;
        }
        super.sorptionPressed(button, pressed);
    }

    @Override
    public void onClose() {
        // The inherited onClose sends again, which could void output produced after the click.
        // This dialog's changes are already saved, and closing it returns to side configuration.
        Minecraft.getInstance().setScreen(parent);
    }
}
