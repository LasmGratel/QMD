package lach_01298.qmd.block.entity;

import com.nred.nuclearcraft.block_entity.internal.fluid.TankSorption;
import com.nred.nuclearcraft.block_entity.internal.inventory.ItemOutputSetting;
import com.nred.nuclearcraft.block_entity.internal.inventory.ItemSorption;
import com.nred.nuclearcraft.registration.CapabilityRegistration;
import com.nred.nuclearcraft.registration.ItemRegistration;
import lach_01298.qmd.recipe.OreLeacherRecipe;
import lach_01298.qmd.registry.QMDBlocks;
import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.items.IItemHandler;

/** Server integration checks against the real, loaded Neohaul recipe and capability systems. */
@GameTestHolder("qmd")
@PrefixGameTestTemplate(false)
public final class OreLeacherGameTests {
    private static final BlockPos MACHINE_POS = new BlockPos(1, 1, 1);
    private static final ResourceLocation GOLD_RECIPE = ResourceLocation.parse("qmd:ore_leacher/gold");

    private OreLeacherGameTests() {
    }

    @GameTest(template = "empty", timeoutTicks = 600)
    public static void completesGoldOreCycle(GameTestHelper helper) {
        Fixture fixture = fixture(helper, MACHINE_POS);
        fixture.addInputs(1, 2);
        fixture.charge();
        long startingEnergy = fixture.machine.getEnergyStoredLong();
        long processEnergy = fixture.machine.getProcessEnergy();
        // The pinned Neohaul release has no gold dust; the legacy recipe must fall back to ingots.
        helper.assertTrue(fixture.recipe.getItemProducts().getFirst().getStack().is(Items.GOLD_INGOT),
                "Gold ore recipe must select gold ingots when the gold dust tag is empty");

        helper.succeedWhen(() -> {
            ItemStack output = fixture.machine.getItem(1);
            helper.assertTrue(output.is(Items.GOLD_INGOT) && output.getCount() == 3,
                    "One gold ore must produce exactly three gold ingots");
            helper.assertTrue(fixture.machine.getItem(0).isEmpty(), "The completed cycle must consume one ore");
            fixture.assertAcids(1);
            helper.assertTrue(startingEnergy - fixture.machine.getEnergyStoredLong() == processEnergy,
                    "A complete unupgraded cycle must consume its full recipe energy");
            helper.assertTrue(fixture.machine.getCurrentTime() == 0, "Progress must reset after the final input");
        });
    }

    @GameTest(template = "empty", timeoutTicks = 80)
    public static void insufficientEnergyAndBlockedOutputPreserveInputs(GameTestHelper helper) {
        Fixture unpowered = fixture(helper, MACHINE_POS);
        unpowered.addInputs(1, 1);
        // Enough for one tick, but not enough to start a complete processing cycle.
        unpowered.machine.getEnergyStorage().setEnergyStored(unpowered.machine.getProcessPower());
        long unpoweredEnergy = unpowered.machine.getEnergyStoredLong();

        Fixture blocked = fixture(helper, new BlockPos(3, 1, 1));
        blocked.addInputs(1, 1);
        blocked.machine.setItem(1, new ItemStack(Items.DIAMOND));
        blocked.charge();
        long blockedEnergy = blocked.machine.getEnergyStoredLong();

        helper.runAfterDelay(30, () -> {
            for (Fixture fixture : new Fixture[]{unpowered, blocked}) {
                helper.assertTrue(fixture.machine.getItem(0).is(Items.GOLD_ORE), "Idle machine must retain its ore");
                fixture.assertAcids(1);
                helper.assertTrue(fixture.machine.getCurrentTime() == 0, "Idle machine must not advance progress");
            }
            helper.assertTrue(unpowered.machine.getItem(1).isEmpty(), "Insufficient energy must not create output");
            helper.assertTrue(unpowered.machine.getEnergyStoredLong() == unpoweredEnergy,
                    "Insufficient cycle energy must not be consumed");
            helper.assertTrue(blocked.machine.getItem(1).is(Items.DIAMOND), "Blocked output must remain intact");
            helper.assertTrue(blocked.machine.getEnergyStoredLong() == blockedEnergy,
                    "Blocked output must not consume energy");
            helper.succeed();
        });
    }

