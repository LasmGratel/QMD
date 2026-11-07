package lach_01298.qmd.machine;

import com.nred.nuclearcraft.block_entity.internal.inventory.ItemOutputSetting;
import com.nred.nuclearcraft.payload.gui.ToggleItemOutputSettingPacket;
import io.netty.buffer.Unpooled;
import lach_01298.qmd.machine.client.OreLeacherGuiActions;
import lach_01298.qmd.registration.QMDMachines;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.network.connection.ConnectionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

/** Common GUI action and packet contracts; these checks never construct a client screen. */
@GameTestHolder("qmd")
@PrefixGameTestTemplate(false)
public final class OreLeacherGuiActionsGameTests {
    private static final ItemOutputSetting[] SETTINGS = {
            ItemOutputSetting.DEFAULT, ItemOutputSetting.VOID_EXCESS, ItemOutputSetting.VOID
    };
    private static final ItemOutputSetting[] FORWARD = {
            ItemOutputSetting.VOID_EXCESS, ItemOutputSetting.VOID, ItemOutputSetting.DEFAULT
    };
    private static final ItemOutputSetting[] REVERSE = {
            ItemOutputSetting.VOID, ItemOutputSetting.DEFAULT, ItemOutputSetting.VOID_EXCESS
    };

    private OreLeacherGuiActionsGameTests() {
    }

