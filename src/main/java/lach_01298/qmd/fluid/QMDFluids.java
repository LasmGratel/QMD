package lach_01298.qmd.fluid;

import com.nred.nuclearcraft.block.fluid.LiquidFluidBlock;
import com.nred.nuclearcraft.info.NCFluid;
import com.nred.nuclearcraft.info.NCFluidMaker;
import lach_01298.qmd.QuantumMinecraftDynamics;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;

/** Fluids used by the migrated collectors and ore-leaching production chains. */
public final class QMDFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, QuantumMinecraftDynamics.MODID);
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(BuiltInRegistries.FLUID, QuantumMinecraftDynamics.MODID);
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(QuantumMinecraftDynamics.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(QuantumMinecraftDynamics.MODID);

    private static final NCFluidMaker MAKER = new NCFluidMaker(FLUID_TYPES, FLUIDS, BLOCKS, ITEMS);

    public static final NCFluid HYDROCHLORIC_ACID = MAKER.registerFluid("hydrochloric_acid", 0xFF99FFEE, NCFluid.ACID_TYPE);
    public static final NCFluid NITRIC_ACID = MAKER.registerFluid("nitric_acid", 0xFF4F9EFF, NCFluid.ACID_TYPE);
    public static final NCFluid SODIUM_CHLORIDE_SOLUTION = MAKER.registerFluid("sodium_chloride_solution",
            FastColor.ARGB32.lerp(0.5F, 0xFF2F43F4, 0xFF0057FA), NCFluid.SALT_SOLUTION_TYPE);
    public static final NCFluid SALT_WATER = MAKER.registerFluid("salt_water",
            FastColor.ARGB32.lerp(0.5F, 0xFF2F43F4, 0xFF2974FF), NCFluid.SALT_SOLUTION_TYPE);
    public static final NCFluid CHLORINE = MAKER.registerFluid("chlorine", 0xFFFFFF8F, NCFluid.GAS_TYPE);
    public static final NCFluid NITRIC_OXIDE = MAKER.registerFluid("nitric_oxide", 0xFFC9EEFF, NCFluid.GAS_TYPE);
    public static final NCFluid NITROGEN_DIOXIDE = MAKER.registerFluid("nitrogen_dioxide", 0xFF782A10, NCFluid.GAS_TYPE);
    public static final NCFluid COMPRESSED_AIR = MAKER.registerFluid("compressed_air", 0xFFBDF0FF, NCFluid.GAS_TYPE);
    public static final NCFluid LIQUID_AIR = MAKER.registerFluid("liquid_air", 0xFF6CDEFF,
            new NCFluid.TypeInfo(8, false, "liquid_still", "liquid_flow",
                    FluidType.Properties.create().density(870).temperature(79).viscosity(170), LiquidFluidBlock::new));
    public static final NCFluid ARGON = MAKER.registerFluid("argon", 0xFFFF75DD, NCFluid.GAS_TYPE);
    public static final NCFluid NEON = MAKER.registerFluid("neon", 0xFFFF9F7A, NCFluid.GAS_TYPE);

    private static final List<NCFluid> ENTRIES = List.of(HYDROCHLORIC_ACID, NITRIC_ACID,
            SODIUM_CHLORIDE_SOLUTION, SALT_WATER, CHLORINE, NITRIC_OXIDE, NITROGEN_DIOXIDE,
            COMPRESSED_AIR, LIQUID_AIR, ARGON, NEON);

    private QMDFluids() {}

    public static List<NCFluid> entries() { return ENTRIES; }

    public static void register(IEventBus modEventBus) {
        FLUID_TYPES.register(modEventBus);
        FLUIDS.register(modEventBus);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
    }

    @OnlyIn(Dist.CLIENT)
    public static void initializeClient(RegisterClientExtensionsEvent event) {
        ENTRIES.forEach(fluid -> event.registerFluidType(fluid.client, fluid.type));
    }

    @OnlyIn(Dist.CLIENT)
    public static void initializeBucketColors(RegisterColorHandlersEvent.Item event) {
        ENTRIES.forEach(fluid -> event.register(
                (stack, tintIndex) -> tintIndex == 0 ? -1 : fluid.client.getTintColor(),
                fluid.bucket.asItem()));
    }

    @OnlyIn(Dist.CLIENT)
    public static void initializeRenderLayers(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ENTRIES.forEach(fluid -> {
            ItemBlockRenderTypes.setRenderLayer(fluid.still.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(fluid.flowing.get(), RenderType.translucent());
        }));
    }
}
