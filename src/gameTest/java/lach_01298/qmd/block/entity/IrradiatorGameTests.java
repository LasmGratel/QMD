package lach_01298.qmd.block.entity;

import com.mojang.serialization.JsonOps;
import lach_01298.qmd.block.menu.IrradiatorMenu;
import lach_01298.qmd.config.IrradiatorConfig;
import lach_01298.qmd.item.ParticleReservoirItem;
import lach_01298.qmd.recipe.IrradiatorRecipe;
import lach_01298.qmd.registry.QMDBlocks;
import lach_01298.qmd.registry.QMDItems;
import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@GameTestHolder("qmd")
@PrefixGameTestTemplate(false)
public final class IrradiatorGameTests {
    private static final BlockPos POS = new BlockPos(2, 2, 2);
    private static IrradiatorBlockEntity machine(GameTestHelper helper) {
        helper.setBlock(POS, QMDBlocks.IRRADIATOR.get());
        return (IrradiatorBlockEntity) helper.getBlockEntity(POS);
    }
    private static ItemStack source(String species, int amount) {
        ItemStack source = QMDItems.PARTICLE_SOURCES.get(species).get().getDefaultInstance();
        ParticleReservoirItem.setStored(source, amount); return source;
    }

    @GameTest(template = "empty", batch = "qmd.irradiator", timeoutTicks = 400)
    public static void worldTicksConsumeSourceParticlesAndProduceRealOutput(GameTestHelper helper) {
        var tile = machine(helper);
        tile.inventory().setStackInSlot(0, new ItemStack(Items.ROTTEN_FLESH));
        tile.inventory().setStackInSlot(1, source("iridium_192", 100_000));
        helper.succeedWhen(() -> {
            helper.assertTrue(tile.inventory().getStackInSlot(2).is(QMDItems.FLESH.get()), "Registered world ticker must irradiate flesh");
            helper.assertTrue(tile.inventory().getStackInSlot(0).isEmpty(), "Processing consumes exactly the one input");
            int expected = 100_000 - IrradiatorConfig.PROCESS_TIME.get() * IrradiatorConfig.FUEL_USAGE.get();
            helper.assertTrue(ParticleReservoirItem.stored(tile.inventory().getStackInSlot(1)) == expected,
                    "Source speed changes time, while source amount is consumed per unit of work");
        });
    }

