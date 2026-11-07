package lach_01298.qmd.machine;

import com.nred.nuclearcraft.registration.ItemRegistration;
import io.netty.buffer.Unpooled;
import lach_01298.qmd.recipe.QMDMachineRecipes;
import lach_01298.qmd.registration.QMDMachines;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.HashSet;

import static com.nred.nuclearcraft.registration.BlockRegistration.ACTIVE;
import static com.nred.nuclearcraft.registration.BlockRegistration.FACING_HORIZONTAL;

/** Headless coverage of the server menu contract and state changes that keep an open menu alive. */
@GameTestHolder("qmd")
@PrefixGameTestTemplate(false)
public final class OreLeacherMenuGameTests {
    private static final BlockPos MACHINE_POS = new BlockPos(1, 1, 1);
    private static final int MACHINE_SLOTS = 6;

    private OreLeacherMenuGameTests() {
    }

    @GameTest(template = "empty")
    public static void menuFactoryProvidesMachineAndPlayerSlots(GameTestHelper helper) {
        Fixture fixture = fixture(helper);
        OreLeacherMenu menu = fixture.menu;
        helper.assertTrue(menu.getType() == QMDMachines.ORE_LEACHER_MENU.get(),
                "The block entity menu factory must return the registered QMD menu type");
        helper.assertTrue(menu.slots.size() == 42, "The menu must expose six machine and 36 player inventory slots");
        int[][] positions = {{36, 11}, {112, 42}, {132, 42}, {152, 42}, {132, 64}, {152, 64}};
        for (int index = 0; index < MACHINE_SLOTS; ++index) {
            Slot slot = menu.getSlot(index);
            helper.assertTrue(slot.container == fixture.machine && slot.getContainerSlot() == index,
                    "Each machine slot must reference its matching persistent block entity inventory slot");
            helper.assertTrue(slot.x == positions[index][0] && slot.y == positions[index][1],
                    "Machine slots must retain the legacy layout coordinates");
        }
        var inventoryIndices = new HashSet<Integer>();
        for (int index = MACHINE_SLOTS; index < menu.slots.size(); ++index) {
            Slot slot = menu.getSlot(index);
            helper.assertTrue(slot.container == fixture.player.getInventory(), "Player slots must use this menu player's inventory");
            inventoryIndices.add(slot.getContainerSlot());
        }
        helper.assertTrue(inventoryIndices.size() == 36 && inventoryIndices.stream().allMatch(index -> index >= 0 && index < 36),
                "All 36 player inventory slots must appear exactly once");
        helper.assertTrue(menu.getSlot(0).mayPlace(new ItemStack(Items.GOLD_ORE))
                        && !menu.getSlot(0).mayPlace(new ItemStack(Items.DIAMOND)),
                "The ore input must accept matching recipes and reject unrelated items");
        for (int index = 1; index <= 3; ++index) {
            helper.assertTrue(!menu.getSlot(index).mayPlace(new ItemStack(Items.GOLD_INGOT)),
                    "Every product slot must reject manual insertion");
        }
        ItemStack speed = ItemRegistration.UPGRADE_MAP.get("speed").toStack();
        ItemStack energy = ItemRegistration.UPGRADE_MAP.get("energy").toStack();
        helper.assertTrue(menu.getSlot(4).mayPlace(speed) && !menu.getSlot(4).mayPlace(energy)
                        && menu.getSlot(5).mayPlace(energy) && !menu.getSlot(5).mayPlace(speed),
                "Upgrade slots must accept only their matching upgrade");
        helper.assertTrue(fixture.machine.getTileUpdatePacketListeners().contains(fixture.player),
                "Creating a menu must subscribe its player to processor updates");

        FriendlyByteBuf extraData = new FriendlyByteBuf(Unpooled.buffer());
        try {
            extraData.writeBlockPos(fixture.machine.getBlockPos());
            OreLeacherMenu reconstructed = new OreLeacherMenu(2, fixture.player.getInventory(), extraData);
            helper.assertTrue(reconstructed.getSlot(0).container == fixture.machine && reconstructed.getType() == menu.getType()
                            && reconstructed.slots.size() == menu.slots.size() && extraData.readableBytes() == 0,
                    "The network menu factory must resolve the encoded block position to the same machine");
            reconstructed.removed(fixture.player);
        } finally {
            extraData.release();
            menu.removed(fixture.player);
        }
        helper.assertTrue(!fixture.machine.getTileUpdatePacketListeners().contains(fixture.player),
                "Closing the menu must remove its processor update subscription");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void shiftClickRoutesOreProductsAndUpgradesWithoutLoss(GameTestHelper helper) {
        Fixture fixture = fixture(helper);
        OreLeacherMenu menu = fixture.menu;
        try {
            int mainSlot = fixture.playerSlot(9);
            menu.getSlot(mainSlot).set(new ItemStack(Items.GOLD_ORE, 7));
            menu.quickMoveStack(fixture.player, mainSlot);
            helper.assertTrue(fixture.machine.getItem(0).is(Items.GOLD_ORE) && fixture.machine.getItem(0).getCount() == 7
                            && menu.getSlot(mainSlot).getItem().isEmpty(),
                    "Shift-clicking ore must move the whole stack into the ore input");

            ItemStack speed = ItemRegistration.UPGRADE_MAP.get("speed").toStack(3);
            ItemStack energy = ItemRegistration.UPGRADE_MAP.get("energy").toStack(2);
            menu.getSlot(fixture.playerSlot(10)).set(speed.copy());
            menu.getSlot(fixture.playerSlot(11)).set(energy.copy());
            menu.quickMoveStack(fixture.player, fixture.playerSlot(10));
            menu.quickMoveStack(fixture.player, fixture.playerSlot(11));
            helper.assertTrue(ItemStack.matches(fixture.machine.getItem(4), speed)
                            && ItemStack.matches(fixture.machine.getItem(5), energy),
                    "Shift-clicking each upgrade must route its full count to the matching upgrade slot");
            helper.assertTrue(fixture.count(speed) == 0 && fixture.count(energy) == 0,
                    "Moved upgrades must leave the player inventory without duplication");

            menu.getSlot(fixture.playerSlot(12)).set(new ItemStack(Items.DIAMOND, 5));
            menu.quickMoveStack(fixture.player, fixture.playerSlot(12));
            helper.assertTrue(fixture.count(new ItemStack(Items.DIAMOND)) == 5
                            && fixture.machine.getItem(0).is(Items.GOLD_ORE),
                    "Unrelated items must remain within the player inventory and leave machine inputs intact");

            fixture.machine.setItem(1, new ItemStack(Items.GOLD_INGOT, 3));
            menu.quickMoveStack(fixture.player, 1);
            helper.assertTrue(fixture.machine.getItem(1).isEmpty() && fixture.count(new ItemStack(Items.GOLD_INGOT)) == 3,
                    "Shift-clicking a product must move its complete stack to the player inventory");
            menu.quickMoveStack(fixture.player, 4);
            menu.quickMoveStack(fixture.player, 5);
            menu.quickMoveStack(fixture.player, 0);
            helper.assertTrue(fixture.machine.getItem(4).isEmpty() && fixture.machine.getItem(5).isEmpty()
                            && fixture.machine.getItem(0).isEmpty() && fixture.count(speed) == 3 && fixture.count(energy) == 2
                            && fixture.count(new ItemStack(Items.GOLD_ORE)) == 7,
                    "Shift-clicking installed upgrades and input ore must return each item exactly once");

            for (int index = 0; index < 36; ++index) {
                fixture.player.getInventory().setItem(index, new ItemStack(Items.COBBLESTONE, 64));
            }
            fixture.machine.setItem(1, new ItemStack(Items.GOLD_INGOT, 3));
            menu.quickMoveStack(fixture.player, 1);
            helper.assertTrue(fixture.machine.getItem(1).is(Items.GOLD_INGOT)
                            && fixture.machine.getItem(1).getCount() == 3 && fixture.count(new ItemStack(Items.GOLD_INGOT)) == 0,
                    "A full player inventory must leave the output stack intact after a failed shift-click");
        } finally {
            menu.removed(fixture.player);
        }
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void menuValidityRejectsDistanceAndReplacedMachine(GameTestHelper helper) {
        Fixture fixture = fixture(helper);
        try {
            BlockPos pos = fixture.machine.getBlockPos();
            fixture.player.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            helper.assertTrue(fixture.menu.stillValid(fixture.player), "A nearby player must be able to use the current machine");
            fixture.player.setPos(pos.getX() + 8.6, pos.getY() + 0.5, pos.getZ() + 0.5);
            helper.assertTrue(!fixture.menu.stillValid(fixture.player), "Moving more than eight blocks away must invalidate the menu");
            fixture.player.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            helper.setBlock(MACHINE_POS, Blocks.AIR);
            helper.assertTrue(!fixture.menu.stillValid(fixture.player), "Removing the machine must invalidate its open menu");
            helper.setBlock(MACHINE_POS, QMDMachines.ORE_LEACHER.get());
            helper.assertTrue(helper.getBlockEntity(MACHINE_POS) != fixture.machine
                            && !fixture.menu.stillValid(fixture.player),
                    "A new ore leacher at the same position must not reactivate a menu for the removed block entity");
        } finally {
            fixture.menu.removed(fixture.player);
        }
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void facingAndActivityChangesKeepMachineInventoryAndProgress(GameTestHelper helper) {
        Fixture fixture = fixture(helper);
        try {
            OreLeacherBlockEntity machine = fixture.machine;
            machine.setItem(0, new ItemStack(Items.GOLD_ORE, 3));
            machine.getEnergyStorage().setEnergyStored(1234);
            machine.setCurrentTime(12.5);
            for (Direction facing : Direction.Plane.HORIZONTAL) {
                fixture.player.setYRot(facing.toYRot());
                var placement = QMDMachines.ORE_LEACHER.get().getStateForPlacement(new BlockPlaceContext(
                        fixture.player, InteractionHand.MAIN_HAND, QMDMachines.ORE_LEACHER_ITEM.toStack(),
                        new BlockHitResult(Vec3.atCenterOf(machine.getBlockPos()), Direction.UP, machine.getBlockPos(), false)));
                helper.assertTrue(placement != null && placement.getValue(FACING_HORIZONTAL) == facing.getOpposite()
                                && !placement.getValue(ACTIVE),
                        "Placement must face the player and start inactive for every horizontal direction");
                machine.setProperty(FACING_HORIZONTAL, facing);
                for (boolean active : new boolean[]{true, false}) {
                    machine.setActivity(active);
                    helper.assertTrue(helper.getBlockState(MACHINE_POS).getValue(FACING_HORIZONTAL) == facing
                                    && helper.getBlockState(MACHINE_POS).getValue(ACTIVE) == active,
                            "Every horizontal facing must retain its direction while changing active state");
                    helper.assertTrue(helper.getBlockEntity(MACHINE_POS) == machine && !machine.isRemoved(),
                            "Facing and activity transitions must preserve the same block entity");
                    helper.assertTrue(machine.getItem(0).getCount() == 3 && machine.getEnergyStoredLong() == 1234
                                    && machine.getCurrentTime() == 12.5 && fixture.menu.stillValid(fixture.player),
                            "State transitions must preserve stored items, energy, progress and the open menu");
                }
            }

            var recipe = helper.getLevel().getRecipeManager().getAllRecipesFor(QMDMachineRecipes.ORE_LEACHER_TYPE.get()).stream()
                    .filter(holder -> holder.id().equals(ResourceLocation.parse("qmd:ore_leacher/gold"))).findFirst().orElseThrow().value();
            for (int tank = 0; tank < 3; ++tank) {
                machine.getTanks().get(tank).setFluidStored(recipe.getFluidIngredients().get(tank).getStack());
            }
            machine.setCurrentTime(0);
            machine.refreshAll();
            machine.getEnergyStorage().setEnergyStored(machine.getMaxEnergyStoredLong());
            machine.update();
            helper.assertTrue(helper.getBlockState(MACHINE_POS).getValue(ACTIVE) && machine.getCurrentTime() > 0,
                    "Starting a real processing tick must activate the block state");
            double progress = machine.getCurrentTime();
            machine.fullHalt = true;
            machine.update();
            helper.assertTrue(!helper.getBlockState(MACHINE_POS).getValue(ACTIVE)
                            && machine.getCurrentTime() == progress && helper.getBlockEntity(MACHINE_POS) == machine,
                    "Halting processing must deactivate the state while retaining its block entity and partial progress");
        } finally {
            fixture.menu.removed(fixture.player);
        }
        helper.succeed();
    }

    private static Fixture fixture(GameTestHelper helper) {
        helper.setBlock(MACHINE_POS, QMDMachines.ORE_LEACHER.get());
        OreLeacherBlockEntity machine = helper.getBlockEntity(MACHINE_POS);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockPos pos = machine.getBlockPos();
        player.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        var menu = machine.createMenu(1, player.getInventory(), player);
        helper.assertTrue(menu instanceof OreLeacherMenu, "The block entity must construct its complete ore leacher menu");
        return new Fixture(machine, player, (OreLeacherMenu) menu);
    }

    private record Fixture(OreLeacherBlockEntity machine, Player player, OreLeacherMenu menu) {
        private int playerSlot(int inventoryIndex) {
            for (int slot = MACHINE_SLOTS; slot < menu.slots.size(); ++slot) {
                Slot candidate = menu.getSlot(slot);
                if (candidate.container == player.getInventory() && candidate.getContainerSlot() == inventoryIndex) return slot;
            }
            throw new IllegalStateException("Player inventory index absent from menu: " + inventoryIndex);
        }

        private int count(ItemStack wanted) {
            int total = 0;
            for (int slot = 0; slot < 36; ++slot) {
                ItemStack stack = player.getInventory().getItem(slot);
                if (ItemStack.isSameItemSameComponents(stack, wanted)) total += stack.getCount();
            }
            return total;
        }
    }
}
