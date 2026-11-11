package lach_01298.qmd.multiblock;

import lach_01298.qmd.block.entity.chamber.AbstractChamberBlockEntity;
import lach_01298.qmd.block.entity.CreativeParticleSourceBlockEntity;
import lach_01298.qmd.block.menu.chamber.AbstractChamberMenu;
import lach_01298.qmd.capability.QMDParticleCapabilities;
import lach_01298.qmd.particle.ParticleBeam;
import lach_01298.qmd.registry.QMDBlocks;
import lach_01298.qmd.registry.QMDItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("qmd")
@PrefixGameTestTemplate(false)
public final class ChamberGameTests {
    private static final BlockPos BASE = new BlockPos(1, 2, 1);
    private static BlockPos pos(int x, int y, int z) { return BASE.offset(x, y, z); }
    private static void put(GameTestHelper helper, int x, int y, int z, ChamberPart part) { helper.setBlock(pos(x, y, z), QMDBlocks.CHAMBER_PARTS.get(part).get()); }
    private static AbstractChamberBlockEntity part(GameTestHelper helper, int x, int y, int z) { return (AbstractChamberBlockEntity) helper.getBlockEntity(pos(x, y, z)); }
    private static void radial(GameTestHelper helper, ChamberPart controller) {
        for (int x = 0; x < 3; x++) for (int y = 0; y < 3; y++) for (int z = 0; z < 3; z++) put(helper, x, y, z, ChamberPart.CASING);
        put(helper, 1, 1, 1, ChamberPart.CHAMBER);
        put(helper, 1, 0, 1, controller);
        put(helper, 1, 2, 1, ChamberPart.ENERGY_PORT);
        put(helper, 0, 1, 1, ChamberPart.BEAM_PORT);
    }
    @GameTest(template = "empty", batch = "qmd.chambers", timeoutTicks = 300)
    public static void beamDumpAssemblesConsumesEnergyAndProducesFluidThroughWorldCapabilities(GameTestHelper helper) {
        radial(helper, ChamberPart.BEAM_DUMP_CONTROLLER);
        put(helper, 2, 1, 1, ChamberPart.FLUID_PORT); part(helper, 2, 1, 1).togglePort();
        helper.setBlock(pos(-1, 1, 1), QMDBlocks.CREATIVE_PARTICLE_SOURCE.get());
        var source = (CreativeParticleSourceBlockEntity) helper.getBlockEntity(pos(-1, 1, 1));
        source.setBeam(new ParticleBeam("proton", 2000, 100, 8));
        helper.startSequence().thenWaitUntil(() -> helper.assertTrue(part(helper, 1, 0, 1).assembled(), "Physical 3x3x3 beam dump must assemble"))
                .thenExecute(() -> {
                    var energy = helper.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, helper.absolutePos(pos(1, 2, 1)), Direction.UP);
                    helper.assertTrue(energy != null && energy.receiveEnergy(100000, false) == 100000, "Energy port must route FE to the controller");
                    helper.runAfterDelay(5, () -> {
                        var controller = part(helper, 1, 0, 1);
                        var fluid = helper.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, helper.absolutePos(pos(2, 1, 1)), Direction.EAST);
                        helper.assertTrue(controller.active() && controller.energy() < 100000, "Actual ZeroCore world updates must charge process FE");
                        helper.assertTrue(fluid != null && fluid.getFluidInTank(0).getAmount() >= 4, "Two thousand protons per tick must produce hydrogen through the fluid outlet");
                        int amount = fluid.getFluidInTank(0).getAmount();
                        helper.assertTrue(fluid.drain(1, IFluidHandler.FluidAction.SIMULATE).getAmount() == 1 && fluid.getFluidInTank(0).getAmount() == amount, "Simulated fluid extraction must preserve output");
                        helper.setBlock(pos(0, 0, 0), Blocks.AIR);
                        helper.succeedWhen(() -> helper.assertTrue(!controller.assembled() && !controller.active(), "Shell break must stop the beam dump"));
                    });
                });
    }
    @GameTest(template = "empty", batch = "qmd.chambers", timeoutTicks = 300)
    public static void decayPortsPreserveOutputChannelsPersistenceAndMenuIdentity(GameTestHelper helper) {
        radial(helper, ChamberPart.DECAY_CONTROLLER);
        for (int[] xz : new int[][]{{1, 0}, {2, 1}, {1, 2}}) { put(helper, xz[0], 1, xz[1], ChamberPart.BEAM_PORT); part(helper, xz[0], 1, xz[1]).togglePort(); }
        helper.setBlock(pos(-1, 1, 1), QMDBlocks.CREATIVE_PARTICLE_SOURCE.get());
        ((CreativeParticleSourceBlockEntity) helper.getBlockEntity(pos(-1, 1, 1))).setBeam(new ParticleBeam("neutron", 1000, 300, 8));
        helper.startSequence().thenWaitUntil(() -> helper.assertTrue(part(helper, 1, 0, 1).assembled(), "Radial decay chamber must assemble"))
                .thenExecute(() -> {
                    var controller = part(helper, 1, 0, 1);
                    helper.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, helper.absolutePos(pos(1, 2, 1)), Direction.UP).receiveEnergy(100000, false);
                    helper.runAfterDelay(3, () -> {
                        helper.assertTrue(controller.outputBeams().size() == 3 && controller.outputBeams().getFirst().particleName().equals("proton"), "Neutron decay must preserve positive/neutral/negative channels");
                        var positive = part(helper, 1, 1, 0);
                        int before = positive.channel(); controller.rotateOutputs();
                        helper.assertTrue(before != positive.channel(), "Output rotation must swap physical side channels");
                        var saved = controller.saveWithFullMetadata(helper.getLevel().registryAccess());
                        var copy = AbstractChamberBlockEntity.create(controller.getBlockPos(), controller.getBlockState());
                        copy.loadWithComponents(saved, helper.getLevel().registryAccess());
                        helper.assertTrue(copy.energy() == controller.energy() && copy.outputBeams().equals(controller.outputBeams()), "Controller FE and exact beams must persist");
                        var player = helper.makeMockPlayer(GameType.CREATIVE); player.setPos(controller.getBlockPos().getX() + .5, controller.getBlockPos().getY() + .5, controller.getBlockPos().getZ() + .5);
                        var menu = (AbstractChamberMenu) controller.createMenu(12, player.getInventory(), player);
                        helper.assertTrue(menu.stillValid(player), "Nearby owner may open the full controller menu");
                        helper.setBlock(pos(1, 0, 1), Blocks.AIR);
                        helper.assertTrue(!menu.stillValid(player), "Replacing a controller must invalidate its old menu"); helper.succeed();
                    });
                });
    }
    @GameTest(template="accelerator",batch="qmd.chambers",timeoutTicks=400)
    public static void targetReactionsConsumeActualItemsAndProtectOutputAutomation(GameTestHelper h) {
        radial(h,ChamberPart.TARGET_CONTROLLER);put(h,2,1,1,ChamberPart.ITEM_PORT);part(h,2,1,1).togglePort();
        h.setBlock(pos(-1,1,1),QMDBlocks.CREATIVE_PARTICLE_SOURCE.get());
        ((CreativeParticleSourceBlockEntity)h.getBlockEntity(pos(-1,1,1))).setBeam(new ParticleBeam("proton",6250000,9000,20));
        h.startSequence().thenWaitUntil(()->h.assertTrue(part(h,1,0,1).assembled(),"Target chamber must assemble with item outlet"))
                .thenExecute(()->{
                    var controller=part(h,1,0,1);controller.inventory().setStackInSlot(0,new ItemStack(QMDItems.get("ingot_osmium").get(),2));
                    h.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK,part(h,1,2,1).getBlockPos(),Direction.UP).receiveEnergy(100000,false);
                    h.runAfterDelay(3,()->{
                        var output=controller.inventory().getStackInSlot(1);h.assertTrue(output.is(QMDItems.NUCLEAR_MATERIALS.get("isotope_iridium_192").get())&&output.getCount()==2&&controller.inventory().getStackInSlot(0).isEmpty(),"Actual beam work must convert two osmium ingots to iridium-192");
                        var cap=h.getLevel().getCapability(Capabilities.ItemHandler.BLOCK,part(h,2,1,1).getBlockPos(),Direction.EAST);
                        h.assertTrue(cap!=null&&cap.insertItem(0,output.copy(),false).getCount()==2&&cap.extractItem(0,1,true).getCount()==1&&controller.inventory().getStackInSlot(1).getCount()==2,"Item output must reject insertion and preserve simulated extraction");
                        cap.extractItem(0,1,false);h.assertTrue(controller.inventory().getStackInSlot(1).getCount()==1,"Real extraction must reach shared inventory");h.succeed();
                    });
                });
    }
    @GameTest(template="accelerator",batch="qmd.chambers",timeoutTicks=400)
    public static void collisionInputsAreShapelessAndOutputsRespectRealBeamPorts(GameTestHelper h) {
        for(int x=0;x<17;x++)for(int y=0;y<3;y++)for(int z=0;z<3;z++)if(y==0||y==2||z==0||z==2||x==0||x==16)put(h,x,y,z,ChamberPart.CASING);else h.setBlock(pos(x,y,z),Blocks.AIR);
        for(int x=1;x<16;x++)put(h,x,1,1,ChamberPart.CHAMBER);
        put(h,8,0,1,ChamberPart.COLLISION_CONTROLLER);put(h,8,2,1,ChamberPart.ENERGY_PORT);
        put(h,0,1,1,ChamberPart.BEAM_PORT);put(h,16,1,1,ChamberPart.BEAM_PORT);
        for(int x:new int[]{2,14})for(int z:new int[]{0,2}){put(h,x,1,z,ChamberPart.BEAM_PORT);part(h,x,1,z).togglePort();}
        h.setBlock(pos(-1,1,1),QMDBlocks.CREATIVE_PARTICLE_SOURCE.get());h.setBlock(pos(17,1,1),QMDBlocks.CREATIVE_PARTICLE_SOURCE.get());
        ((CreativeParticleSourceBlockEntity)h.getBlockEntity(pos(-1,1,1))).setBeam(new ParticleBeam("neutron",10000,1000,20));
        ((CreativeParticleSourceBlockEntity)h.getBlockEntity(pos(17,1,1))).setBeam(new ParticleBeam("proton",10000,1000,20));
        h.startSequence().thenWaitUntil(()->h.assertTrue(part(h,8,0,1).assembled(),"Seventeen-block collision apparatus must assemble"))
                .thenExecute(()->{
                    var controller=part(h,8,0,1);h.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK,part(h,8,2,1).getBlockPos(),Direction.UP).receiveEnergy(100000,false);
                    h.runAfterDelay(4,()->{
                        h.assertTrue(controller.active()&&controller.outputBeams().stream().anyMatch(b->b.particleName().equals("deuteron")),"Opposite unordered proton/neutron beams must synthesize deuterons");
                        var input=h.getLevel().getCapability(QMDParticleCapabilities.BLOCK,part(h,0,1,1).getBlockPos(),Direction.WEST);
                        h.assertTrue(input!=null&&!input.canExtractParticle(Direction.WEST)&&input.extractParticle(Direction.WEST)==null,"Input beam ports must expose receive only");
                        var output=h.getLevel().getCapability(QMDParticleCapabilities.BLOCK,part(h,2,1,0).getBlockPos(),Direction.NORTH);
                        h.assertTrue(output!=null&&!output.canReceiveParticle(Direction.NORTH,new ParticleBeam("proton",1,0,20).toStack()),"Output beam ports must reject input");
                        var ports=java.util.List.of(part(h,2,1,0),part(h,2,1,2),part(h,14,1,0),part(h,14,1,2));
                        var original=ports.stream().map(AbstractChamberBlockEntity::channel).toList();
                        h.assertTrue(new java.util.HashSet<>(original).equals(java.util.Set.of(0,1,2,3)),"Physical collision outlets must expose every product channel exactly once");
                        for(int rotation=1;rotation<=4;rotation++){
                            controller.rotateOutputs();
                            for(int index=0;index<ports.size();index++)h.assertTrue(ports.get(index).channel()==(original.get(index)+rotation)%4,"Each rotation must advance every physical outlet and wrap after four rotations");
                        }
                        h.succeed();
                    });
                });
    }
    private ChamberGameTests() {}
}
