package lach_01298.qmd.item.player;

import io.netty.buffer.Unpooled;
import lach_01298.qmd.menu.BeamMeterMenu;
import lach_01298.qmd.particle.ParticleStack;
import lach_01298.qmd.particle.Particles;
import lach_01298.qmd.physics.ParticlePhysics;
import lach_01298.qmd.registry.QMDItems;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.ArrayList;
import java.util.List;

@GameTestHolder("qmd")
@PrefixGameTestTemplate(false)
public final class BeamMeterGameTests {
    private BeamMeterGameTests() {
    }

    @GameTest(template = "empty", batch = "qmd.player.beam_meter")
    public static void menuHasNoSlotsAndBelongsToTheExactHeldMeter(GameTestHelper helper) {
        Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
        owner.getInventory().selected = 2;
        owner.getInventory().setItem(2, new ItemStack(QMDItems.BEAM_METER.get()));
        owner.getInventory().setItem(10, new ItemStack(Items.DIAMOND, 17));
        owner.getInventory().setItem(35, new ItemStack(Items.GOLD_INGOT, 23));
        List<ItemStack> before = inventorySnapshot(owner);

        BeamMeterReading initial = BeamMeterReading.empty(9);
        BeamMeterMenu menu = new BeamMeterMenu(31, owner.getInventory(), InteractionHand.MAIN_HAND, 2, initial);
        helper.assertTrue(menu.slots.isEmpty(), "The read-only meter menu must expose no synthetic or player slots");
        assertInventory(helper, owner, before,
                "Constructing the meter menu must not move, duplicate, or consume inventory contents");
        helper.assertTrue(menu.stillValid(owner),
                "The owning player must keep the menu while the same selected hotbar slot holds the meter");

        Player other = helper.makeMockPlayer(GameType.SURVIVAL);
        other.getInventory().selected = 2;
        other.getInventory().setItem(2, new ItemStack(QMDItems.BEAM_METER.get()));
        helper.assertTrue(!menu.stillValid(other),
                "A different player must not inherit a menu even with a meter in the same inventory index");

        owner.getInventory().selected = 3;
        helper.assertTrue(!menu.stillValid(owner),
                "Changing the selected main-hand slot must invalidate the captured meter menu");
        owner.getInventory().selected = 2;
        owner.getInventory().setItem(2, ItemStack.EMPTY);
        helper.assertTrue(!menu.stillValid(owner),
                "Removing the meter from its captured hotbar slot must invalidate the menu");
        owner.getInventory().setItem(2, new ItemStack(QMDItems.BEAM_METER.get()));
        owner.setHealth(0F);
        helper.assertTrue(!menu.stillValid(owner), "A dead owner must not retain an interactive meter menu");

        Player offhandOwner = helper.makeMockPlayer(GameType.SURVIVAL);
        offhandOwner.getInventory().selected = 0;
        offhandOwner.setItemInHand(InteractionHand.OFF_HAND,
                new ItemStack(QMDItems.BEAM_METER.get()));
        BeamMeterMenu offhand = new BeamMeterMenu(32, offhandOwner.getInventory(),
                InteractionHand.OFF_HAND, BeamMeterMenu.OFFHAND_SLOT, initial);
        helper.assertTrue(offhand.stillValid(offhandOwner), "An offhand meter must own its menu through the offhand");
        offhandOwner.getInventory().selected = 7;
        helper.assertTrue(offhand.stillValid(offhandOwner),
                "Changing the selected hotbar slot must not invalidate an offhand meter");
        offhandOwner.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
        helper.assertTrue(!offhand.stillValid(offhandOwner),
                "Removing the captured offhand meter must invalidate its menu");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.player.beam_meter")
    public static void openingAndStatePayloadsRoundTripExactlyAndRespectMenuOwnership(GameTestHelper helper) {
        BeamMeterReading beam = BeamMeterReading.beam(
                new ParticleStack(Particles.proton, 12_345, 9_876_543_210L, 17.25D), 6);
        BeamMeterReading port = BeamMeterReading.chamberPort(3);

        FriendlyByteBuf opening = new FriendlyByteBuf(Unpooled.buffer());
        try {
            BeamMeterMenu.writeOpeningData(opening, InteractionHand.MAIN_HAND, 4, beam);
            Player reconstructedOwner = helper.makeMockPlayer(GameType.SURVIVAL);
            reconstructedOwner.getInventory().selected = 4;
            reconstructedOwner.getInventory().setItem(4, new ItemStack(QMDItems.BEAM_METER.get()));
            BeamMeterMenu reconstructed = new BeamMeterMenu(40, reconstructedOwner.getInventory(), opening);
            helper.assertTrue(reconstructed.hand() == InteractionHand.MAIN_HAND
                            && reconstructed.heldSlot() == 4 && reconstructed.reading().equals(beam)
                            && opening.readableBytes() == 0,
                    "Menu opening data must preserve the exact hand, hotbar slot, and initial reading");
        } finally {
            opening.release();
        }

        BeamMeterStatePayload first = new BeamMeterStatePayload(41, beam);
        BeamMeterStatePayload second = new BeamMeterStatePayload(42, port);
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            BeamMeterStatePayload.STREAM_CODEC.encode(buffer, first);
            BeamMeterStatePayload.STREAM_CODEC.encode(buffer, second);
            BeamMeterStatePayload decodedFirst = BeamMeterStatePayload.STREAM_CODEC.decode(buffer);
            helper.assertTrue(decodedFirst.equals(first) && buffer.readableBytes() > 0,
                    "The first meter payload must round-trip exactly without consuming the next payload");
            BeamMeterStatePayload decodedSecond = BeamMeterStatePayload.STREAM_CODEC.decode(buffer);
            helper.assertTrue(decodedSecond.equals(second) && buffer.readableBytes() == 0,
                    "The second meter payload must round-trip exactly and consume only its own bytes");
        } finally {
            buffer.release();
        }

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.getInventory().selected = 1;
        player.getInventory().setItem(1, new ItemStack(QMDItems.BEAM_METER.get()));
        BeamMeterMenu menu = new BeamMeterMenu(51, player.getInventory(), InteractionHand.MAIN_HAND, 1,
                BeamMeterReading.empty(0));
        player.containerMenu = menu;
        helper.assertTrue(BeamMeterStatePayload.apply(player, new BeamMeterStatePayload(51, beam))
                        && menu.reading().equals(beam),
                "A matching payload must update the current valid meter menu");
        helper.assertTrue(!BeamMeterStatePayload.apply(player, new BeamMeterStatePayload(52, port))
                        && menu.reading().equals(beam),
                "A payload for another container must not change the current meter display");
        player.containerMenu = player.inventoryMenu;
        helper.assertTrue(!BeamMeterStatePayload.apply(player, new BeamMeterStatePayload(51, port))
                        && menu.reading().equals(beam),
                "A payload arriving after menu closure must not update a stale meter menu");
        player.containerMenu = menu;
        player.getInventory().setItem(1, ItemStack.EMPTY);
        helper.assertTrue(!BeamMeterStatePayload.apply(player, new BeamMeterStatePayload(51, port))
                        && menu.reading().equals(beam),
                "A matching-container payload must still be rejected after the held meter is removed");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.player.beam_meter")
    public static void beamlineReadingAttenuatesAnIndependentSnapshotExactlyOnce(GameTestHelper helper) {
        ParticleStack source = new ParticleStack(Particles.proton, 10_000, 1_234_567_890_123L, 20D);
        ParticleStack expected = source.copy();
        int beamlineLength = 7;
        expected.addFocus(-ParticlePhysics.focusLoss(beamlineLength, expected));
        double expectedLossPerBlock = ParticlePhysics.focusLoss(1, expected);
        double expectedDistance = ParticlePhysics.travelDistance(expected);

        BeamMeterReading reading = BeamMeterReading.beam(source, beamlineLength);
        helper.assertTrue(reading.kind() == BeamMeterReading.Kind.BEAM
                        && reading.particleName().equals(Particles.proton.getName())
                        && reading.amount() == 10_000
                        && reading.meanEnergy() == 1_234_567_890_123L
                        && reading.beamlineLength() == beamlineLength,
                "A beamline snapshot must retain the measured species, count, energy, and complete line length");
        helper.assertTrue(reading.focus() == expected.getFocus()
                        && reading.focusLossPerBlock() == expectedLossPerBlock
                        && reading.travelDistance() == expectedDistance,
                "A beamline snapshot must apply full-line loss once, then report its per-block loss and range");
        helper.assertTrue(source.getParticle() == Particles.proton && source.getAmount() == 10_000
                        && source.getMeanEnergy() == 1_234_567_890_123L && source.getFocus() == 20D,
                "Measuring a beam must copy the capability sample without extracting or mutating it");
        helper.succeed();
    }

    private static List<ItemStack> inventorySnapshot(Player player) {
        List<ItemStack> result = new ArrayList<>();
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            result.add(player.getInventory().getItem(slot).copy());
        }
        return result;
    }

    private static void assertInventory(GameTestHelper helper, Player player, List<ItemStack> expected,
                                        String message) {
        helper.assertTrue(player.getInventory().getContainerSize() == expected.size(), message);
        for (int slot = 0; slot < expected.size(); slot++) {
            helper.assertTrue(ItemStack.matches(player.getInventory().getItem(slot), expected.get(slot)), message);
        }
    }
}
