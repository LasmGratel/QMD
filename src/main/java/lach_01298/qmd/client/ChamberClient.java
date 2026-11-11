package lach_01298.qmd.client;

import lach_01298.qmd.client.screen.chamber.TargetChamberScreen;
import lach_01298.qmd.client.screen.chamber.DecayChamberScreen;
import lach_01298.qmd.client.screen.chamber.CollisionChamberScreen;
import lach_01298.qmd.client.screen.chamber.BeamDumpScreen;
import lach_01298.qmd.client.screen.chamber.NeutralContainmentScreen;
import lach_01298.qmd.client.screen.chamber.NucleosynthesisChamberScreen;
import lach_01298.qmd.multiblock.ChamberPart;
import lach_01298.qmd.registry.QMDBlocks;
import lach_01298.qmd.registry.QMDMenus;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class ChamberClient {

    public static void screens(RegisterMenuScreensEvent event) {
        event.register(QMDMenus.TARGET_CHAMBER.get(), TargetChamberScreen::new);
        event.register(QMDMenus.DECAY_CHAMBER.get(), DecayChamberScreen::new);
        event.register(QMDMenus.COLLISION_CHAMBER.get(), CollisionChamberScreen::new);
        event.register(QMDMenus.BEAM_DUMP.get(), BeamDumpScreen::new);
        event.register(QMDMenus.NEUTRAL_CONTAINMENT.get(), NeutralContainmentScreen::new);
        event.register(QMDMenus.NUCLEOSYNTHESIS_CHAMBER.get(), NucleosynthesisChamberScreen::new);
    }

    @SuppressWarnings("deprecation")
    public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            for (var role : java.util.List.of(ChamberPart.GLASS, ChamberPart.VACUUM_GLASS))
                ItemBlockRenderTypes.setRenderLayer(
                    QMDBlocks.CHAMBER_PARTS.get(role)
                        .get(),
                    RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(
                QMDBlocks.CHAMBER_PARTS.get(ChamberPart.PLASMA_GLASS)
                    .get(),
                RenderType.translucent());
        });
    }

    private ChamberClient() {}
}
