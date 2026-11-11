package lach_01298.qmd.multiblock;

import com.mojang.serialization.JsonOps;
import com.nred.nuclearcraft.block_entity.hx.HeatExchangerTubeEntity;
import com.nred.nuclearcraft.registration.BlockRegistration;
import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity;
import lach_01298.qmd.block.entity.liquefier.LiquefierControllerBlockEntity;
import lach_01298.qmd.block.menu.LiquefierMenu;
import lach_01298.qmd.multiblock.liquefier.LiquefierLogic;
import lach_01298.qmd.recipe.LiquefierRecipe;
import lach_01298.qmd.registry.QMDBlocks;
import lach_01298.qmd.registry.QMDFluids;
import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static com.nred.nuclearcraft.registration.BlockRegistration.ACTIVE;

/** Real mixed Neohaul/QMD multiblocks, using registered blocks, capabilities and the ZeroCore lifecycle. */
@GameTestHolder("qmd")
@PrefixGameTestTemplate(false)
public final class LiquefierGameTests {
    private static final BlockPos CONTROLLER = new BlockPos(0, 2, 2);
    private static final BlockPos GAS_IN = new BlockPos(0, 1, 2), GAS_OUT = new BlockPos(4, 1, 2), ENERGY = new BlockPos(2, 1, 0);

