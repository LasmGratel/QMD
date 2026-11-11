package lach_01298.qmd.block.entity.chamber;

import com.nred.nuclearcraft.block_entity.multiblock.AbstractPartBlockEntity;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.PartPosition;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import lach_01298.qmd.block.chamber.AbstractChamberBlock;
import lach_01298.qmd.block.menu.chamber.AbstractChamberMenu;
import lach_01298.qmd.capability.QMDParticleCapabilities;
import lach_01298.qmd.multiblock.ChamberMultiblock;
import lach_01298.qmd.multiblock.ChamberPart;
import lach_01298.qmd.multiblock.ChamberReactions;
import lach_01298.qmd.multiblock.chamber.AbstractVacuumChamberState;
import lach_01298.qmd.particle.IParticleStackHandler;
import lach_01298.qmd.particle.ParticleBeam;
import lach_01298.qmd.particle.ParticleStack;
import lach_01298.qmd.particle.ParticleStorageBeamline;
import lach_01298.qmd.recipe.chamber.AbstractChamberRecipe;
import lach_01298.qmd.registry.QMDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/** Shared physical chamber part. Only its controller owns process inventory, work and energy. */
public abstract class AbstractChamberBlockEntity extends AbstractPartBlockEntity<ChamberMultiblock>
    implements MenuProvider {

    private final ChamberPart role;
    private boolean output;
    private int channel;
    private boolean enabled = true, active, assembled;
    protected boolean swapOutputs;
    protected int outputRotation;
    private int lastRedstone;
    private Component validationError = Component.empty();
    private BlockPos visualMin = BlockPos.ZERO, visualMax = BlockPos.ZERO;
    private boolean laserAlongX;
    public double efficiency = 1;
    public int requiredPower = 5000;
    protected long work, requiredWork = 1;
    protected String recipeId = "";
    @Nullable
    protected AbstractChamberRecipe previousRecipe;
    private final ChamberEnergy energy = new ChamberEnergy();
    private final ItemStackHandler inventory = new ItemStackHandler(2) {

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return slot == 0;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private final FluidTank[] tanks = java.util.stream.IntStream.range(0, 8)
        .mapToObj(i -> new FluidTank(i == 0 ? 16000 : 1000) {

            @Override
            protected void onContentsChanged() {
                setChanged();
            }

            @Override
            public boolean isFluidValid(FluidStack stack) {
                return acceptsFluid(i, stack);
            }
        })
        .toArray(FluidTank[]::new);
    @Nullable
    private final AbstractVacuumChamberState vacuum;
    private final ParticleStorageBeamline beam = new ParticleStorageBeamline(0);
    private List<ParticleBeam> inputBeams = List.of(), outputBeams = List.of();

    protected AbstractChamberBlockEntity(BlockPos pos, BlockState state) {
        super(QMDBlockEntities.PARTICLE_CHAMBER.get(), pos, state);
        role = ((AbstractChamberBlock) state.getBlock()).role();
        vacuum = createVacuumState();
    }

    /** Retains the persisted qmd:particle_chamber type while restoring the concrete controller. */
    public static AbstractChamberBlockEntity create(BlockPos pos, BlockState state) {
        return switch (((AbstractChamberBlock) state.getBlock()).role()) {
            case TARGET_CONTROLLER -> new TargetChamberBlockEntity(pos, state);
            case DECAY_CONTROLLER -> new DecayChamberBlockEntity(pos, state);
            case COLLISION_CONTROLLER -> new CollisionChamberBlockEntity(pos, state);
            case BEAM_DUMP_CONTROLLER -> new BeamDumpBlockEntity(pos, state);
            case CONTAINMENT_CONTROLLER -> new NeutralContainmentBlockEntity(pos, state);
            case NUCLEOSYNTHESIS_CONTROLLER -> new NucleosynthesisChamberBlockEntity(pos, state);
            default -> new ChamberPartBlockEntity(pos, state);
        };
    }

    @Nullable
    protected AbstractVacuumChamberState createVacuumState() {
        return null;
    }

    public ChamberPart role() {
        return role;
    }

    @Nullable
    public AbstractVacuumChamberState vacuum() {
        return vacuum;
    }

    public void consumeEnergy(int amount) {
        energy.consume(amount);
        setChanged();
    }

    public void setInputBeams(List<ParticleStack> values) {
        inputBeams = values.stream()
            .map(ParticleBeam::fromStack)
            .toList();
    }

    public void setActive(boolean value) {
        active = value;
        if (level != null && !isRemoved()
            && level.getBlockEntity(worldPosition) == this
            && getBlockState().getValue(AbstractChamberBlock.ACTIVE) != active)
            level.setBlock(worldPosition, getBlockState().setValue(AbstractChamberBlock.ACTIVE, active), 3);
    }

    public boolean output() {
        return output;
    }

    public int channel() {
        return channel;
    }

    public void assignChannel(int value) {
        channel = value;
    }

    public boolean assembled() {
        return assembled;
    }

    public boolean active() {
        return active;
    }

    public boolean enabled() {
        return enabled;
    }

    public Component validationError() {
        return validationError;
    }

    public BlockPos visualMin() {
        return visualMin;
    }

    public BlockPos visualMax() {
        return visualMax;
    }

    public boolean laserAlongX() {
        return laserAlongX;
    }

    public void visualBounds(BlockPos minimum, BlockPos maximum, boolean alongX) {
        visualMin = minimum.immutable();
        visualMax = maximum.immutable();
        laserAlongX = alongX;
    }

    public int energy() {
        return energy.getEnergyStored();
    }

    public int energyCapacity() {
        return energy.getMaxEnergyStored();
    }

    public long work() {
        return work;
    }

    public long requiredWork() {
        return requiredWork;
    }

    public List<ParticleBeam> inputBeams() {
        return inputBeams;
    }

    public List<ParticleBeam> outputBeams() {
        return outputBeams;
    }

    public ItemStackHandler inventory() {
        return inventory;
    }

    public FluidTank tank(int index) {
        return tanks[index];
    }

    public void clearAllMaterial() {
        for (var tank : tanks) tank.setFluid(FluidStack.EMPTY);
        for (int i = 0; i < inventory.getSlots(); i++) inventory.setStackInSlot(i, ItemStack.EMPTY);
        inputBeams = List.of();
        outputBeams = ChamberReactions.emptyProducts(role().kind.particleOutputs);
        beam.setParticleStack(null);
        work = 0;
        if (vacuum != null) vacuum.clearMaterial();
        setChanged();
        syncOpenMenus();
    }

    public Direction outward() {
        return getBlockState().getValue(AbstractChamberBlock.FACING);
    }

    public void setOutward(Direction facing) {
        if (level != null && outward() != facing)
            level.setBlock(worldPosition, getBlockState().setValue(AbstractChamberBlock.FACING, facing), 3);
    }

    @Override
    public ChamberMultiblock createController() {
        return new ChamberMultiblock(java.util.Objects.requireNonNull(level));
    }

    @Override
    public Class<ChamberMultiblock> getControllerType() {
        return ChamberMultiblock.class;
    }

    @Nullable
    public AbstractChamberBlockEntity controller() {
        return role.controller() ? this
            : getMultiblockController().map(ChamberMultiblock::controller)
                .orElse(null);
    }

    @Override
    public boolean isGoodForPosition(PartPosition position, IMultiblockValidator validator) {
        boolean good = role.interior ? position == PartPosition.Interior
            : role.casing() ? position != PartPosition.Interior : position.isFace();
        if (!good) validator.setLastError(
            worldPosition,
            "qmd.multiblock_validation.chamber.invalid_part",
            worldPosition.getX(),
            worldPosition.getY(),
            worldPosition.getZ());
        return good;
    }

    public void formed(int capacity, int power, double efficiency) {
        assembled = true;
        requiredPower = power;
        this.efficiency = efficiency;
        energy.resize(capacity);
        setChanged();
    }

    public void broken() {
        if (vacuum != null) vacuum.broken();
        assembled = false;
        active = false;
        outputBeams = List.of();
        beam.setParticleStack(null);
        setChanged();
        syncVisuals();
    }

    public void togglePort() {
        output = !output;
        if (level != null) {
            level.setBlock(worldPosition, getBlockState().setValue(AbstractChamberBlock.ACTIVE, output), 3);
            level.invalidateCapabilities(worldPosition);
            getMultiblockController().ifPresent(ChamberMultiblock::requestValidation);
        }
        setChanged();
    }

    public void toggleEnabled() {
        enabled = !enabled;
        setChanged();
    }

    public void rotateOutputs() {
        swapOutputs = !swapOutputs;
        refreshOutputChannels();
    }

    protected final void refreshOutputChannels() {
        getMultiblockController().ifPresent(ChamberMultiblock::refreshChannels);
        setChanged();
    }

    public int outputChannel(int index) {
        return swapOutputs && index != 1 ? 2 - index : index;
    }

    public int redstoneLevel() {
        var controller = controller();
        if (role != ChamberPart.REDSTONE_PORT || controller == null
            || controller.vacuum == null
            || !controller.assembled) return 0;
        return Math.clamp(
            (int) (15
                * (output ? controller.vacuum.casingTemperature() / 2400D : controller.vacuum.temperature() / 110D)),
            0,
            15);
    }

    public void updateRedstone() {
        if (level == null || level.isClientSide) return;
        getMultiblockController().ifPresent(machine -> {
            for (var part : machine.parts(ChamberPart.REDSTONE_PORT)) {
                int signal = part.redstoneLevel();
                if (signal != part.lastRedstone) {
                    part.lastRedstone = signal;
                    level.updateNeighborsAt(
                        part.getBlockPos(),
                        part.getBlockState()
                            .getBlock());
                }
            }
        });
    }

    @Nullable
    public IEnergyStorage energyCapability() {
        AbstractChamberBlockEntity controller = controller();
        return role.energyPort() && controller != null ? controller.energy : null;
    }

    @Nullable
    public IItemHandler itemCapability() {
        AbstractChamberBlockEntity controller = controller();
        if (!role.itemPort() || controller == null) return null;
        return new IItemHandler() {

            @Override
            public int getSlots() {
                return 1;
            }

            @Override
            public ItemStack getStackInSlot(int slot) {
                return controller.inventory.getStackInSlot(output ? 1 : 0);
            }

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                return output ? stack : controller.inventory.insertItem(0, stack, simulate);
            }

            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return output ? controller.inventory.extractItem(1, amount, simulate) : ItemStack.EMPTY;
            }

            @Override
            public int getSlotLimit(int slot) {
                return 64;
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return !output;
            }
        };
    }

    @Nullable
    public IFluidHandler fluidCapability() {
        AbstractChamberBlockEntity controller = controller();
        if (!role.fluidPort() || controller == null) return null;
        return new IFluidHandler() {

            private FluidTank tank() {
                return controller.tanks[controller.vacuum == null ? (output ? 1 : 0) : fluidTankIndex()];
            }

            @Override
            public int getTanks() {
                return 1;
            }

            @Override
            public FluidStack getFluidInTank(int index) {
                return tank().getFluid();
            }

            @Override
            public int getTankCapacity(int index) {
                return tank().getCapacity();
            }

            @Override
            public boolean isFluidValid(int index, FluidStack stack) {
                return !output;
            }

            @Override
            public int fill(FluidStack stack, FluidAction action) {
                int filled = output ? 0 : tank().fill(stack, action);
                if (filled > 0 && action.execute()) controller.setChanged();
                return filled;
            }

            @Override
            public FluidStack drain(FluidStack stack, FluidAction action) {
                FluidStack result = output ? tank().drain(stack, action) : FluidStack.EMPTY;
                if (!result.isEmpty() && action.execute()) controller.setChanged();
                return result;
            }

            @Override
            public FluidStack drain(int amount, FluidAction action) {
                FluidStack result = output ? tank().drain(amount, action) : FluidStack.EMPTY;
                if (!result.isEmpty() && action.execute()) controller.setChanged();
                return result;
            }
        };
    }

    public int fluidTankIndex() {
        return role == ChamberPart.COOLANT_VENT ? (output ? 1 : 0)
            : role == ChamberPart.HEATER_VENT ? (output ? 3 : 2) : channel;
    }

    protected boolean acceptsFluid(int index, FluidStack stack) {
        return true;
    }

    protected final boolean acceptsRecipeFluid(RecipeType<? extends AbstractChamberRecipe> type, int position,
        boolean input, boolean unordered, FluidStack stack) {
        return level.getRecipeManager()
            .getAllRecipesFor(type)
            .stream()
            .anyMatch(holder -> {
                var ingredients = input ? holder.value()
                    .getFluidIngredients()
                    : holder.value()
                        .getFluidProducts();
                return unordered ? ingredients.stream()
                    .anyMatch(ingredient -> !ingredient.isEmpty() && ingredient.test(stack))
                    : position < ingredients.size() && !ingredients.get(position)
                        .isEmpty()
                        && ingredients.get(position)
                            .test(stack);
            });
    }

    @Nullable
    public IParticleStackHandler particleCapability(@Nullable Direction side) {
        if (!role.beamPort() || side != null && side != outward()) return null;
        return new IParticleStackHandler() {

            private boolean valid(Direction face) {
                return face == null || face == outward();
            }

            @Override
            public boolean receiveParticle(Direction face, ParticleStack stack) {
                return !output && valid(face) && beam.receiveParticle(face, stack);
            }

            @Override
            public boolean canReceiveParticle(Direction face, ParticleStack stack) {
                return !output && valid(face) && beam.canReceiveParticle(face, stack);
            }

            @Override
            public boolean canExtractParticle(Direction face) {
                return output && valid(face) && beam.canExtractParticle(face);
            }

            @Override
            public ParticleStack getParticle() {
                return beam.getParticle();
            }

            @Override
            public ParticleStack extractParticle(Direction face) {
                return canExtractParticle(face) ? beam.extractParticle(face) : null;
            }

            @Override
            public ParticleStack extractParticle(Direction face, lach_01298.qmd.particle.Particle type) {
                return canExtractParticle(face) ? beam.extractParticle(face, type) : null;
            }

            @Override
            public ParticleStack extractParticle(Direction face, int amount) {
                return canExtractParticle(face) ? beam.extractParticle(face, amount) : null;
            }

            @Override
            public ParticleStack extractParticle(Direction face, lach_01298.qmd.particle.Particle type, int amount) {
                return canExtractParticle(face) ? beam.extractParticle(face, type, amount) : null;
            }
        };
    }

    public ParticleStack pullBeam() {
        ParticleStack received = beam.extractParticle(null);
        if (received != null || level == null) return received;
        BlockPos neighbor = worldPosition.relative(outward());
        if (!level.hasChunkAt(neighbor)) return null;
        IParticleStackHandler handler = level
            .getCapability(QMDParticleCapabilities.BLOCK, neighbor, outward().getOpposite());
        return handler == null ? null : handler.extractParticle(outward().getOpposite());
    }

    public void publish(ParticleBeam value) {
        beam.setParticleStack(value.toStack());
        if (level == null || value.particleName()
            .isEmpty()) return;
        BlockPos neighbor = worldPosition.relative(outward());
        if (!level.hasChunkAt(neighbor)) return;
        IParticleStackHandler handler = level
            .getCapability(QMDParticleCapabilities.BLOCK, neighbor, outward().getOpposite());
        if (handler != null && handler.receiveParticle(outward().getOpposite(), value.toStack()))
            beam.setParticleStack(null);
    }

    /** Called once by the assembled ZeroCore controller, never by individual part tickers. */
    public abstract void processTick(List<AbstractChamberBlockEntity> inputs, List<AbstractChamberBlockEntity> outputs,
        int length);

    protected final void setOutputBeams(List<ParticleBeam> beams) {
        outputBeams = beams;
    }

    public void syncOpenMenus() {
        if (level == null || level.isClientSide) return;
        validationError = getMultiblockController().flatMap(ChamberMultiblock::getLastError)
            .map(error -> error.getChatMessage())
            .orElse(Component.empty());
        for (Player player : level.players())
            if (player instanceof ServerPlayer serverPlayer && player.containerMenu instanceof AbstractChamberMenu menu
                && menu.controller() == this) {
                    serverPlayer.connection.send(ClientboundBlockEntityDataPacket.create(this));
                }
    }

    public void syncVisuals() {}

    public CompoundTag visualTag(HolderLookup.Provider registries) {
        var tag = new CompoundTag();
        tag.putBoolean("qmdVisual", true);
        tag.putBoolean("assembled", assembled);
        tag.putBoolean("active", active);
        tag.putLong("visualMin", visualMin.asLong());
        tag.putLong("visualMax", visualMax.asLong());
        tag.putBoolean("laserX", laserAlongX);
        tag.put("material", tanks[2].writeToNBT(registries, new CompoundTag()));
        tag.putInt("materialCapacity", tanks[2].getCapacity());
        return tag;
    }

    private void readVisual(CompoundTag tag, HolderLookup.Provider registries) {
        assembled = tag.getBoolean("assembled");
        active = tag.getBoolean("active");
        visualMin = BlockPos.of(tag.getLong("visualMin"));
        visualMax = BlockPos.of(tag.getLong("visualMax"));
        laserAlongX = tag.getBoolean("laserX");
        tanks[2].setCapacity(Math.max(1, tag.getInt("materialCapacity")));
        tanks[2].readFromNBT(registries, tag.getCompound("material"));
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        if (tag.getBoolean("qmdVisual")) {
            readVisual(tag, registries);
            return;
        }
        super.loadAdditional(tag, registries);
    }

    @Override
    public void syncDataFrom(CompoundTag tag, HolderLookup.Provider registries,
        it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity.SyncReason reason) {
        if (tag.getBoolean("qmdVisual")) {
            readVisual(tag, registries);
            return;
        }
        super.syncDataFrom(tag, registries, reason);
    }

    public void dropInventory() {
        if (level == null || level.isClientSide || !role.controller()) return;
        for (int i = 0; i < inventory.getSlots(); i++) Containers.dropItemStack(
            level,
            worldPosition.getX(),
            worldPosition.getY(),
            worldPosition.getZ(),
            inventory.getStackInSlot(i));
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.qmd." + role.id);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag writeAll(CompoundTag tag, HolderLookup.Provider registries) {
        super.writeAll(tag, registries);
        tag.putBoolean("output", output);
        tag.putInt("channel", channel);
        tag.putInt("redstone", lastRedstone);
        if (role != null && role.controller()) {
            tag.putBoolean("enabled", enabled);
            tag.putBoolean("assembled", assembled);
            tag.putBoolean("active", active);
            tag.putBoolean("swapOutputs", swapOutputs);
            tag.putInt("outputRotation", outputRotation);
            tag.putString("validation", Component.Serializer.toJson(validationError, registries));
            tag.putLong("visualMin", visualMin.asLong());
            tag.putLong("visualMax", visualMax.asLong());
            tag.putBoolean("laserX", laserAlongX);
            tag.putInt("energy", energy.getEnergyStored());
            tag.putInt("capacity", energy.getMaxEnergyStored());
            tag.putInt("power", requiredPower);
            tag.putDouble("efficiency", efficiency);
            tag.putLong("work", work);
            tag.putLong("requiredWork", requiredWork);
            tag.putString("recipe", recipeId);
            tag.put("inventory", inventory.serializeNBT(registries));
            if (vacuum != null) tag.put("vacuum", vacuum.save());
            for (int i = 0; i < tanks.length; i++) {
                tag.put("tank" + i, tanks[i].writeToNBT(registries, new CompoundTag()));
                tag.putInt("tankCapacity" + i, tanks[i].getCapacity());
            }
            var beams = new net.minecraft.nbt.ListTag();
            for (ParticleBeam value : inputBeams) beams.add(
                value.toStack() == null ? new CompoundTag()
                    : value.toStack()
                        .writeToNBT(new CompoundTag()));
            tag.put("inputs", beams);
            beams = new net.minecraft.nbt.ListTag();
            for (ParticleBeam value : outputBeams) beams.add(
                value.toStack() == null ? new CompoundTag()
                    : value.toStack()
                        .writeToNBT(new CompoundTag()));
            tag.put("outputs", beams);
        }
        return tag;
    }

    @Override
    public void readAll(CompoundTag tag, HolderLookup.Provider registries) {
        super.readAll(tag, registries);
        output = tag.getBoolean("output");
        channel = Math.clamp(tag.getInt("channel"), 0, 7);
        lastRedstone = Math.clamp(tag.getInt("redstone"), 0, 15);
        if (role != null && role.controller()) {
            enabled = !tag.contains("enabled") || tag.getBoolean("enabled");
            assembled = tag.getBoolean("assembled");
            active = tag.getBoolean("active");
            swapOutputs = tag.getBoolean("swapOutputs");
            outputRotation = Math.floorMod(tag.getInt("outputRotation"), 4);
            try {
                validationError = tag.contains("validation")
                    ? Component.Serializer.fromJson(tag.getString("validation"), registries)
                    : Component.empty();
            } catch (Exception ignored) {
                validationError = Component.empty();
            }
            visualMin = BlockPos.of(tag.getLong("visualMin"));
            visualMax = BlockPos.of(tag.getLong("visualMax"));
            laserAlongX = tag.getBoolean("laserX");
            energy.resize(Math.max(40000, tag.getInt("capacity")));
            energy.load(tag.getInt("energy"));
            requiredPower = Math.max(0, tag.getInt("power"));
            efficiency = tag.getDouble("efficiency");
            if (!Double.isFinite(efficiency) || efficiency <= 0) efficiency = 1;
            work = Math.max(0, tag.getLong("work"));
            requiredWork = Math.max(1, tag.getLong("requiredWork"));
            recipeId = tag.getString("recipe");
            inventory.deserializeNBT(registries, tag.getCompound("inventory"));
            if (vacuum != null) vacuum.load(tag.getCompound("vacuum"));
            for (int i = 0; i < tanks.length; i++) {
                if (tag.contains("tankCapacity" + i)) tanks[i].setCapacity(Math.max(1, tag.getInt("tankCapacity" + i)));
                tanks[i].readFromNBT(registries, tag.getCompound("tank" + i));
            }
            inputBeams = readBeams(tag, "inputs");
            outputBeams = readBeams(tag, "outputs");
        }
    }

    private static List<ParticleBeam> readBeams(CompoundTag tag, String key) {
        var list = tag.getList(key, net.minecraft.nbt.Tag.TAG_COMPOUND);
        var result = new ArrayList<ParticleBeam>();
        for (int i = 0; i < Math.min(4, list.size()); i++)
            result.add(ParticleBeam.fromStack(ParticleStack.loadParticleStackFromNBT(list.getCompound(i))));
        return List.copyOf(result);
    }

    private final class ChamberEnergy extends EnergyStorage {

        ChamberEnergy() {
            super(40000, Integer.MAX_VALUE, 0);
        }

        @Override
        public int receiveEnergy(int amount, boolean simulate) {
            int received = super.receiveEnergy(amount, simulate);
            if (received > 0 && !simulate) setChanged();
            return received;
        }

        void resize(int capacity) {
            this.capacity = Math.max(40000, capacity);
            energy = Math.min(energy, this.capacity);
        }

        void consume(int amount) {
            energy = Math.max(0, energy - amount);
        }

        void load(int amount) {
            energy = Math.clamp(amount, 0, capacity);
        }
    }
}
