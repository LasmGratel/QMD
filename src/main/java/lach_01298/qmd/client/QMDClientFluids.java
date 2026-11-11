package lach_01298.qmd.client;

import lach_01298.qmd.registry.QMDFluids;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;

/** All QMD fluid rendering hooks live on the physical client. */
public final class QMDClientFluids {

    public static void initializeClient(RegisterClientExtensionsEvent event) {
        QMDFluids.entries()
            .forEach(fluid -> {
                IClientFluidTypeExtensions renderer = fluid.client;
                if (QMDFluids.EXOTIC.containsValue(fluid)) renderer = new IClientFluidTypeExtensions() {

                    @Override
                    public ResourceLocation getStillTexture() {
                        return ResourceLocation.parse("qmd:block/fluid/antimatter_still");
                    }

                    @Override
                    public ResourceLocation getFlowingTexture() {
                        return ResourceLocation.parse("qmd:block/fluid/antimatter_flow");
                    }

                    @Override
                    public int getTintColor() {
                        return fluid.client.getTintColor();
                    }
                };
                event.registerFluidType(renderer, fluid.type);
            });
    }

    public static void initializeBucketColors(RegisterColorHandlersEvent.Item event) {
        QMDFluids.entries()
            .forEach(
                fluid -> event.register(
                    (stack, tintIndex) -> tintIndex == 0 ? -1 : fluid.client.getTintColor(),
                    fluid.bucket.asItem()));
    }

    public static void initializeRenderLayers(FMLClientSetupEvent event) {
        event.enqueueWork(
            () -> QMDFluids.entries()
                .forEach(fluid -> {
                    ItemBlockRenderTypes.setRenderLayer(fluid.still.get(), RenderType.translucent());
                    ItemBlockRenderTypes.setRenderLayer(fluid.flowing.get(), RenderType.translucent());
                }));
    }

    private QMDClientFluids() {}
}
