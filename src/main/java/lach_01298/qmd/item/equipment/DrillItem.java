package lach_01298.qmd.item.equipment;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Fixed-radius pickaxe/shovel drill with charge-backed durability. */
public final class DrillItem extends Item implements EnergyEquipment {

    public static final int ENERGY_PER_BLOCK = 100;
    public static final int ENERGY_TIER = 3;

    private final int radius;
    private final int capacity;
    private final int miningLevel;
    private final float miningSpeed;

    public DrillItem(int radius, int capacity, int miningLevel, float miningSpeed) {
        super(
            new Item.Properties().stacksTo(1)
                .attributes(createAttributes()));
        this.radius = radius;
        this.capacity = capacity;
        this.miningLevel = miningLevel;
        this.miningSpeed = miningSpeed;
    }

    private static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
            .add(
                Attributes.ATTACK_DAMAGE,
                new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 1.0D, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .add(
                Attributes.ATTACK_SPEED,
                new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -2.8D, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .build();
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return EquipmentEnergy.has(stack, ENERGY_PER_BLOCK) && isEffective(state) ? miningSpeed : 1.0F;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        if (!isEffective(state)) return false;
        TagKey<Block> incorrect = EquipmentEnergy.has(stack, ENERGY_PER_BLOCK) && radius >= 1
            ? incorrectTag(miningLevel)
            : BlockTags.INCORRECT_FOR_STONE_TOOL;
        return !state.is(incorrect);
    }

    private static boolean isEffective(BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL);
    }

    private static TagKey<Block> incorrectTag(int level) {
        if (level >= 4) return BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
        if (level >= 3) return BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
        if (level >= 2) return BlockTags.INCORRECT_FOR_IRON_TOOL;
        if (level >= 1) return BlockTags.INCORRECT_FOR_STONE_TOOL;
        return BlockTags.INCORRECT_FOR_WOODEN_TOOL;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        if (!level.isClientSide && state.getDestroySpeed(level, pos) != 0.0F
            && (!(miningEntity instanceof Player player) || !player.isCreative())) {
            EquipmentEnergy.extract(stack, ENERGY_PER_BLOCK);
        }
        return true;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return EquipmentEnergy.stored(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return EquipmentEnergy.barWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return EquipmentEnergy.barColor(stack);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue() {
        return 20;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 20;
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return enchantment.is(Enchantments.FORTUNE) || enchantment.is(Enchantments.SILK_TOUCH);
    }

    @Override
    public boolean isPrimaryItemFor(ItemStack stack, Holder<Enchantment> enchantment) {
        return supportsEnchantment(stack, enchantment);
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return true;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return ItemAbilities.DEFAULT_PICKAXE_ACTIONS.contains(itemAbility)
            || ItemAbilities.DEFAULT_SHOVEL_ACTIONS.contains(itemAbility);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(
            Component.translatable("info.qmd.item.energy_stored", EquipmentEnergy.stored(stack), energyCapacity()));
        tooltip.add(Component.translatable("info.qmd.item.energy_tier", ENERGY_TIER));
        int diameter = radius * 2 + 1;
        tooltip.add(Component.translatable("info.qmd.item.drill.desc", diameter, diameter));
        super.appendHoverText(stack, context, tooltip, flag);
    }

    public Set<BlockPos> additionalBlocks(BlockPos origin, Direction hitFace) {
        Set<BlockPos> positions = new LinkedHashSet<>();
        for (int first = -radius; first <= radius; first++) {
            for (int second = -radius; second <= radius; second++) {
                BlockPos pos = switch (hitFace.getAxis()) {
                    case Y -> origin.offset(first, 0, second);
                    case Z -> origin.offset(first, second, 0);
                    case X -> origin.offset(0, first, second);
                };
                if (!pos.equals(origin)) positions.add(pos);
            }
        }
        return positions;
    }

    public int radius() {
        return radius;
    }

    public int miningLevel() {
        return miningLevel;
    }

    public float miningSpeed() {
        return miningSpeed;
    }

    @Override
    public int energyCapacity() {
        return capacity;
    }
}
