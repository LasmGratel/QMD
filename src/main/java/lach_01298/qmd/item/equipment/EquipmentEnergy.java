package lach_01298.qmd.item.equipment;

import net.minecraft.ChatFormatting;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

/** FE access and durability-bar presentation shared by drills and HEV armor. */
public final class EquipmentEnergy {

    @Nullable
    public static IEnergyStorage storage(ItemStack stack) {
        return stack.getCapability(Capabilities.EnergyStorage.ITEM);
    }

    public static int stored(ItemStack stack) {
        IEnergyStorage energy = storage(stack);
        return energy == null ? 0 : energy.getEnergyStored();
    }

    public static int capacity(ItemStack stack) {
        IEnergyStorage energy = storage(stack);
        if (energy != null) return energy.getMaxEnergyStored();
        return stack.getItem() instanceof EnergyEquipment equipment ? equipment.energyCapacity() : 0;
    }

    public static boolean has(ItemStack stack, int amount) {
        IEnergyStorage energy = storage(stack);
        return energy != null && energy.extractEnergy(amount, true) == amount;
    }

    public static boolean consume(ItemStack stack, int amount) {
        IEnergyStorage energy = storage(stack);
        if (energy == null || energy.extractEnergy(amount, true) != amount) return false;
        energy.extractEnergy(amount, false);
        return true;
    }

    public static int extract(ItemStack stack, int amount) {
        IEnergyStorage energy = storage(stack);
        return energy == null ? 0 : energy.extractEnergy(amount, false);
    }

    public static int barWidth(ItemStack stack) {
        int capacity = capacity(stack);
        return capacity <= 0 ? 0 : Math.round(13.0F * Mth.clamp((float) stored(stack) / capacity, 0.0F, 1.0F));
    }

    public static int barColor(ItemStack stack) {
        int capacity = capacity(stack);
        float fraction = capacity <= 0 ? 0.0F : Mth.clamp((float) stored(stack) / capacity, 0.0F, 1.0F);
        return FastColor.ARGB32.lerp(fraction, ChatFormatting.RED.getColor(), ChatFormatting.GREEN.getColor());
    }

    private EquipmentEnergy() {}
}
