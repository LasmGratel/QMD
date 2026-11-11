package lach_01298.qmd.block.entity;

import lach_01298.qmd.block.BeamlineBlock;
import lach_01298.qmd.capability.QMDParticleCapabilities;
import lach_01298.qmd.particle.ParticleStack;
import lach_01298.qmd.particle.Particles;
import lach_01298.qmd.registry.QMDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("qmd")
@PrefixGameTestTemplate(false)
public final class BeamlineGameTests {
    private static void line(GameTestHelper h) { for(int x=1;x<=3;x++)h.setBlock(new BlockPos(x,2,2),QMDBlocks.BEAMLINE.get()); }
    @GameTest(template="empty",batch="qmd.beamline",timeoutTicks=40)
    public static void wholeLineAttenuatesOnceAndExtractionConsumesSharedSample(GameTestHelper h) {
        line(h);var world=h.getLevel();BlockPos first=h.absolutePos(new BlockPos(1,2,2)),last=h.absolutePos(new BlockPos(3,2,2));
        var input=world.getCapability(QMDParticleCapabilities.BLOCK,first,Direction.WEST);var output=world.getCapability(QMDParticleCapabilities.BLOCK,last,Direction.EAST);
        h.assertTrue(input!=null&&output!=null&&world.getCapability(QMDParticleCapabilities.BLOCK,first,Direction.UP)==null,"Only the two axial sides expose beam transport");
        h.assertTrue(input.receiveParticle(Direction.WEST,new ParticleStack(Particles.proton,10000,Long.MAX_VALUE,10)),"Loaded line must accept a complete beam");
        ParticleStack beam=output.extractParticle(Direction.EAST);
        h.assertTrue(beam!=null&&beam.getAmount()==10000&&beam.getMeanEnergy()==Long.MAX_VALUE&&Math.abs(beam.getFocus()-9.88)<1e-6,"Three segments charge exactly one three-block attenuation and preserve long energy");
        h.assertTrue(input.extractParticle(Direction.WEST)==null,"Extraction from either endpoint consumes the same sample");h.succeed();
    }
    @GameTest(template="empty",batch="qmd.beamline",timeoutTicks=40)
    public static void topologyChangesAndExpiryCannotReplayStoredBeams(GameTestHelper h) {
        line(h);var world=h.getLevel();BlockPos first=h.absolutePos(new BlockPos(1,2,2)),last=h.absolutePos(new BlockPos(3,2,2));
        var input=world.getCapability(QMDParticleCapabilities.BLOCK,first,Direction.WEST);var output=world.getCapability(QMDParticleCapabilities.BLOCK,last,Direction.EAST);
        input.receiveParticle(Direction.WEST,new ParticleStack(Particles.electron,7,5000,10));h.setBlock(new BlockPos(2,2,2),Blocks.AIR);
        h.assertTrue(output.extractParticle(Direction.EAST)==null&&input.extractParticle(Direction.WEST)==null,"Breaking the line clears disconnected samples, including cached capability handles");
        input.receiveParticle(Direction.WEST,new ParticleStack(Particles.electron,7,5000,10));
        h.runAfterDelay(4,()->{h.assertTrue(output.getParticle()==null&&input.extractParticle(Direction.WEST)==null,"An unconsumed beam expires rather than powering downstream forever");h.succeed();});
    }
    @GameTest(template="empty",batch="qmd.beamline",timeoutTicks=40)
    public static void mismatchedAxesDoNotJoinAndSimulationDoesNotConsume(GameTestHelper h) {
        line(h);h.setBlock(new BlockPos(2,2,2),QMDBlocks.BEAMLINE.get().defaultBlockState().setValue(BeamlineBlock.AXIS,Direction.Axis.Z));
        var world=h.getLevel();var input=world.getCapability(QMDParticleCapabilities.BLOCK,h.absolutePos(new BlockPos(1,2,2)),Direction.WEST);
        var far=world.getCapability(QMDParticleCapabilities.BLOCK,h.absolutePos(new BlockPos(3,2,2)),Direction.EAST);
        var beam=new ParticleStack(Particles.proton,5,8000,5);h.assertTrue(input.canReceiveParticle(Direction.WEST,beam)&&input.getParticle()==null,"Acceptance checks must not insert a beam");
        input.receiveParticle(Direction.WEST,beam);h.assertTrue(far.extractParticle(Direction.EAST)==null,"A perpendicular segment must not join two straight lines");h.succeed();
    }
}
