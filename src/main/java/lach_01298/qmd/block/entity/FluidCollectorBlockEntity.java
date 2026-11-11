package lach_01298.qmd.block.entity;

import com.nred.nuclearcraft.block_entity.energy.ITileEnergy;
import com.nred.nuclearcraft.block_entity.energyFluid.TileEnergyFluid;
import com.nred.nuclearcraft.block_entity.fluid.ITileFluid;
import com.nred.nuclearcraft.block_entity.internal.energy.EnergyConnection;
import com.nred.nuclearcraft.block_entity.internal.fluid.Tank;
import com.nred.nuclearcraft.block_entity.internal.fluid.TankSorption;
import javax.annotation.Nullable;
import lach_01298.qmd.config.QMDConfig;
import lach_01298.qmd.recipe.CollectorRecipe;
import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.Comparator;

/** Shared NC energy/fluid integration, generation, world-recipe refresh and persistence. */
public abstract class FluidCollectorBlockEntity extends TileEnergyFluid {

    public static final int TANK_CAPACITY = 32_000;
    public static final int SCAN_INTERVAL = 100;
    public static final int SCANNED_NEIGHBORS = 124;

    private final CollectorRecipe.Kind kind;
    private FluidStack output = FluidStack.EMPTY;
    private double efficiency;
    private int scanCountdown;
    private boolean processing;
    @Nullable
    private ResourceLocation recipeId;

