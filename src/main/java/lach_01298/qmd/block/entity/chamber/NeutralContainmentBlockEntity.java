package lach_01298.qmd.block.entity.chamber;

import lach_01298.qmd.block.menu.chamber.NeutralContainmentMenu;
import lach_01298.qmd.multiblock.chamber.AbstractVacuumChamberState;
import lach_01298.qmd.multiblock.chamber.NeutralContainmentState;
import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;

public final class NeutralContainmentBlockEntity extends AbstractVacuumChamberBlockEntity {

    public NeutralContainmentBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    protected AbstractVacuumChamberState createVacuumState() {
        return new NeutralContainmentState(this);
    }

    @Override
    protected boolean acceptsFluid(int index, FluidStack stack) {
        if (level == null) return true;
        if (index < 2) return acceptsCoolant(index, stack);
        return acceptsRecipeFluid(QMDRecipes.NEUTRAL_CONTAINMENT_TYPE.get(), 0, false, false, stack)
            || index == 2 && (acceptsRecipeFluid(QMDRecipes.CELL_FILLING_TYPE.get(), 0, true, true, stack)
                || acceptsRecipeFluid(QMDRecipes.CELL_FILLING_TYPE.get(), 0, false, true, stack));
    }

    @Override
    public void syncVisuals() {
        if (level instanceof net.minecraft.server.level.ServerLevel server && !isRemoved()
            && server.hasChunkAt(worldPosition)) {
            var packet = net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
                .create(this, (tile, registries) -> visualTag(registries));
            for (var player : server.getChunkSource().chunkMap
                .getPlayers(new net.minecraft.world.level.ChunkPos(worldPosition), false))
                player.connection.send(packet);
        }
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new NeutralContainmentMenu(id, inventory, this);
    }
}
