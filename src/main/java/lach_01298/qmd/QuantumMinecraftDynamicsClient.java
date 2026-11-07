package lach_01298.qmd;

import lach_01298.qmd.fluid.QMDFluids;
import lach_01298.qmd.machine.OreLeacherScreen;
import lach_01298.qmd.registration.QMDMachines;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = QuantumMinecraftDynamics.MODID, dist = Dist.CLIENT)
public final class QuantumMinecraftDynamicsClient {
    public QuantumMinecraftDynamicsClient(ModContainer container, IEventBus bus) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        bus.addListener(QuantumMinecraftDynamicsClient::registerScreens);
        bus.addListener(QMDFluids::initializeClient);
        bus.addListener(QMDFluids::initializeBucketColors);
        bus.addListener(QMDFluids::initializeRenderLayers);
    }

    private static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(QMDMachines.ORE_LEACHER_MENU.get(), OreLeacherScreen::new);
    }
}
