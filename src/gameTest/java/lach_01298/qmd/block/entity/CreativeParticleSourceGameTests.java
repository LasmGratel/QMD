package lach_01298.qmd.block.entity;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import lach_01298.qmd.block.menu.CreativeParticleSourceMenu;
import lach_01298.qmd.capability.QMDParticleCapabilities;
import lach_01298.qmd.network.ConfigureParticleSourcePayload;
import lach_01298.qmd.network.ParticleSourceStatePayload;
import lach_01298.qmd.particle.IParticleStackHandler;
import lach_01298.qmd.particle.ParticleBeam;
import lach_01298.qmd.particle.ParticleStack;
import lach_01298.qmd.particle.Particles;
import lach_01298.qmd.registry.QMDBlocks;
import lach_01298.qmd.registry.QMDMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.network.connection.ConnectionType;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/** Real source ticks and capabilities, persistent beams and the server configuration-menu contract. */
@GameTestHolder("qmd")
@PrefixGameTestTemplate(false)
public final class CreativeParticleSourceGameTests {
    private static final BlockPos SOURCE = new BlockPos(2, 2, 2);
    private static final ParticleBeam BEAM = new ParticleBeam("proton", 7, Long.MAX_VALUE, 123.75);

    private CreativeParticleSourceGameTests() {}

