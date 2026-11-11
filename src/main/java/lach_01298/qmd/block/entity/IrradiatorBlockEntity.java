package lach_01298.qmd.block.entity;

import com.nred.nuclearcraft.capability.radiation.resistance.RadiationResistance;
import com.nred.nuclearcraft.capability.radiation.source.RadiationSource;
import javax.annotation.Nullable;
import lach_01298.qmd.block.IrradiatorBlock;
import lach_01298.qmd.block.menu.IrradiatorMenu;
import lach_01298.qmd.config.IrradiatorConfig;
import lach_01298.qmd.item.ParticleReservoirItem;
import lach_01298.qmd.recipe.IrradiatorRecipe;
import lach_01298.qmd.registry.QMDBlockEntities;
import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.Comparator;

/** A source-driven irradiation processor: no synthetic FE input and no item-count fuel consumption. */
public final class IrradiatorBlockEntity extends BlockEntity implements MenuProvider {

    public static final int INPUT = 0, FUEL = 1, OUTPUT = 2;

    public enum Sorption {

        NONE,
        INPUT,
        OUTPUT,
        BOTH;

        public boolean canInsert() {
            return this == INPUT || this == BOTH;
        }

        public boolean canExtract() {
            return this == OUTPUT || this == BOTH;
        }
    }

    public enum OutputMode {
        DEFAULT,
        VOID_EXCESS,
        VOID_ALL
    }

    private final Sorption[][] sorptions = new Sorption[6][3];
    private final ItemStackHandler inventory = new ItemStackHandler(3) {

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public int getSlotLimit(int slot) {
            return slot == FUEL ? 1 : 64;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return slot == INPUT ? acceptsInput(stack) : slot == FUEL && acceptsFuel(stack);
        }
    };
    private final RadiationResistance resistance = new RadiationResistance(0);
    private final RadiationSource radiation = new RadiationSource(0);
    private boolean redstoneControl = true, processing;
    private OutputMode outputMode = OutputMode.DEFAULT;
    private double progress, target = 200, speed;
    @Nullable
    private ResourceLocation workRecipe;

    public IrradiatorBlockEntity(BlockPos pos, BlockState state) {
        super(QMDBlockEntities.IRRADIATOR.get(), pos, state);
        for (Sorption[] face : sorptions) {
            face[INPUT] = Sorption.INPUT;
            face[FUEL] = Sorption.INPUT;
            face[OUTPUT] = Sorption.OUTPUT;
        }
    }

    public ItemStackHandler inventory() {
        return inventory;
    }

    public RadiationResistance resistance() {
        resistance.setBaseRadResistance(IrradiatorConfig.RADIATION_RESISTANCE.get());
        return resistance;
    }

    public RadiationSource radiationSource() {
        return radiation;
    }

    public double progress() {
        return progress;
    }

    public double target() {
        return target;
    }

    public double speed() {
        return speed;
    }

    public boolean processing() {
        return processing;
    }

    public boolean redstoneControl() {
        return redstoneControl;
    }

    public OutputMode outputMode() {
        return outputMode;
    }

    public Sorption sorption(Direction face, int slot) {
        return sorptions[face.ordinal()][slot];
    }

    public void toggleRedstoneControl() {
        redstoneControl = !redstoneControl;
        setChanged();
    }

    public void cycleOutputMode() {
        outputMode = OutputMode.values()[(outputMode.ordinal() + 1) % OutputMode.values().length];
        setChanged();
    }

    public void cycleSorption(Direction face, int slot) {
        if (slot < 0 || slot >= 3) return;
        Sorption current = sorptions[face.ordinal()][slot];
        sorptions[face.ordinal()][slot] = Sorption.values()[(current.ordinal() + 1) % Sorption.values().length];
        setChanged();
    }

    public boolean acceptsInput(ItemStack stack) {
        return find(QMDRecipes.IRRADIATOR_TYPE.get(), stack) != null;
    }

