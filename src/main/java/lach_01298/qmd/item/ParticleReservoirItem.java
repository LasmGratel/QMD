package lach_01298.qmd.item;

import lach_01298.qmd.registry.QMDDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;

import java.util.List;
import java.util.function.Supplier;

/** A per-item particle reservoir. Consuming one reservoir never mutates the caller's inventory stack. */
public class ParticleReservoirItem extends Item {

    private final int capacity;
    private final Supplier<ItemStack> empty;

    public ParticleReservoirItem(Properties properties, int capacity, Supplier<ItemStack> empty) {
        super(properties);
        if (capacity <= 0) throw new IllegalArgumentException("Particle capacity must be positive");
        this.capacity = capacity;
        this.empty = empty;
    }

    public int capacity() {
        return capacity;
    }

    public static int stored(ItemStack stack) {
        if (!(stack.getItem() instanceof ParticleReservoirItem item)) return 0;
        Integer amount = stack.get(QMDDataComponents.PARTICLE_AMOUNT);
        if (amount != null) return Mth.clamp(amount, 0, item.capacity);
        CompoundTag legacy = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
            .copyTag();
        if (legacy.contains("particle_storage", CompoundTag.TAG_COMPOUND)) {
            return Mth.clamp(
                legacy.getCompound("particle_storage")
                    .getInt("particle_amount"),
                0,
                item.capacity);
        }
        // Plain crafted/loot source items begin full; an explicit zero component remains empty.
        return item.capacity;
    }

    public static void setStored(ItemStack stack, int amount) {
        if (!(stack.getItem() instanceof ParticleReservoirItem item)) return;
        stack.set(QMDDataComponents.PARTICLE_AMOUNT, Mth.clamp(amount, 0, item.capacity));
        CompoundTag legacy = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
            .copyTag();
        if (legacy.contains("particle_storage")) {
            legacy.remove("particle_storage");
            if (legacy.isEmpty()) stack.remove(DataComponents.CUSTOM_DATA);
            else stack.set(DataComponents.CUSTOM_DATA, CustomData.of(legacy));
        }
    }

    public ItemStack consume(ItemStack source, int amount) {
        if (source.getItem() != this || amount < 0 || amount > stored(source)) return ItemStack.EMPTY;
        int remaining = stored(source) - amount;
        if (remaining == 0) {
            ItemStack shell = empty.get();
            if (shell.isEmpty()) return shell;
            ItemStack result = source.transmuteCopy(shell.getItem(), 1);
            result.remove(QMDDataComponents.PARTICLE_AMOUNT);
            result.remove(QMDDataComponents.CELL_FLUID);
            CompoundTag custom = result.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .copyTag();
            custom.remove("particle_storage");
            if (custom.isEmpty()) result.remove(DataComponents.CUSTOM_DATA);
            else result.set(DataComponents.CUSTOM_DATA, CustomData.of(custom));
            return result;
        }
        ItemStack result = source.copyWithCount(1);
        setStored(result, remaining);
        return result;
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        setStored(stack, capacity);
        return stack;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stored(stack) < capacity;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13F * stored(stack) / capacity);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x55CCAA;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(
            Component.translatable("tooltip.qmd.particle_reservoir", stored(stack), capacity)
                .withStyle(ChatFormatting.DARK_GREEN));
    }
}
