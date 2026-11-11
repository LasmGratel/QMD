package lach_01298.qmd.item.equipment;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;

/** HEV armor piece with charged defense and the legacy suit protection metadata. */
public final class HevArmorItem extends ArmorItem implements EnergyEquipment {

    public static final int DEFAULT_COLOR = 0xFF8B2E;
    public static final int ENERGY_TIER = 3;
    public static final int CAPACITY = 1_000_000;

    private final int chargedDefense;
    private final int baseDefense;
    private final double radiationResistance;
    private final double specialProtection;

    public HevArmorItem(Holder<ArmorMaterial> material, Type type, int chargedDefense, int baseDefense,
        double radiationResistance, double specialProtection, Item.Properties properties) {
        super(material, type, properties.stacksTo(1));
        this.chargedDefense = chargedDefense;
        this.baseDefense = baseDefense;
        this.radiationResistance = radiationResistance;
        this.specialProtection = specialProtection;
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        ItemAttributeModifiers modifiers = super.getDefaultAttributeModifiers(stack);
        if (EquipmentEnergy.stored(stack) <= 0) return modifiers;

        EquipmentSlotGroup group = EquipmentSlotGroup.bySlot(getType().getSlot());
        ResourceLocation chargedId = ResourceLocation.fromNamespaceAndPath("qmd", "hev_charged." + getType().getName());
        modifiers = modifiers.withModifierAdded(
            Attributes.ARMOR,
            new AttributeModifier(chargedId, chargedDefense - baseDefense, AttributeModifier.Operation.ADD_VALUE),
            group);
        modifiers = modifiers.withModifierAdded(
            Attributes.ARMOR_TOUGHNESS,
            new AttributeModifier(chargedId, 4.0D, AttributeModifier.Operation.ADD_VALUE),
            group);
        if (getType() == Type.LEGGINGS) {
            modifiers = modifiers.withModifierAdded(
                Attributes.MOVEMENT_SPEED,
                new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath("qmd", "hev_movement_speed"),
                    0.25D,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                group);
        } else if (getType() == Type.BOOTS) {
            modifiers = modifiers.withModifierAdded(
                Attributes.STEP_HEIGHT,
                new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath("qmd", "hev_step_height"),
                    0.402D,
                    AttributeModifier.Operation.ADD_VALUE),
                group);
        }
        return modifiers;
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
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(
            Component.translatable("info.qmd.item.energy_stored", EquipmentEnergy.stored(stack), energyCapacity()));
        tooltip.add(Component.translatable("info.qmd.item.energy_tier", ENERGY_TIER));
        tooltip.add(Component.translatable(getDescriptionId(stack) + ".desc"));
        super.appendHoverText(stack, context, tooltip, flag);
    }

    public int chargedDefense() {
        return chargedDefense;
    }

    public int baseDefense() {
        return baseDefense;
    }

    public double radiationResistance() {
        return radiationResistance;
    }

    public double specialProtection() {
        return specialProtection;
    }

    @Override
    public int energyCapacity() {
        return CAPACITY;
    }
}