    @GameTest(template = "empty")
    public static void automationHonorsSimulationAndSideConfiguration(GameTestHelper helper) {
        Fixture fixture = fixture(helper, MACHINE_POS);
        IItemHandler items = helper.getLevel().getCapability(Capabilities.ItemHandler.BLOCK,
                fixture.machine.getBlockPos(), Direction.NORTH);
        IFluidHandler fluids = helper.getLevel().getCapability(Capabilities.FluidHandler.BLOCK,
                fixture.machine.getBlockPos(), Direction.NORTH);
        helper.assertTrue(items != null && fluids != null, "Item and fluid capabilities must be registered");

        helper.assertTrue(items.insertItem(0, new ItemStack(Items.GOLD_ORE, 2), true).isEmpty(),
                "Simulated ore insertion must be accepted");
        helper.assertTrue(fixture.machine.getItem(0).isEmpty(), "Simulated insertion must not mutate inventory");
        helper.assertTrue(items.insertItem(0, new ItemStack(Items.GOLD_ORE, 2), false).isEmpty(),
                "Ore input must accept automation");
        helper.assertTrue(items.insertItem(1, new ItemStack(Items.GOLD_INGOT), false).getCount() == 1,
                "An output slot must reject insertion");
        helper.assertTrue(items.extractItem(0, 1, false).isEmpty(), "Input sorption must prevent extraction");

        for (int tank = 0; tank < 3; ++tank) {
            FluidStack acid = fixture.acid(tank, 2);
            helper.assertTrue(fluids.fill(acid, IFluidHandler.FluidAction.SIMULATE) == acid.getAmount(),
                    "The acid input must accept a simulated fill");
            helper.assertTrue(fixture.machine.getTanks().get(tank).getFluidAmount() == 0,
                    "Simulated fill must leave the tank empty");
            helper.assertTrue(fluids.fill(acid, IFluidHandler.FluidAction.EXECUTE) == acid.getAmount(),
                    "The three distinct acids must fill separate tanks");
        }
        fixture.assertAcids(2);
        helper.assertTrue(fluids.drain(1, IFluidHandler.FluidAction.EXECUTE).isEmpty(),
                "Input tanks must reject automated draining by default");
        fixture.machine.setTankSorption(Direction.NORTH, 0, TankSorption.BOTH);
        FluidStack drained = fluids.drain(fixture.acid(0, 1), IFluidHandler.FluidAction.EXECUTE);
        helper.assertTrue(drained.getAmount() == fixture.acid(0, 1).getAmount(),
                "Changing tank sorption must enable acid extraction");
        fixture.machine.setItemSorption(Direction.NORTH, 0, ItemSorption.OUT);
        helper.assertTrue(items.extractItem(0, 1, false).getCount() == 1,
                "Changing slot sorption must enable ore extraction");
        helper.assertTrue(fixture.machine.getItem(0).getCount() == 1, "Only the extracted ore may leave the inventory");
        helper.assertTrue(helper.getLevel().getCapability(CapabilityRegistration.CAPABILITY_RADIATION_SOURCE,
                fixture.machine.getBlockPos(), null) != null, "Neohaul must expose the inherited radiation source");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 80)
    public static void upgradesAndRedstoneHaltPreserveProgress(GameTestHelper helper) {
        Fixture fixture = fixture(helper, MACHINE_POS);
        fixture.addInputs(1, 1);
        double originalSpeed = fixture.machine.getSpeedMultiplier();
        fixture.machine.setItem(4, ItemRegistration.UPGRADE_MAP.get("speed").toStack());
        double speedPower = fixture.machine.getPowerMultiplier();
        fixture.machine.setItem(5, ItemRegistration.UPGRADE_MAP.get("energy").toStack());
        helper.assertTrue(fixture.machine.getSpeedMultiplier() > originalSpeed, "Speed upgrades must accelerate processing");
        helper.assertTrue(fixture.machine.getPowerMultiplier() < speedPower, "Energy upgrades must reduce upgrade power");
        fixture.charge();
        fixture.machine.setRedstoneControl(true);
        BlockPos signal = MACHINE_POS.south();
        helper.setBlock(signal, Blocks.REDSTONE_BLOCK);
        fixture.machine.refreshIsRedstonePowered(helper.getLevel(), fixture.machine.getBlockPos());
        long heldEnergy = fixture.machine.getEnergyStoredLong();

        helper.runAfterDelay(5, () -> {
            helper.assertTrue(fixture.machine.isHalted(), "Adjacent redstone must halt an enabled machine");
            helper.assertTrue(fixture.machine.getCurrentTime() == 0, "Redstone halt must prevent processing");
            helper.assertTrue(fixture.machine.getEnergyStoredLong() == heldEnergy, "Redstone halt must preserve energy");
            helper.destroyBlock(signal);
            fixture.machine.refreshIsRedstonePowered(helper.getLevel(), fixture.machine.getBlockPos());
            helper.runAfterDelay(8, () -> {
                double progress = fixture.machine.getCurrentTime();
                helper.assertTrue(progress > 0, "Removing redstone must resume processing");
                helper.setBlock(signal, Blocks.REDSTONE_BLOCK);
                fixture.machine.refreshIsRedstonePowered(helper.getLevel(), fixture.machine.getBlockPos());
                helper.runAfterDelay(5, () -> {
                    helper.assertTrue(fixture.machine.getCurrentTime() == progress,
                            "Redstone halt must retain partially completed progress");
                    fixture.assertAcids(1);
                    helper.succeed();
                });
            });
        });
    }

