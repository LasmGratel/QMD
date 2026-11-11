package lach_01298.qmd.block.entity;

import com.nred.nuclearcraft.block_entity.internal.fluid.TankSorption;
import lach_01298.qmd.registry.QMDBlocks;
import lach_01298.qmd.registry.QMDFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Real registered collectors, loaded world recipes, sided capabilities, bucket use and NBT. */
@GameTestHolder("qmd")
@PrefixGameTestTemplate(false)
public final class CollectorGameTests {
    private static final BlockPos LIQUID = new BlockPos(2, 4, 2);
    private static final BlockPos ATMOSPHERE = new BlockPos(2, 0, 2);

    private CollectorGameTests() {}

    @GameTest(template = "empty", batch = "qmd.collector")
    public static void liquidCollectorUsesFullVolumeAndItsOwnEnergyCost(GameTestHelper helper) {
        LiquidCollectorBlockEntity tile = liquid(helper);
        charge(helper, tile);
        tile.refreshCollection();
        helper.assertTrue(tile.getEfficiency() == 1D && tile.getCollectionRate() == 1000,
                "A full ocean source-water volume must collect one bucket per productive tick");
        helper.assertTrue(tile.getOutputTank().getCapacity() == 32_000
                        && tile.getMaxEnergyStoredLong() == 20L * tile.getPowerPerTick(),
                "Collector must retain the 32-bucket tank and twenty-tick energy buffer");
        long energy = tile.getEnergyStoredLong();
        tile.serverTick();
        helper.assertTrue(tile.getOutputTank().getFluid().is(QMDFluids.SALT_WATER.still.get())
                        && tile.getOutputTank().getFluidAmount() == 1000 && tile.isProcessing(),
                "Registered liquid collector must generate the loaded salt-water recipe");
        helper.assertTrue(tile.getEnergyStoredLong() == energy - tile.getPowerPerTick(),
                "Liquid collector must debit its own configured cost, rather than the atmosphere cost");
        helper.assertTrue(helper.getLevel().getBlockState(tile.getBlockPos().below()).is(Blocks.WATER),
                "Collection must not consume the sampled source block");

        // The sampled block counts once; the collector itself is excluded from the 124 neighbors.
        for (BlockPos pos : BlockPos.betweenClosed(tile.getBlockPos().offset(-2, -4, -2), tile.getBlockPos().offset(2, 0, 2))) {
            if (!pos.equals(tile.getBlockPos()) && !pos.equals(tile.getBlockPos().below())) {
                helper.getLevel().setBlock(pos, Blocks.STONE.defaultBlockState(), 2);
            }
        }
        tile.refreshCollection();
        helper.assertTrue(tile.getEfficiency() == 1D / 124D && tile.getCollectionRate() == 8,
                "A single sampled fluid block must preserve legacy efficiency and integer truncation");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.collector")
    public static void atmosphereCollectorMeasuresOcclusionAndWorldTicks(GameTestHelper helper) {
        AtmosphereCollectorBlockEntity tile = atmosphere(helper);
        charge(helper, tile);
        tile.refreshCollection();
        helper.assertTrue(tile.getEfficiency() == 1D && tile.getCollectionRate() == 1000,
                "An open volume in the overworld must collect compressed air at full efficiency");
        BlockPos obstruction = tile.getBlockPos().above();
        helper.getLevel().setBlock(obstruction, Blocks.GLASS.defaultBlockState(), 2);
        helper.assertTrue(tile.isOcclusive(Blocks.GLASS.defaultBlockState(), obstruction),
                "Transparent solid glass must obstruct the legacy volume scan");
        helper.assertTrue(!tile.isOcclusive(Blocks.OAK_LEAVES.defaultBlockState(), obstruction)
                        && !tile.isOcclusive(Blocks.SHORT_GRASS.defaultBlockState(), obstruction)
                        && !tile.isOcclusive(Blocks.STONE_SLAB.defaultBlockState(), obstruction),
                "Foliage, replaceable plants and non-full opaque-material slabs must remain scan openings");
        for (var opening : new net.minecraft.world.level.block.Block[]{Blocks.OAK_DOOR, Blocks.IRON_DOOR,
                Blocks.OAK_TRAPDOOR, Blocks.IRON_TRAPDOOR, Blocks.LADDER, Blocks.IRON_BARS,
                Blocks.OAK_PRESSURE_PLATE, Blocks.STONE_PRESSURE_PLATE}) {
            helper.assertTrue(!tile.isOcclusive(opening.defaultBlockState(), obstruction),
                    "Non-full wood/iron/stone material must remain an opening: " + opening);
        }
        for (var occluder : new net.minecraft.world.level.block.Block[]{Blocks.STONE, Blocks.GLASS_PANE,
                Blocks.ICE, Blocks.TORCH, Blocks.REDSTONE_WIRE, Blocks.OAK_BUTTON, Blocks.END_ROD}) {
            helper.assertTrue(tile.isOcclusive(occluder.defaultBlockState(), obstruction),
                    "Full solids and legacy nonopaque circuit/glass/ice materials must obstruct: " + occluder);
        }
        for (var foliageMaterial : new net.minecraft.world.level.block.Block[]{Blocks.PUMPKIN, Blocks.MELON,
                Blocks.DRAGON_EGG, Blocks.CHORUS_FLOWER, Blocks.COCOA, Blocks.NETHER_WART}) {
            helper.assertTrue(!tile.isOcclusive(foliageMaterial.defaultBlockState(), obstruction),
                    "Legacy foliage-map-color material exclusions must survive modern map-color changes: " + foliageMaterial);
        }
        tile.refreshCollection();
        helper.assertTrue(Math.abs(tile.getEfficiency() - 123D / 124D) < 1E-12
                        && tile.getCollectionRate() == 991,
                "A single obstruction must reduce the full 124-block volume, rather than use sky visibility");
        helper.getLevel().setBlock(obstruction, Blocks.AIR.defaultBlockState(), 2);
        tile.refreshCollection();
        long energy = tile.getEnergyStoredLong();
        helper.runAfterDelay(5, () -> {
            long spent = energy - tile.getEnergyStoredLong();
            helper.assertTrue(spent > 0 && spent % tile.getPowerPerTick() == 0
                            && tile.getOutputTank().getFluidAmount() == spent / tile.getPowerPerTick() * 1000,
                    "The block's registered world ticker must generate fluid and consume matching energy");
            helper.assertTrue(tile.getOutputTank().getFluid().is(QMDFluids.COMPRESSED_AIR.still.get()),
                    "World ticking must generate the registered compressed-air fluid");
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "qmd.collector")
    public static void blockedOrUnpoweredCollectorsPreserveTankAndEnergy(GameTestHelper helper) {
        AtmosphereCollectorBlockEntity tile = atmosphere(helper);
        tile.refreshCollection();
        tile.serverTick();
        helper.assertTrue(tile.getOutputTank().isEmpty() && !tile.isProcessing() && tile.getCollectionFluid().isEmpty(),
                "An unpowered collector must neither generate nor advertise available collection");
        charge(helper, tile);
        long energy = tile.getEnergyStoredLong();
        tile.getOutputTank().setFluidStored(new FluidStack(Fluids.WATER, 500));
        tile.serverTick();
        helper.assertTrue(tile.getOutputTank().getFluid().is(Fluids.WATER)
                        && tile.getOutputTank().getFluidAmount() == 500 && tile.getEnergyStoredLong() == energy,
                "An incompatible stored fluid must not be overwritten or waste energy");
        tile.getOutputTank().setFluidStored(new FluidStack(QMDFluids.COMPRESSED_AIR.still.get(), 32_000));
        tile.serverTick();
        helper.assertTrue(tile.getOutputTank().getFluidAmount() == 32_000 && tile.getEnergyStoredLong() == energy,
                "A full output tank must halt production without spending energy");
        tile.getOutputTank().setFluidStored(new FluidStack(QMDFluids.COMPRESSED_AIR.still.get(), 31_990));
        tile.serverTick();
        helper.assertTrue(tile.getOutputTank().getFluidAmount() == 32_000
                        && tile.getEnergyStoredLong() == energy - tile.getPowerPerTick(),
                "A partial final fill must fit capacity and spend exactly one productive tick of energy");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.collector")
    public static void outputCapabilitiesSimulateAndBucketsDrainWithoutInsertion(GameTestHelper helper) {
        LiquidCollectorBlockEntity tile = liquid(helper);
        tile.getOutputTank().setFluidStored(new FluidStack(QMDFluids.SALT_WATER.still.get(), 2000));
        for (Direction side : Direction.values()) {
            IFluidHandler fluid = helper.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, tile.getBlockPos(), side);
            var energy = helper.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, tile.getBlockPos(), side);
            helper.assertTrue(fluid != null && energy != null && energy.canReceive() && !energy.canExtract(),
                    "Every side must expose fluid output and energy input");
            helper.assertTrue(fluid.fill(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE) == 0,
                    "Output-only collector tanks must reject automation insertion");
            helper.assertTrue(fluid.drain(250, IFluidHandler.FluidAction.SIMULATE).getAmount() == 250
                            && tile.getOutputTank().getFluidAmount() == 2000,
                    "Simulated output extraction must not alter the tank");
        }
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BUCKET));
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(tile.getBlockPos()), Direction.UP, tile.getBlockPos(), false);
        player.setShiftKeyDown(true);
        player.setPose(Pose.CROUCHING);
        ItemInteractionResult crouching = tile.getBlockState().useItemOn(player.getMainHandItem(), helper.getLevel(),
                player, InteractionHand.MAIN_HAND, hit);
        helper.assertTrue(crouching == ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
                        && player.getMainHandItem().is(Items.BUCKET) && tile.getOutputTank().getFluidAmount() == 2000,
                "Crouching with a bucket must retain the legacy route that defers the collector's tank interaction");
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        helper.assertTrue(tile.getBlockState().useItemOn(player.getMainHandItem(), helper.getLevel(),
                        player, InteractionHand.MAIN_HAND, hit) == ItemInteractionResult.SUCCESS,
                "Crouching with an empty hand must still use the original collection-status interaction");
        player.setShiftKeyDown(false);
        player.setPose(Pose.STANDING);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BUCKET));
        ItemInteractionResult result = tile.getBlockState().useItemOn(player.getMainHandItem(), helper.getLevel(),
                player, InteractionHand.MAIN_HAND, hit);
        helper.assertTrue(result == ItemInteractionResult.SUCCESS
                        && player.getMainHandItem().is(QMDFluids.SALT_WATER.bucket.asItem())
                        && tile.getOutputTank().getFluidAmount() == 1000,
                "Normal block interaction must fill a real salt-water bucket and drain exactly one bucket");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.collector")
    public static void collectorPushesStoredFluidWhileUnpowered(GameTestHelper helper) {
        AtmosphereCollectorBlockEntity source = atmosphere(helper);
        BlockPos receiverPos = source.getBlockPos().east();
        helper.getLevel().setBlock(receiverPos, QMDBlocks.ORE_LEACHER.get().defaultBlockState(), 3);
        var receiver = (lach_01298.qmd.block.entity.OreLeacherBlockEntity) helper.getLevel().getBlockEntity(receiverPos);
        source.getOutputTank().setFluidStored(new FluidStack(QMDFluids.NITRIC_ACID.still.get(), 1000));
        source.serverTick();
        helper.assertTrue(source.getOutputTank().isEmpty()
                        && receiver.getTanks().stream().mapToInt(tank -> tank.getFluidAmount()).sum() == 1000,
                "Already stored output must push to adjacent compatible fluid input without collection power");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.collector")
    public static void collectionAndPushNeverForceUnloadedNeighborChunks(GameTestHelper helper) {
        BlockPos boundary = loadedChunkBoundary(helper);
        int unloadedX = (boundary.getX() >> 4) + 1;
        int unloadedZ = boundary.getZ() >> 4;
        var chunks = helper.getLevel().getChunkSource();
        helper.assertTrue(chunks.getChunkNow(unloadedX, unloadedZ) == null,
                "The regression fixture must begin beside a truly unloaded chunk");
        // A detached tile exercises the real world-read and push paths without placing blocks or
        // adding tickets at the edge of the already loaded area.
        for (boolean liquid : new boolean[]{false, true}) {
            FluidCollectorBlockEntity tile = liquid
                    ? new LiquidCollectorBlockEntity(boundary, QMDBlocks.LIQUID_COLLECTOR.get().defaultBlockState())
                    : new AtmosphereCollectorBlockEntity(boundary, QMDBlocks.ATMOSPHERE_COLLECTOR.get().defaultBlockState());
            tile.setLevel(helper.getLevel());
            tile.getEnergyStorage().setEnergyStored(tile.getPowerPerTick());
            tile.getOutputTank().setFluidStored(new FluidStack(Fluids.WATER, 1000));
            long energy = tile.getEnergyStoredLong();
            tile.refreshCollection();
            tile.serverTick();
            helper.assertTrue(tile.getEfficiency() == 0D && tile.getCollectionRecipeId() == null
                            && tile.getCollectionRate() == 0 && tile.getEnergyStoredLong() == energy
                            && tile.getOutputTank().getFluidAmount() == 1000,
                    "An unavailable scan must stop generation, preserve stored contents and retry on its normal interval");
            helper.assertTrue(chunks.getChunkNow(unloadedX, unloadedZ) == null,
                    "Neither area/biome sampling nor stored-output push may force an unloaded neighboring chunk");
        }
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.collector")
    public static void persistenceRetainsContentsAndRevalidatesWorldAfterLoad(GameTestHelper helper) {
        LiquidCollectorBlockEntity tile = liquid(helper);
        charge(helper, tile);
        tile.refreshCollection();
        tile.serverTick();
        tile.setTankSorption(Direction.NORTH, 0, TankSorption.NON);
        var registries = helper.getLevel().registryAccess();
        CompoundTag saved = tile.writeAll(new CompoundTag(), registries);
        var copy = new LiquidCollectorBlockEntity(tile.getBlockPos(), tile.getBlockState());
        copy.setLevel(helper.getLevel());
        copy.readAll(saved, registries);
        helper.assertTrue(copy.getEnergyStoredLong() == tile.getEnergyStoredLong()
                        && FluidStack.matches(copy.getOutputTank().getFluid(), tile.getOutputTank().getFluid()),
                "Collector NBT must retain exact energy and tank contents");
        helper.assertTrue(copy.getScanCountdown() == 0 && copy.getCollectionRecipeId() == null,
                "Reloaded collectors must revalidate the world instead of persisting a stale recipe instance");
        helper.assertTrue(copy.hasConfigurableFluidConnections() && copy.getTankSorption(Direction.NORTH, 0) == TankSorption.NON
                        && copy.getTankSorption(Direction.SOUTH, 0) == TankSorption.OUT
                        && copy.getFluidSideCapability(Direction.NORTH) == null,
                "Non-default fluid sorption must survive NBT while other sides retain their output setting");
        copy.toggleTankSorption(Direction.NORTH, 0, TankSorption.Type.OUTPUT, false);
        helper.assertTrue(copy.getTankSorption(Direction.NORTH, 0) == TankSorption.OUT
                        && copy.getFluidSideCapability(Direction.NORTH).drain(100, IFluidHandler.FluidAction.SIMULATE).getAmount() == 100
                        && copy.getOutputTank().getFluidAmount() == 1000,
                "The inherited side-toggle contract must remain functional and restore output capability");
        helper.getLevel().setBlock(tile.getBlockPos().below(), Blocks.STONE.defaultBlockState(), 2);
        long energy = copy.getEnergyStoredLong();
        copy.serverTick();
        helper.assertTrue(copy.getCollectionRecipeId() == null && copy.getCollectionRate() == 0
                        && copy.getEnergyStoredLong() == energy && copy.getOutputTank().getFluidAmount() == 1000,
                "The first load tick must reject changed surroundings while preserving stored output");
        saved.putDouble("efficiency", Double.NaN);
        copy.readAll(saved, registries);
        helper.assertTrue(copy.getEfficiency() == 0D && copy.getOutputTank().getCapacity() == 32_000,
                "Malformed efficiency must be sanitized and tank capacity remain fixed");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.collector")
    public static void collectionRescansAfterOneHundredTicksAndPreservesLegacyRedstoneBehavior(GameTestHelper helper) {
        LiquidCollectorBlockEntity tile = liquid(helper);
        charge(helper, tile);
        tile.refreshCollection();
        var originalRecipe = tile.getCollectionRecipeId();
        helper.getLevel().setBlock(tile.getBlockPos().above(), Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
        helper.assertTrue(helper.getLevel().hasNeighborSignal(tile.getBlockPos()), "Fixture must actually power the collector");
        helper.getLevel().setBlock(tile.getBlockPos().below(), Blocks.STONE.defaultBlockState(), 2);
        for (int tick = 0; tick < 99; ++tick) tile.serverTick();
        helper.assertTrue(tile.getScanCountdown() == 1 && originalRecipe.equals(tile.getCollectionRecipeId())
                        && tile.getOutputTank().getFluidAmount() > 0,
                "Legacy collection must run despite redstone and retain the cached world sample until tick one hundred");
        int stored = tile.getOutputTank().getFluidAmount();
        tile.serverTick();
        helper.assertTrue(tile.getScanCountdown() == 100 && tile.getCollectionRecipeId() == null
                        && tile.getOutputTank().getFluidAmount() == stored,
                "Tick one hundred must reject the changed sample and preserve previously collected fluid");
        helper.succeed();
    }

    private static LiquidCollectorBlockEntity liquid(GameTestHelper helper) {
        BlockPos absolute = helper.absolutePos(LIQUID);
        for (BlockPos pos : BlockPos.betweenClosed(absolute.offset(-2, -4, -2), absolute.offset(2, 0, 2))) {
            helper.getLevel().setBlock(pos, Blocks.WATER.defaultBlockState(), 2);
        }
        // fillbiome uses vanilla's quart-resolution biome update and does not change recipe data.
        // Level.getBiome uses a jittered neighboring quart sample. Include surrounding cells too.
        BlockPos biomeMin = absolute.offset(-4, -4, -4);
        BlockPos biomeMax = absolute.offset(4, 4, 4);
        String coordinates = biomeMin.getX() + " " + biomeMin.getY() + " " + biomeMin.getZ() + " "
                + biomeMax.getX() + " " + biomeMax.getY() + " " + biomeMax.getZ();
        var server = helper.getLevel().getServer();
        server.getCommands().performPrefixedCommand(server.createCommandSourceStack().withSuppressedOutput(),
                "fillbiome " + coordinates + " minecraft:ocean");
        helper.assertTrue(helper.getLevel().getBiome(absolute).is(Biomes.OCEAN), "Fixture must use a real ocean biome");
        helper.setBlock(LIQUID, QMDBlocks.LIQUID_COLLECTOR.get());
        return helper.getBlockEntity(LIQUID);
    }

    private static AtmosphereCollectorBlockEntity atmosphere(GameTestHelper helper) {
        BlockPos absolute = helper.absolutePos(ATMOSPHERE);
        for (BlockPos pos : BlockPos.betweenClosed(absolute.offset(-2, 0, -2), absolute.offset(2, 4, 2))) {
            helper.getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
        }
        helper.setBlock(ATMOSPHERE, QMDBlocks.ATMOSPHERE_COLLECTOR.get());
        return helper.getBlockEntity(ATMOSPHERE);
    }

    private static void charge(GameTestHelper helper, FluidCollectorBlockEntity tile) {
        var energy = helper.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, tile.getBlockPos(), Direction.NORTH);
        helper.assertTrue(energy != null && energy.receiveEnergy(Integer.MAX_VALUE, false) > 0,
                "Registered collector must accept external energy");
    }

    private static BlockPos loadedChunkBoundary(GameTestHelper helper) {
        BlockPos origin = helper.absolutePos(BlockPos.ZERO);
        var chunks = helper.getLevel().getChunkSource();
        for (int offsetX = -32; offsetX <= 32; offsetX++) {
            for (int offsetZ = -32; offsetZ <= 32; offsetZ++) {
                int chunkX = (origin.getX() >> 4) + offsetX;
                int chunkZ = (origin.getZ() >> 4) + offsetZ;
                if (chunks.getChunkNow(chunkX, chunkZ) != null && chunks.getChunkNow(chunkX + 1, chunkZ) == null) {
                    return new BlockPos((chunkX << 4) + 15, helper.getLevel().getMaxBuildHeight() - 2, (chunkZ << 4) + 8);
                }
            }
        }
        throw new IllegalStateException("No loaded/unloaded east chunk boundary found for collector regression fixture");
    }
}
