package lach_01298.qmd.block.entity.accelerator;

import com.nred.nuclearcraft.block_entity.multiblock.AbstractPartBlockEntity;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockPartTypeProvider;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.PartPosition;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import lach_01298.qmd.block.accelerator.AbstractAcceleratorBlock;
import lach_01298.qmd.multiblock.accelerator.Accelerator;
import lach_01298.qmd.multiblock.accelerator.AcceleratorKind;
import lach_01298.qmd.multiblock.accelerator.AcceleratorPartType;
import lach_01298.qmd.particle.IParticleStackHandler;
import lach_01298.qmd.particle.ParticleStack;
import lach_01298.qmd.particle.ParticleStorage;
import lach_01298.qmd.particle.ParticleStorageAccelerator;
import lach_01298.qmd.particle.ParticleStorageSource;
import lach_01298.qmd.registry.QMDBlockEntities;
import lach_01298.qmd.registry.QMDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;

public abstract class AbstractAcceleratorBlockEntity extends AbstractPartBlockEntity<Accelerator>
    implements IMultiblockPartTypeProvider<Accelerator, AcceleratorPartType>, MenuProvider {

    public final ItemStackHandler inventory = new ItemStackHandler(6) {

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return slot < 2;
        }

        @Override
        public int getSlotLimit(int slot) {
            return partType().role == AcceleratorPartType.Role.SOURCE ? 1 : 64;
        }
    };
    public final FluidTank sourceTank = new FluidTank(16000) {

        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    };
    private long inputReceivedAt = Long.MIN_VALUE;
    public final ParticleStorage inputBeam = new ParticleStorageAccelerator() {

        @Override
        public boolean receiveParticle(Direction side, ParticleStack stack) {
            if (!super.receiveParticle(side, stack)) return false;
            inputReceivedAt = level == null ? Long.MIN_VALUE : level.getGameTime();
            return true;
        }
    };
    public final ParticleStorageSource outputBeam = new ParticleStorageSource();
    private int redstoneLevel;

    protected AbstractAcceleratorBlockEntity(BlockPos pos, BlockState state) {
        super(QMDBlockEntities.ACCELERATOR_PART.get(), pos, state);
    }

    public static AbstractAcceleratorBlockEntity create(BlockPos pos, BlockState state) {
        var type = ((lach_01298.qmd.block.accelerator.AbstractAcceleratorBlock) state.getBlock()).partType();
        if (type.role == AcceleratorPartType.Role.SOURCE) return new AcceleratorSourceBlockEntity(pos, state);
        if (type.role != AcceleratorPartType.Role.CONTROLLER) return new AcceleratorComponentBlockEntity(pos, state);
        return switch (type.kind) {
            case LINEAR -> new LinearAcceleratorBlockEntity(pos, state);
            case RING -> new RingAcceleratorBlockEntity(pos, state);
            case DECELERATOR -> new DeceleratorBlockEntity(pos, state);
            case DIVERTER -> new BeamDiverterBlockEntity(pos, state);
            case SPLITTER -> new BeamSplitterBlockEntity(pos, state);
            case SPECTROMETER -> new MassSpectrometerBlockEntity(pos, state);
        };
    }

    public AcceleratorPartType partType() {
        return QMDBlocks.acceleratorTypeOf(getBlockState().getBlock());
    }

    public Accelerator controller() {
        return getMultiblockController().orElse(null);
    }

    public Direction facing() {
        return getBlockState().getValue(AbstractAcceleratorBlock.FACING);
    }

    public boolean output() {
        return getBlockState().getValue(AbstractAcceleratorBlock.OUTPUT);
    }

    public int redstoneLevel() {
        return output() ? redstoneLevel : 0;
    }

    public void setRedstoneLevel(int amount) {
        int value = Math.clamp(amount, 0, 15);
        if (value == redstoneLevel) return;
        redstoneLevel = value;
        setChanged();
        if (level != null) {
            level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
            level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
        }
    }

    /** Direct server-script access replaces the old optional computer callbacks without a scripting dependency. */
    public CompoundTag computerSnapshot() {
        var acc = controller();
        return partType().role == AcceleratorPartType.Role.COMPUTER_PORT && acc != null && level != null
            ? acc.menuSnapshot(level.registryAccess())
            : new CompoundTag();
    }

    public boolean computerSetPercentage(int value) {
        var acc = controller();
        if (partType().role != AcceleratorPartType.Role.COMPUTER_PORT || level == null
            || level.isClientSide
            || acc == null
            || !acc.isAssembled()
            || value < -1
            || value > 100) return false;
        acc.percentage = value;
        acc.markReferenceCoordForUpdate();
        return true;
    }

    /** A pushed sample belongs to one machine tick; it cannot queue or survive a stopped upstream source. */
    public ParticleStack consumeInputBeam() {
        ParticleStack result = level != null && level.getGameTime() - inputReceivedAt >= 0
            && level.getGameTime() - inputReceivedAt <= 1 ? inputBeam.getParticle() : null;
        inputBeam.setParticleStack(null);
        inputReceivedAt = Long.MIN_VALUE;
        return result;
    }

    @Override
    public Accelerator createController() {
        return new Accelerator(java.util.Objects.requireNonNull(level));
    }

    @Override
    public Class<Accelerator> getControllerType() {
        return Accelerator.class;
    }

    @Override
    public boolean isGoodForPosition(PartPosition position, IMultiblockValidator validator) {
        var type = partType();
        if (type.isInterior()) return position == PartPosition.Interior;
        if (type.role == AcceleratorPartType.Role.CASING) return true;
        // The inward wall of a ring lies inside its cuboid bounding box and is checked by the topology validator.
        if (type.role == AcceleratorPartType.Role.GLASS) return true;
        if (position.isFace() || position == PartPosition.Interior) return true;
        return super.isGoodForPosition(position, validator);
    }

    public IEnergyStorage energyCapability() {
        return controller() != null && controller().isAssembled() ? controller().energy : null;
    }

    public IParticleStackHandler particleCapability(Direction side) {
        var acc = controller();
        if (acc == null || !acc.isAssembled()) return null;
        Direction outward = acc.outward(worldPosition);
        if (outward == null || side != null && side != outward) return null;
        return partType().role == AcceleratorPartType.Role.SYNCHROTRON_PORT || output() ? outputBeam : inputBeam;
    }

    public IItemHandler itemCapability() {
        var acc = controller();
        boolean spectrometer = acc != null && acc.isAssembled()
            && acc.kind == AcceleratorKind.SPECTROMETER
            && acc.controller != null;
        if (partType().role == AcceleratorPartType.Role.PROCESS_PORT)
            return spectrometer ? acc.controller.inventory : null;
        if (partType().role == AcceleratorPartType.Role.COLLECTOR && spectrometer) {
            int slot = Math.clamp(getBlockPos().getY() - acc.getMinY() - 2, 0, 3) + 2;
            return new RangedWrapper(acc.controller.inventory, slot, slot + 1);
        }
        if (partType().role == AcceleratorPartType.Role.SOURCE) return new RangedWrapper(menuInventory(), 0, 2);
        return partType().role == AcceleratorPartType.Role.CONTROLLER && partType().kind == AcceleratorKind.SPECTROMETER
            ? inventory
            : null;
    }

    public IItemHandlerModifiable menuInventory() {
        var acc = controller();
        return partType().role == AcceleratorPartType.Role.SOURCE && acc != null
            && acc.isAssembled()
            && acc.kind == AcceleratorKind.SPECTROMETER
            && acc.controller != null ? acc.controller.inventory : inventory;
    }

    public FluidTank sourceFluid() {
        var acc = controller();
        return partType().role == AcceleratorPartType.Role.SOURCE && acc != null
            && acc.isAssembled()
            && acc.kind == AcceleratorKind.SPECTROMETER ? acc.processFluids[0] : sourceTank;
    }

    public IFluidHandler fluidCapability() {
        if (partType().role == AcceleratorPartType.Role.SOURCE) return directional(sourceFluid(), true);
        var acc = controller();
        if (acc == null || !acc.isAssembled()) return null;
        return switch (partType().role) {
            case COOLANT_INLET -> directional(acc.coolantInput, true);
            case COOLANT_OUTLET -> directional(acc.coolantOutput, false);
            case COLLECTOR -> directional(
                acc.processFluids[Math.clamp(getBlockPos().getY() - acc.getMinY() - 2, 0, 3) + 1],
                false);
            default -> partType().role == AcceleratorPartType.Role.CONTROLLER
                || partType().role == AcceleratorPartType.Role.PROCESS_PORT ? directional(acc.processFluids[0], true)
                    : null;
        };
    }

    private static IFluidHandler directional(FluidTank tank, boolean input) {
        return new IFluidHandler() {

            @Override
            public int getTanks() {
                return 1;
            }

            @Override
            public FluidStack getFluidInTank(int n) {
                return n == 0 ? tank.getFluid()
                    .copy() : FluidStack.EMPTY;
            }

            @Override
            public int getTankCapacity(int n) {
                return n == 0 ? tank.getCapacity() : 0;
            }

            @Override
            public boolean isFluidValid(int n, FluidStack stack) {
                return n == 0 && input && tank.isFluidValid(stack);
            }

            @Override
            public int fill(FluidStack stack, FluidAction action) {
                return input ? tank.fill(stack, action) : 0;
            }

            @Override
            public FluidStack drain(FluidStack stack, FluidAction action) {
                return input ? FluidStack.EMPTY : tank.drain(stack, action);
            }

            @Override
            public FluidStack drain(int amount, FluidAction action) {
                return input ? FluidStack.EMPTY : tank.drain(amount, action);
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock()
            .getName();
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new lach_01298.qmd.block.menu.accelerator.AcceleratorPartMenu(id, inventory, this);
    }

    public void writeMenuData(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(worldPosition);
    }

    @Override
    public CompoundTag writeAll(CompoundTag tag, HolderLookup.Provider provider) {
        super.writeAll(tag, provider);
        tag.put("inventory", inventory.serializeNBT(provider));
        tag.put("sourceTank", sourceTank.writeToNBT(provider, new CompoundTag()));
        tag.putInt("redstoneLevel", redstoneLevel);
        return tag;
    }

    @Override
    public void readAll(CompoundTag tag, HolderLookup.Provider provider) {
        super.readAll(tag, provider);
        inventory.deserializeNBT(provider, tag.getCompound("inventory"));
        sourceTank.setFluid(FluidStack.EMPTY);
        sourceTank.readFromNBT(provider, tag.getCompound("sourceTank"));
        redstoneLevel = Math.clamp(tag.getInt("redstoneLevel"), 0, 15);
    }
}
