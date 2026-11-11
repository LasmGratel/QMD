package lach_01298.qmd.client;

import lach_01298.qmd.registry.QMDBlocks;
import lach_01298.qmd.registry.QMDMenus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import lach_01298.qmd.client.screen.accelerator.LinearAcceleratorScreen;
import lach_01298.qmd.client.screen.accelerator.RingAcceleratorScreen;
import lach_01298.qmd.client.screen.accelerator.DeceleratorScreen;
import lach_01298.qmd.client.screen.accelerator.BeamDiverterScreen;
import lach_01298.qmd.client.screen.accelerator.BeamSplitterScreen;
import lach_01298.qmd.client.screen.accelerator.MassSpectrometerScreen;
import lach_01298.qmd.client.screen.accelerator.AcceleratorSourceScreen;
import lach_01298.qmd.client.screen.accelerator.AcceleratorPartScreen;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/** The legacy accelerator glass uses alpha transparency and hides adjoining glass faces. */
public final class AcceleratorClient {

    public static void screens(RegisterMenuScreensEvent event) {
        event.register(QMDMenus.LINEAR_ACCELERATOR.get(), LinearAcceleratorScreen::new);
        event.register(QMDMenus.RING_ACCELERATOR.get(), RingAcceleratorScreen::new);
        event.register(QMDMenus.DECELERATOR.get(), DeceleratorScreen::new);
        event.register(QMDMenus.BEAM_DIVERTER.get(), BeamDiverterScreen::new);
        event.register(QMDMenus.BEAM_SPLITTER.get(), BeamSplitterScreen::new);
        event.register(QMDMenus.MASS_SPECTROMETER.get(), MassSpectrometerScreen::new);
        event.register(QMDMenus.ACCELERATOR_SOURCE.get(), AcceleratorSourceScreen::new);
        event.register(QMDMenus.ACCELERATOR_PART.get(), AcceleratorPartScreen::new);
    }

    @SuppressWarnings("deprecation")
    public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(
            () -> ItemBlockRenderTypes.setRenderLayer(
                QMDBlocks.ACCELERATOR_BLOCKS.get("accelerator_glass")
                    .get(),
                RenderType.translucent()));
    }

    private AcceleratorClient() {}
}
