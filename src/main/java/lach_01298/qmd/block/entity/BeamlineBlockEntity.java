package lach_01298.qmd.block.entity;

import lach_01298.qmd.block.BeamlineBlock;
import lach_01298.qmd.particle.*;
import lach_01298.qmd.registry.QMDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * All collinear loaded segments resolve one shared bounded sample. The sample is consumed at
 * either end and attenuated exactly once by the whole length, independent of block tick order.
 */
public final class BeamlineBlockEntity extends BlockEntity implements ITileParticleStorage {

    private final ParticleStorageBeamline storage = new ParticleStorageBeamline(1);
    private long receivedAt = Long.MIN_VALUE;
    private BlockPos lastEnd;
    private final IParticleStackHandler handler = new IParticleStackHandler() {

        private BeamlineBlockEntity line(Direction side) {
            return side == null || side.getAxis() == axis() ? resolve() : null;
        }

        @Override
        public boolean canReceiveParticle(Direction side, ParticleStack stack) {
            var line = line(side);
            return line != null && line.storage.canReceiveParticle(side, stack);
        }

        @Override
        public boolean receiveParticle(Direction side, ParticleStack stack) {
            var line = line(side);
            if (line == null || !line.storage.receiveParticle(side, stack)) return false;
            line.receivedAt = level.getGameTime();
            return true;
        }

        @Override
        public boolean canExtractParticle(Direction side) {
            var line = line(side);
            return line != null && line.fresh() && line.storage.canExtractParticle(side);
        }

        @Override
        public ParticleStack extractParticle(Direction side) {
            return extractParticle(side, Integer.MAX_VALUE);
        }

        @Override
        public ParticleStack extractParticle(Direction side, int amount) {
            var line = line(side);
            return line != null && line.fresh() ? line.storage.extractParticle(side, amount) : null;
        }

        @Override
        public ParticleStack extractParticle(Direction side, Particle type) {
            return extractParticle(side, type, Integer.MAX_VALUE);
        }

        @Override
        public ParticleStack extractParticle(Direction side, Particle type, int amount) {
            var line = line(side);
            return line != null && line.fresh()
                && line.storage.getParticleStack() != null
                && line.storage.getParticleStack()
                    .getParticle() == type ? line.storage.extractParticle(side, amount) : null;
        }

        @Override
        public ParticleStack getParticle() {
            var line = resolve();
            return line != null && line.fresh() ? line.storage.getParticle() : null;
        }
    };

    public BeamlineBlockEntity(BlockPos pos, BlockState state) {
        super(QMDBlockEntities.BEAMLINE.get(), pos, state);
        storage.setTileEntity(this);
    }

    public Direction.Axis axis() {
        return getBlockState().getValue(BeamlineBlock.AXIS);
    }

    public IParticleStackHandler capability(Direction side) {
        return side == null || side.getAxis() == axis() ? handler : null;
    }

    private boolean fresh() {
        if (level == null || receivedAt == Long.MIN_VALUE) return false;
        long elapsed = level.getGameTime() - receivedAt;
        if (elapsed < 0 || elapsed > 2) {
            storage.setParticleStack(null);
            receivedAt = Long.MIN_VALUE;
            return false;
        }
        return true;
    }

    private BeamlineBlockEntity resolve() {
        if (level == null || isRemoved() || !level.hasChunkAt(worldPosition)) return null;
        Direction forward = Direction.get(Direction.AxisDirection.POSITIVE, axis()), backward = forward.getOpposite();
        BlockPos first = worldPosition, last = worldPosition;
        int length = 1;
        for (Direction d : new Direction[] { backward, forward }) {
            BlockPos current = worldPosition;
            while (true) {
                BlockPos next = current.relative(d);
                if (!level.hasChunkAt(next)) return null;
                BlockState state = level.getBlockState(next);
                if (!(state.getBlock() instanceof BeamlineBlock) || state.getValue(BeamlineBlock.AXIS) != axis()) break;
                if (++length > 4096) return null;
                current = next;
            }
            if (d == backward) first = current;
            else last = current;
        }
        if (!(level.getBlockEntity(first) instanceof BeamlineBlockEntity canonical)) return null;
        if (canonical.storage.getLength() != length || canonical.lastEnd != null && !canonical.lastEnd.equals(last)) {
            canonical.storage.setParticleStack(null);
            canonical.receivedAt = Long.MIN_VALUE;
        }
        canonical.storage.setLength(length);
        canonical.lastEnd = last;
        return canonical;
    }

    @Override
    public List<? extends ParticleStorage> getParticleBeams() {
        var line = resolve();
        if (line != null) line.fresh();
        return List.of(line == null ? storage : line.storage);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        storage.writeToNBT(tag, 0);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        storage.setParticleStack(null);
        storage.readFromNBT(tag, 0);
        receivedAt = Long.MIN_VALUE;
        lastEnd = null;
    }
}
