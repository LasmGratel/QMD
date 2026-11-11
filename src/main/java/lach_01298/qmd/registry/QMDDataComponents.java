package lach_01298.qmd.registry;

import com.mojang.serialization.Codec;
import lach_01298.qmd.QuantumMinecraftDynamics;
import lach_01298.qmd.particle.CellFluid;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class QMDDataComponents {

    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister
        .createDataComponents(Registries.DATA_COMPONENT_TYPE, QuantumMinecraftDynamics.MODID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> PARTICLE_AMOUNT = COMPONENTS
        .registerComponentType(
            "particle_amount",
            builder -> builder.persistent(Codec.intRange(0, Integer.MAX_VALUE))
                .networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CellFluid>> CELL_FLUID = COMPONENTS
        .registerComponentType(
            "cell_fluid",
            builder -> builder.persistent(CellFluid.CODEC)
                .networkSynchronized(CellFluid.STREAM_CODEC));

    private QMDDataComponents() {}
}
