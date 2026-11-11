package lach_01298.qmd.client.screen;

import lach_01298.qmd.block.entity.OreLeacherBlockEntity;
import lach_01298.qmd.block.menu.OreLeacherMenu;

import com.nred.nuclearcraft.gui.NCButton;
import com.nred.nuclearcraft.recipe.ProcessorRecipe;
import com.nred.nuclearcraft.screen.processor.ProcessorScreenImpl.BasicUpgradableEnergyProcessorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/** The legacy ore leacher layout with Neohaul's full interactive processor controls. */
public class OreLeacherScreen
    extends BasicUpgradableEnergyProcessorScreen<OreLeacherMenu, OreLeacherBlockEntity, ProcessorRecipe> {

    private static final ResourceLocation TEXTURE = ResourceLocation.parse("qmd:screen/ore_leacher");

    public OreLeacherScreen(OreLeacherMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, TEXTURE);
    }

    @Override
    protected void drawProgressBar(GuiGraphics guiGraphics) {
        drawOreLeacherProgress(guiGraphics, guiTextures, leftPos, topPos, getProgressBarWidth());
    }

    private static void drawOreLeacherProgress(GuiGraphics guiGraphics, ResourceLocation texture, int left, int top,
        int progressWidth) {
        int width = Math.clamp(progressWidth, 0, 48);
        if (width > 0) {
            guiGraphics.blitSprite(texture, 256, 256, 176, 0, left + 40, top + 29, width, 11);
        }
        int finalWidth = Math.max(0, width - 32);
        if (finalWidth > 0) {
            guiGraphics.blitSprite(texture, 256, 256, 176, 12, left + 94, top + 46, finalWidth, 8);
        }
    }

    @Override
    protected void setConfigScreen() {
        Minecraft.getInstance()
            .setScreen(new SideConfig());
    }

    /** Neohaul recognizes this superclass and disables machine slots over its sorption buttons. */
    private final class SideConfig extends SideConfigScreen {

        private SideConfig() {
            super(
                OreLeacherScreen.this.menu,
                OreLeacherScreen.this,
                OreLeacherScreen.this.menu.inventory,
                Component.empty(),
                TEXTURE);
        }

        @Override
        protected void drawProgressBar(GuiGraphics guiGraphics) {
            drawOreLeacherProgress(guiGraphics, guiTextures, leftPos, topPos, getProgressBarWidth());
        }

        @Override
        protected boolean sorptionButtonActionPerformed(NCButton button, int pressed) {
            for (int i = 0; i < info.fluidInputSize; i++) {
                if (button.id == info.fluidInputSorptionButtonID[i]) {
                    Minecraft.getInstance()
                        .setScreen(new OreLeacherFluidInputScreen(this, tile, info.fluidInputTanks[i]));
                    return true;
                }
            }
            for (int i = 0; i < info.itemOutputSize; i++) {
                if (button.id == info.itemOutputSorptionButtonID[i]) {
                    Minecraft.getInstance()
                        .setScreen(new OreLeacherItemOutputScreen(this, tile, info.itemOutputSlots[i]));
                    return true;
                }
            }
            return super.sorptionButtonActionPerformed(button, pressed);
        }
    }
}
