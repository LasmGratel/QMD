package lach_01298.qmd.registry;

import lach_01298.qmd.QuantumMinecraftDynamics;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

/** The QMD creative tab and its configured item ordering. */
public final class QMDCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
        .create(Registries.CREATIVE_MODE_TAB, QuantumMinecraftDynamics.MODID);

    static {
        CREATIVE_MODE_TABS.register(
            "items",
            () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.qmd"))
                .icon(
                    () -> QMDItems.get("ingot_tungsten")
                        .get()
                        .getDefaultInstance())
                .displayItems((parameters, output) -> {
                    output.accept(QMDItems.ORE_LEACHER_ITEM);
                    output.accept(QMDItems.LIQUID_COLLECTOR_ITEM);
                    output.accept(QMDItems.ATMOSPHERE_COLLECTOR_ITEM);
                    output.accept(QMDItems.CREATIVE_PARTICLE_SOURCE_ITEM);
                    QMDItems.entries()
                        .forEach(output::accept);
                    QMDBlocks.liquefierBlocks()
                        .forEach(output::accept);
                    QMDBlocks.ACCELERATOR_BLOCKS.values()
                        .forEach(output::accept);
                    QMDBlocks.CHAMBER_PARTS.values()
                        .forEach(output::accept);
                    QMDBlocks.FISSION_BLOCKS.forEach(output::accept);
                    QMDItems.NUCLEAR_MATERIALS.values()
                        .forEach(output::accept);
                    QMDBlocks.DISCHARGE_LAMPS.values()
                        .forEach(output::accept);
                    output.accept(QMDItems.BEAMLINE_ITEM);
                    output.accept(QMDItems.SUPER_ALLOY_BLADE_ITEM);
                    List.of(QMDItems.FLESH, QMDItems.IRRADIATOR_ITEM)
                        .forEach(item -> output.accept(item.get()));
                    output.accept(QMDItems.EMPTY_CELL);
                    QMDItems.PARTICLE_SOURCES.values()
                        .forEach(
                            item -> output.accept(
                                item.get()
                                    .getDefaultInstance()));
                    QMDItems.PARTICLE_CELLS.values()
                        .forEach(
                            item -> output.accept(
                                item.get()
                                    .getDefaultInstance()));
                    QMDFluids.entries()
                        .forEach(fluid -> output.accept(fluid.bucket));
                    QMDItems.addEquipmentToCreativeTab(output);
                    List.of(QMDItems.LEPTON_CANNON, QMDItems.GLUON_GUN, QMDItems.ANTIMATTER_LAUNCHER)
                        .forEach(item -> output.accept(item.get()));
                    List.of(
                        QMDItems.GREEN_LUMINOUS_PAINT_ITEM,
                        QMDItems.BLUE_LUMINOUS_PAINT_ITEM,
                        QMDItems.ORANGE_LUMINOUS_PAINT_ITEM,
                        QMDItems.BEAM_METER,
                        QMDItems.POTASSIUM_IODINE_TABLET)
                        .forEach(item -> output.accept(item.get()));
                })
                .build());
    }

    private QMDCreativeTabs() {}
}
