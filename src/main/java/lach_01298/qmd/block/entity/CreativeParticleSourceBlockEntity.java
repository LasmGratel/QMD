package lach_01298.qmd.block.entity;

import lach_01298.qmd.block.menu.CreativeParticleSourceMenu;
import lach_01298.qmd.capability.QMDParticleCapabilities;
import lach_01298.qmd.particle.ITileParticleStorage;
import lach_01298.qmd.particle.ParticleBeam;
import lach_01298.qmd.particle.ParticleStack;
import lach_01298.qmd.particle.ParticleStorageSource;
import lach_01298.qmd.registry.QMDBlockEntities;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/** A creative-only infinite source. Redstone halts adjacent emission, as in legacy. */
public final class CreativeParticleSourceBlockEntity extends BlockEntity implements MenuProvider, ITileParticleStorage {

    private final ParticleStorageSource storage = new ParticleStorageSource() {

        @Override
        public boolean canExtractParticle(Direction side) {
            return available() && super.canExtractParticle(side);
        }

        @Override
        public ParticleStack getParticle() {
            return available() ? super.getParticle() : null;
        }

        private boolean available() {
            return level == null || !isRemoved() && !isPowered();
        }
    };
    private final List<ParticleStorageSource> beams = List.of(storage);

    public CreativeParticleSourceBlockEntity(BlockPos pos, BlockState state) {
        super(QMDBlockEntities.CREATIVE_PARTICLE_SOURCE.get(), pos, state);
        storage.setTileEntity(this);
    }

    public void serverTick() {
        if (level == null || level.isClientSide || storage.getParticleStack() == null || isPowered()) return;
        for (Direction direction : Direction.values()) {
            BlockPos neighbor = worldPosition.relative(direction);
            if (!level.hasChunkAt(neighbor)) continue;
            var handler = level.getCapability(QMDParticleCapabilities.BLOCK, neighbor, direction.getOpposite());
            if (handler != null) handler.receiveParticle(direction.getOpposite(), storage.getParticle());
        }
    }

    /** Vanilla weak/strong-power rules, with loaded checks before every neighboring block read. */
    private boolean isPowered() {
        for (Direction direction : Direction.values()) {
            BlockPos neighbor = worldPosition.relative(direction);
            if (!level.hasChunkAt(neighbor)) continue;
            BlockState state = level.getBlockState(neighbor);
            if (state.getSignal(level, neighbor, direction) > 0) return true;
            if (state.shouldCheckWeakPower(level, neighbor, direction)) {
                for (Direction direct : Direction.values()) {
                    BlockPos signalPos = neighbor.relative(direct);
                    if (level.hasChunkAt(signalPos) && level.getDirectSignal(signalPos, direct) > 0) return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<ParticleStorageSource> getParticleBeams() {
        return beams;
    }

    public ParticleBeam getBeam() {
        return ParticleBeam.fromStack(storage.getParticleStack());
    }

    public void setBeam(ParticleBeam beam) {
        storage.setParticleStack(beam.toStack());
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
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.qmd.creative_particle_source");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return player.isCreative() ? new CreativeParticleSourceMenu(id, inventory, this) : null;
    }

    public void writeMenuData(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(worldPosition);
        ParticleBeam.STREAM_CODEC.encode(buffer, getBeam());
    }
}
