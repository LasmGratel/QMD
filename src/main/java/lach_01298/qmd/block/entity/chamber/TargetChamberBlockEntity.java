package lach_01298.qmd.block.entity.chamber;

import lach_01298.qmd.block.menu.chamber.TargetChamberMenu;
import lach_01298.qmd.multiblock.ChamberReactions;
import lach_01298.qmd.particle.ParticleBeam;
import lach_01298.qmd.particle.ParticleStack;
import lach_01298.qmd.recipe.chamber.AbstractChamberRecipe;
import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public final class TargetChamberBlockEntity extends AbstractMatterChamberBlockEntity {

    public TargetChamberBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    protected RecipeType<? extends AbstractChamberRecipe> recipeType() {
        return QMDRecipes.TARGET_CHAMBER_TYPE.get();
    }

    @Override
    protected List<ParticleBeam> products(AbstractChamberRecipe recipe, List<ParticleStack> inputs, int length) {
        return ChamberReactions.radialProducts(recipe, inputs, efficiency, length, java.math.RoundingMode.HALF_UP);
    }

    @Override
    protected long threshold(AbstractChamberRecipe recipe) {
        return ChamberReactions.targetThreshold(recipe, efficiency);
    }

    @Override
    protected int workBufferCycles() {
        return 64;
    }

    @Override
    protected long incomingWork(ParticleStack input) {
        return input.getAmount();
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new TargetChamberMenu(id, inventory, this);
    }
}
