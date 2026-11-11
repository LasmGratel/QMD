package lach_01298.qmd.block.entity;

import lach_01298.qmd.block.entity.accelerator.AbstractAcceleratorBlockEntity;

import lach_01298.qmd.block.accelerator.AbstractAcceleratorBlock;
import lach_01298.qmd.block.menu.accelerator.AbstractAcceleratorMenu;
import lach_01298.qmd.capability.QMDParticleCapabilities;
import lach_01298.qmd.item.ParticleReservoirItem;
import lach_01298.qmd.multiblock.accelerator.Accelerator;
import lach_01298.qmd.particle.ParticleBeam;
import lach_01298.qmd.registry.QMDBlocks;
import lach_01298.qmd.registry.QMDItems;
import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Physical ZeroCore assembly and actual registered world ticks, without injecting controller progress. */
@GameTestHolder("qmd")
@PrefixGameTestTemplate(false)
public final class AcceleratorGameTests {
    private static final BlockPos ORIGIN = new BlockPos(1, 2, 1);
    private static BlockPos at(int x,int y,int z) { return ORIGIN.offset(x,y,z); }
    private static void put(GameTestHelper h,int x,int y,int z,String name) { h.setBlock(at(x,y,z),QMDBlocks.ACCELERATOR_BLOCKS.get(name).get()); }
    private static AbstractAcceleratorBlockEntity part(GameTestHelper h,int x,int y,int z) { return (AbstractAcceleratorBlockEntity) h.getBlockEntity(at(x,y,z)); }
    private static void linear(GameTestHelper h) {
        for(int x=0;x<6;x++) for(int y=0;y<5;y++) for(int z=0;z<5;z++) {
            if(x==0||x==5||y==0||y==4||z==0||z==4) put(h,x,y,z,"accelerator_casing");
            else h.setBlock(at(x,y,z),Blocks.AIR);
        }
        for(int x=1;x<=4;x++)put(h,x,2,2,"accelerator_beam");
        for(int y=1;y<=3;y++)for(int z=1;z<=3;z++)if(y!=2||z!=2)put(h,2,y,z,"accelerator_rf_cavity_copper");
        put(h,4,1,2,"accelerator_magnet_copper");put(h,4,3,2,"accelerator_magnet_copper");put(h,4,2,1,"accelerator_magnet_copper");put(h,4,2,3,"accelerator_magnet_copper");
        put(h,3,0,2,"linear_accelerator_controller");put(h,3,1,0,"accelerator_energy_port");put(h,3,2,0,"accelerator_coolant_inlet");put(h,3,3,0,"accelerator_coolant_outlet");
        put(h,0,2,2,"accelerator_ion_source_basic");
        var source=part(h,0,2,2);h.getLevel().setBlock(source.getBlockPos(),source.getBlockState().setValue(AbstractAcceleratorBlock.FACING,Direction.EAST),3);
        put(h,5,2,2,"accelerator_beam_port");var output=part(h,5,2,2);h.getLevel().setBlock(output.getBlockPos(),output.getBlockState().setValue(AbstractAcceleratorBlock.OUTPUT,true),3);
        source.inventory.setStackInSlot(0,QMDItems.PARTICLE_SOURCES.get("tungsten_filament").get().getDefaultInstance());
    }
    @GameTest(template="accelerator",batch="qmd.accelerator",timeoutTicks=400)
    public static void linacAssemblesConsumesSourcesAndStopsAfterWallBreak(GameTestHelper h) {
        linear(h);
        h.startSequence().thenWaitUntil(() -> {
            Accelerator acc=part(h,3,0,2).controller();h.assertTrue(acc!=null&&acc.isAssembled(),"Physical linac must assemble: "+(acc==null?"no controller":acc.validationMessage()));
        }).thenExecute(() -> {
            Accelerator acc=part(h,3,0,2).controller();
            acc.energy.receiveEnergy(1_000_000,false);acc.percentage=100;
            h.runAfterDelay(4,()->{
                var source=part(h,0,2,2);var output=part(h,5,2,2);ParticleBeam beam=ParticleBeam.fromStack(output.outputBeam.getParticle());
                h.assertTrue(beam.particleName().equals("electron")&&beam.amount()==10000&&beam.meanEnergy()==200&&beam.focus()>0,"Registered ticks must produce and focus a 200 keV electron beam");
                h.assertTrue(ParticleReservoirItem.stored(source.inventory.getStackInSlot(0))<50_000_000,"Ion source particles must be consumed");
                h.assertTrue(acc.energy.stored<1_000_000,"RF and magnet power must be charged");
                h.assertTrue(h.getLevel().getCapability(QMDParticleCapabilities.BLOCK,output.getBlockPos(),Direction.WEST)==null,"Only the outward beam face may expose a capability");
                h.setBlock(at(1,0,1),Blocks.AIR);
                h.succeedWhen(()->h.assertTrue(!acc.isAssembled()&&!acc.operating&&output.outputBeam.getParticle()==null,"Breaking the shell must disassemble and clear outputs"));
            });
        });
    }
    @GameTest(template="accelerator",batch="qmd.accelerator",timeoutTicks=400)
    public static void linacPersistenceAndMenuValidateLivePartIdentity(GameTestHelper h) {
        linear(h);
        h.startSequence().thenWaitUntil(()->{
            Accelerator acc=part(h,3,0,2).controller();h.assertTrue(acc!=null&&acc.isAssembled(),"Linac must assemble before persistence checks: "+(acc==null?"no controller":acc.validationMessage()));
        }).thenExecute(()->{
            var controller=part(h,3,0,2);Accelerator acc=controller.controller();h.assertTrue(acc!=null&&acc.isAssembled(),"Linac must assemble before persistence checks");
            acc.energy.stored=1234567890123L;acc.energy.capacity=Long.MAX_VALUE;acc.percentage=35;
            CompoundTag snapshot=acc.menuSnapshot(h.getLevel().registryAccess());
            h.assertTrue(snapshot.getLong("energy")==1234567890123L&&snapshot.getInt("percentage")==35,"Menu snapshots must preserve exact long energy and settings");
            var source=part(h,0,2,2);ItemStack cell=QMDItems.PARTICLE_SOURCES.get("tungsten_filament").get().getDefaultInstance();ParticleReservoirItem.setStored(cell,1234);source.inventory.setStackInSlot(0,cell);
            CompoundTag saved=source.saveWithFullMetadata(h.getLevel().registryAccess());var copy=AbstractAcceleratorBlockEntity.create(source.getBlockPos(),source.getBlockState());copy.loadWithComponents(saved,h.getLevel().registryAccess());
            h.assertTrue(ParticleReservoirItem.stored(copy.inventory.getStackInSlot(0))==1234,"Source inventory components must persist");
            var player=h.makeMockPlayer(net.minecraft.world.level.GameType.CREATIVE);player.setPos(controller.getBlockPos().getX()+.5,controller.getBlockPos().getY()+.5,controller.getBlockPos().getZ()+.5);
            var menu=(AbstractAcceleratorMenu) controller.createMenu(7, player.getInventory(), player);h.assertTrue(menu.stillValid(player),"Nearby menu owner may use the accelerator");
            player.containerMenu=menu;
            acc.coolantInput.setFluid(new net.neoforged.neoforge.fluids.FluidStack(net.minecraft.world.level.material.Fluids.WATER,100));
            acc.coolantOutput.setFluid(new net.neoforged.neoforge.fluids.FluidStack(net.minecraft.world.level.material.Fluids.WATER,200));
            h.assertTrue(menu.snapshot().getInt("coolantOutputCapacity")==acc.coolantOutput.getCapacity(),"Coolant gauges must use the assembled capacity");
            h.assertTrue(menu.clickMenuButton(player,10)&&acc.coolantInput.isEmpty()&&acc.coolantOutput.getFluidAmount()==200,"Tank controls clear only the selected channel");
            h.assertTrue(menu.clickMenuButton(player,4)&&acc.coolantOutput.isEmpty()&&source.inventory.getStackInSlot(0).isEmpty(),"Controller clear-all must reach the physical ion-source inventory");
            h.setBlock(at(3,0,2),Blocks.AIR);h.assertTrue(!menu.stillValid(player),"Removed controller invalidates old menus");h.succeed();
        });
    }
    private static void dipole(GameTestHelper h,int cx,int cy,int cz) {
        for(int x=-1;x<=1;x++)for(int y=-1;y<=1;y++)for(int z=-1;z<=1;z++) {
            if(x==0&&y==0&&z==0)continue;
            if(x==0&&z==0&&Math.abs(y)==1)put(h,cx+x,cy+y,cz+z,"accelerator_magnet_copper");
            else if(Math.abs(x)+Math.abs(y)+Math.abs(z)!=1 || y!=0 || !h.getBlockState(at(cx+x,cy+y,cz+z)).is(QMDBlocks.ACCELERATOR_BLOCKS.get("accelerator_beam").get()))put(h,cx+x,cy+y,cz+z,"accelerator_yoke");
        }
    }
    private static void port(GameTestHelper h,int x,int y,int z,boolean output) {
        put(h,x,y,z,"accelerator_beam_port");var p=part(h,x,y,z);h.getLevel().setBlock(p.getBlockPos(),p.getBlockState().setValue(AbstractAcceleratorBlock.OUTPUT,output),3);
    }
    private static CreativeParticleSourceBlockEntity source(GameTestHelper h,int x,int y,int z) {
        h.setBlock(at(x,y,z),QMDBlocks.CREATIVE_PARTICLE_SOURCE.get());var s=(CreativeParticleSourceBlockEntity)h.getBlockEntity(at(x,y,z));s.setBeam(new ParticleBeam("proton",7,5000,10));return s;
    }
    private static void ring(GameTestHelper h) {
        for(int x=0;x<11;x++)for(int y=0;y<5;y++)for(int z=0;z<11;z++) {
            boolean footprint=x<=4||x>=6||z<=4||z>=6;
            boolean innerWall=(x==4||x==6)&&z>=4&&z<=6||(z==4||z==6)&&x>=4&&x<=6;
            if(footprint&&(x==0||x==10||z==0||z==10||y==0||y==4||innerWall))put(h,x,y,z,"accelerator_casing");
            else h.setBlock(at(x,y,z),Blocks.AIR);
        }
        for(int n=2;n<=8;n++){put(h,n,2,2,"accelerator_beam");put(h,n,2,8,"accelerator_beam");put(h,2,2,n,"accelerator_beam");put(h,8,2,n,"accelerator_beam");}
        for(int x:new int[]{2,8})for(int z:new int[]{2,8}) { dipole(h,x,2,z);put(h,x,1,z,"accelerator_magnet_magnesium_diboride");put(h,x,3,z,"accelerator_magnet_magnesium_diboride"); }
        for(int y=1;y<=3;y++)for(int z=1;z<=3;z++)if(y!=2||z!=2)put(h,5,y,z,"accelerator_rf_cavity_copper");
        put(h,5,1,8,"accelerator_magnet_copper");put(h,5,3,8,"accelerator_magnet_copper");put(h,5,2,7,"accelerator_magnet_copper");put(h,5,2,9,"accelerator_magnet_copper");
        put(h,5,0,2,"ring_accelerator_controller");put(h,4,1,0,"accelerator_energy_port");put(h,4,2,0,"accelerator_coolant_inlet");put(h,4,3,0,"accelerator_coolant_outlet");
        port(h,2,2,0,false);port(h,8,2,10,true);put(h,2,2,1,"accelerator_beam");put(h,8,2,9,"accelerator_beam");put(h,8,2,0,"accelerator_synchrotron_port");put(h,8,2,1,"accelerator_beam");source(h,2,2,-1);
    }
    @GameTest(template="accelerator",batch="qmd.accelerator",timeoutTicks=500)
    public static void squareToroidFormsAndAcceleratesWithSynchrotronOutput(GameTestHelper h) {
        ring(h);
        h.startSequence().thenWaitUntil(()->{var a=part(h,5,0,2).controller();h.assertTrue(a!=null&&a.isAssembled(),"Toroid must form with open central hole: "+(a==null?"none":a.validationMessage()));})
                .thenExecute(()->{var a=part(h,5,0,2).controller();a.percentage=100;a.heat=0;a.energy.receiveEnergy(2_000_000,false);})
                .thenWaitUntil(()->{
                    var a=part(h,5,0,2).controller();var beam=part(h,8,2,10).outputBeam.getParticle();var photons=part(h,8,2,0).outputBeam.getParticle();
                    h.assertTrue(a.dipoles==4&&a.cavities==1&&beam!=null&&beam.getMeanEnergy()>5000&&photons!=null&&photons.getParticle()==lach_01298.qmd.particle.Particles.photon,"A physical ring must accelerate and emit its photon beam");
                }).thenSucceed();
    }
    private static void splitter(GameTestHelper h) {
        for(int x=0;x<5;x++)for(int y=0;y<5;y++)for(int z=0;z<5;z++)if(x==0||x==4||y==0||y==4||z==0||z==4)put(h,x,y,z,"accelerator_casing");else h.setBlock(at(x,y,z),Blocks.AIR);
        put(h,2,2,2,"accelerator_beam");put(h,1,2,2,"accelerator_beam");put(h,3,2,2,"accelerator_beam");put(h,2,2,3,"accelerator_beam");dipole(h,2,2,2);
        put(h,2,0,2,"beam_splitter_controller");put(h,1,1,0,"accelerator_energy_port");put(h,2,1,0,"accelerator_coolant_inlet");put(h,3,1,0,"accelerator_coolant_outlet");
        port(h,0,2,2,false);port(h,4,2,2,true);port(h,2,2,4,true);source(h,-1,2,2);
    }
    @GameTest(template="accelerator",batch="qmd.accelerator",timeoutTicks=400)
    public static void splitterRoutesTwoHalvesThroughItsPhysicalPorts(GameTestHelper h) {
        splitter(h);
        h.startSequence().thenWaitUntil(()->{var a=part(h,2,0,2).controller();h.assertTrue(a!=null&&a.isAssembled(),"Splitter must assemble: "+(a==null?"none":a.validationMessage()));})
                .thenExecute(()->{var a=part(h,2,0,2).controller();a.energy.receiveEnergy(1_000_000,false);a.percentage=100;})
                .thenWaitUntil(()->{
                    var straight=part(h,4,2,2).outputBeam.getParticle();var bent=part(h,2,2,4).outputBeam.getParticle();
                    h.assertTrue(straight!=null&&bent!=null&&straight.getAmount()==3&&bent.getAmount()==3,"A seven-particle beam retains legacy floor division in both splitter branches");
                    h.assertTrue(straight.getMeanEnergy()==5000&&bent.getMeanEnergy()<=5000&&straight.getFocus()<10,"Routing must preserve straight energy and apply bent loss and attenuation");
                }).thenSucceed();
    }
    @GameTest(template="accelerator",batch="qmd.accelerator",timeoutTicks=500)
    public static void deceleratorLowersEnergyAndRetainsBeamAmount(GameTestHelper h) {
        ring(h);put(h,5,0,2,"decelerator_controller");put(h,8,2,0,"accelerator_casing");
        h.startSequence().thenWaitUntil(()->{var a=part(h,5,0,2).controller();h.assertTrue(a!=null&&a.isAssembled(),"Physical decelerator must assemble");})
                .thenExecute(()->{var a=part(h,5,0,2).controller();a.percentage=100;a.heat=0;a.energy.receiveEnergy(2_000_000,false);})
                .thenWaitUntil(()->{var beam=part(h,8,2,10).outputBeam.getParticle();h.assertTrue(beam!=null&&beam.getAmount()==7&&beam.getMeanEnergy()==0&&beam.getFocus()>=10,"Deceleration lowers energy while focusing without charging ring attenuation");}).thenSucceed();
    }
    @GameTest(template="accelerator",batch="qmd.accelerator",timeoutTicks=500)
    public static void diverterBendsChargedBeamsAndRejectsNeutralBending(GameTestHelper h) {
        splitter(h);put(h,2,0,2,"beam_diverter_controller");put(h,4,2,2,"accelerator_casing");
        h.startSequence().thenWaitUntil(()->{var a=part(h,2,0,2).controller();h.assertTrue(a!=null&&a.isAssembled(),"Physical diverter must assemble");})
                .thenExecute(()->{var a=part(h,2,0,2).controller();a.percentage=100;a.energy.receiveEnergy(1_000_000,false);})
                .thenWaitUntil(()->{var beam=part(h,2,2,4).outputBeam.getParticle();h.assertTrue(beam!=null&&beam.getAmount()==7&&beam.getMeanEnergy()<=5000,"Diverter routes the complete charged beam through its bent port");})
                .thenExecute(()->{var s=(CreativeParticleSourceBlockEntity)h.getBlockEntity(at(-1,2,2));s.setBeam(new ParticleBeam("photon",7,5000,10));})
                .thenIdle(4).thenExecute(()->{var a=part(h,2,0,2).controller();h.assertTrue(a.error==5&&!a.operating&&part(h,2,2,4).outputBeam.getParticle()==null,"A neutral beam cannot be bent by a dipole");}).thenSucceed();
    }
    @GameTest(template="accelerator",batch="qmd.accelerator",timeoutTicks=1600)
    public static void massSpectrometerSeparatesGoldThroughRealMagnetLayersAndCoolantAutomation(GameTestHelper h) {
        for(int x=0;x<5;x++)for(int y=0;y<7;y++)for(int z=0;z<7;z++) {
            if(x==0||x==4||y==0||y==6||z==0||z==6)put(h,x,y,z,"accelerator_casing");
            else if(x==2)put(h,x,y,z,"accelerator_beam");
            else put(h,x,y,z,y==1||y==5||z==1||z==5?"accelerator_magnet_bscco":"accelerator_yoke");
        }
        put(h,2,0,3,"mass_spectrometer_controller");put(h,1,1,0,"accelerator_energy_port");put(h,1,2,0,"accelerator_coolant_inlet");put(h,1,3,0,"accelerator_coolant_outlet");
        put(h,2,1,0,"accelerator_ion_source_basic");var ion=part(h,2,1,0);h.getLevel().setBlock(ion.getBlockPos(),ion.getBlockState().setValue(AbstractAcceleratorBlock.FACING,Direction.SOUTH),3);
        for(int y=2;y<=5;y++){put(h,2,y,0,"accelerator_ion_collector");var collector=part(h,2,y,0);h.getLevel().setBlock(collector.getBlockPos(),collector.getBlockState().setValue(AbstractAcceleratorBlock.FACING,Direction.SOUTH),3);}
        var controller=part(h,2,0,3);controller.inventory.setStackInSlot(0,new ItemStack(net.minecraft.world.item.Items.GOLD_ORE));
        long[] supplied={0};
        h.startSequence().thenWaitUntil(()->{var a=controller.controller();h.assertTrue(a!=null&&a.isAssembled(),"Spectrometer must assemble all source/collector/separation layers: "+(a==null?"none":a.validationMessage()));})
                .thenExecute(()->{
                    var a=controller.controller();a.percentage=100;a.heat=0;
                    var input=part(h,1,2,0).fluidCapability();var output=part(h,1,3,0).fluidCapability();
                    var helium=new net.neoforged.neoforge.fluids.FluidStack(net.minecraft.core.registries.BuiltInRegistries.FLUID.get(net.minecraft.resources.ResourceLocation.parse("nuclearcraftneohaul:liquid_helium")),1000);
                    h.assertTrue(input.fill(helium,net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE)>0&&a.coolantInput.isEmpty(),"Coolant fill simulation cannot modify the multiblock tank");
                    h.assertTrue(input.drain(1000,net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE).isEmpty()&&output.fill(helium,net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE)==0,"Coolant ports enforce input/output direction");
                }).thenWaitUntil(()->{
                    var a=controller.controller();supplied[0]+=a.energy.receiveEnergy(100000,false);
                    var helium=new net.neoforged.neoforge.fluids.FluidStack(net.minecraft.core.registries.BuiltInRegistries.FLUID.get(net.minecraft.resources.ResourceLocation.parse("nuclearcraftneohaul:liquid_helium")),1000);
                    part(h,1,2,0).fluidCapability().fill(helium,net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
                    part(h,1,3,0).fluidCapability().drain(Integer.MAX_VALUE,net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
                    h.assertTrue(controller.inventory.getStackInSlot(3).is(net.minecraft.world.item.Items.GOLD_INGOT)&&controller.inventory.getStackInSlot(3).getCount()==4,"Registered ticks must complete the full default 1000-work ore separation cycle");
                    h.assertTrue(controller.inventory.getStackInSlot(0).isEmpty()&&supplied[0]-a.energy.stored>=a.requiredEnergy*1000,"A completed spectrometer cycle consumes its ore and every tick's supplied FE");
                }).thenSucceed();
    }
    @GameTest(template="accelerator",batch="qmd.accelerator.reload",timeoutTicks=1_000_000)
    public static void removingSourceRecipesStopsTheMachineAndReloadRestoresActualOutput(GameTestHelper h) {
        linear(h);
        h.startSequence().thenWaitUntil(()->{var a=part(h,3,0,2).controller();h.assertTrue(a!=null&&a.isAssembled(),"Reload fixture linac must assemble");})
                .thenExecute(()->{var a=part(h,3,0,2).controller();a.percentage=100;a.energy.receiveEnergy(1_000_000,false);})
                .thenWaitUntil(()->h.assertTrue(part(h,5,2,2).outputBeam.getParticle()!=null,"A real beam must exist before the recipes are removed"))
                .thenExecute(()->{
                    var manager=h.getLevel().getRecipeManager();var original=java.util.List.copyOf(manager.getRecipes());
                    manager.replaceRecipes(original.stream().filter(holder->holder.value().getType()!=QMDRecipes.ACCELERATOR_SOURCE_TYPE.get()).toList());
                    h.runAfterDelay(3,()->{
                        h.assertTrue(!part(h,3,0,2).controller().operating&&part(h,5,2,2).outputBeam.getParticle()==null,"Removing recipes must clear output on the existing machine");
                        manager.replaceRecipes(original);var server=h.getLevel().getServer();
                        var reload=java.util.concurrent.CompletableFuture.runAsync(()->server.reloadResources(java.util.List.copyOf(server.getPackRepository().getSelectedIds())).join());
                        long deadline=System.nanoTime()+java.util.concurrent.TimeUnit.SECONDS.toNanos(30);
                        h.startSequence().thenExecuteFor(1_000_000,()->h.assertTrue(System.nanoTime()<deadline,"Accelerator resource reload must finish within thirty seconds"));
                        h.startSequence().thenWaitUntil(()->h.assertTrue(reload.isDone(),"Waiting for the accelerator resource reload"))
                                .thenExecute(()->{reload.join();h.assertTrue(h.getLevel().getRecipeManager()!=manager,"Reload must replace the live recipe manager");part(h,3,0,2).controller().energy.receiveEnergy(1_000_000,false);})
                                .thenWaitUntil(()->h.assertTrue(part(h,5,2,2).outputBeam.getParticle()!=null,"Actual world ticks must resume beam output after reload"))
                                .thenSucceed();
                    });
                });
    }
}

