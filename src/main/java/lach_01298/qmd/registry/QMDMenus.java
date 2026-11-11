package lach_01298.qmd.registry;

import lach_01298.qmd.QuantumMinecraftDynamics;
import lach_01298.qmd.block.menu.accelerator.LinearAcceleratorMenu;
import lach_01298.qmd.block.menu.accelerator.RingAcceleratorMenu;
import lach_01298.qmd.block.menu.accelerator.DeceleratorMenu;
import lach_01298.qmd.block.menu.accelerator.BeamDiverterMenu;
import lach_01298.qmd.block.menu.accelerator.BeamSplitterMenu;
import lach_01298.qmd.block.menu.accelerator.MassSpectrometerMenu;
import lach_01298.qmd.block.menu.accelerator.AcceleratorSourceMenu;
import lach_01298.qmd.block.menu.accelerator.AcceleratorPartMenu;

import lach_01298.qmd.block.menu.chamber.TargetChamberMenu;
import lach_01298.qmd.block.menu.chamber.DecayChamberMenu;
import lach_01298.qmd.block.menu.chamber.CollisionChamberMenu;
import lach_01298.qmd.block.menu.chamber.BeamDumpMenu;
import lach_01298.qmd.block.menu.chamber.NeutralContainmentMenu;
import lach_01298.qmd.block.menu.chamber.NucleosynthesisChamberMenu;
import lach_01298.qmd.block.menu.CreativeParticleSourceMenu;
import lach_01298.qmd.block.menu.IrradiatorMenu;
import lach_01298.qmd.block.menu.LiquefierMenu;
import lach_01298.qmd.block.menu.OreLeacherMenu;
import lach_01298.qmd.menu.BeamMeterMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** All QMD container menu types. */
public final class QMDMenus {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister
        .create(Registries.MENU, QuantumMinecraftDynamics.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<OreLeacherMenu>> ORE_LEACHER = MENUS
        .register("ore_leacher", () -> IMenuTypeExtension.create(OreLeacherMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<CreativeParticleSourceMenu>> CREATIVE_PARTICLE_SOURCE = MENUS
        .register("creative_particle_source", () -> IMenuTypeExtension.create(CreativeParticleSourceMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<LiquefierMenu>> LIQUEFIER = MENUS
        .register("liquefier_controller", () -> IMenuTypeExtension.create(LiquefierMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<LinearAcceleratorMenu>> LINEAR_ACCELERATOR = MENUS
        .register("accelerator", () -> IMenuTypeExtension.create(LinearAcceleratorMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<RingAcceleratorMenu>> RING_ACCELERATOR = MENUS
        .register("ring_accelerator", () -> IMenuTypeExtension.create(RingAcceleratorMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<DeceleratorMenu>> DECELERATOR = MENUS
        .register("decelerator", () -> IMenuTypeExtension.create(DeceleratorMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<BeamDiverterMenu>> BEAM_DIVERTER = MENUS
        .register("beam_diverter", () -> IMenuTypeExtension.create(BeamDiverterMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<BeamSplitterMenu>> BEAM_SPLITTER = MENUS
        .register("beam_splitter", () -> IMenuTypeExtension.create(BeamSplitterMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<MassSpectrometerMenu>> MASS_SPECTROMETER = MENUS
        .register("mass_spectrometer", () -> IMenuTypeExtension.create(MassSpectrometerMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<AcceleratorSourceMenu>> ACCELERATOR_SOURCE = MENUS
        .register("accelerator_source", () -> IMenuTypeExtension.create(AcceleratorSourceMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<AcceleratorPartMenu>> ACCELERATOR_PART = MENUS
        .register("accelerator_part", () -> IMenuTypeExtension.create(AcceleratorPartMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<TargetChamberMenu>> TARGET_CHAMBER = MENUS
        .register("particle_chamber", () -> IMenuTypeExtension.create(TargetChamberMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<DecayChamberMenu>> DECAY_CHAMBER = MENUS
        .register("decay_chamber", () -> IMenuTypeExtension.create(DecayChamberMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<CollisionChamberMenu>> COLLISION_CHAMBER = MENUS
        .register("collision_chamber", () -> IMenuTypeExtension.create(CollisionChamberMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<BeamDumpMenu>> BEAM_DUMP = MENUS
        .register("beam_dump", () -> IMenuTypeExtension.create(BeamDumpMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<NeutralContainmentMenu>> NEUTRAL_CONTAINMENT = MENUS
        .register("neutral_containment", () -> IMenuTypeExtension.create(NeutralContainmentMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<NucleosynthesisChamberMenu>> NUCLEOSYNTHESIS_CHAMBER = MENUS
        .register("nucleosynthesis_chamber", () -> IMenuTypeExtension.create(NucleosynthesisChamberMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<IrradiatorMenu>> IRRADIATOR = MENUS
        .register("irradiator", () -> IMenuTypeExtension.create(IrradiatorMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<BeamMeterMenu>> BEAM_METER = MENUS
        .register("beam_meter", () -> IMenuTypeExtension.create(BeamMeterMenu::new));

    private QMDMenus() {}
}
