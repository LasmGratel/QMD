package lach_01298.qmd.block.entity.chamber;

import lach_01298.qmd.block.chamber.AbstractChamberBlock;
import lach_01298.qmd.block.chamber.BeamDumpBlock;
import lach_01298.qmd.block.chamber.ChamberPartBlock;
import lach_01298.qmd.block.chamber.CollisionChamberBlock;
import lach_01298.qmd.block.chamber.DecayChamberBlock;
import lach_01298.qmd.block.chamber.NeutralContainmentBlock;
import lach_01298.qmd.block.chamber.NucleosynthesisChamberBlock;
import lach_01298.qmd.block.chamber.TargetChamberBlock;
import lach_01298.qmd.block.menu.chamber.AbstractChamberMenu;
import lach_01298.qmd.block.menu.chamber.BeamDumpMenu;
import lach_01298.qmd.block.menu.chamber.CollisionChamberMenu;
import lach_01298.qmd.block.menu.chamber.DecayChamberMenu;
import lach_01298.qmd.block.menu.chamber.NeutralContainmentMenu;
import lach_01298.qmd.block.menu.chamber.NucleosynthesisChamberMenu;
import lach_01298.qmd.block.menu.chamber.TargetChamberMenu;
import lach_01298.qmd.multiblock.ChamberPart;
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

/** Saves keep their existing registry ID while recovering the right concrete controller and menu. */
@GameTestHolder("qmd")
@PrefixGameTestTemplate(false)
public final class ChamberTypeGameTests {

    @GameTest(template = "accelerator")
    public static void savedChambersRestoreConcreteControllersAndOpenMatchingMenus(GameTestHelper helper) {
        var cases = List.of(
            new ControllerCase(ChamberPart.TARGET_CONTROLLER, TargetChamberBlock.class, TargetChamberBlockEntity.class, TargetChamberMenu.class, QMDMenus.TARGET_CHAMBER.get()),
            new ControllerCase(ChamberPart.DECAY_CONTROLLER, DecayChamberBlock.class, DecayChamberBlockEntity.class, DecayChamberMenu.class, QMDMenus.DECAY_CHAMBER.get()),
            new ControllerCase(ChamberPart.COLLISION_CONTROLLER, CollisionChamberBlock.class, CollisionChamberBlockEntity.class, CollisionChamberMenu.class, QMDMenus.COLLISION_CHAMBER.get()),
            new ControllerCase(ChamberPart.BEAM_DUMP_CONTROLLER, BeamDumpBlock.class, BeamDumpBlockEntity.class, BeamDumpMenu.class, QMDMenus.BEAM_DUMP.get()),
            new ControllerCase(ChamberPart.CONTAINMENT_CONTROLLER, NeutralContainmentBlock.class, NeutralContainmentBlockEntity.class, NeutralContainmentMenu.class, QMDMenus.NEUTRAL_CONTAINMENT.get()),
            new ControllerCase(ChamberPart.NUCLEOSYNTHESIS_CONTROLLER, NucleosynthesisChamberBlock.class, NucleosynthesisChamberBlockEntity.class, NucleosynthesisChamberMenu.class, QMDMenus.NUCLEOSYNTHESIS_CHAMBER.get()));
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var registries = helper.getLevel().registryAccess();
        for (int index = 0; index < cases.size(); index++) {
            var entry = cases.get(index);
            var relativePos = new BlockPos(1 + index * 2, 1, 1);
            var block = QMDBlocks.CHAMBER_PARTS.get(entry.part()).get();
            helper.setBlock(relativePos, block);
            var controller = (AbstractChamberBlockEntity) helper.getBlockEntity(relativePos);
            helper.assertTrue(block.getClass() == entry.blockClass() && controller.getClass() == entry.entityClass()
                && controller.getType() == QMDBlockEntities.PARTICLE_CHAMBER.get(),
                "Placed chambers must create the correct concrete block/entity pair: " + entry.part());
            controller.inventory().setStackInSlot(1, new ItemStack(Items.DIAMOND, 7));
            controller.tank(1).setFluid(new FluidStack(Fluids.WATER, 123));
            controller.toggleEnabled();
            var saved = controller.saveWithFullMetadata(registries);
            helper.assertTrue(saved.getString("id").equals("qmd:particle_chamber"),
                "Existing chamber saves must retain their original block entity ID");
            var restored = (AbstractChamberBlockEntity) BlockEntity.loadStatic(controller.getBlockPos(), controller.getBlockState(), saved, registries);
            helper.assertTrue(restored.getClass() == entry.entityClass() && restored.role() == entry.part()
                && restored.inventory().getStackInSlot(1).is(Items.DIAMOND)
                && restored.inventory().getStackInSlot(1).getCount() == 7
                && restored.tank(1).getFluidAmount() == 123 && restored.tank(1).getFluid().is(Fluids.WATER)
                && !restored.enabled(), "Registry loading must preserve concrete type, inventory, fluid and controls: " + entry.part());
            player.setPos(controller.getBlockPos().getCenter());
            var menu = (AbstractChamberMenu) controller.createMenu(index + 1, player.getInventory(), player);
            player.containerMenu = menu;
            helper.assertTrue(menu.getClass() == entry.menuClass() && menu.getType() == entry.menuType()
                && menu.kind() == entry.part().kind && menu.controller() == controller && menu.stillValid(player),
                "Every concrete controller must open its registered menu for the nearby owner: " + entry.part());
        }
        var casing = QMDBlocks.CHAMBER_PARTS.get(ChamberPart.CASING).get();
        var casingPos = new BlockPos(1, 1, 3);
        helper.setBlock(casingPos, casing);
        helper.assertTrue(casing instanceof ChamberPartBlock && helper.getBlockEntity(casingPos) instanceof ChamberPartBlockEntity,
            "Non-controller chamber parts must keep their separate shared part implementation");
        helper.succeed();
    }

    private record ControllerCase(ChamberPart part, Class<? extends AbstractChamberBlock> blockClass,
        Class<? extends AbstractChamberBlockEntity> entityClass, Class<? extends AbstractChamberMenu> menuClass,
        MenuType<?> menuType) {}

    private ChamberTypeGameTests() {}
}
