package lach_01298.qmd.item.weapon;

import lach_01298.qmd.config.WeaponConfig;
import lach_01298.qmd.entity.GluonBeamEntity;
import lach_01298.qmd.registry.QMDDamageTypes;
import lach_01298.qmd.registry.QMDEntities;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.List;

public final class GluonGunItem extends ParticleWeaponItem {

    public GluonGunItem() {
        super("glueballs");
    }

    public static boolean silkTouch(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
            .copyTag()
            .getBoolean("SilkTouch");
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    private static Component getSilkTouchName() {
        return Component.translatable("enchantment.minecraft.silk_touch");
    }

    private static Component getFortuneName() {
        return Component.translatable("enchantment.minecraft.fortune")
            .copy()
            .append(CommonComponents.SPACE)
            .append(Component.translatable("enchantment.level.4"));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (silkTouch(stack)) {
            tooltip.add(getSilkTouchName());
        } else {
            tooltip.add(getFortuneName());
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack gun = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                boolean silk = !silkTouch(gun);
                CustomData.update(DataComponents.CUSTOM_DATA, gun, tag -> tag.putBoolean("SilkTouch", silk));
                if (silk) {
                    player.displayClientMessage(getSilkTouchName(), true);
                } else {
                    player.displayClientMessage(getFortuneName(), true);
                }
            }
            return InteractionResultHolder.sidedSuccess(gun, level.isClientSide);
        }
        if (!hasAmmunition(player, ammunitionSlot(player), WeaponConfig.GLUON_USAGE.get())) {
            return InteractionResultHolder.fail(gun);
        }
        player.startUsingItem(hand);
        if (!level.isClientSide) {
            GluonBeamEntity beam = new GluonBeamEntity(QMDEntities.GLUON_BEAM.get(), level);
            beam.configure(player, hand, WeaponConfig.GLUON_RANGE.get(), 0xFFFFFF);
            level.addFreshEntity(beam);
        }
        return InteractionResultHolder.consume(gun);
    }

    public void fire(Player player, InteractionHand hand, GluonBeamEntity beam) {
        int slot = ammunitionSlot(player);
        int usage = WeaponConfig.GLUON_USAGE.get();
        if (!hasAmmunition(player, slot, usage)) {
            player.stopUsingItem();
            beam.discard();
            return;
        }
        HitResult hit = trace(player, WeaponConfig.GLUON_RANGE.get(), true);
        beam.setLength(
            player.getEyePosition()
                .distanceTo(hit.getLocation()));
        if (hit instanceof EntityHitResult target) {
            target.getEntity()
                .hurt(
                    QMDDamageTypes
                        .source(QMDDamageTypes.GLUON, beam, player),
                    WeaponConfig.GLUON_DAMAGE.get()
                        .floatValue());
            irradiate(target.getEntity(), WeaponConfig.GLUON_RADIATION.get());
        } else if (hit instanceof BlockHitResult block && hit.getType() == HitResult.Type.BLOCK
            && player instanceof ServerPlayer serverPlayer) {
                mine(serverPlayer, player.getItemInHand(hand), block);
            }
        consume(player, slot, usage);
    }

    private static void mine(ServerPlayer player, ItemStack gun, BlockHitResult hit) {
        ServerLevel level = player.serverLevel();
        var pos = hit.getBlockPos();
        var state = level.getBlockState(pos);
        if (state.getDestroySpeed(level, pos) < 0 || !level.mayInteract(player, pos)
            || !player.mayUseItemAt(pos, hit.getDirection(), gun)
            || player.blockActionRestricted(level, pos, player.gameMode.getGameModeForPlayer())) return;
        var event = new BlockEvent.BreakEvent(level, pos, state, player);
        if (NeoForge.EVENT_BUS.post(event)
            .isCanceled()) return;
        var blockEntity = level.getBlockEntity(pos);
        if (!level.destroyBlock(pos, false, player)) return;
        if (!player.isCreative()) {
            ItemStack tool = new ItemStack(Items.DIAMOND_PICKAXE);
            var enchantments = level.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT);
            tool.enchant(
                enchantments.getHolderOrThrow(silkTouch(gun) ? Enchantments.SILK_TOUCH : Enchantments.FORTUNE),
                silkTouch(gun) ? 1 : 4);
            Block.dropResources(state, level, pos, blockEntity, player, tool);
        }
    }
}
