package lach_01298.qmd.particle;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

/** Immutable identity and components of the fluid contained in a particle cell. */
public record CellFluid(Holder<Fluid> fluid, DataComponentPatch components) {

    public static final Codec<CellFluid> CODEC = FluidStack.CODEC.xmap(CellFluid::of, value -> value.stack(1));
    public static final StreamCodec<RegistryFriendlyByteBuf, CellFluid> STREAM_CODEC = FluidStack.STREAM_CODEC
        .map(CellFluid::of, value -> value.stack(1));

    public static CellFluid of(FluidStack stack) {
        if (stack.isEmpty()) throw new IllegalArgumentException("Cell fluid identity cannot be empty");
        return new CellFluid(stack.getFluidHolder(), stack.getComponentsPatch());
    }

    public FluidStack stack(int amount) {
        return new FluidStack(fluid, amount, components);
    }
}
