package lach_01298.qmd.block.entity;

import lach_01298.qmd.capability.QMDParticleCapabilities;
import lach_01298.qmd.particle.ParticleBeam;
import lach_01298.qmd.particle.ParticleStack;
import lach_01298.qmd.particle.ParticleStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

/** A real capability receiver that exists only in the GameTest source set. */
@EventBusSubscriber(modid = "qmd", bus = EventBusSubscriber.Bus.MOD)
public final class ParticleReceiverFixture {
    private static final ResourceLocation ID = ResourceLocation.parse("qmd:gametest_particle_receiver");
    static Block block;
    private static BlockEntityType<Receiver> type;

    private ParticleReceiverFixture() {}

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        event.register(Registries.BLOCK, ID, () -> block = new ReceiverBlock());
        event.register(Registries.BLOCK_ENTITY_TYPE, ID,
                () -> type = BlockEntityType.Builder.of(Receiver::new, block).build(null));
    }

    @SubscribeEvent
    public static void capabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(QMDParticleCapabilities.BLOCK, type, (receiver, side) -> receiver.storage);
    }

    private static final class ReceiverBlock extends Block implements EntityBlock {
        private ReceiverBlock() {
            super(Properties.of().strength(1));
        }

        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new Receiver(pos, state);
        }

        @Override
        protected RenderShape getRenderShape(BlockState state) {
            return RenderShape.INVISIBLE;
        }
    }

    public static final class Receiver extends BlockEntity {
        final RecordingStorage storage = new RecordingStorage();

        private Receiver(BlockPos pos, BlockState state) {
            super(type, pos, state);
            storage.setTileEntity(this);
        }
    }

    static final class RecordingStorage extends ParticleStorage {
        int insertions;
        Direction lastSide;
        ParticleBeam lastBeam = ParticleBeam.EMPTY;
        boolean reject;

        @Override
        public boolean canReceiveParticle(Direction side, ParticleStack stack) {
            return !reject && super.canReceiveParticle(side, stack);
        }

        @Override
        public boolean receiveParticle(Direction side, ParticleStack stack) {
            if (!super.receiveParticle(side, stack)) return false;
            insertions++;
            lastSide = side;
            lastBeam = ParticleBeam.fromStack(stack);
            return true;
        }
    }
}