    protected FluidCollectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
        CollectorRecipe.Kind kind) {
        super(
            type,
            pos,
            state,
            energyCapacity(kind),
            ITileEnergy.energyConnectionAll(EnergyConnection.IN),
            TANK_CAPACITY,
            ignored -> null,
            ITileFluid.fluidConnectionAll(TankSorption.OUT));
        this.kind = kind;
    }

    private static long energyCapacity(CollectorRecipe.Kind kind) {
        return 20L * configuredPower(kind);
    }

    private static int configuredPower(CollectorRecipe.Kind kind) {
        return kind == CollectorRecipe.Kind.LIQUID ? QMDConfig.LIQUID_COLLECTOR_POWER.get()
            : QMDConfig.ATMOSPHERE_COLLECTOR_POWER.get();
    }

    public final int getPowerPerTick() {
        return configuredPower(kind);
    }

    public final CollectorRecipe.Kind getCollectorKind() {
        return kind;
    }

    public final Tank getOutputTank() {
        return getTanks().getFirst();
    }

    @Override
    public boolean hasConfigurableFluidConnections() {
        return true;
    }

    public final double getEfficiency() {
        return efficiency;
    }

    public final int getScanCountdown() {
        return scanCountdown;
    }

    public final boolean isProcessing() {
        return processing;
    }

    @Nullable
    public final ResourceLocation getCollectionRecipeId() {
        return recipeId;
    }

    public final boolean hasSufficientEnergy() {
        return getEnergyStoredLong() >= getPowerPerTick();
    }

    /** Potential collection rate, matching the original empty-hand status even when the tank is full. */
    public final int getCollectionRate() {
        return hasSufficientEnergy() && !output.isEmpty() ? (int) (output.getAmount() * efficiency) : 0;
    }

    public final FluidStack getCollectionFluid() {
        return hasSufficientEnergy() ? output.copy() : FluidStack.EMPTY;
    }

    public final RecipeType<CollectorRecipe> getCollectorRecipeType() {
        return kind == CollectorRecipe.Kind.LIQUID ? QMDRecipes.LIQUID_COLLECTOR_TYPE.get()
            : QMDRecipes.ATMOSPHERE_COLLECTOR_TYPE.get();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide()) {
            scanCountdown = 0;
        }
    }

    public final void serverTick() {
        if (level == null || level.isClientSide()) {
            return;
        }
        refreshEnergyCapacity();
        if (--scanCountdown <= 0) {
            refreshCollection();
        }
        boolean wasProcessing = processing;
        processing = generateFluid();

        // Stored output remains available to adjacent tanks even when collection cannot run.
        int storedBeforePush = getOutputTank().getFluidAmount();
        pushFluid();
        if (wasProcessing != processing || storedBeforePush != getOutputTank().getFluidAmount()) {
            setChanged();
        }
    }

    private void refreshEnergyCapacity() {
        long capacity = energyCapacity(kind);
        if (getMaxEnergyStoredLong() != capacity) {
            getEnergyStorage().setStorageCapacity(capacity);
            getEnergyStorage().setMaxTransfer(capacity);
            getEnergyStorage().cullEnergyStored();
            setChanged();
        }
    }

    /** Force a world scan; normal ticking repeats it every five seconds, including after data reloads. */
    public final void refreshCollection() {
        if (level == null || level.isClientSide()) {
            return;
        }
        scanCountdown = SCAN_INTERVAL;
        // These columns also contain both quart-biome candidates used by Level.getBiome.
        // Refuse an incomplete scan rather than synchronously loading a neighboring chunk.
        if (!isScanAreaLoaded()) {
            output = FluidStack.EMPTY;
            recipeId = null;
            efficiency = 0D;
            setChanged();
            return;
        }
        BlockState sampledBlock = kind == CollectorRecipe.Kind.LIQUID ? level.getBlockState(worldPosition.below())
            : Blocks.AIR.defaultBlockState();
        CollectorRecipe.Input input = new CollectorRecipe.Input(
            sampledBlock,
            level.getBiome(worldPosition),
            level.dimension());
        RecipeHolder<CollectorRecipe> recipe = level.getRecipeManager()
            .getAllRecipesFor(getCollectorRecipeType())
            .stream()
            .filter(
                holder -> holder.value()
                    .matches(input, level))
            .min(
                Comparator.<RecipeHolder<CollectorRecipe>>comparingInt(
                    holder -> holder.value()
                        .priority())
                    .reversed()
                    .thenComparing(
                        holder -> holder.id()
                            .toString()))
            .orElse(null);
        output = recipe == null ? FluidStack.EMPTY
            : recipe.value()
                .output();
        recipeId = recipe == null ? null : recipe.id();
        efficiency = output.isEmpty() ? 0D : Math.clamp(scanEfficiency(sampledBlock), 0D, 1D);
        setChanged();
    }

    protected abstract double scanEfficiency(BlockState sampledBlock);

    private boolean isScanAreaLoaded() {
        int minChunkX = (worldPosition.getX() - 2) >> 4;
        int maxChunkX = (worldPosition.getX() + 2) >> 4;
        int minChunkZ = (worldPosition.getZ() - 2) >> 4;
        int maxChunkZ = (worldPosition.getZ() + 2) >> 4;
        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                if (level.getChunkSource()
                    .getChunkNow(chunkX, chunkZ) == null) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void pushFluidToSide(Direction side) {
        if (level == null || getOutputTank().isEmpty()) {
            return;
        }
        BlockPos neighbor = worldPosition.relative(side);
        if (level.getChunkSource()
            .getChunkNow(neighbor.getX() >> 4, neighbor.getZ() >> 4) != null) {
            super.pushFluidToSide(side);
        }
    }

    private boolean generateFluid() {
        int amount = getCollectionRate();
        Tank tank = getOutputTank();
        if (amount <= 0 || tank.isFull()
            || !tank.isEmpty() && !FluidStack.isSameFluidSameComponents(tank.getFluid(), output)) {
            return false;
        }
        int generated = tank.fill(output.copyWithAmount(amount), IFluidHandler.FluidAction.EXECUTE);
        if (generated <= 0) {
            return false;
        }
        // Charge only for successful generation, using this collector's own configured cost.
        getEnergyStorage().changeEnergyStored(-getPowerPerTick());
        setChanged();
        return true;
    }

    @Override
    public int receiveEnergy(int maxReceive, Direction side, boolean simulate) {
        int received = canReceiveEnergy(side) ? getEnergyStorage().receiveEnergy(maxReceive, simulate) : 0;
        if (!simulate && received > 0) {
            setChanged();
        }
        return received;
    }

    @Override
    public void onWrapperDrain(FluidStack drained, IFluidHandler.FluidAction action) {
        if (action.execute() && !drained.isEmpty()) {
            setChanged();
        }
    }

    @Override
    public CompoundTag writeAll(CompoundTag tag, HolderLookup.Provider registries) {
        super.writeAll(tag, registries);
        tag.putDouble("efficiency", efficiency);
        tag.putInt("scanCountdown", scanCountdown);
        tag.putBoolean("isProcessing", processing);
        return tag;
    }

    @Override
    public void readAll(CompoundTag tag, HolderLookup.Provider registries) {
        super.readAll(tag, registries);
        double savedEfficiency = tag.getDouble("efficiency");
        efficiency = Double.isFinite(savedEfficiency) ? Math.clamp(savedEfficiency, 0D, 1D) : 0D;
        processing = tag.getBoolean("isProcessing");
        // World and recipe data must be revalidated on the first server tick after loading.
        scanCountdown = 0;
        recipeId = null;
        output = FluidStack.EMPTY;
        getOutputTank().setTankCapacity(TANK_CAPACITY);
        getOutputTank().clampTankAmount();
        refreshEnergyCapacity();
    }
}