    @GameTest(template = "empty")
    public static void tankFacesFollowEveryHorizontalMachineFacing(GameTestHelper helper) {
        Direction[] facings = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        Direction[][] expected = {
                {Direction.DOWN, Direction.UP, Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH},
                {Direction.DOWN, Direction.UP, Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.WEST},
                {Direction.DOWN, Direction.UP, Direction.WEST, Direction.EAST, Direction.SOUTH, Direction.NORTH},
                {Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST}
        };
        for (int index = 0; index < facings.length; index++) {
            Direction[] faces = OreLeacherGuiActions.relativeDirections(facings[index]);
            helper.assertTrue(Arrays.equals(faces, expected[index]),
                    "Tank bottom/top/left/right/front/back must match machine facing " + facings[index]);
            helper.assertTrue(EnumSet.copyOf(Arrays.asList(faces)).equals(EnumSet.allOf(Direction.class)),
                    "Every world face must occur exactly once in the tank controls");
            for (int pair = 0; pair < faces.length; pair += 2) {
                helper.assertTrue(faces[pair].getOpposite() == faces[pair + 1],
                        "Bottom/top, left/right and front/back controls must address opposite faces");
            }
            Direction[] anotherDialog = OreLeacherGuiActions.relativeDirections(facings[index]);
            helper.assertTrue(faces != anotherDialog && Arrays.equals(faces, anotherDialog),
                    "Each tank dialog must receive its own direction array with the same mapping");
            faces[4] = Direction.DOWN;
            helper.assertTrue(Arrays.equals(anotherDialog, expected[index])
                            && Arrays.equals(OreLeacherGuiActions.relativeDirections(facings[index]), expected[index]),
                    "Mutating one tank dialog's directions must not corrupt another or later dialogs");
        }
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void outputClicksPersistBeforeExactlyOneSendInBothDirections(GameTestHelper helper) {
        OreLeacherBlockEntity machine = machine(helper, new BlockPos(1, 1, 1));
        seedOutputs(machine);
        for (int slot = 1; slot <= 3; slot++) {
            for (boolean reverse : new boolean[]{false, true}) {
                for (int index = 0; index < SETTINGS.length; index++) {
                    for (int output = 1; output <= 3; output++) {
                        machine.setItemOutputSetting(output, SETTINGS[output - 1]);
                    }
                    machine.setItemOutputSetting(slot, SETTINGS[index]);
                    ItemOutputSetting[] before = outputSettings(machine);
                    ItemStack[] itemsBefore = outputItems(machine);
                    ItemOutputSetting expected = (reverse ? REVERSE : FORWARD)[index];
                    int clickedSlot = slot;
                    List<ItemOutputSetting> sent = new ArrayList<>();
                    ItemOutputSetting selected = OreLeacherGuiActions.cycleItemOutputSetting(machine, slot, SETTINGS[index], reverse, setting -> {
                        helper.assertTrue(setting == expected && machine.getItemOutputSetting(clickedSlot) == expected,
                                "The callback must observe the selected mode already stored on the clicked output");
                        assertOtherSettings(helper, machine, clickedSlot, before);
                        sent.add(setting);
                    });
                    helper.assertTrue(selected == expected && machine.getItemOutputSetting(slot) == expected,
                            "Forward and reverse clicks must return and persist the expected mode from every starting mode");
                    helper.assertTrue(sent.equals(List.of(expected)),
                            "One click must transmit exactly one copy of its selected mode");
                    assertOtherSettings(helper, machine, slot, before);
                    assertItems(helper, machine, itemsBefore,
                            "The local preview must leave inventory changes, including VOID, to the server packet handler");
                }
            }
        }
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void outputPacketsPreserveOrVoidOnlyTheSelectedProduct(GameTestHelper helper) {
        OreLeacherBlockEntity preview = machine(helper, new BlockPos(1, 1, 1));
        OreLeacherBlockEntity authoritative = machine(helper, new BlockPos(3, 1, 1));
        for (int slot = 1; slot <= 3; slot++) {
            seedOutputs(authoritative);
            for (int output = 1; output <= 3; output++) {
                preview.setItemOutputSetting(output, ItemOutputSetting.DEFAULT);
                authoritative.setItemOutputSetting(output, ItemOutputSetting.DEFAULT);
            }
            int clickedSlot = slot;
            List<ToggleItemOutputSettingPacket> sent = new ArrayList<>();
            Consumer<ItemOutputSetting> sender = setting -> {
                helper.assertTrue(preview.getItemOutputSetting(clickedSlot) == setting,
                        "The packet must be built after the preview's setting changes");
                ToggleItemOutputSettingPacket packet = roundTrip(helper,
                        new ToggleItemOutputSettingPacket(preview, clickedSlot, setting));
                sent.add(packet);
                OutputPacketHandler.apply(packet, authoritative);
            };
            ItemStack[] original = outputItems(authoritative);

            OreLeacherGuiActions.cycleItemOutputSetting(preview, slot, preview.getItemOutputSetting(slot), false, sender);
            helper.assertTrue(authoritative.getItemOutputSetting(slot) == ItemOutputSetting.VOID_EXCESS,
                    "The received first forward click must select VOID_EXCESS on the correct product");
            assertItems(helper, authoritative, original, "VOID_EXCESS must preserve all existing output stacks");
            OutputPacketHandler.apply(sent.getLast(), authoritative);
            assertItems(helper, authoritative, original, "Acknowledging VOID_EXCESS again must preserve existing output");

            OreLeacherGuiActions.cycleItemOutputSetting(preview, slot, preview.getItemOutputSetting(slot), false, sender);
            helper.assertTrue(authoritative.getItemOutputSetting(slot) == ItemOutputSetting.VOID
                            && authoritative.getItem(slot).isEmpty(),
                    "The received VOID click must immediately clear the selected product");
            for (int output = 1; output <= 3; output++) {
                if (output != slot) {
                    helper.assertTrue(authoritative.getItemOutputSetting(output) == ItemOutputSetting.DEFAULT
                                    && ItemStack.matches(authoritative.getItem(output), original[output - 1]),
                            "Voiding one product must preserve both the mode and contents of all other products");
                }
            }
            ItemStack[] afterVoid = outputItems(authoritative);
            OutputPacketHandler.apply(sent.getLast(), authoritative);
            assertItems(helper, authoritative, afterVoid, "Reapplying VOID to an empty product must leave other products intact");

            authoritative.setItem(slot, new ItemStack(Items.DIAMOND, 7));
            ItemStack[] newlyProduced = outputItems(authoritative);
            OreLeacherGuiActions.cycleItemOutputSetting(preview, slot, preview.getItemOutputSetting(slot), false, sender);
            helper.assertTrue(authoritative.getItemOutputSetting(slot) == ItemOutputSetting.DEFAULT,
                    "The click after VOID must transmit DEFAULT rather than replay a stale destructive mode");
            assertItems(helper, authoritative, newlyProduced, "Returning to DEFAULT must retain newly added output");
            OutputPacketHandler.apply(sent.getLast(), authoritative);
            assertItems(helper, authoritative, newlyProduced, "Acknowledging DEFAULT again must retain newly added output");
            helper.assertTrue(sent.size() == 3, "Three clicks must produce exactly three packets, including one VOID packet");
        }
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void delayedAcknowledgementCannotRewindTheDialogSelection(GameTestHelper helper) {
        OreLeacherBlockEntity preview = machine(helper, new BlockPos(1, 1, 1));
        OreLeacherBlockEntity authoritative = machine(helper, new BlockPos(3, 1, 1));
        for (int slot = 1; slot <= 3; slot++) {
            for (boolean reverse : new boolean[]{false, true}) {
                seedOutputs(authoritative);
                for (int output = 1; output <= 3; output++) {
                    preview.setItemOutputSetting(output, ItemOutputSetting.DEFAULT);
                    authoritative.setItemOutputSetting(output, ItemOutputSetting.DEFAULT);
                }
                int clickedSlot = slot;
                List<ItemOutputSetting> sent = new ArrayList<>();
                Consumer<ItemOutputSetting> sender = setting -> {
                    helper.assertTrue(preview.getItemOutputSetting(clickedSlot) == setting,
                            "Every rapid click must update the preview before sending its selected mode");
                    sent.add(setting);
                    OutputPacketHandler.apply(roundTrip(helper,
                            new ToggleItemOutputSettingPacket(preview, clickedSlot, setting)), authoritative);
                };
                ItemOutputSetting first = reverse ? ItemOutputSetting.VOID : ItemOutputSetting.VOID_EXCESS;
                ItemOutputSetting second = reverse ? ItemOutputSetting.VOID_EXCESS : ItemOutputSetting.VOID;
                ItemOutputSetting selected = OreLeacherGuiActions.cycleItemOutputSetting(
                        preview, slot, ItemOutputSetting.DEFAULT, reverse, sender);
                // Neohaul sends full block-entity NBT after applying each mode packet.
                CompoundTag delayedAcknowledgement = authoritative.writeAll(new CompoundTag(), helper.getLevel().registryAccess());
                selected = OreLeacherGuiActions.cycleItemOutputSetting(preview, slot, selected, reverse, sender);
                helper.assertTrue(selected == second && authoritative.getItemOutputSetting(slot) == second,
                        "The dialog and server must both reach the second selected mode before the delayed acknowledgement");

                authoritative.setItem(slot, new ItemStack(Items.DIAMOND, 7));
                ItemStack[] newlyProduced = outputItems(authoritative);
                // This is the common state application used by the actual block-entity update path.
                // The dialog's local selection remains ahead of the acknowledged tile state.
                preview.readAll(delayedAcknowledgement, helper.getLevel().registryAccess());
                helper.assertTrue(preview.getItemOutputSetting(slot) == first && selected == second,
                        "An older server acknowledgement must be able to rewind the tile without rewinding the dialog selection");
                selected = OreLeacherGuiActions.cycleItemOutputSetting(preview, slot, selected, reverse, sender);
                helper.assertTrue(selected == ItemOutputSetting.DEFAULT
                                && preview.getItemOutputSetting(slot) == ItemOutputSetting.DEFAULT
                                && authoritative.getItemOutputSetting(slot) == ItemOutputSetting.DEFAULT,
                        "The next click must cycle the local selection to DEFAULT rather than cycle the stale tile mode");
                helper.assertTrue(sent.equals(List.of(first, second, ItemOutputSetting.DEFAULT)),
                        "Three rapid clicks must send exactly their three selected modes despite a delayed acknowledgement");
                assertItems(helper, authoritative, newlyProduced,
                        "Returning to DEFAULT after a delayed acknowledgement must not resend VOID and clear newly added output");
            }
        }
        helper.succeed();
    }

    private static OreLeacherBlockEntity machine(GameTestHelper helper, BlockPos relativePos) {
        helper.setBlock(relativePos, QMDMachines.ORE_LEACHER.get());
        OreLeacherBlockEntity machine = helper.getBlockEntity(relativePos);
        helper.assertTrue(machine != null, "The test must use the registered ore-leacher block entity");
        return machine;
    }

    private static void seedOutputs(OreLeacherBlockEntity machine) {
        machine.setItem(1, new ItemStack(Items.GOLD_INGOT, 11));
        machine.setItem(2, new ItemStack(Items.IRON_INGOT, 23));
        machine.setItem(3, new ItemStack(Items.COPPER_INGOT, 37));
    }

    private static ItemOutputSetting[] outputSettings(OreLeacherBlockEntity machine) {
        return new ItemOutputSetting[]{machine.getItemOutputSetting(1), machine.getItemOutputSetting(2), machine.getItemOutputSetting(3)};
    }

    private static ItemStack[] outputItems(OreLeacherBlockEntity machine) {
        return new ItemStack[]{machine.getItem(1).copy(), machine.getItem(2).copy(), machine.getItem(3).copy()};
    }

    private static void assertOtherSettings(GameTestHelper helper, OreLeacherBlockEntity machine,
                                            int clickedSlot, ItemOutputSetting[] before) {
        for (int output = 1; output <= 3; output++) {
            if (output != clickedSlot) {
                helper.assertTrue(machine.getItemOutputSetting(output) == before[output - 1],
                        "A click must change only its selected output's mode");
            }
        }
    }

    private static void assertItems(GameTestHelper helper, OreLeacherBlockEntity machine, ItemStack[] expected, String message) {
        for (int output = 1; output <= 3; output++) {
            helper.assertTrue(ItemStack.matches(machine.getItem(output), expected[output - 1]), message);
        }
    }

    private static ToggleItemOutputSettingPacket roundTrip(GameTestHelper helper, ToggleItemOutputSettingPacket packet) {
        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(),
                helper.getLevel().registryAccess(), ConnectionType.NEOFORGE);
        try {
            ToggleItemOutputSettingPacket.STREAM_CODEC.encode(buffer, packet);
            ToggleItemOutputSettingPacket decoded = ToggleItemOutputSettingPacket.STREAM_CODEC.decode(buffer);
            helper.assertTrue(buffer.readableBytes() == 0, "The output setting codec must consume exactly its packet");
            return decoded;
        } finally {
            buffer.release();
        }
    }

    /** Exercises the pinned handler's actual state mutation without a client connection. */
    private static final class OutputPacketHandler extends ToggleItemOutputSettingPacket.Handler {
        private static void apply(ToggleItemOutputSettingPacket packet, OreLeacherBlockEntity machine) {
            onPacket(packet, machine);
        }
    }
}
