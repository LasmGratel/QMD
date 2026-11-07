package lach_01298.qmd;

import com.mojang.logging.LogUtils;
import lach_01298.qmd.item.QMDItems;
import lach_01298.qmd.fluid.QMDFluids;
import lach_01298.qmd.particle.Particles;
import lach_01298.qmd.recipe.QMDMachineRecipes;
import lach_01298.qmd.registration.QMDMachines;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(QuantumMinecraftDynamics.MODID)
public final class QuantumMinecraftDynamics {
    public static final String MODID = "qmd";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    static {
        CREATIVE_MODE_TABS.register("items", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.qmd"))
                .icon(() -> QMDItems.get("ingot_tungsten").get().getDefaultInstance())
                .displayItems((parameters, output) -> {
                    output.accept(QMDMachines.ORE_LEACHER_ITEM);
                    output.accept(QMDMachines.LIQUID_COLLECTOR_ITEM);
                    output.accept(QMDMachines.ATMOSPHERE_COLLECTOR_ITEM);
                    QMDItems.entries().forEach(output::accept);
                    QMDFluids.entries().forEach(fluid -> output.accept(fluid.bucket));
                })
                .build());
    }

    public QuantumMinecraftDynamics(IEventBus modEventBus, ModContainer container) {
        Particles.initialize();
        QMDItems.ITEMS.register(modEventBus);
        QMDMachineRecipes.register(modEventBus);
        QMDMachines.register(modEventBus);
        QMDFluids.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        container.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        LOGGER.info("Quantum Minecraft Dynamics: {} particle definitions initialized",
                Particles.getRegisteredParticles().size());
    }
}
