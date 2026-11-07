package lach_01298.qmd.machine.client;

import com.nred.nuclearcraft.block_entity.processor.info.ProcessorMenuInfoImpl.BasicUpgradableProcessorMenuInfo;
import com.nred.nuclearcraft.payload.processor.EnergyProcessorUpdatePacket;
import com.nred.nuclearcraft.recipe.ProcessorRecipe;
import com.nred.nuclearcraft.screen.NCScreen;
import com.nred.nuclearcraft.screen.processor.FluidSorptionsScreen;
import lach_01298.qmd.machine.OreLeacherBlockEntity;
import lach_01298.qmd.machine.OreLeacherMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/** Retains Neohaul's complete tank controls with the same relative faces as item controls. */
public final class OreLeacherFluidInputScreen extends FluidSorptionsScreen.Input<OreLeacherBlockEntity,
        EnergyProcessorUpdatePacket, BasicUpgradableProcessorMenuInfo<OreLeacherBlockEntity, EnergyProcessorUpdatePacket, ProcessorRecipe>, ProcessorRecipe> {
    public OreLeacherFluidInputScreen(NCScreen<OreLeacherMenu> parent, OreLeacherBlockEntity tile, int tank) {
        super(parent, tile, tank);
        // Neohaul 0.10.10 initializes this array with world directions. All inherited rendering,
        // buttons, resets and packets use its contents, so replace them before widgets are created.
        Direction[] relative = OreLeacherGuiActions.relativeDirections(tile.getFacingHorizontal());
        System.arraycopy(relative, 0, dirs, 0, relative.length);
    }

    @Override
    protected void drawForegroundLayer(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        // Neohaul's block icon renderer takes presentation directions (WEST means front).
        // Keep that fixed order, as ItemSorptionsScreen does, while dirs addresses world faces.
        guiGraphics.setColor(1F, 1F, 1F, 0.75F);
        BlockState state = tile.getBlockState(tile.getTilePos());
        for (int i = 0; i < dirs.length; i++) {
            renderGuiBlock(guiGraphics, state, Direction.from3DDataValue(i), a[i] + 1, b[i] + 1, 16, 16);
        }
        guiGraphics.setColor(1F, 1F, 1F, 1F);
    }
}
