package lach_01298.qmd.multiblock;

import lach_01298.qmd.block.entity.chamber.AbstractChamberBlockEntity;
import lach_01298.qmd.block.entity.CreativeParticleSourceBlockEntity;
import lach_01298.qmd.block.menu.chamber.AbstractChamberMenu;
import lach_01298.qmd.item.ParticleReservoirItem;
import lach_01298.qmd.particle.ParticleBeam;
import lach_01298.qmd.registry.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/** Actual cryogenic cooldown, reactions and capabilities; fixtures never inject temperature or progress. */
@GameTestHolder("qmd") @PrefixGameTestTemplate(false)
public final class VacuumGameTests {
    private static final BlockPos BASE=new BlockPos(2,2,2);
    private static BlockPos at(int x,int y,int z){return BASE.offset(x,y,z);}
    private static void put(GameTestHelper h,int x,int y,int z,ChamberPart role){h.setBlock(at(x,y,z),QMDBlocks.CHAMBER_PARTS.get(role).get());}
    private static AbstractChamberBlockEntity part(GameTestHelper h,int x,int y,int z){return (AbstractChamberBlockEntity)h.getBlockEntity(at(x,y,z));}
    private static void shell(GameTestHelper h,int sx,int sy,int sz){for(int x=0;x<sx;x++)for(int y=0;y<sy;y++)for(int z=0;z<sz;z++)if(x==0||x==sx-1||y==0||y==sy-1||z==0||z==sz-1)put(h,x,y,z,ChamberPart.VACUUM_CASING);else h.setBlock(at(x,y,z),Blocks.AIR);}
    private static void source(GameTestHelper h,int x,int y,int z,ParticleBeam beam){h.setBlock(at(x,y,z),QMDBlocks.CREATIVE_PARTICLE_SOURCE.get());((CreativeParticleSourceBlockEntity)h.getBlockEntity(at(x,y,z))).setBeam(beam);}
    private static void neutral(GameTestHelper h){
        shell(h,7,7,7);put(h,3,0,3,ChamberPart.CONTAINMENT_CONTROLLER);put(h,3,6,3,ChamberPart.VACUUM_ENERGY_PORT);
        put(h,0,2,3,ChamberPart.COOLANT_VENT);put(h,6,2,3,ChamberPart.COOLANT_VENT);part(h,6,2,3).togglePort();
        put(h,0,3,3,ChamberPart.VACUUM_BEAM_PORT);put(h,6,3,3,ChamberPart.VACUUM_BEAM_PORT);
        put(h,3,3,0,ChamberPart.VACUUM_LASER);put(h,3,3,6,ChamberPart.VACUUM_LASER);put(h,3,1,0,ChamberPart.VACUUM_ITEM_PORT);
        for(int y:new int[]{1,5})for(int x=2;x<=4;x++)for(int z=2;z<=4;z++)if(x==2||x==4||z==2||z==4)put(h,x,y,z,ChamberPart.VACUUM_COIL);
        source(h,-1,3,3,new ParticleBeam("positron",10000,0,4));source(h,7,3,3,new ParticleBeam("antiproton",10000,0,4));
    }
    private static void service(GameTestHelper h,AbstractChamberBlockEntity controller){
        if(controller.isRemoved())return;
        var machine=controller.getMultiblockController().orElse(null);
        if(machine!=null)for(var p:machine.parts(ChamberPart.VACUUM_ENERGY_PORT)){var e=h.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK,p.getBlockPos(),null);if(e!=null)e.receiveEnergy(Integer.MAX_VALUE,false);}
        controller.tank(0).fill(new FluidStack(com.nred.nuclearcraft.registration.FluidRegistration.CUSTOM_FLUID_MAP.get("liquid_helium").still.get(),10000),IFluidHandler.FluidAction.EXECUTE);
        controller.tank(1).drain(Integer.MAX_VALUE,IFluidHandler.FluidAction.EXECUTE);
        h.runAfterDelay(20,()->service(h,controller));
    }
    @GameTest(template="accelerator",batch="qmd.vacuum",timeoutTicks=5000)
    public static void neutralContainmentCoolsMakesAntihydrogenAndExchangesPartialCells(GameTestHelper h){
        neutral(h);
        h.startSequence().thenWaitUntil(()->h.assertTrue(part(h,3,0,3).assembled(),"Exact coil rings, opposed lasers and perpendicular beams must assemble"))
                .thenExecute(()->service(h,part(h,3,0,3)))
                .thenWaitUntil(()->h.assertTrue(part(h,3,0,3).tank(2).getFluidAmount()>=120,"Real helium cooling must reach operating temperature and convert unordered beams"))
                .thenExecute(()->{
                    var controller=part(h,3,0,3);h.assertTrue(controller.vacuum().temperature()<=110,"Containment may react only when cryogenic");
                    var cap=h.getLevel().getCapability(Capabilities.ItemHandler.BLOCK,part(h,3,1,0).getBlockPos(),Direction.NORTH);
                    h.assertTrue(cap!=null&&cap.insertItem(0,new ItemStack(QMDItems.EMPTY_CELL.get()),true).isEmpty()&&controller.inventory().getStackInSlot(0).isEmpty(),"Automation simulation must preserve cell input");
                    cap.insertItem(0,new ItemStack(QMDItems.EMPTY_CELL.get()),false);
                    h.runAfterDelay(3,()->{
                        ItemStack full=controller.inventory().getStackInSlot(1);h.assertTrue(full.is(QMDItems.PARTICLE_CELLS.get("antihydrogen").get())&&ParticleReservoirItem.stored(full)>0,"Cold containment must fill a real data-component cell");
                        ItemStack partial=full.copy();ParticleReservoirItem.setStored(partial,45500);controller.inventory().setStackInSlot(1,ItemStack.EMPTY);controller.inventory().setStackInSlot(0,partial);
                        h.runAfterDelay(3,()->{
                            h.assertTrue(ParticleReservoirItem.stored(controller.inventory().getStackInSlot(1))>45500,"Partially filled cells must retain and increase their amount");
                            var saved=controller.saveWithFullMetadata(h.getLevel().registryAccess());var copy=AbstractChamberBlockEntity.create(controller.getBlockPos(),controller.getBlockState());copy.loadWithComponents(saved,h.getLevel().registryAccess());
                            h.assertTrue(copy.vacuum().temperature()==controller.vacuum().temperature()&&copy.tank(2).getFluidAmount()==controller.tank(2).getFluidAmount(),"Thermal state, remainders and exotic storage must persist");
                            controller.tank(2).setFluid(FluidStack.EMPTY);h.setBlock(at(0,0,0),Blocks.AIR);
                            h.succeedWhen(()->h.assertTrue(!controller.assembled()&&!controller.active(),"Shell removal must suspend cold operation"));
                        });
                    });
                });
    }
    private static void nucleosynthesis(GameTestHelper h){
        shell(h,11,7,5);put(h,5,0,2,ChamberPart.NUCLEOSYNTHESIS_CONTROLLER);put(h,5,6,2,ChamberPart.VACUUM_ENERGY_PORT);
        put(h,4,3,0,ChamberPart.COOLANT_VENT);put(h,4,3,4,ChamberPart.COOLANT_VENT);part(h,4,3,4).togglePort();
        put(h,6,3,0,ChamberPart.HEATER_VENT);put(h,6,3,4,ChamberPart.HEATER_VENT);part(h,6,3,4).togglePort();
        put(h,0,2,2,ChamberPart.VACUUM_BEAM_PORT);source(h,-1,2,2,new ParticleBeam("muon",100000,500,8));
        for(int end:new int[]{0,10})for(int y:new int[]{3,5}){put(h,end,y,2,ChamberPart.VACUUM_FLUID_PORT);if(end==10)part(h,end,y,2).togglePort();}
        for(int x=1;x<=9;x++)for(int y=1;y<=5;y++)for(int z=1;z<=3;z++){
            if(z==2&&(y==5||(x==1||x==9)&&y>=2))put(h,x,y,z,ChamberPart.VACUUM_BEAM);
            else if(z==2&&y==2&&(x==2||x==8))put(h,x,y,z,ChamberPart.PLASMA_NOZZLE);
            else if(x>=3&&x<=7&&(z==2&&(y==1||y==3)||y==2&&(z==1||z==3)))put(h,x,y,z,ChamberPart.PLASMA_GLASS);
        }
        put(h,2,1,1,ChamberPart.HEATER_IRON);
        // The upper plasma-glass rail is two blocks below the beam bridge. A valid
        // redstone heater joins it to the connected apparatus, as required by ZeroCore.
        put(h,3,4,2,ChamberPart.HEATER_REDSTONE);
    }
    @GameTest(template="accelerator",batch="qmd.vacuum",timeoutTicks=5000)
    public static void nucleosynthesisCoolsReactsRoutesTanksAndPreservesMenuInventory(GameTestHelper h){
        nucleosynthesis(h);
        h.startSequence().thenWaitUntil(()->{
                    var controller=part(h,5,0,2);
                    h.assertTrue(controller.assembled(),"Connected 11x7x5 plasma apparatus must assemble: "+controller.getMultiblockController().flatMap(ChamberMultiblock::getLastError).map(error->error.getChatMessage().getString()).orElse("no validation error"));
                })
                .thenExecute(()->{
                    var controller=part(h,5,0,2);service(h,controller);
                    h.assertTrue(controller.getMultiblockController().orElseThrow().parts(ChamberPart.PLASMA_GLASS).size()==20,"All four plasma-glass rails must belong to the assembled controller");
                    var hydrogen=com.nred.nuclearcraft.registration.FluidRegistration.GAS_MAP.get("hydrogen").still.get();
                    for(int y:new int[]{3,5}){var cap=h.getLevel().getCapability(Capabilities.FluidHandler.BLOCK,part(h,0,y,2).getBlockPos(),Direction.WEST);h.assertTrue(cap!=null&&cap.fill(new FluidStack(hydrogen,5000),IFluidHandler.FluidAction.EXECUTE)==5000,"End ports must route independent recipe fluid inputs");}
                }).thenWaitUntil(()->h.assertTrue(part(h,5,0,2).tank(6).getFluidAmount()>0,"Actual muon work must convert hydrogen to deuterium"))
                .thenExecute(()->{
                    var controller=part(h,5,0,2);h.assertTrue(controller.vacuum().plasmaOn()&&controller.vacuum().casingTemperature()>293,"Real plasma and reaction heat must exist");
                    var output=h.getLevel().getCapability(Capabilities.FluidHandler.BLOCK,part(h,10,5,2).getBlockPos(),Direction.EAST);
                    h.assertTrue(output!=null&&!output.drain(1,IFluidHandler.FluidAction.SIMULATE).isEmpty()&&output.fill(controller.tank(6).getFluid(),IFluidHandler.FluidAction.EXECUTE)==0,"Output fluid automation must drain and reject insertion");
                    var player=h.makeMockPlayer(GameType.CREATIVE);player.setPos(controller.getBlockPos().getX()+.5,controller.getBlockPos().getY()+.5,controller.getBlockPos().getZ()+.5);
                    var menu=(AbstractChamberMenu) controller.createMenu(9, player.getInventory(), player);player.getInventory().setItem(9,new ItemStack(net.minecraft.world.item.Items.IRON_INGOT,13));
                    menu.quickMoveStack(player,0);h.assertTrue(player.getInventory().countItem(net.minecraft.world.item.Items.IRON_INGOT)==13,"No-slot chamber shift-click must preserve all player inventory");
                    controller.tank(4).setFluid(FluidStack.EMPTY);controller.tank(5).setFluid(FluidStack.EMPTY);
                    h.runAfterDelay(3,()->{h.assertTrue(!controller.vacuum().plasmaOn()&&!controller.active(),"Emptying both inputs must extinguish plasma safely");h.succeed();});
                });
    }
    @GameTest(template="accelerator",batch="qmd.vacuum_reload",timeoutTicks=60000)
    public static void chamberRecipeTablesRebuildAfterAuthoritativeReload(GameTestHelper h){
        var manager=h.getLevel().getRecipeManager();var type=QMDRecipes.NUCLEOSYNTHESIS_CHAMBER_TYPE.get();
        var first=manager.getAllRecipesFor(type);h.assertTrue(first.size()==105,"All 105 legacy nucleosynthesis entries must load");
        var server=h.getLevel().getServer();var future=CompletableFuture.runAsync(()->server.reloadResources(List.copyOf(server.getPackRepository().getSelectedIds())).join());
        long deadline=System.nanoTime()+java.util.concurrent.TimeUnit.SECONDS.toNanos(60);
        h.succeedWhen(()->{h.assertTrue(System.nanoTime()<deadline,"Resource reload exceeded wall deadline");h.assertTrue(future.isDone(),"Waiting for reload");future.join();var current=h.getLevel().getRecipeManager().getAllRecipesFor(type);h.assertTrue(current.size()==105&&current.getFirst().value()!=first.getFirst().value(),"Reload must replace authoritative chamber instances");});
    }
    @GameTest(template="empty",batch="qmd.vacuum_contracts",timeoutTicks=100)
    public static void cellExchangeUsesCustomRecipeCountsProductsAndConservesBlockedTransactions(GameTestHelper h){
        put(h,0,0,0,ChamberPart.CONTAINMENT_CONTROLLER);var controller=part(h,0,0,0);
        var recipe=new lach_01298.qmd.recipe.chamber.CellFillingRecipe(
                List.of(com.nred.nuclearcraft.recipe.SizedChanceItemIngredient.of(net.minecraft.world.item.Items.IRON_INGOT,2)),
                List.of(com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient.of(net.minecraft.world.level.material.Fluids.WATER,30)),List.of(),
                List.of(com.nred.nuclearcraft.recipe.SizedChanceItemIngredient.of(net.minecraft.world.item.Items.GOLD_INGOT,3)),
                List.of(com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient.of(net.minecraft.world.level.material.Fluids.WATER,5)),List.of(),1,0,0,0,0);
        controller.inventory().setStackInSlot(0,new ItemStack(net.minecraft.world.item.Items.IRON_INGOT,4));
        controller.tank(2).setFluid(new FluidStack(net.minecraft.world.level.material.Fluids.WATER,100));
        controller.inventory().setStackInSlot(1,new ItemStack(net.minecraft.world.item.Items.GOLD_INGOT,63));
        h.assertTrue(!CellExchange.process(controller,recipe)&&controller.inventory().getStackInSlot(0).getCount()==4&&controller.tank(2).getFluidAmount()==100,"Blocked custom output must preserve both ingredients");
        controller.inventory().setStackInSlot(1,ItemStack.EMPTY);
        h.assertTrue(CellExchange.process(controller,recipe)&&controller.inventory().getStackInSlot(0).getCount()==2&&controller.inventory().getStackInSlot(1).is(net.minecraft.world.item.Items.GOLD_INGOT)&&controller.inventory().getStackInSlot(1).getCount()==3&&controller.tank(2).getFluidAmount()==75,"Custom cell_filling recipe must consume2 items/30mB and produce3 other items/5mB");
        var visual=controller.visualTag(h.getLevel().registryAccess());h.assertTrue(!visual.contains("inventory")&&!visual.contains("energy")&&visual.getBoolean("qmdVisual"),"Tracking observer packets must exclude inventory and full machine state");
        var saved=controller.writeAll(new net.minecraft.nbt.CompoundTag(),h.getLevel().registryAccess());saved.putInt("energy",12345);controller.readAll(saved,h.getLevel().registryAccess());
        var packet=net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(controller,(tile,registries)->visual);
        controller.onDataPacket(null,packet,h.getLevel().registryAccess());
        h.assertTrue(controller.energy()==12345&&controller.inventory().getStackInSlot(0).getCount()==2&&controller.inventory().getStackInSlot(1).getCount()==3,"Applying compact visual updates must preserve full menu inventory and energy state");h.succeed();
    }
    private VacuumGameTests(){}
}