    @GameTest(template = "empty", batch = "qmd.irradiator")
    public static void redstoneBlockedOutputsAndFuelExhaustionAreConservative(GameTestHelper helper) {
        var tile = machine(helper);
        tile.inventory().setStackInSlot(0, new ItemStack(Items.ROTTEN_FLESH, 4));
        tile.inventory().setStackInSlot(1, source("iridium_192", 5));
        tile.inventory().setStackInSlot(2, new ItemStack(Items.STONE));
        tile.serverTick();
        helper.assertTrue(ParticleReservoirItem.stored(tile.inventory().getStackInSlot(1)) == 5 && tile.progress() == 0,
                "Incompatible blocked outputs cannot consume sources");
        tile.inventory().setStackInSlot(2, ItemStack.EMPTY);
        helper.setBlock(POS.above(), Blocks.REDSTONE_BLOCK);
        tile.serverTick();
        helper.assertTrue(tile.progress() == 0 && ParticleReservoirItem.stored(tile.inventory().getStackInSlot(1)) == 5,
                "Powered redstone control pauses without source loss");
        helper.setBlock(POS.above(), Blocks.AIR);
        tile.serverTick();
        helper.assertTrue(tile.inventory().getStackInSlot(1).isEmpty() && tile.progress() > 0 && tile.progress() <= 0.5,
                "A final partial source provides only its paid fractional work");
        helper.assertTrue(tile.inventory().getStackInSlot(0).getCount() == 4 && tile.inventory().getStackInSlot(2).isEmpty(),
                "Fuel exhaustion cannot duplicate outputs or consume uncompleted inputs");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.irradiator")
    public static void automationConfigurationAndPersistenceRoundTrip(GameTestHelper helper) {
        var tile = machine(helper);
        var handler = helper.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, tile.getBlockPos(), Direction.NORTH);
        helper.assertTrue(handler != null && handler.insertItem(0, new ItemStack(Items.ROTTEN_FLESH), false).isEmpty(), "Input automation accepts recipes");
        helper.assertTrue(handler.insertItem(1, source("cobalt_60", 400), false).isEmpty(), "Fuel automation accepts a partially depleted source");
        helper.assertTrue(handler.insertItem(2, new ItemStack(Items.ROTTEN_FLESH), false).getCount() == 1
                && handler.extractItem(0, 1, false).isEmpty(), "Default connections reject output insertion and input extraction");
        tile.cycleSorption(Direction.NORTH, 0); // INPUT -> OUTPUT
        helper.assertTrue(handler.extractItem(0, 1, false).is(Items.ROTTEN_FLESH), "Existing capability immediately observes side configuration");
        tile.inventory().setStackInSlot(0, new ItemStack(Items.ROTTEN_FLESH));
        tile.serverTick();
        tile.toggleRedstoneControl(); tile.cycleOutputMode();
        var restored = BlockEntity.loadStatic(tile.getBlockPos(), tile.getBlockState(), tile.saveWithFullMetadata(helper.getLevel().registryAccess()), helper.getLevel().registryAccess());
        helper.assertTrue(restored instanceof IrradiatorBlockEntity, "Registry-aware NBT reload restores the actual QMD block entity");
        var copy = (IrradiatorBlockEntity) restored;
        helper.assertTrue(copy.progress() == tile.progress() && !copy.redstoneControl()
                && copy.outputMode() == IrradiatorBlockEntity.OutputMode.VOID_EXCESS
                && copy.sorption(Direction.NORTH, 0) == IrradiatorBlockEntity.Sorption.OUTPUT
                && ParticleReservoirItem.stored(copy.inventory().getStackInSlot(1)) == ParticleReservoirItem.stored(tile.inventory().getStackInSlot(1)),
                "Progress, source components, side modes, output mode and redstone settings persist together");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.irradiator")
    public static void menuSlotsShiftClickAndOwnerScopedActions(GameTestHelper helper) {
        var tile = machine(helper);
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setPos(tile.getBlockPos().getCenter());
        var menu = new IrradiatorMenu(7, player.getInventory(), tile); player.containerMenu = menu;
        player.getInventory().setItem(9, new ItemStack(Items.ROTTEN_FLESH, 3));
        helper.assertTrue(menu.quickMoveStack(player, 3).getCount() == 3 && tile.inventory().getStackInSlot(0).getCount() == 3,
                "Shift-click routes process inputs into the input slot");
        player.getInventory().setItem(10, source("cobalt_60", 12345));
        helper.assertTrue(!menu.quickMoveStack(player, 4).isEmpty() && ParticleReservoirItem.stored(tile.inventory().getStackInSlot(1)) == 12345,
                "Shift-click preserves partial source components");
        helper.assertTrue(!menu.getSlot(2).mayPlace(new ItemStack(Items.STONE)) && menu.slots.size() == 39, "Full menu has three machine slots and thirty-six player slots");
        helper.assertTrue(menu.clickMenuButton(player, 0) && !tile.redstoneControl(), "Matching open owner may configure machine");
        helper.assertTrue(!menu.clickMenuButton(player, -1) && !menu.clickMenuButton(player, 28), "Malformed menu actions are rejected");
        var stranger = helper.makeMockPlayer(GameType.SURVIVAL); stranger.containerMenu = menu;
        helper.assertTrue(!menu.clickMenuButton(stranger, 0), "Other players cannot mutate this menu");
        player.setPos(tile.getBlockPos().getCenter().add(20, 0, 0));
        helper.assertTrue(!menu.stillValid(player) && !menu.clickMenuButton(player, 0), "Distance bounds revoke stale menu access");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.irradiator")
    public static void outputAutomationPushesIntoAdjacentRealInventory(GameTestHelper helper) {
        var tile = machine(helper);
        helper.setBlock(POS.east(), Blocks.CHEST);
        tile.inventory().setStackInSlot(2, new ItemStack(QMDItems.FLESH.get(), 7));
        tile.serverTick();
        var chest = helper.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, helper.absolutePos(POS.east()), Direction.WEST);
        helper.assertTrue(chest != null && chest.getStackInSlot(0).is(QMDItems.FLESH.get())
                && chest.getStackInSlot(0).getCount() == 7 && tile.inventory().getStackInSlot(2).isEmpty(),
                "Configured output faces automatically push into the neighbor's real sided inventory");
        helper.assertTrue(tile.resistance().getBaseRadResistance() == IrradiatorConfig.RADIATION_RESISTANCE.get(),
                "Radiation shielding uses the configured Neohaul resistance capability");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.irradiator.reload", timeoutTicks = 1_000_000)
    public static void recipeRemovalStopsThenActualResourceReloadRestores(GameTestHelper helper) {
        var tile = machine(helper);
        tile.inventory().setStackInSlot(0, new ItemStack(Items.ROTTEN_FLESH));
        tile.inventory().setStackInSlot(1, source("cobalt_60", 10000));
        var manager = helper.getLevel().getRecipeManager();
        var original = List.copyOf(manager.getRecipes());
        try {
            manager.replaceRecipes(original.stream().filter(holder -> holder.value().getType() != QMDRecipes.IRRADIATOR_TYPE.get()).toList());
            tile.serverTick();
            helper.assertTrue(!tile.processing() && ParticleReservoirItem.stored(tile.inventory().getStackInSlot(1)) == 10000,
                    "Removing authoritative recipes stops runtime work without source loss");
        } finally { manager.replaceRecipes(original); }
        var server = helper.getLevel().getServer();
        long deadline = System.nanoTime() + java.util.concurrent.TimeUnit.SECONDS.toNanos(30);
        CompletableFuture<Void> reload = CompletableFuture.runAsync(() -> server.reloadResources(List.copyOf(server.getPackRepository().getSelectedIds())).join());
        helper.startSequence().thenExecuteFor(1_000_000, () -> {
            helper.assertTrue(System.nanoTime() <= deadline, "Irradiator resource reload exceeded thirty seconds");
            if (reload.isCompletedExceptionally())
                helper.fail("Irradiator resource reload failed: " + reload.handle((unused, failure) -> failure).join());
        });
        helper.startSequence().thenWaitUntil(() -> helper.assertTrue(reload.isDone(), "Wait for actual resource reload"))
                .thenExecute(() -> {
            reload.join();
            var reloaded = helper.getLevel().getRecipeManager();
            helper.assertTrue(reloaded != manager && reloaded.getAllRecipesFor(QMDRecipes.IRRADIATOR_TYPE.get()).size() == 7
                    && reloaded.getAllRecipesFor(QMDRecipes.IRRADIATOR_FUEL_TYPE.get()).size() == 2, "Reload restores all seven irradiation and both source recipes");
            var ops = helper.getLevel().registryAccess().createSerializationContext(JsonOps.INSTANCE);
            for (var holder : reloaded.getRecipes()) if (holder.value() instanceof IrradiatorRecipe recipe) {
                var codec = IrradiatorRecipe.codec(recipe.kind()).codec();
                var copy = codec.parse(ops, codec.encodeStart(ops, recipe).getOrThrow()).getOrThrow();
                helper.assertTrue(copy.multiplier() == recipe.multiplier() && copy.radiation() == recipe.radiation()
                        && ItemStack.matches(copy.output(), recipe.output()), "Codec retains all process/fuel properties");
            }
            helper.assertTrue(helper.getLevel().getBlockEntity(tile.getBlockPos()) == tile && !tile.isRemoved(),
                    "A real resource reload preserves the placed machine instance");
            tile.inventory().setStackInSlot(0, new ItemStack(Items.ROTTEN_FLESH));
            tile.inventory().setStackInSlot(1, source("cobalt_60", 10000));
            tile.serverTick();
            helper.assertTrue(tile.processing() && ParticleReservoirItem.stored(tile.inventory().getStackInSlot(1)) == 9990,
                    "The same irradiator consumes real source particles using newly reloaded process and fuel recipes");
        }).thenSucceed();
    }
}