    @GameTest(template = "empty", batch = "qmd.liquefier", timeoutTicks = 200)
    public static void mixedHeatExchangerPartsAssembleAndLiquefyOnWorldTicks(GameTestHelper helper) {
        fixture(helper, logic -> {
            helper.assertTrue(logic.pressure == 10 && Math.abs(logic.energyEfficiency - 0.9) < 1E-9
                    && logic.multiblock.networks.size() == 1, "One connected copper compressor and nozzle must form the legacy ten-bar circuit");
            feed(helper, logic);
            logic.multiblock.computerActivated = true;
            logic.setIsExchangerOn();
            long initial = logic.energyStorage.getEnergyStoredLong();
            helper.runAfterDelay(20, () -> {
                helper.assertTrue(logic.gasTanks.get(1).getFluid().is(QMDFluids.LIQUID_AIR.still.get())
                        && logic.gasTanks.get(1).getFluidAmount() > 0, "World ticks must liquefy the registered gas recipe");
                helper.assertTrue(logic.gasTanks.getFirst().getFluidAmount() < 6400 && logic.multiblock.shellTanks.getFirst().getFluidAmount() < 1000
                        && logic.energyStorage.getEnergyStoredLong() < initial, "Both circuits and energy must be consumed together");
                helper.assertTrue(logic.multiblock.shellTanks.get(1).getFluidAmount() > 0, "Coolant conversion must produce warm shell output");
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty", batch = "qmd.liquefier", timeoutTicks = 200)
    public static void capabilitiesRespectPortModesAndEnergyDirection(GameTestHelper helper) {
        fixture(helper, logic -> {
            var in = helper.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, helper.absolutePos(GAS_IN), Direction.WEST);
            var out = helper.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, helper.absolutePos(GAS_OUT), Direction.EAST);
            var energy = helper.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, helper.absolutePos(ENERGY), Direction.NORTH);
            helper.assertTrue(in != null && out != null && energy != null, "Registered ports must expose NeoForge automation capabilities");
            helper.assertTrue(in.fill(new FluidStack(QMDFluids.COMPRESSED_AIR.still.get(), 640), IFluidHandler.FluidAction.SIMULATE) == 640
                    && logic.gasTanks.getFirst().isEmpty(), "Simulated gas fill must be non-mutating");
            helper.assertTrue(in.fill(new FluidStack(Fluids.WATER, 100), IFluidHandler.FluidAction.EXECUTE) == 0,
                    "The gas port must reject fluids absent from the live gas recipe table");
            in.fill(new FluidStack(QMDFluids.COMPRESSED_AIR.still.get(), 640), IFluidHandler.FluidAction.EXECUTE);
            helper.assertTrue(out.fill(new FluidStack(QMDFluids.COMPRESSED_AIR.still.get(), 640), IFluidHandler.FluidAction.EXECUTE) == 0
                    && in.drain(100, IFluidHandler.FluidAction.EXECUTE).isEmpty(), "Ports must preserve their input/output directions");
            logic.gasTanks.get(1).setFluidStored(new FluidStack(QMDFluids.LIQUID_AIR.still.get(), 10));
            helper.assertTrue(out.drain(4, IFluidHandler.FluidAction.EXECUTE).getAmount() == 4
                    && logic.gasTanks.get(1).getFluidAmount() == 6, "Output automation must drain the shared liquid output");
            helper.assertTrue(energy.receiveEnergy(1000, false) == 1000 && energy.extractEnergy(1000, false) == 0
                    && !energy.canExtract(), "Energy ports must accept FE without exposing extraction");
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "qmd.liquefier", timeoutTicks = 200)
    public static void blockedOutputsAndInvalidCoolantCannotConsumeInputs(GameTestHelper helper) {
        fixture(helper, logic -> {
            feed(helper, logic);
            logic.gasTanks.get(1).setFluidStored(new FluidStack(QMDFluids.LIQUID_AIR.still.get(), logic.gasTanks.get(1).getCapacity()));
            long energy = logic.energyStorage.getEnergyStoredLong();
            helper.assertTrue(!logic.processAvailable() && logic.energyStorage.getEnergyStoredLong() == energy
                    && logic.gasTanks.getFirst().getFluidAmount() == 6400, "A full liquid output must stop both circuits and energy use");
            logic.gasTanks.get(1).setFluidStored(FluidStack.EMPTY);
            logic.gasTanks.getFirst().setFluidStored(new FluidStack(QMDFluids.NEON.still.get(), 6400));
            helper.assertTrue(!logic.processAvailable() && logic.multiblock.shellTanks.getFirst().getFluidAmount() == 1000,
                    "Water above neon's inversion temperature must not liquefy neon or consume coolant");
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "qmd.liquefier", timeoutTicks = 200)
    public static void fractionalProgressAndAllMaterialSurviveLogicNbt(GameTestHelper helper) {
        fixture(helper, logic -> {
            feed(helper, logic); helper.assertTrue(logic.processAvailable(), "A valid circuit must advance fractional liquefaction");
            CompoundTag saved = new CompoundTag();
            logic.writeToLogicTag(saved, helper.getLevel().registryAccess(), ISyncableEntity.SyncReason.FullSync);
            helper.assertTrue(saved.getDouble("gasRemainder") > 0 && saved.getDouble("gasRemainder") < 1,
                    "One tick must retain its unfinished gas conversion");
            LiquefierLogic restored = new LiquefierLogic(logic);
            restored.readFromLogicTag(saved, helper.getLevel().registryAccess(), ISyncableEntity.SyncReason.FullSync);
            CompoundTag roundTrip = new CompoundTag(); restored.writeToLogicTag(roundTrip, helper.getLevel().registryAccess(), ISyncableEntity.SyncReason.FullSync);
            helper.assertTrue(roundTrip.equals(saved), "Gas and liquid tanks, energy, efficiencies and both fractional remainders must round trip");
            helper.assertTrue(restored.processAvailable(), "Restored progress must continue using loaded recipes");
            helper.assertTrue(restored.gasTanks.get(1).getFluidAmount() == 1, "Two fractional ticks must finish exactly one conversion");
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "qmd.liquefier", timeoutTicks = 200)
    public static void menuClearRequiresItsOwnerAndLiveAssembledController(GameTestHelper helper) {
        fixture(helper, logic -> {
            var player = helper.makeMockPlayer(GameType.SURVIVAL); var controller = logic.controller();
            player.setPos(controller.getBlockPos().getCenter());
            var menu = new LiquefierMenu(9, player.getInventory(), controller); player.containerMenu = menu;
            feed(helper, logic);
            helper.assertTrue(menu.slots.isEmpty() && menu.stillValid(player), "The full legacy controller must open a valid configuration-only menu");
            helper.assertTrue(!menu.clickMenuButton(player, 1), "Unknown button identifiers must not clear material");
            var other = helper.makeMockPlayer(GameType.SURVIVAL); other.containerMenu = menu;
            helper.assertTrue(!menu.clickMenuButton(other, 0), "Another player must not mutate this menu");
            helper.assertTrue(menu.clickMenuButton(player, 0) && logic.energyStorage.isEmpty()
                    && logic.gasTanks.stream().allMatch(tank -> tank.isEmpty())
                    && logic.multiblock.shellTanks.stream().allMatch(tank -> tank.isEmpty()), "The owner's clear button must clear both circuits, fractional progress and energy");
            player.setPos(player.position().add(100, 0, 0));
            helper.assertTrue(!menu.stillValid(player) && !menu.clickMenuButton(player, 0), "Distance invalidation must also prevent stale clear actions");
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "qmd.liquefier", timeoutTicks = 200)
    public static void brokenNozzleLayerRejectsAssemblyWithValidationError(GameTestHelper helper) {
        build(helper);
        helper.setBlock(new BlockPos(1, 2, 1), Blocks.AIR);
        helper.runAfterDelay(20, () -> {
            var controller = (LiquefierControllerBlockEntity) helper.getBlockEntity(CONTROLLER);
            helper.assertTrue(!controller.isMachineAssembled() && controller.getMultiblockController().orElseThrow().hasLastError(),
                    "A gap in the baffle/nozzle layer must prevent assembly and retain a useful validation error");
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "qmd.liquefier.reload", timeoutTicks = 1_000_000)
    public static void recipeRemovalStopsAnExistingMachineAndResourceReloadRestoresIt(GameTestHelper helper) {
        AtomicReference<CompletableFuture<Void>> reload = new AtomicReference<>();
        fixture(helper, logic -> {
            feed(helper, logic);
            var server = helper.getLevel().getServer(); var manager = helper.getLevel().getRecipeManager();
            var original = List.copyOf(manager.getRecipes());
            try {
                manager.replaceRecipes(original.stream().filter(holder -> holder.value().getType() != QMDRecipes.LIQUEFIER_GAS_TYPE.get()).toList());
                helper.assertTrue(!logic.processAvailable(), "Removing the gas recipes must immediately stop a preexisting machine");
            } finally { manager.replaceRecipes(original); }
            reload.set(CompletableFuture.runAsync(() -> server.reloadResources(List.copyOf(server.getPackRepository().getSelectedIds())).join()));
            helper.startSequence().thenWaitUntil(() -> helper.assertTrue(reload.get().isDone(), "Waiting for server resource reload"))
                    .thenExecute(() -> {
                        reload.get().join();
                        helper.assertTrue(helper.getLevel().getRecipeManager() != manager && logic.processAvailable(),
                                "A resource reload must supply new live recipe objects to the same assembled machine");
                    }).thenSucceed();
        });
    }

    @GameTest(template = "empty", batch = "qmd.liquefier")
    public static void everyLegacyGasAndCoolantRecipeLoadsAndCodecRoundTrips(GameTestHelper helper) {
        var manager = helper.getLevel().getRecipeManager();
        var gases = manager.getAllRecipesFor(QMDRecipes.LIQUEFIER_GAS_TYPE.get());
        var coolants = manager.getAllRecipesFor(QMDRecipes.LIQUEFIER_COOLANT_TYPE.get());
        helper.assertTrue(gases.size() == 7 && coolants.size() == 5, "All seven legacy gases and all five coolants must be migrated");
        var ops = helper.getLevel().registryAccess().createSerializationContext(JsonOps.INSTANCE);
        for (var holder : manager.getRecipes()) {
            if (holder.value() instanceof LiquefierRecipe recipe) {
                var encoded = LiquefierRecipe.codec(recipe.kind()).codec().encodeStart(ops, recipe).getOrThrow();
                var decoded = LiquefierRecipe.codec(recipe.kind()).codec().parse(ops, encoded).getOrThrow();
                helper.assertTrue(decoded.amount() == recipe.amount() && FluidStack.isSameFluidSameComponents(decoded.output(), recipe.output())
                        && decoded.output().getAmount() == recipe.output().getAmount() && decoded.heat() == recipe.heat()
                        && decoded.energy() == recipe.energy() && decoded.pressureCoefficient() == recipe.pressureCoefficient()
                        && decoded.inputTemperature() == recipe.inputTemperature() && decoded.outputTemperature() == recipe.outputTemperature(),
                        "Recipe codec must retain every runtime/JEI field: " + holder.id());
            }
        }
        helper.succeed();
    }
    @GameTest(template="empty",batch="qmd.liquefier",timeoutTicks=200)
    public static void fractionalEnergyIsPrepaidOnceAndPersistsWithoutPerTickOvercharge(GameTestHelper helper) {
        fixture(helper,logic->{
            feed(helper,logic);var manager=helper.getLevel().getRecipeManager();var original=List.copyOf(manager.getRecipes());
            var holder=manager.getAllRecipesFor(QMDRecipes.LIQUEFIER_GAS_TYPE.get()).stream().filter(h->h.value().accepts(logic.gasTanks.getFirst().getFluid())).findFirst().orElseThrow();
            var old=holder.value();var fractional=new LiquefierRecipe(old.kind(),old.ingredient(),old.amount(),old.output(),old.heat(),.05,old.inputTemperature(),old.outputTemperature(),old.pressureCoefficient());
            var modified=new java.util.ArrayList<net.minecraft.world.item.crafting.RecipeHolder<?>>(original);modified.remove(holder);modified.add(new net.minecraft.world.item.crafting.RecipeHolder<>(holder.id(),fractional));
            manager.replaceRecipes(modified);
            try {
                long energy=logic.energyStorage.getEnergyStoredLong();double expected=0;
                for(int tick=0;tick<30;tick++){
                    helper.assertTrue(logic.processAvailable(),"Valid fractional-energy work must advance");
                    expected+=logic.multiblock.heatTransferRate/old.heat()*logic.heatEfficiency*.05/logic.energyEfficiency;
                }
                long charged=energy-logic.energyStorage.getEnergyStoredLong();
                helper.assertTrue(charged>=expected-1E-8&&charged<expected+1+1E-8&&charged<30,"Fractional FE charges must equal total work rounded up once, without one FE per tick");
                CompoundTag saved=new CompoundTag();logic.writeToLogicTag(saved,helper.getLevel().registryAccess(),ISyncableEntity.SyncReason.FullSync);
                helper.assertTrue(saved.getDouble("energyCredit")>=0&&saved.getDouble("energyCredit")<1,"Prepaid sub-FE credit remains bounded and persisted");
            } finally {manager.replaceRecipes(original);}
            helper.succeed();
        });
    }

    private static void fixture(GameTestHelper helper, Consumer<LiquefierLogic> ready) {
        build(helper);
        helper.runAfterDelay(20, () -> {
            var controller = (LiquefierControllerBlockEntity) helper.getBlockEntity(CONTROLLER);
            helper.assertTrue(controller.isMachineAssembled(), "Mixed Neohaul HX parts and QMD parts must assemble: "
                    + controller.getMultiblockController().map(mb -> mb.getLastError().toString()).orElse("no controller"));
            helper.assertTrue(controller.liquefier() != null, "The registered liquefier logic dispatch must replace standard HX logic");
            ready.accept(controller.liquefier());
        });
    }
    private static void feed(GameTestHelper helper, LiquefierLogic logic) {
        var fluid = helper.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, helper.absolutePos(GAS_IN), Direction.WEST);
        helper.assertTrue(fluid != null && fluid.fill(new FluidStack(QMDFluids.COMPRESSED_AIR.still.get(), 6400), IFluidHandler.FluidAction.EXECUTE) == 6400,
                "Input automation must fill the assembled shared gas tank");
        logic.multiblock.shellTanks.getFirst().setFluidStored(new FluidStack(Fluids.WATER, 1000));
        logic.energyStorage.setEnergyStored(logic.energyStorage.getMaxEnergyStoredLong());
    }
    private static void build(GameTestHelper helper) {
        for (BlockPos pos : BlockPos.betweenClosed(BlockPos.ZERO, new BlockPos(4, 4, 4))) {
            boolean shell = pos.getX() == 0 || pos.getX() == 4 || pos.getY() == 0 || pos.getY() == 4 || pos.getZ() == 0 || pos.getZ() == 4;
            helper.setBlock(pos, shell ? BlockRegistration.HX_MAP.get("heat_exchanger_casing").get() : Blocks.AIR);
        }
        for (int x = 1; x <= 3; x++) for (int z = 1; z <= 3; z++) helper.setBlock(new BlockPos(x, 2, z), BlockRegistration.HX_MAP.get("heat_exchanger_shell_baffle").get());
        helper.setBlock(CONTROLLER, QMDBlocks.LIQUEFIER_CONTROLLER.get());
        helper.setBlock(new BlockPos(2, 2, 2), QMDBlocks.LIQUEFIER_NOZZLE.get());
        helper.setBlock(new BlockPos(2, 3, 2), BlockRegistration.HX_MAP.get("copper_heat_exchanger_tube").get());
        var tube = (HeatExchangerTubeEntity) helper.getBlockEntity(new BlockPos(2, 3, 2));
        tube.setTubeSettingOpen(Direction.UP, true); tube.setTubeSettingOpen(Direction.DOWN, true);
        helper.setBlock(new BlockPos(2, 4, 2), QMDBlocks.LIQUEFIER_COPPER_COMPRESSOR.get());
        helper.setBlock(new BlockPos(0, 3, 2), BlockRegistration.HX_MAP.get("heat_exchanger_inlet").get());
        helper.setBlock(new BlockPos(4, 3, 2), BlockRegistration.HX_MAP.get("heat_exchanger_outlet").get());
        helper.setBlock(GAS_IN, QMDBlocks.LIQUEFIER_FLUID_PORT.get());
        helper.setBlock(GAS_OUT, QMDBlocks.LIQUEFIER_FLUID_PORT.get().defaultBlockState().setValue(ACTIVE, true));
        helper.setBlock(ENERGY, QMDBlocks.LIQUEFIER_ENERGY_PORT.get());
    }
}
