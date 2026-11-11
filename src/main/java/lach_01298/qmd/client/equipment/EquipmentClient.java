package lach_01298.qmd.client.equipment;

import com.mojang.blaze3d.vertex.VertexConsumer;
import lach_01298.qmd.item.equipment.DrillItem;
import lach_01298.qmd.item.equipment.HevArmorItem;
import lach_01298.qmd.registry.QMDItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;

/** Client-only HEV tint and drill plane preview hooks. */
public final class EquipmentClient {

    public static void clientHooks(net.neoforged.bus.api.IEventBus bus) {
        bus.addListener(EquipmentClient::registerItemColors);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(EquipmentClient::renderDrillArea);
    }

    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(
            (stack, tintIndex) -> tintIndex == 0 ? DyedItemColor.getOrDefault(stack, HevArmorItem.DEFAULT_COLOR) : -1,
            QMDItems.HELM_HEV.get(),
            QMDItems.CHEST_HEV.get(),
            QMDItems.LEGS_HEV.get(),
            QMDItems.BOOTS_HEV.get());
    }

    public static void renderDrillArea(RenderHighlightEvent.Block event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) return;
        ItemStack stack = minecraft.player.getMainHandItem();
        if (!(stack.getItem() instanceof DrillItem drill)) return;

        Vec3 camera = event.getCamera()
            .getPosition();
        VertexConsumer lines = event.getMultiBufferSource()
            .getBuffer(RenderType.lines());
        for (BlockPos pos : drill.additionalBlocks(
            event.getTarget()
                .getBlockPos(),
            event.getTarget()
                .getDirection())) {
            BlockState state = minecraft.level.getBlockState(pos);
            if (state.isAir()) continue;
            state.getShape(minecraft.level, pos, CollisionContext.of(minecraft.player))
                .forAllBoxes(
                    (minX, minY, minZ, maxX, maxY, maxZ) -> LevelRenderer.renderLineBox(
                        event.getPoseStack(),
                        lines,
                        pos.getX() + minX - camera.x,
                        pos.getY() + minY - camera.y,
                        pos.getZ() + minZ - camera.z,
                        pos.getX() + maxX - camera.x,
                        pos.getY() + maxY - camera.y,
                        pos.getZ() + maxZ - camera.z,
                        0.0F,
                        0.0F,
                        0.0F,
                        0.4F));
        }
    }

    private EquipmentClient() {}
}