    @GameTest(template = "empty", batch = "qmd.particle.source")
    public static void capabilityProvidesIndependentInfiniteBeamsOnEverySide(GameTestHelper helper) {
        CreativeParticleSourceBlockEntity source = source(helper);
        source.setBeam(BEAM);
        for (Direction side : Direction.values()) {
            assertCapability(helper, source, side);
        }
        assertCapability(helper, source, null);
        source.setBeam(ParticleBeam.EMPTY);
        IParticleStackHandler capability = helper.getLevel().getCapability(
                QMDParticleCapabilities.BLOCK, source.getBlockPos(), null);
        helper.assertTrue(capability != null && capability.getParticle() == null
                        && !capability.canExtractParticle(null) && capability.extractParticle(null) == null,
                "Clearing a source must immediately clear its existing capability without replacing it");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.particle.source")
    public static void realWorldTicksPushAllFacesAndRedstoneStopsAndResumes(GameTestHelper helper) {
        CreativeParticleSourceBlockEntity source = source(helper);
        source.setBeam(BEAM);
        Map<Direction, ParticleReceiverFixture.Receiver> receivers = receivers(helper, source);
        helper.runAfterDelay(3, () -> {
            for (var entry : receivers.entrySet()) {
                var storage = entry.getValue().storage;
                helper.assertTrue(storage.insertions > 0 && storage.lastBeam.equals(BEAM)
                                && storage.lastSide == entry.getKey().getOpposite(),
                        "The registered source ticker must push a complete exact beam into the opposite face of every neighbor");
            }
            helper.assertTrue(source.getBeam().equals(BEAM), "Sending on six faces must not consume the configured beam");
            // Power replaces only the upper receiver; keep the other five as probes.
            helper.getLevel().setBlock(source.getBlockPos().above(), Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
            IParticleStackHandler paused = helper.getLevel().getCapability(QMDParticleCapabilities.BLOCK, source.getBlockPos(), Direction.NORTH);
            helper.assertTrue(paused != null && !paused.canExtractParticle(Direction.NORTH)
                            && paused.extractParticle(Direction.NORTH) == null && paused.getParticle() == null,
                    "Neighbor pull and capability inspection must obey the same redstone pause as emission");
            helper.assertTrue(helper.getLevel().hasNeighborSignal(source.getBlockPos()),
                    "The test must actually power the source");
            Map<Direction, Integer> before = new EnumMap<>(Direction.class);
            receivers.forEach((direction, receiver) -> before.put(direction, receiver.storage.insertions));
            helper.runAfterDelay(3, () -> {
                receivers.forEach((direction, receiver) -> {
                    if (direction != Direction.UP) {
                        helper.assertTrue(receiver.storage.insertions == before.get(direction),
                                "Powered source ticks must stop sending on every still-connected face");
                    }
                });
                helper.assertTrue(source.getBeam().equals(BEAM), "Redstone halt must retain the configured beam");
                helper.getLevel().setBlock(source.getBlockPos().above(), Blocks.AIR.defaultBlockState(), 3);
                helper.runAfterDelay(3, () -> {
                    receivers.forEach((direction, receiver) -> {
                        if (direction != Direction.UP) {
                            helper.assertTrue(receiver.storage.insertions > before.get(direction)
                                            && receiver.storage.lastBeam.equals(BEAM),
                                    "Removing redstone must resume exact particle output without reconfiguration");
                        }
                    });
                    helper.succeed();
                });
            });
        });
    }

    @GameTest(template = "empty", batch = "qmd.particle.source")
    public static void rejectedNeighborsAndEmptySourceDoNotChangeOtherOutputs(GameTestHelper helper) {
        CreativeParticleSourceBlockEntity source = source(helper);
        source.setBeam(BEAM);
        Map<Direction, ParticleReceiverFixture.Receiver> receivers = receivers(helper, source);
        receivers.get(Direction.NORTH).storage.reject = true;
        source.serverTick();
        for (var entry : receivers.entrySet()) {
            helper.assertTrue(entry.getValue().storage.insertions == (entry.getKey() == Direction.NORTH ? 0 : 1),
                    "A rejecting neighbor must not stop a creative source from serving the other faces");
        }
        source.setBeam(ParticleBeam.EMPTY);
        source.serverTick();
        for (var entry : receivers.entrySet()) {
            helper.assertTrue(entry.getValue().storage.insertions == (entry.getKey() == Direction.NORTH ? 0 : 1),
                    "An empty configuration must not send or replay the previous beam");
        }
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.particle.source")
    public static void strongRedstoneThroughSolidNeighborHaltsAndResumesEmission(GameTestHelper helper) {
        CreativeParticleSourceBlockEntity source = source(helper);
        source.setBeam(BEAM);
        var level = helper.getLevel();
        BlockPos receiverPos = source.getBlockPos().west();
        level.setBlock(receiverPos, ParticleReceiverFixture.block.defaultBlockState(), 3);
        var receiver = (ParticleReceiverFixture.Receiver) level.getBlockEntity(receiverPos);
        helper.assertTrue(receiver != null, "The strong-power probe must use a real adjacent particle receiver");
        BlockPos conductorPos = source.getBlockPos().east();
        BlockPos signalPos = conductorPos.east();
        level.setBlock(conductorPos, Blocks.STONE.defaultBlockState(), 3);
        level.setBlock(signalPos, Blocks.LEVER.defaultBlockState()
                .setValue(BlockStateProperties.ATTACH_FACE, AttachFace.WALL)
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)
                .setValue(BlockStateProperties.POWERED, true), 3);
        helper.assertTrue(level.hasNeighborSignal(source.getBlockPos()),
                "The fixture must power the source through a solid conductor rather than a directly adjacent signal block");
        source.serverTick();
        helper.assertTrue(receiver.storage.insertions == 0 && source.getBeam().equals(BEAM),
                "Strong power relayed by a solid neighbor must halt emission while retaining the configured beam");
        level.setBlock(signalPos, Blocks.AIR.defaultBlockState(), 3);
        helper.assertTrue(!level.hasNeighborSignal(source.getBlockPos()), "Removing the distant signal must unpower the source");
        source.serverTick();
        helper.assertTrue(receiver.storage.insertions == 1 && receiver.storage.lastBeam.equals(BEAM)
                        && receiver.storage.lastSide == Direction.EAST && source.getBeam().equals(BEAM),
                "Removing relayed strong power must resume exact emission into the receiver's opposite face");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.particle.source")
    public static void sourceNbtPreservesLegacyKeysExactEnergyAndClearedState(GameTestHelper helper) {
        CreativeParticleSourceBlockEntity source = source(helper);
        source.setBeam(BEAM);
        var registries = helper.getLevel().registryAccess();
        CompoundTag saved = source.saveWithoutMetadata(registries);
        helper.assertTrue(saved.contains("ParticleStorage0")
                        && saved.getCompound("ParticleStorage0").getCompound("particle_stack").getLong("meanEnergy") == Long.MAX_VALUE,
                "The port must retain legacy ParticleStorage0/particle_stack keys and full long energy");
        var restored = new CreativeParticleSourceBlockEntity(source.getBlockPos(), source.getBlockState());
        restored.setLevel(helper.getLevel());
        restored.loadWithComponents(saved, registries);
        helper.assertTrue(restored.getBeam().equals(BEAM), "Saving and loading must preserve species, amount, energy and focus");
        source.setBeam(ParticleBeam.EMPTY);
        restored.loadWithComponents(source.saveWithoutMetadata(registries), registries);
        helper.assertTrue(restored.getBeam().equals(ParticleBeam.EMPTY), "Loading empty storage must clear a stale beam");
        saved.getCompound("ParticleStorage0").getCompound("particle_stack").putString("particle", "missing_particle");
        restored.loadWithComponents(saved, registries);
        helper.assertTrue(restored.getBeam().equals(ParticleBeam.EMPTY), "Unknown persisted particles must load as empty");
        restored.setBeam(BEAM);
        restored.loadWithComponents(new CompoundTag(), registries);
        helper.assertTrue(restored.getBeam().equals(ParticleBeam.EMPTY),
                "A complete load without ParticleStorage0 must clear the previous in-memory configuration");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.particle.source")
    public static void menuFactoryAndPayloadPreserveInventoryAndRequireCurrentCreativeMenu(GameTestHelper helper) {
        Fixture fixture = fixture(helper, GameType.CREATIVE);
        fixture.player.getInventory().setItem(0, new ItemStack(Items.DIAMOND, 17));
        helper.assertTrue(fixture.menu.getType() == QMDMenus.CREATIVE_PARTICLE_SOURCE.get()
                        && fixture.menu.slots.isEmpty() && fixture.menu.stillValid(fixture.player),
                "The creative configuration must have its registered menu and no inventory slots");
        helper.assertTrue(ConfigureParticleSourcePayload.apply(fixture.player,
                        new ConfigureParticleSourcePayload(fixture.menu.containerId, fixture.source.getBlockPos(), BEAM))
                        && fixture.source.getBeam().equals(BEAM),
                "A nearby creative player with the current matching menu must be able to configure the source");
        helper.assertTrue(fixture.menu.quickMoveStack(fixture.player, 0).isEmpty()
                        && fixture.player.getInventory().getItem(0).is(Items.DIAMOND)
                        && fixture.player.getInventory().getItem(0).getCount() == 17,
                "A slot-free source menu must leave player inventory unchanged even for a shift-click request");
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            buffer.writeBlockPos(fixture.source.getBlockPos());
            ParticleBeam.STREAM_CODEC.encode(buffer, BEAM);
            var reconstructed = new CreativeParticleSourceMenu(2, fixture.player.getInventory(), buffer);
            helper.assertTrue(reconstructed.getType() == fixture.menu.getType()
                            && reconstructed.getBlockPos().equals(fixture.source.getBlockPos())
                            && reconstructed.getBeam().equals(BEAM) && reconstructed.getDisplayedBeam().equals(BEAM)
                            && reconstructed.slots.isEmpty()
                            && buffer.readableBytes() == 0,
                    "The menu network factory must decode the position and complete initial particle snapshot");
        } finally {
            buffer.release();
        }
        helper.assertTrue(ConfigureParticleSourcePayload.apply(fixture.player,
                        new ConfigureParticleSourcePayload(fixture.menu.containerId, fixture.source.getBlockPos(), ParticleBeam.EMPTY))
                        && fixture.source.getBeam().equals(ParticleBeam.EMPTY),
                "The same authorized menu must support clearing a previously configured beam");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.particle.source")
    public static void settingsRejectSurvivalWrongIdsDistanceAndReplacedTiles(GameTestHelper helper) {
        Fixture fixture = fixture(helper, GameType.CREATIVE);
        fixture.source.setBeam(BEAM);
        ParticleBeam replacement = new ParticleBeam("electron", 3, 42, 0.5);
        helper.assertTrue(!ConfigureParticleSourcePayload.apply(fixture.player,
                        new ConfigureParticleSourcePayload(fixture.menu.containerId + 1, fixture.source.getBlockPos(), replacement))
                        && !ConfigureParticleSourcePayload.apply(fixture.player,
                        new ConfigureParticleSourcePayload(fixture.menu.containerId, fixture.source.getBlockPos().east(), replacement)),
                "A packet for another container or position must not configure this source");
        fixture.player.containerMenu = fixture.player.inventoryMenu;
        helper.assertTrue(!fixture.menu.applySettings(fixture.player, replacement),
                "A stale menu must reject settings after the player closes it");
        fixture.player.containerMenu = fixture.menu;
        BlockPos pos = fixture.source.getBlockPos();
        fixture.player.setPos(pos.getX() + 8.6, pos.getY() + 0.5, pos.getZ() + 0.5);
        helper.assertTrue(!fixture.menu.stillValid(fixture.player)
                        && !fixture.menu.applySettings(fixture.player, replacement),
                "A player more than eight blocks away must not configure the source");
        fixture.player.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        Player survival = helper.makeMockPlayer(GameType.SURVIVAL);
        survival.setPos(fixture.player.getX(), fixture.player.getY(), fixture.player.getZ());
        var survivalMenu = new CreativeParticleSourceMenu(2, survival.getInventory(), fixture.source);
        survival.containerMenu = survivalMenu;
        helper.assertTrue(fixture.source.createMenu(2, survival.getInventory(), survival) == null
                        && !survivalMenu.stillValid(survival) && !survivalMenu.applySettings(survival, replacement)
                        && fixture.source.getBeam().equals(BEAM),
                "A survival player must be denied menu creation and settings even when a menu is constructed with that player as owner");
        helper.setBlock(SOURCE, Blocks.AIR);
        helper.assertTrue(!fixture.menu.stillValid(fixture.player)
                        && !fixture.menu.applySettings(fixture.player, replacement),
                "A removed source must invalidate its open menu and reject subsequent settings");
        helper.setBlock(SOURCE, QMDBlocks.CREATIVE_PARTICLE_SOURCE.get());
        CreativeParticleSourceBlockEntity newSource = helper.getBlockEntity(SOURCE);
        helper.assertTrue(newSource != fixture.source && !fixture.menu.stillValid(fixture.player)
                        && !fixture.menu.applySettings(fixture.player, replacement)
                        && newSource.getBeam().equals(ParticleBeam.EMPTY),
                "Replacing a source at the same position must not revive the old menu or change the new block entity");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.particle.source")
    public static void beamAndConfigurationCodecsKeepExactConsecutiveSnapshots(GameTestHelper helper) {
        ParticleStack mutable = BEAM.toStack();
        ParticleBeam snapshot = ParticleBeam.fromStack(mutable);
        mutable.setAmount(1);
        mutable.setMeanEnergy(0);
        helper.assertTrue(snapshot.equals(BEAM), "Network and persistent beam values must be independent of mutable machine stacks");
        for (ParticleBeam beam : new ParticleBeam[]{BEAM, ParticleBeam.EMPTY}) {
            var encoded = ParticleBeam.CODEC.encodeStart(NbtOps.INSTANCE, beam).getOrThrow();
            helper.assertTrue(ParticleBeam.CODEC.parse(NbtOps.INSTANCE, encoded).getOrThrow().equals(beam),
                    "The persistent beam codec must round-trip exact long energy and explicit empty state");
        }
        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(),
                helper.getLevel().registryAccess(), ConnectionType.NEOFORGE);
        try {
            var active = new ConfigureParticleSourcePayload(37, helper.absolutePos(SOURCE), BEAM);
            var empty = new ConfigureParticleSourcePayload(38, helper.absolutePos(SOURCE).above(), ParticleBeam.EMPTY);
            ConfigureParticleSourcePayload.STREAM_CODEC.encode(buffer, active);
            ConfigureParticleSourcePayload.STREAM_CODEC.encode(buffer, empty);
            helper.assertTrue(ConfigureParticleSourcePayload.STREAM_CODEC.decode(buffer).equals(active)
                            && buffer.readableBytes() > 0,
                    "The first packet must preserve its exact settings and leave the following packet untouched");
            helper.assertTrue(ConfigureParticleSourcePayload.STREAM_CODEC.decode(buffer).equals(empty)
                            && buffer.readableBytes() == 0,
                    "The second packet must preserve empty state and consume exactly the remaining bytes");
        } finally {
            buffer.release();
        }
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.particle.source")
    public static void statePacketsCaptureIndependentBeamsAndClearOnlyMatchingMenus(GameTestHelper helper) {
        Fixture fixture = fixture(helper, GameType.CREATIVE);
        fixture.source.setBeam(BEAM);
        var active = new ParticleSourceStatePayload(fixture.menu.containerId, fixture.source.getBlockPos(), fixture.source.getBeam());
        fixture.source.setBeam(ParticleBeam.EMPTY);
        var empty = new ParticleSourceStatePayload(fixture.menu.containerId, fixture.source.getBlockPos(), fixture.source.getBeam());
        helper.assertTrue(active.beam().equals(BEAM) && empty.beam().equals(ParticleBeam.EMPTY),
                "An already-created GUI packet must retain its immutable beam after the source changes");
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            ParticleSourceStatePayload.STREAM_CODEC.encode(buffer, active);
            ParticleSourceStatePayload.STREAM_CODEC.encode(buffer, empty);
            var decodedActive = ParticleSourceStatePayload.STREAM_CODEC.decode(buffer);
            helper.assertTrue(decodedActive.equals(active) && buffer.readableBytes() > 0
                            && ParticleSourceStatePayload.apply(fixture.player, decodedActive)
                            && fixture.menu.getDisplayedBeam().equals(BEAM),
                    "A matching state packet must update the menu display with the full long-energy beam");
            var decodedEmpty = ParticleSourceStatePayload.STREAM_CODEC.decode(buffer);
            helper.assertTrue(decodedEmpty.equals(empty) && buffer.readableBytes() == 0
                            && ParticleSourceStatePayload.apply(fixture.player, decodedEmpty)
                            && fixture.menu.getDisplayedBeam().equals(ParticleBeam.EMPTY),
                    "The following empty snapshot must consume only its own bytes and clear stale GUI state");
            helper.assertTrue(!ParticleSourceStatePayload.apply(fixture.player,
                            new ParticleSourceStatePayload(fixture.menu.containerId + 1, fixture.source.getBlockPos(), BEAM))
                            && !ParticleSourceStatePayload.apply(fixture.player,
                            new ParticleSourceStatePayload(fixture.menu.containerId, fixture.source.getBlockPos().east(), BEAM))
                            && fixture.menu.getDisplayedBeam().equals(ParticleBeam.EMPTY),
                    "State packets for a different container or source must leave the current display unchanged");
            fixture.player.containerMenu = fixture.player.inventoryMenu;
            helper.assertTrue(!ParticleSourceStatePayload.apply(fixture.player, active)
                            && fixture.menu.getDisplayedBeam().equals(ParticleBeam.EMPTY),
                    "A packet arriving after menu closure must not update its stale display");
            helper.assertTrue(fixture.source.getBeam().equals(ParticleBeam.EMPTY),
                    "Applying display packets must never modify the authoritative source configuration");
        } finally {
            buffer.release();
        }
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.particle.source")
    public static void malformedBeamCodecsRejectUnknownNegativeAndNonfiniteValues(GameTestHelper helper) {
        for (int invalid = 0; invalid < 6; invalid++) {
            CompoundTag data = (CompoundTag) ParticleBeam.CODEC.encodeStart(NbtOps.INSTANCE, BEAM).getOrThrow();
            switch (invalid) {
                case 0 -> data.putString("particle", "missing_particle");
                case 1 -> data.putInt("amount", -1);
                case 2 -> data.putLong("meanEnergy", -1);
                case 3 -> data.putDouble("focus", -1);
                case 4 -> data.putDouble("focus", Double.NaN);
                case 5 -> data.putDouble("focus", Double.POSITIVE_INFINITY);
                default -> throw new IllegalStateException();
            }
            helper.assertTrue(ParticleBeam.CODEC.parse(NbtOps.INSTANCE, data).error().isPresent(),
                    "The persistent codec must reject unknown species, negative limits and nonfinite focus");
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            try {
                buffer.writeUtf(data.getString("particle"));
                buffer.writeVarInt(data.getInt("amount"));
                buffer.writeLong(data.getLong("meanEnergy"));
                buffer.writeDouble(data.getDouble("focus"));
                boolean rejected = false;
                try {
                    ParticleBeam.STREAM_CODEC.decode(buffer);
                } catch (DecoderException expected) {
                    rejected = true;
                }
                helper.assertTrue(rejected, "The network decoder must reject the same malformed beam values");
            } finally {
                buffer.release();
            }
        }
        CompoundTag fakeEmpty = (CompoundTag) ParticleBeam.CODEC.encodeStart(NbtOps.INSTANCE, BEAM).getOrThrow();
        fakeEmpty.putString("particle", "");
        helper.assertTrue(ParticleBeam.CODEC.parse(NbtOps.INSTANCE, fakeEmpty).error().isPresent(),
                "A blank species must not hide nonzero particle quantities or energy");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.particle.source")
    public static void sourceTicksNeverLoadAnUnloadedNeighborChunk(GameTestHelper helper) {
        BlockPos boundary = loadedChunkBoundary(helper);
        int unloadedX = (boundary.getX() >> 4) + 1;
        int unloadedZ = boundary.getZ() >> 4;
        var chunks = helper.getLevel().getChunkSource();
        var detached = new CreativeParticleSourceBlockEntity(boundary,
                QMDBlocks.CREATIVE_PARTICLE_SOURCE.get().defaultBlockState());
        // Configure before attaching the level: setChanged's vanilla neighbor updates are not the tick under test.
        detached.setBeam(BEAM);
        detached.setLevel(helper.getLevel());
        helper.assertTrue(!chunks.hasChunk(unloadedX, unloadedZ) && chunks.getChunkNow(unloadedX, unloadedZ) == null,
                "The boundary fixture must start beside a genuinely unloaded chunk without a pending FULL ticket");
        detached.serverTick();
        helper.assertTrue(!chunks.hasChunk(unloadedX, unloadedZ) && chunks.getChunkNow(unloadedX, unloadedZ) == null,
                "Particle output and redstone checks must neither request nor load the neighboring chunk");
        helper.assertTrue(detached.getBeam().equals(BEAM), "An unloaded neighbor must not alter the configured beam");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.particle.reload", timeoutTicks = 1_000_000)
    public static void resourceReloadKeepsConfiguredSourceCapabilityAndRealEmission(GameTestHelper helper) {
        CreativeParticleSourceBlockEntity source = source(helper);
        source.setBeam(BEAM);
        var level = helper.getLevel();
        IParticleStackHandler capability = level.getCapability(
                QMDParticleCapabilities.BLOCK, source.getBlockPos(), Direction.WEST);
        helper.assertTrue(capability != null, "The source capability must exist before the reload");
        BlockPos receiverPos = source.getBlockPos().east();
        level.setBlock(receiverPos, ParticleReceiverFixture.block.defaultBlockState(), 3);
        var receiver = (ParticleReceiverFixture.Receiver) level.getBlockEntity(receiverPos);
        helper.assertTrue(receiver != null, "The reload probe must use a real adjacent receiving block entity");
        // Accept only after the reload to make subsequent insertions prove that world ticks resume.
        receiver.storage.reject = true;
        var previousManager = level.getRecipeManager();
        var server = level.getServer();
        var selected = List.copyOf(server.getPackRepository().getSelectedIds());
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(30);
        CompletableFuture<Void> reload = CompletableFuture.runAsync(() -> server.reloadResources(selected).join());
        helper.startSequence().thenExecuteFor(1_000_000, () -> {
            helper.assertTrue(System.nanoTime() <= deadline, "Particle-source resource reload exceeded thirty seconds");
            if (reload.isCompletedExceptionally()) {
                helper.fail("Particle-source resource reload failed: " + reload.handle((unused, failure) -> failure).join());
            }
        });
        helper.startSequence().thenWaitUntil(() -> helper.assertTrue(reload.isDone(), "Waiting for particle-source resource reload"))
                .thenExecute(() -> {
                    reload.getNow(null);
                    helper.assertTrue(level.getRecipeManager() != previousManager,
                            "The test must complete a real server resource reload with a new recipe manager");
                    helper.assertTrue(level.getBlockEntity(source.getBlockPos()) == source && !source.isRemoved()
                                    && source.getBeam().equals(BEAM)
                                    && level.getCapability(QMDParticleCapabilities.BLOCK, source.getBlockPos(), Direction.WEST) == capability,
                            "Reload must preserve the placed source, exact configuration and existing capability identity");
                    helper.assertTrue(ParticleBeam.fromStack(capability.extractParticle(Direction.WEST)).equals(BEAM)
                                    && source.getBeam().equals(BEAM),
                            "The capability obtained before reload must still provide non-consuming exact beams afterward");
                    int before = receiver.storage.insertions;
                    receiver.storage.reject = false;
                    helper.runAfterDelay(3, () -> {
                        helper.assertTrue(level.getBlockEntity(receiverPos) == receiver
                                        && receiver.storage.insertions > before && receiver.storage.lastBeam.equals(BEAM)
                                        && receiver.storage.lastSide == Direction.WEST && source.getBeam().equals(BEAM),
                                "Actual post-reload world ticks must emit the unchanged beam into the existing adjacent capability");
                        helper.succeed();
                    });
                });
    }

    private static void assertCapability(GameTestHelper helper, CreativeParticleSourceBlockEntity source, Direction side) {
        IParticleStackHandler capability = helper.getLevel().getCapability(
                QMDParticleCapabilities.BLOCK, source.getBlockPos(), side);
        helper.assertTrue(capability != null && capability.canExtractParticle(side)
                        && !capability.canReceiveParticle(side, BEAM.toStack())
                        && !capability.receiveParticle(side, BEAM.toStack()),
                "Every source face, including unsided access, must expose extraction and reject insertion");
        ParticleStack extracted = capability.extractParticle(side);
        helper.assertTrue(ParticleBeam.fromStack(extracted).equals(BEAM), "Extraction must return the complete configured beam");
        extracted.setAmount(1);
        helper.assertTrue(source.getBeam().equals(BEAM), "Mutating an extracted beam must not change an infinite source");
        helper.assertTrue(capability.extractParticle(side, Particles.electron) == null
                        && capability.extractParticle(side, 0) == null,
                "Wrong-species and nonpositive extraction must fail without changing source state");
        ParticleStack partial = capability.extractParticle(side, Particles.proton, 2);
        helper.assertTrue(partial != null && partial.getAmount() == 2 && source.getBeam().equals(BEAM),
                "Count-limited extraction must return only the requested count without consuming the source");
    }

    private static CreativeParticleSourceBlockEntity source(GameTestHelper helper) {
        helper.setBlock(SOURCE, QMDBlocks.CREATIVE_PARTICLE_SOURCE.get());
        CreativeParticleSourceBlockEntity source = helper.getBlockEntity(SOURCE);
        helper.assertTrue(source != null, "The registered creative source must provide its real block entity");
        return source;
    }

    private static Map<Direction, ParticleReceiverFixture.Receiver> receivers(GameTestHelper helper,
                                                                           CreativeParticleSourceBlockEntity source) {
        Map<Direction, ParticleReceiverFixture.Receiver> result = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.values()) {
            BlockPos pos = source.getBlockPos().relative(direction);
            helper.getLevel().setBlock(pos, ParticleReceiverFixture.block.defaultBlockState(), 3);
            var receiver = (ParticleReceiverFixture.Receiver) helper.getLevel().getBlockEntity(pos);
            helper.assertTrue(receiver != null, "Every output fixture must be a real registered receiving block entity");
            result.put(direction, receiver);
        }
        return result;
    }

    private static Fixture fixture(GameTestHelper helper, GameType gameType) {
        CreativeParticleSourceBlockEntity source = source(helper);
        Player player = helper.makeMockPlayer(gameType);
        BlockPos pos = source.getBlockPos();
        player.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        var menu = source.createMenu(1, player.getInventory(), player);
        helper.assertTrue(menu instanceof CreativeParticleSourceMenu, "The source must create its registered configuration menu");
        player.containerMenu = menu;
        return new Fixture(source, player, (CreativeParticleSourceMenu) menu);
    }

    private static BlockPos loadedChunkBoundary(GameTestHelper helper) {
        BlockPos origin = helper.absolutePos(BlockPos.ZERO);
        var chunks = helper.getLevel().getChunkSource();
        for (int offsetX = -32; offsetX <= 32; offsetX++) {
            for (int offsetZ = -32; offsetZ <= 32; offsetZ++) {
                int chunkX = (origin.getX() >> 4) + offsetX;
                int chunkZ = (origin.getZ() >> 4) + offsetZ;
                // A null getChunkNow can still have a FULL ticket awaiting asynchronous generation.
                // Use the source's actual hasChunkAt guard as well so this cannot select a pending chunk.
                if (chunks.hasChunk(chunkX, chunkZ) && chunks.getChunkNow(chunkX, chunkZ) != null
                        && !chunks.hasChunk(chunkX + 1, chunkZ) && chunks.getChunkNow(chunkX + 1, chunkZ) == null) {
                    return new BlockPos((chunkX << 4) + 15, helper.getLevel().getMaxBuildHeight() - 2, (chunkZ << 4) + 8);
                }
            }
        }
        throw new IllegalStateException("No loaded/unloaded east chunk boundary found for source regression fixture");
    }

    private record Fixture(CreativeParticleSourceBlockEntity source, Player player, CreativeParticleSourceMenu menu) {}
}
