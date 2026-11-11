package lach_01298.qmd.block.entity.liquefier;

import com.nred.nuclearcraft.block_entity.hx.AbstractHeatExchangerEntity;
import com.nred.nuclearcraft.block_entity.hx.IHeatExchangerController;
import com.nred.nuclearcraft.handler.BlockEntityMenuInfo;
import com.nred.nuclearcraft.multiblock.hx.HeatExchanger;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.PartPosition;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import lach_01298.qmd.block.menu.LiquefierMenu;
import lach_01298.qmd.multiblock.liquefier.LiquefierLogic;
import lach_01298.qmd.registry.QMDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import static com.nred.nuclearcraft.registration.BlockRegistration.FACING_ALL;

public final class LiquefierControllerBlockEntity extends AbstractHeatExchangerEntity
    implements IHeatExchangerController<LiquefierControllerBlockEntity> {

    private static final BlockEntityMenuInfo<LiquefierControllerBlockEntity> INFO = new BlockEntityMenuInfo<>(
        "liquefier_controller",
        LiquefierControllerBlockEntity.class,
        LiquefierMenu::new);
    private boolean renderer;

    public LiquefierControllerBlockEntity(BlockPos pos, BlockState state) {
        super(QMDBlockEntities.LIQUEFIER_CONTROLLER.get(), pos, state);
    }

    @Override
    public boolean isGoodForPosition(PartPosition position, IMultiblockValidator validator) {
        return position.isFace();
    }

    @Override
    public String getLogicID() {
        return "liquefier";
    }

    @Override
    public BlockEntityMenuInfo<LiquefierControllerBlockEntity> getContainerInfo() {
        return INFO;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.qmd.liquefier_controller");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new LiquefierMenu(id, inventory, this);
    }

    @Override
    public boolean canOpenGui(Level level, BlockPos pos, BlockState state) {
        return isMachineAssembled();
    }

    @Override
    public boolean isRenderer() {
        return renderer;
    }

    @Override
    public void setIsRenderer(boolean renderer) {
        this.renderer = renderer;
    }

    public LiquefierLogic liquefier() {
        return getLogic() instanceof LiquefierLogic logic ? logic : null;
    }

    @Override
    public void onPreMachineAssembled(HeatExchanger controller) {
        super.onPreMachineAssembled(controller);
        if (!level.isClientSide) getPartPosition().getDirection()
            .ifPresent(dir -> level.setBlock(worldPosition, getBlockState().setValue(FACING_ALL, dir), 2));
    }

    @Override
    public void onBlockNeighborChanged(BlockState state, Level level, BlockPos pos, BlockPos neighbor) {
        super.onBlockNeighborChanged(state, level, pos, neighbor);
        if (getLogic() != null) getLogic().setIsExchangerOn();
    }
}
