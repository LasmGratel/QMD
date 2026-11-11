package lach_01298.qmd.item.player;

import lach_01298.qmd.menu.BeamMeterMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

/** Hand-held particle capability probe. Measurements are captured and displayed in a real menu. */
public final class BeamMeterItem extends Item {

    public BeamMeterItem() {
        this(new Properties());
    }

    public BeamMeterItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        return inspect(context);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return inspect(context);
    }

    private static InteractionResult inspect(UseOnContext context) {
        if (context.getPlayer() == null) return InteractionResult.PASS;
        BeamMeterReading reading = BeamMeterReading.measure(
            context.getLevel(),
            context.getClickedPos(),
            context.getPlayer()
                .isCrouching());
        if (reading == null) return InteractionResult.PASS;

        if (context.getPlayer() instanceof ServerPlayer player) {
            InteractionHand hand = context.getHand();
            int slot = hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : BeamMeterMenu.OFFHAND_SLOT;
            player
                .openMenu(
                    new SimpleMenuProvider(
                        (containerId, inventory,
                            ignored) -> new BeamMeterMenu(containerId, inventory, hand, slot, reading),
                        Component.translatable("menu.qmd.beam_meter")),
                    buffer -> BeamMeterMenu.writeOpeningData(buffer, hand, slot, reading));
        }
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }
}