    public boolean acceptsFuel(ItemStack stack) {
        return stack.getItem() instanceof ParticleReservoirItem && ParticleReservoirItem.stored(stack) > 0
            && find(QMDRecipes.IRRADIATOR_FUEL_TYPE.get(), stack) != null;
    }

    @Nullable
    private RecipeHolder<IrradiatorRecipe> find(RecipeType<IrradiatorRecipe> type, ItemStack stack) {
        if (level == null || stack.isEmpty()) return null;
        return level.getRecipeManager()
            .getAllRecipesFor(type)
            .stream()
            .filter(
                holder -> holder.value()
                    .accepts(stack))
            .min(
                Comparator.comparing(
                    holder -> holder.id()
                        .toString()))
            .orElse(null);
    }

    public IItemHandler itemCapability(@Nullable Direction face) {
        return new IItemHandler() {

            @Override
            public int getSlots() {
                return 3;
            }

            @Override
            public ItemStack getStackInSlot(int slot) {
                return inventory.getStackInSlot(slot);
            }

            @Override
            public int getSlotLimit(int slot) {
                return inventory.getSlotLimit(slot);
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return (face == null || sorption(face, slot).canInsert()) && inventory.isItemValid(slot, stack);
            }

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                return isItemValid(slot, stack) ? inventory.insertItem(slot, stack, simulate) : stack;
            }

            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return face == null || sorption(face, slot).canExtract() ? inventory.extractItem(slot, amount, simulate)
                    : ItemStack.EMPTY;
            }
        };
    }

    public void serverTick() {
        if (level == null || level.isClientSide) return;
        if (outputMode == OutputMode.VOID_ALL && !inventory.getStackInSlot(OUTPUT)
            .isEmpty()) inventory.setStackInSlot(OUTPUT, ItemStack.EMPTY);
        autoPush();
        var recipe = find(QMDRecipes.IRRADIATOR_TYPE.get(), inventory.getStackInSlot(INPUT));
        var fuel = find(QMDRecipes.IRRADIATOR_FUEL_TYPE.get(), inventory.getStackInSlot(FUEL));
        processing = false;
        radiation.setRadiationLevel(0);
        speed = fuel == null ? 0
            : fuel.value()
                .multiplier();
        if (recipe == null) {
            progress = 0;
            workRecipe = null;
            updateActive();
            return;
        }
        if (workRecipe != null && !workRecipe.equals(recipe.id())) progress = 0;
        workRecipe = recipe.id();
        target = Math.min(
            Double.MAX_VALUE,
            (double) IrradiatorConfig.PROCESS_TIME.get() * recipe.value()
                .multiplier());
        if (!Double.isFinite(target)) target = Double.MAX_VALUE;
        progress = Math.min(progress, Math.nextDown(target));
        boolean halted = redstoneControl && level.hasNeighborSignal(worldPosition);
        ItemStack fuelStack = inventory.getStackInSlot(FUEL);
        if (!halted && fuel != null
            && fuelStack.getCount() == 1
            && fuelStack.getItem() instanceof ParticleReservoirItem reservoir
            && ParticleReservoirItem.stored(fuelStack) > 0
            && canAcceptOutput(
                recipe.value()
                    .output())) {
            double desiredWork = Math.min(speed, target - progress);
            int usage = IrradiatorConfig.FUEL_USAGE.get();
            int available = ParticleReservoirItem.stored(fuelStack);
            // Final fractional work uses the remaining source amount; no full free tick on exhaustion.
            double work = usage == 0 ? desiredWork : Math.min(desiredWork, (double) available / usage);
            int used = usage == 0 ? 0 : (int) Math.min(available, Math.ceil(work * usage));
            if (work > 0) {
                inventory.setStackInSlot(FUEL, reservoir.consume(fuelStack, used));
                progress = Math.min(target, progress + work);
                processing = true;
                radiation.setRadiationLevel(
                    Math.min(
                        Double.MAX_VALUE,
                        recipe.value()
                            .radiation() * work));
                if (progress >= target) {
                    inventory.extractItem(INPUT, 1, false);
                    putOutput(
                        recipe.value()
                            .output());
                    progress = 0;
                    autoPush();
                }
                setChanged();
            }
        } else if (!halted && progress > 0) {
            progress = Math.max(0, progress - 150D); // Legacy loseProgress rate.
            setChanged();
        }
        updateActive();
    }

    private boolean canAcceptOutput(ItemStack result) {
        if (outputMode == OutputMode.VOID_ALL) return true;
        ItemStack current = inventory.getStackInSlot(OUTPUT);
        if (!current.isEmpty() && !ItemStack.isSameItemSameComponents(current, result)) return false;
        return outputMode == OutputMode.VOID_EXCESS
            || current.getCount() + result.getCount() <= result.getMaxStackSize();
    }

    private void putOutput(ItemStack result) {
        if (outputMode == OutputMode.VOID_ALL) {
            inventory.setStackInSlot(OUTPUT, ItemStack.EMPTY);
            return;
        }
        ItemStack current = inventory.getStackInSlot(OUTPUT);
        ItemStack output = result
            .copyWithCount(Math.min(result.getMaxStackSize(), current.getCount() + result.getCount()));
        inventory.setStackInSlot(OUTPUT, output);
    }

    private void updateActive() {
        if (level != null && getBlockState().hasProperty(IrradiatorBlock.ACTIVE)
            && getBlockState().getValue(IrradiatorBlock.ACTIVE) != processing)
            level.setBlock(worldPosition, getBlockState().setValue(IrradiatorBlock.ACTIVE, processing), 2);
    }

    private void autoPush() {
        if (level == null) return;
        for (Direction face : Direction.values()) {
            BlockPos adjacent = worldPosition.relative(face);
            if (!level.hasChunkAt(adjacent)) continue;
            IItemHandler target = level.getCapability(Capabilities.ItemHandler.BLOCK, adjacent, face.getOpposite());
            if (target == null) continue;
            for (int slot = 0; slot < 3; slot++) {
                if (!sorption(face, slot).canExtract()) continue;
                ItemStack stack = inventory.getStackInSlot(slot);
                if (stack.isEmpty()) continue;
                ItemStack remaining = ItemHandlerHelper.insertItemStacked(target, stack.copy(), false);
                if (remaining.getCount() != stack.getCount()) inventory.setStackInSlot(slot, remaining);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putDouble("time", progress);
        tag.putBoolean("redstoneControl", redstoneControl);
        tag.putInt("OutputMode", outputMode.ordinal());
        if (workRecipe != null) tag.putString("WorkRecipe", workRecipe.toString());
        int[] modes = new int[18];
        for (int side = 0; side < 6; side++)
            for (int slot = 0; slot < 3; slot++) modes[side * 3 + slot] = sorptions[side][slot].ordinal();
        tag.putIntArray("Sorptions", modes);
        tag.putDouble("ShieldingResistance", resistance.getShieldingRadResistance());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        double saved = tag.getDouble("time");
        progress = Double.isFinite(saved) ? Math.max(0, saved) : 0;
        redstoneControl = !tag.contains("redstoneControl") || tag.getBoolean("redstoneControl");
        outputMode = OutputMode.values()[Math.clamp(tag.getInt("OutputMode"), 0, 2)];
        workRecipe = ResourceLocation.tryParse(tag.getString("WorkRecipe"));
        int[] modes = tag.getIntArray("Sorptions");
        if (modes.length == 18) for (int side = 0; side < 6; side++) for (int slot = 0; slot < 3; slot++)
            sorptions[side][slot] = Sorption.values()[Math.clamp(modes[side * 3 + slot], 0, 3)];
        double shielding = tag.getDouble("ShieldingResistance");
        resistance.setShieldingRadResistance(Double.isFinite(shielding) ? Math.max(0, shielding) : 0);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.qmd.irradiator");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new IrradiatorMenu(id, inventory, this);
    }
}
