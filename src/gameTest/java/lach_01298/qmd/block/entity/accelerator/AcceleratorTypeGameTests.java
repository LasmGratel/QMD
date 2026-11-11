package lach_01298.qmd.block.entity.accelerator;

import lach_01298.qmd.block.accelerator.*;
import lach_01298.qmd.block.menu.accelerator.*;
import lach_01298.qmd.registry.QMDBlockEntities;
import lach_01298.qmd.registry.QMDBlocks;
import lach_01298.qmd.registry.QMDMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;

@GameTestHolder("qmd")
@PrefixGameTestTemplate(false)
public final class AcceleratorTypeGameTests {

    @GameTest(template = "accelerator")
    public static void savedAcceleratorsRestoreConcreteEntitiesAndMatchingMenus(GameTestHelper helper) {
        var cases = List.of(
            new MachineCase("linear_accelerator_controller", LinearAcceleratorBlock.class, LinearAcceleratorBlockEntity.class, LinearAcceleratorMenu.class, QMDMenus.LINEAR_ACCELERATOR.get(), 0),
            new MachineCase("ring_accelerator_controller", RingAcceleratorBlock.class, RingAcceleratorBlockEntity.class, RingAcceleratorMenu.class, QMDMenus.RING_ACCELERATOR.get(), 0),
            new MachineCase("decelerator_controller", DeceleratorBlock.class, DeceleratorBlockEntity.class, DeceleratorMenu.class, QMDMenus.DECELERATOR.get(), 0),
            new MachineCase("beam_diverter_controller", BeamDiverterBlock.class, BeamDiverterBlockEntity.class, BeamDiverterMenu.class, QMDMenus.BEAM_DIVERTER.get(), 0),
            new MachineCase("beam_splitter_controller", BeamSplitterBlock.class, BeamSplitterBlockEntity.class, BeamSplitterMenu.class, QMDMenus.BEAM_SPLITTER.get(), 0),
            new MachineCase("mass_spectrometer_controller", MassSpectrometerBlock.class, MassSpectrometerBlockEntity.class, MassSpectrometerMenu.class, QMDMenus.MASS_SPECTROMETER.get(), 6),
            new MachineCase("accelerator_ion_source_basic", AcceleratorSourceBlock.class, AcceleratorSourceBlockEntity.class, AcceleratorSourceMenu.class, QMDMenus.ACCELERATOR_SOURCE.get(), 2),
            new MachineCase("accelerator_casing", AcceleratorComponentBlock.class, AcceleratorComponentBlockEntity.class, AcceleratorPartMenu.class, QMDMenus.ACCELERATOR_PART.get(), 0));
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var registries = helper.getLevel().registryAccess();
        for (int index = 0; index < cases.size(); index++) {
            var entry = cases.get(index);
            var block = QMDBlocks.ACCELERATOR_BLOCKS.get(entry.id()).get();
            var pos = new BlockPos(1 + index * 2, 1, 1);
            helper.setBlock(pos, block.defaultBlockState().setValue(AbstractAcceleratorBlock.OUTPUT, true));
            var entity = (AbstractAcceleratorBlockEntity) helper.getBlockEntity(pos);
            helper.assertTrue(block.getClass() == entry.blockClass() && entity.getClass() == entry.entityClass()
                && entity.getType() == QMDBlockEntities.ACCELERATOR_PART.get(),
                "Registry placement must construct the concrete block/entity pair: " + entry.id());
            entity.inventory.setStackInSlot(1, new ItemStack(Items.DIAMOND));
            entity.sourceTank.setFluid(new FluidStack(Fluids.WATER, 123));
            entity.setRedstoneLevel(7);
            var saved = entity.saveWithFullMetadata(registries);
            helper.assertTrue(saved.getString("id").equals("qmd:accelerator_part"), "Existing accelerator saves must retain their block entity ID");
            var restored = (AbstractAcceleratorBlockEntity) BlockEntity.loadStatic(entity.getBlockPos(), entity.getBlockState(), saved, registries);
            helper.assertTrue(restored.getClass() == entry.entityClass() && restored.inventory.getStackInSlot(1).is(Items.DIAMOND)
                && restored.inventory.getStackInSlot(1).getCount() == 1 && restored.sourceTank.getFluidAmount() == 123
                && restored.sourceTank.getFluid().is(Fluids.WATER) && restored.redstoneLevel() == 7,
                "Registry loading must preserve concrete type, item components, tank and output setting: " + entry.id());
            player.setPos(entity.getBlockPos().getCenter());
            var menu = (AbstractAcceleratorMenu) entity.createMenu(index + 1, player.getInventory(), player);
            player.containerMenu = menu;
            helper.assertTrue(menu.getClass() == entry.menuClass() && menu.getType() == entry.menuType()
                && menu.part() == entity && menu.stillValid(player) && menu.slots.size() == entry.machineSlots() + 36,
                "Each accelerator view must open the correct registered menu with its physical slots: " + entry.id());
            if (entry.machineSlots() == 6)
                helper.assertTrue(!menu.slots.get(1).isActive() && !menu.slots.get(1).mayPlace(new ItemStack(Items.DIAMOND)),
                    "The spectrometer's reserved physical slot must remain hidden and reject insertion");
        }
        helper.succeed();
    }

    private record MachineCase(String id, Class<? extends AbstractAcceleratorBlock> blockClass,
        Class<? extends AbstractAcceleratorBlockEntity> entityClass, Class<? extends AbstractAcceleratorMenu> menuClass,
        MenuType<?> menuType, int machineSlots) {}

    private AcceleratorTypeGameTests() {}
}
