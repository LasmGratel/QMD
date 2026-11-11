package lach_01298.qmd;

import com.mojang.logging.LogUtils;
import lach_01298.qmd.config.AcceleratorConfig;
import lach_01298.qmd.config.ChamberConfig;
import lach_01298.qmd.config.EquipmentConfig;
import lach_01298.qmd.config.FissionConfig;
import lach_01298.qmd.config.IrradiatorConfig;
import lach_01298.qmd.config.LiquefierConfig;
import lach_01298.qmd.config.MaterialRecipeConfig;
import lach_01298.qmd.config.ParticleCellConfig;
import lach_01298.qmd.config.QMDConfig;
import lach_01298.qmd.config.TurbineConfig;
import lach_01298.qmd.config.VacuumConfig;
import lach_01298.qmd.config.WeaponConfig;
import lach_01298.qmd.item.player.BeamMeterStatePayload;
import lach_01298.qmd.item.player.PlayerContentConfig;
import lach_01298.qmd.network.AcceleratorStatePayload;
import lach_01298.qmd.network.ConfigureParticleSourcePayload;
import lach_01298.qmd.network.LiquefierStatePayload;
import lach_01298.qmd.network.MachineViewerStatsPayload;
import lach_01298.qmd.particle.Particles;
import lach_01298.qmd.registry.QMDArmorMaterials;
import lach_01298.qmd.registry.QMDBlockEntities;
import lach_01298.qmd.registry.QMDBlocks;
import lach_01298.qmd.registry.QMDCreativeTabs;
import lach_01298.qmd.registry.QMDDataComponents;
import lach_01298.qmd.registry.QMDEntities;
import lach_01298.qmd.registry.QMDFluids;
import lach_01298.qmd.registry.QMDItems;
import lach_01298.qmd.registry.QMDMenus;
import lach_01298.qmd.registry.QMDRecipes;
import lach_01298.qmd.registry.QMDSounds;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(QuantumMinecraftDynamics.MODID)
public final class QuantumMinecraftDynamics {

    public static final String MODID = "qmd";
    public static final Logger LOGGER = LogUtils.getLogger();

    public QuantumMinecraftDynamics(IEventBus modEventBus, ModContainer container) {
        Particles.initialize();
        QMDDataComponents.COMPONENTS.register(modEventBus);
        QMDArmorMaterials.ARMOR_MATERIALS.register(modEventBus);
        QMDFluids.register(modEventBus);
        QMDBlocks.BLOCKS.register(modEventBus);
        QMDItems.register(modEventBus);
        QMDEntities.ENTITIES.register(modEventBus);
        QMDBlockEntities.register(modEventBus);
        QMDMenus.MENUS.register(modEventBus);
        QMDRecipes.register(modEventBus);
        QMDSounds.SOUNDS.register(modEventBus);
        QMDCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        modEventBus.addListener(AcceleratorStatePayload::register);
        modEventBus.addListener(BeamMeterStatePayload::register);
        modEventBus.addListener(LiquefierStatePayload::register);
        modEventBus.addListener(ConfigureParticleSourcePayload::register);
        modEventBus.addListener(MachineViewerStatsPayload::register);
        modEventBus.addListener(MachineViewerStatsPayload::onConfigReload);
        NeoForge.EVENT_BUS.addListener(MachineViewerStatsPayload::onDatapackSync);
        container.registerConfig(ModConfig.Type.COMMON, QMDConfig.SPEC);
        container.registerConfig(ModConfig.Type.COMMON, VacuumConfig.SPEC, "qmd-vacuum.toml");
        container.registerConfig(ModConfig.Type.COMMON, ChamberConfig.SPEC, "qmd-chambers.toml");
        container.registerConfig(ModConfig.Type.COMMON, TurbineConfig.SPEC, "qmd-turbine.toml");
        container.registerConfig(ModConfig.Type.COMMON, IrradiatorConfig.SPEC, "qmd-irradiator.toml");
        container.registerConfig(ModConfig.Type.COMMON, LiquefierConfig.SPEC, "qmd-liquefier.toml");
        container.registerConfig(ModConfig.Type.COMMON, AcceleratorConfig.SPEC, "qmd-accelerator.toml");
        container.registerConfig(ModConfig.Type.COMMON, FissionConfig.SPEC, "qmd-fission.toml");
        container.registerConfig(ModConfig.Type.COMMON, ParticleCellConfig.SPEC, "qmd-particle-cells.toml");
        container.registerConfig(ModConfig.Type.COMMON, EquipmentConfig.SPEC, "qmd-equipment.toml");
        container.registerConfig(ModConfig.Type.COMMON, WeaponConfig.SPEC, "qmd-weapons.toml");
        container.registerConfig(ModConfig.Type.COMMON, MaterialRecipeConfig.SPEC, "qmd-material-recipes.toml");
        container.registerConfig(ModConfig.Type.COMMON, PlayerContentConfig.SPEC, "qmd-player.toml");
        LOGGER.info(
            "Quantum Minecraft Dynamics: {} particle definitions initialized",
            Particles.getRegisteredParticles()
                .size());
    }
}
