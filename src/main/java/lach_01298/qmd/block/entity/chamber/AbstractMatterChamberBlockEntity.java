package lach_01298.qmd.block.entity.chamber;

import lach_01298.qmd.particle.ParticleStack;
import lach_01298.qmd.physics.ParticlePhysics;
import lach_01298.qmd.recipe.chamber.AbstractChamberRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/** Work buffering and material conversion shared by target chambers and beam dumps. */
public abstract class AbstractMatterChamberBlockEntity extends AbstractParticleChamberBlockEntity {

    protected AbstractMatterChamberBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    protected abstract int workBufferCycles();

    protected abstract long incomingWork(ParticleStack input);

    @Override
    protected final void processMatter(AbstractChamberRecipe recipe, List<ParticleStack> incoming) {
        long added = incoming.getFirst() == null ? 0 : incomingWork(incoming.getFirst());
        long max = requiredWork > Long.MAX_VALUE / workBufferCycles() ? Long.MAX_VALUE
            : requiredWork * workBufferCycles();
        work = Math.min(max, ParticlePhysics.saturatedAdd(work, added));
        int cycles = 0;
        while (work >= requiredWork && matchesMatter(recipe) && canFit(recipe) && cycles++ < 1000) {
            produceMatter(recipe);
            work -= requiredWork;
        }
    }
}