    @GameTest(template = "empty")
    public static void energyStarvationLosesLegacyProgress(GameTestHelper helper) {
        Fixture fixture = fixture(helper, MACHINE_POS);
        fixture.addInputs(1, 1);
        fixture.machine.setCurrentTime(20);
        fixture.machine.setResetTime(0);
        fixture.machine.getEnergyStorage().setEnergyStored(0);
        fixture.machine.update();
        helper.assertTrue(fixture.machine.getCurrentTime() == 18.5,
                "One energy-starved tick must lose 1.5 ticks of unupgraded progress");
        fixture.machine.setRedstoneControl(true);
        fixture.machine.setIsRedstonePowered(true);
        fixture.machine.update();
        helper.assertTrue(fixture.machine.getCurrentTime() == 18.5, "Redstone halt must suspend progress loss");
        helper.assertTrue(fixture.machine.getItem(0).getCount() == 1, "Starvation must preserve ore");
        fixture.assertAcids(1);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void machineStateSurvivesNbtRoundTrip(GameTestHelper helper) {
        Fixture fixture = fixture(helper, MACHINE_POS);
        fixture.addInputs(2, 2);
        fixture.machine.setItem(4, ItemRegistration.UPGRADE_MAP.get("speed").toStack(3));
        fixture.machine.setItem(5, ItemRegistration.UPGRADE_MAP.get("energy").toStack(2));
        fixture.machine.getEnergyStorage().setEnergyStored(1234);
        fixture.machine.setCurrentTime(12.5);
        fixture.machine.setResetTime(4.5);
        fixture.machine.setRedstoneControl(true);
        fixture.machine.setAlternateComparator(true);
        fixture.machine.setItemSorption(Direction.EAST, 0, ItemSorption.NON);
        fixture.machine.setTankSorption(Direction.WEST, 1, TankSorption.OUT);
        fixture.machine.setItemOutputSetting(1, ItemOutputSetting.VOID_EXCESS);
        CompoundTag saved = fixture.machine.writeAll(new CompoundTag(), helper.getLevel().registryAccess());

        OreLeacherBlockEntity restored = new OreLeacherBlockEntity(fixture.machine.getBlockPos(), fixture.machine.getBlockState());
        restored.setLevel(helper.getLevel());
        restored.readAll(saved, helper.getLevel().registryAccess());
        helper.assertTrue(restored.getItem(0).is(Items.GOLD_ORE) && restored.getItem(0).getCount() == 2,
                "Ore inventory must survive serialization");
        helper.assertTrue(restored.getItem(4).getCount() == 3 && restored.getItem(5).getCount() == 2,
                "Both upgrade slots must survive serialization");
        helper.assertTrue(restored.getEnergyStoredLong() == 1234, "Stored energy must survive serialization");
        helper.assertTrue(restored.getCurrentTime() == 12.5 && restored.getResetTime() == 4.5,
                "Current and reset progress must survive serialization");
        helper.assertTrue(restored.getRedstoneControl() && restored.getAlternateComparator(),
                "Redstone and comparator settings must survive serialization");
        helper.assertTrue(restored.getItemSorption(Direction.EAST, 0) == ItemSorption.NON,
                "Per-face item sorption must survive serialization");
        helper.assertTrue(restored.getTankSorption(Direction.WEST, 1) == TankSorption.OUT,
                "Per-face tank sorption must survive serialization");
        helper.assertTrue(restored.getItemOutputSetting(1) == ItemOutputSetting.VOID_EXCESS,
                "Output settings must survive serialization");
        helper.assertTrue(restored.getInputTanksSeparated(), "Input tank separation must survive serialization");
        for (int tank = 0; tank < 3; ++tank) {
            helper.assertTrue(FluidStack.matches(restored.getTanks().get(tank).getFluid(), fixture.acid(tank, 2)),
                    "Acid identity and amount must survive serialization");
        }
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "reload", timeoutTicks = 1200)
    public static void resourceReloadReplacesCachedRecipes(GameTestHelper helper) {
        var server = helper.getLevel().getServer();
        var previousManager = server.getRecipeManager();
        OreLeacherRecipe previousGold = goldRecipe(helper);
        var previousCache = QMDRecipes.ORE_LEACHER_HANDLER.getRecipeList(previousManager);
        helper.assertTrue(previousCache.stream().anyMatch(recipe -> recipe == previousGold),
                "Prime the Neohaul handler cache with the current gold recipe before reloading");

        // Each named GameTest batch finishes before the next starts. Keep this reload apart from ticking tests.
        // The future is polled on later ticks, so resource application can continue on the server thread.
        var reload = server.reloadResources(server.getPackRepository().getSelectedIds());
        helper.succeedWhen(() -> {
            helper.assertTrue(reload.isDone(), "Waiting for the asynchronous resource reload");
            reload.getNow(null); // Propagate reload failures only after completion; never block the server thread.
            OreLeacherRecipe currentGold = goldRecipe(helper);
            var currentCache = QMDRecipes.ORE_LEACHER_HANDLER.getRecipeList(server.getRecipeManager());
            helper.assertTrue(currentGold != previousGold, "A resource reload must decode a new gold recipe instance");
            helper.assertTrue(currentCache != previousCache, "The Neohaul recipe handler must replace its cached list");
            helper.assertTrue(currentCache.stream().noneMatch(recipe -> recipe == previousGold),
                    "The recipe handler must discard stale recipe instances");
            helper.assertTrue(currentCache.stream().anyMatch(recipe -> recipe == currentGold),
                    "The recipe handler must use the reloaded recipe manager's instances");
            helper.assertTrue(currentCache.size() == previousCache.size(),
                    "Reloading the same selected packs must preserve the default recipe count");
            helper.assertTrue(currentGold.getItemProducts().getFirst().getStack().is(Items.GOLD_INGOT)
                            && currentGold.getItemProducts().getFirst().count() == 3,
                    "Reloading must retain the default gold ingot fallback");
            helper.assertTrue(currentGold.getFluidIngredients().size() == 3
                            && currentGold.getFluidIngredients().stream().allMatch(input -> input.amount() == 16),
                    "Reloading must retain all three16mB acid inputs");
        });
    }

    private static Fixture fixture(GameTestHelper helper, BlockPos relativePos) {
        helper.setBlock(relativePos, QMDBlocks.ORE_LEACHER.get());
        OreLeacherBlockEntity machine = helper.getBlockEntity(relativePos);
        helper.assertTrue(machine != null, "Placing the ore leacher must create its block entity");
        return new Fixture(helper, machine, goldRecipe(helper));
    }

    private static OreLeacherRecipe goldRecipe(GameTestHelper helper) {
        return helper.getLevel().getRecipeManager()
                .getAllRecipesFor(QMDRecipes.ORE_LEACHER_TYPE.get()).stream()
                .filter(holder -> holder.id().equals(GOLD_RECIPE)).findFirst()
                .orElseThrow(() -> new IllegalStateException("The default gold ore leacher recipe must load")).value();
    }

    private record Fixture(GameTestHelper helper, OreLeacherBlockEntity machine, OreLeacherRecipe recipe) {
        private FluidStack acid(int tank, int batches) {
            return recipe.getFluidIngredients().get(tank).getStack().copyWithAmount(
                    recipe.getFluidIngredients().get(tank).amount() * batches);
        }

        private void addInputs(int ores, int acidBatches) {
            for (int tank = 0; tank < 3; ++tank) {
                machine.getTanks().get(tank).setFluidStored(acid(tank, acidBatches));
            }
            machine.setItem(0, new ItemStack(Items.GOLD_ORE, ores));
            machine.refreshAll();
            helper.assertTrue(machine.getCanProcessInputs(), "Ore and three acids must match the loaded recipe");
        }

        private void charge() {
            var energy = helper.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK,
                    machine.getBlockPos(), Direction.NORTH);
            helper.assertTrue(energy != null && energy.canReceive(), "Energy input capability must be registered");
            helper.assertTrue(energy.receiveEnergy(Integer.MAX_VALUE, false) > 0,
                    "The machine must expose a positive energy capacity and accept power");
        }

        private void assertAcids(int batches) {
            for (int tank = 0; tank < 3; ++tank) {
                helper.assertTrue(FluidStack.matches(machine.getTanks().get(tank).getFluid(), acid(tank, batches)),
                        "Acid tank " + tank + " must contain exactly " + batches + " recipe batches");
            }
        }
    }
}
