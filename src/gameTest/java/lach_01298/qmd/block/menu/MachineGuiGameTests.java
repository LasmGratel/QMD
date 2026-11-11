package lach_01298.qmd.block.menu;

import lach_01298.qmd.block.entity.accelerator.AbstractAcceleratorBlockEntity;
import lach_01298.qmd.block.entity.chamber.AbstractChamberBlockEntity;
import lach_01298.qmd.block.menu.accelerator.AbstractAcceleratorMenu;
import lach_01298.qmd.block.menu.chamber.AbstractChamberMenu;
import lach_01298.qmd.multiblock.ChamberPart;
import lach_01298.qmd.registry.QMDBlocks;
import lach_01298.qmd.registry.QMDItems;
import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Exercises the server side of the migrated widgets without loading client rendering classes. */
@GameTestHolder("qmd")
@PrefixGameTestTemplate(false)
public final class MachineGuiGameTests {
    @GameTest(template = "empty", batch = "qmd.gui")
    public static void sourceWidgetsTransferInventoryClearTankAndRejectStaleMenus(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 2, 2);
        helper.setBlock(pos, QMDBlocks.ACCELERATOR_BLOCKS.get("accelerator_ion_source_basic").get());
        var source = (AbstractAcceleratorBlockEntity) helper.getBlockEntity(pos);
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setPos(source.getBlockPos().getCenter());
        var menu = (AbstractAcceleratorMenu) source.createMenu(7, player.getInventory(), player);
        player.containerMenu = menu;
        var cell = new ItemStack(QMDItems.PARTICLE_SOURCES.get("tungsten_filament").get());
        player.getInventory().setItem(9, cell);
        menu.quickMoveStack(player, 2);
        helper.assertTrue(source.inventory.getStackInSlot(0).is(QMDItems.PARTICLE_SOURCES.get("tungsten_filament").get()) && player.getInventory().getItem(9).isEmpty(),
                "Source inventory widgets must transfer the actual reservoir through the menu");
        source.sourceFluid().setFluid(new FluidStack(Fluids.WATER, 1000));
        helper.assertTrue(menu.snapshot().getInt("sourceTankCapacity") == source.sourceFluid().getCapacity(),
                "The instrument must receive the real tank capacity");
        var stranger = helper.makeMockPlayer(GameType.CREATIVE); stranger.containerMenu = menu;
        helper.assertTrue(!menu.clickMenuButton(stranger, 20) && source.sourceFluid().getFluidAmount() == 1000,
                "Another player cannot activate the owner's clear-tank widget");
        helper.assertTrue(menu.clickMenuButton(player, 20) && source.sourceFluid().isEmpty() && !source.inventory.getStackInSlot(0).isEmpty(),
                "An unassembled source can clear its fluid without consuming its reservoir");
        helper.setBlock(pos, Blocks.AIR);
        helper.assertTrue(!menu.clickMenuButton(player, 20), "A stale screen cannot clear a replaced source");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.gui")
    public static void chamberTankWidgetsExchangeContainersAndClearOnlyMachineContents(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 2, 2);
        helper.setBlock(pos, QMDBlocks.CHAMBER_PARTS.get(ChamberPart.TARGET_CONTROLLER).get());
        var chamber = (AbstractChamberBlockEntity) helper.getBlockEntity(pos);
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setPos(chamber.getBlockPos().getCenter());
        var menu = (AbstractChamberMenu) chamber.createMenu(8, player.getInventory(), player); player.containerMenu = menu;
        player.getInventory().setItem(9, new ItemStack(Items.DIAMOND, 7));
        menu.setCarried(new ItemStack(Items.WATER_BUCKET));
        helper.assertTrue(!menu.clickMenuButton(player, 2) && menu.getCarried().is(Items.WATER_BUCKET) && chamber.tank(0).isEmpty(),
                "The tank widget must preserve containers rejected by the recipe filter");
        var recipes = helper.getLevel().getRecipeManager().getAllRecipesFor(QMDRecipes.TARGET_CHAMBER_TYPE.get());
        var inputFluid = recipes.stream().flatMap(recipe -> recipe.value().getFluidIngredients().stream())
                .flatMap(ingredient -> java.util.Arrays.stream(ingredient.getFluidsRaw())).filter(stack -> !stack.isEmpty()).findFirst().orElseThrow();
        var outputFluid = recipes.stream().flatMap(recipe -> recipe.value().getFluidProducts().stream())
                .flatMap(ingredient -> java.util.Arrays.stream(ingredient.getFluidsRaw())).filter(stack -> !stack.isEmpty()).findFirst().orElseThrow();
        var outputBucket = FluidUtil.getFilledBucket(outputFluid.copyWithAmount(1000));
        menu.setCarried(FluidUtil.getFilledBucket(inputFluid.copyWithAmount(1000)));
        helper.assertTrue(menu.clickMenuButton(player, 2) && menu.getCarried().is(Items.BUCKET)
                        && chamber.tank(0).getFluidAmount() == 1000,
                "The input tank widget must empty a carried bucket and return its container");
        chamber.tank(1).setFluid(outputFluid.copyWithAmount(1000));
        helper.assertTrue(menu.clickMenuButton(player, 3) && ItemStack.isSameItemSameComponents(menu.getCarried(), outputBucket) && chamber.tank(1).isEmpty(),
                "The output tank widget must fill a carried bucket without duplicating fluid");
        chamber.tank(1).setFluid(outputFluid.copyWithAmount(500));
        helper.assertTrue(menu.clickMenuButton(player, 11) && chamber.tank(0).isEmpty() && chamber.tank(1).getFluidAmount() == 500,
                "Clearing one tank must preserve the adjacent output");
        chamber.inventory().setStackInSlot(1, new ItemStack(Items.IRON_INGOT));
        var stranger = helper.makeMockPlayer(GameType.CREATIVE); stranger.containerMenu = menu;
        helper.assertTrue(!menu.clickMenuButton(stranger, 10), "Only the menu owner may clear all materials");
        helper.assertTrue(menu.clickMenuButton(player, 10) && chamber.tank(1).isEmpty() && chamber.inventory().getStackInSlot(1).isEmpty(),
                "Clear-all must empty the actual machine inventory and tanks");
        helper.assertTrue(player.getInventory().countItem(Items.DIAMOND) == 7 && ItemStack.isSameItemSameComponents(menu.getCarried(), outputBucket),
                "Clearing the machine must preserve player inventory and the carried container");
        helper.setBlock(pos, Blocks.AIR);
        helper.assertTrue(!menu.clickMenuButton(player, 10), "Removed controllers reject actions from stale screens");
        helper.succeed();
    }
}
