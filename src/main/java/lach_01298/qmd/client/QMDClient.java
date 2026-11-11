package lach_01298.qmd.client;

import lach_01298.qmd.QuantumMinecraftDynamics;
import lach_01298.qmd.client.equipment.EquipmentClient;
import lach_01298.qmd.client.particle.AntimatterProjectileRenderer;
import lach_01298.qmd.client.particle.GammaFlashRenderer;
import lach_01298.qmd.client.particle.WeaponBeamRenderer;
import lach_01298.qmd.client.screen.BeamMeterScreen;
import lach_01298.qmd.client.screen.CreativeParticleSourceScreen;
import lach_01298.qmd.client.screen.IrradiatorScreen;
import lach_01298.qmd.client.screen.LiquefierScreen;
import lach_01298.qmd.client.screen.OreLeacherScreen;
import lach_01298.qmd.config.MachineViewerStats;
import lach_01298.qmd.registry.QMDBlockEntities;
import lach_01298.qmd.registry.QMDEntities;
import lach_01298.qmd.registry.QMDMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = QuantumMinecraftDynamics.MODID, dist = Dist.CLIENT)
public final class QMDClient {

    public QMDClient(ModContainer container, IEventBus bus) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        bus.addListener(QMDClient::registerScreens);
        bus.addListener(QMDClient::registerEntityRenderers);
        bus.addListener(QMDClientFluids::initializeClient);
        bus.addListener(QMDClientFluids::initializeBucketColors);
        bus.addListener(QMDClientFluids::initializeRenderLayers);
        bus.addListener(ChamberClient::setup);
        bus.addListener(AcceleratorClient::setup);
        EquipmentClient.clientHooks(bus);
        NeoForge.EVENT_BUS.addListener(QMDClient::onLoggingOut);
        NeoForge.EVENT_BUS.addListener(lach_01298.qmd.client.particle.GluonBeamSound::onEntityJoin);
    }

    private static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(QMDMenus.ORE_LEACHER.get(), OreLeacherScreen::new);
        event.register(QMDMenus.IRRADIATOR.get(), IrradiatorScreen::new);
        event.register(QMDMenus.LIQUEFIER.get(), LiquefierScreen::new);
        AcceleratorClient.screens(event);
        ChamberClient.screens(event);
        event.register(QMDMenus.CREATIVE_PARTICLE_SOURCE.get(), CreativeParticleSourceScreen::new);
        event.register(QMDMenus.BEAM_METER.get(), BeamMeterScreen::new);
    }

    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(QMDEntities.LEPTON_BEAM.get(), WeaponBeamRenderer::new);
        event.registerEntityRenderer(QMDEntities.GLUON_BEAM.get(), WeaponBeamRenderer::new);
        event.registerEntityRenderer(QMDEntities.ANTIMATTER_PROJECTILE.get(), AntimatterProjectileRenderer::new);
        event.registerBlockEntityRenderer(QMDBlockEntities.LIQUEFIER_CONTROLLER.get(), LiquefierRenderer::new);
        event.registerBlockEntityRenderer(QMDBlockEntities.PARTICLE_CHAMBER.get(), ChamberRenderer::new);
        event.registerEntityRenderer(QMDEntities.GAMMA_FLASH.get(), GammaFlashRenderer::new);
    }

    private static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        MachineViewerStats.resetClient();
    }
}
