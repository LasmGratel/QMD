package lach_01298.qmd.block.menu;

import lach_01298.qmd.block.entity.CreativeParticleSourceBlockEntity;
import lach_01298.qmd.network.ParticleSourceStatePayload;
import lach_01298.qmd.particle.ParticleBeam;
import lach_01298.qmd.registry.QMDMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

/** The legacy source is a configuration-only menu with no inventory slots. */
public final class CreativeParticleSourceMenu extends AbstractContainerMenu {

    private final CreativeParticleSourceBlockEntity source;
    private final Player owner;
    private ParticleBeam displayedBeam;
    private ParticleBeam lastSent;

    public CreativeParticleSourceMenu(int id, Inventory inventory, CreativeParticleSourceBlockEntity source) {
        super(QMDMenus.CREATIVE_PARTICLE_SOURCE.get(), id);
        this.source = source;
        owner = inventory.player;
        displayedBeam = source.getBeam();
    }

    public CreativeParticleSourceMenu(int id, Inventory inventory, FriendlyByteBuf data) {
        this(id, inventory, requireSource(inventory.player, data.readBlockPos()));
        displayedBeam = ParticleBeam.STREAM_CODEC.decode(data);
    }

    private static CreativeParticleSourceBlockEntity requireSource(Player player, BlockPos pos) {
        if (player.level()
            .hasChunkAt(pos)
            && player.level()
                .getBlockEntity(pos) instanceof CreativeParticleSourceBlockEntity source) {
            return source;
        }
        throw new IllegalStateException("Cannot open particle source menu at " + pos);
    }

    public BlockPos getBlockPos() {
        return source.getBlockPos();
    }

    public ParticleBeam getBeam() {
        return owner.level().isClientSide ? displayedBeam : source.getBeam();
    }

    public ParticleBeam getDisplayedBeam() {
        return displayedBeam;
    }

    @Override
    public boolean stillValid(Player player) {
        return player == owner && player.isCreative()
            && !source.isRemoved()
            && source.getLevel() == player.level()
            && player.level()
                .hasChunkAt(getBlockPos())
            && player.level()
                .getBlockEntity(getBlockPos()) == source
            && player.distanceToSqr(getBlockPos().getCenter()) <= 64;
    }

    public boolean applySettings(Player player, ParticleBeam beam) {
        if (player.level().isClientSide || player.containerMenu != this || !stillValid(player)) return false;
        source.setBeam(beam);
        broadcastChanges();
        return true;
    }

    /** Only a matching open menu may receive an authoritative display snapshot. */
    public void receiveBeam(ParticleBeam beam) {
        displayedBeam = beam;
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        sendBeam(false);
    }

    @Override
    public void sendAllDataToRemote() {
        super.sendAllDataToRemote();
        sendBeam(true);
    }

    private void sendBeam(boolean force) {
        ParticleBeam beam = source.getBeam();
        if (owner instanceof ServerPlayer player && player.containerMenu == this
            && stillValid(player)
            && (force || !beam.equals(lastSent))) {
            PacketDistributor.sendToPlayer(player, new ParticleSourceStatePayload(containerId, getBlockPos(), beam));
            lastSent = beam;
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
