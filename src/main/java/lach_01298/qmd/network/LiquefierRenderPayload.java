package lach_01298.qmd.network;

import lach_01298.qmd.block.entity.liquefier.LiquefierControllerBlockEntity;
import lach_01298.qmd.multiblock.liquefier.LiquefierLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.fluids.FluidStack;
import java.util.List;

/** Tracking-chunk rendering state also reaches players who have not opened the controller menu. */
public record LiquefierRenderPayload(BlockPos pos, boolean renderer, boolean active, List<FluidStack> fluids,
    List<Integer> capacities) implements CustomPacketPayload {

    public static final Type<LiquefierRenderPayload> TYPE = new Type<>(ResourceLocation.parse("qmd:liquefier_render"));
    public static final StreamCodec<RegistryFriendlyByteBuf, LiquefierRenderPayload> STREAM_CODEC = new StreamCodec<>() {

        @Override
        public LiquefierRenderPayload decode(RegistryFriendlyByteBuf buf) {
            BlockPos pos = buf.readBlockPos();
            boolean renderer = buf.readBoolean(), active = buf.readBoolean();
            var fluids = new java.util.ArrayList<FluidStack>(3);
            var capacities = new java.util.ArrayList<Integer>(3);
            for (int i = 0; i < 3; i++) {
                fluids.add(FluidStack.OPTIONAL_STREAM_CODEC.decode(buf));
                capacities.add(buf.readVarInt());
            }
            return new LiquefierRenderPayload(pos, renderer, active, fluids, capacities);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, LiquefierRenderPayload packet) {
            buf.writeBlockPos(packet.pos);
            buf.writeBoolean(packet.renderer);
            buf.writeBoolean(packet.active);
            for (int i = 0; i < 3; i++) {
                FluidStack.OPTIONAL_STREAM_CODEC.encode(buf, packet.fluids.get(i));
                buf.writeVarInt(packet.capacities.get(i));
            }
        }
    };

    public LiquefierRenderPayload {
        pos = pos.immutable();
        fluids = fluids.stream()
            .map(FluidStack::copy)
            .toList();
        capacities = List.copyOf(capacities);
        if (fluids.size() != 3 || capacities.size() != 3
            || capacities.stream()
                .anyMatch(capacity -> capacity < 0))
            throw new IllegalArgumentException("Invalid liquefier rendering state");
    }

    @Override
    public List<FluidStack> fluids() {
        return fluids.stream()
            .map(FluidStack::copy)
            .toList();
    }

    @Override
    public Type<LiquefierRenderPayload> type() {
        return TYPE;
    }

    public static LiquefierRenderPayload from(LiquefierLogic logic) {
        var tanks = List
            .of(logic.multiblock.shellTanks.get(0), logic.multiblock.shellTanks.get(1), logic.gasTanks.get(1));
        return new LiquefierRenderPayload(
            logic.controller()
                .getBlockPos(),
            logic.controller()
                .isRenderer(),
            logic.multiblock.isExchangerOn,
            tanks.stream()
                .map(
                    tank -> tank.getFluid()
                        .copy())
                .toList(),
            tanks.stream()
                .map(tank -> tank.getCapacity())
                .toList());
    }

    public static void apply(Player player, LiquefierRenderPayload packet) {
        if (!player.level()
            .hasChunkAt(packet.pos)
            || !(player.level()
                .getBlockEntity(packet.pos) instanceof LiquefierControllerBlockEntity tile)
            || tile.liquefier() == null) return;
        var logic = tile.liquefier();
        tile.setIsRenderer(packet.renderer);
        logic.multiblock.isExchangerOn = packet.active;
        var tanks = List
            .of(logic.multiblock.shellTanks.get(0), logic.multiblock.shellTanks.get(1), logic.gasTanks.get(1));
        for (int i = 0; i < 3; i++) {
            tanks.get(i)
                .setCapacity(packet.capacities.get(i));
            tanks.get(i)
                .setFluidStored(packet.fluids.get(i));
        }
    }
}
