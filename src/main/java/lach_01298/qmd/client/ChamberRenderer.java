package lach_01298.qmd.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import lach_01298.qmd.block.entity.chamber.AbstractChamberBlockEntity;
import lach_01298.qmd.multiblock.ChamberPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

/** The original rotating contained-fluid volume and opposed red laser prism, rendered through modern buffers. */
public final class ChamberRenderer implements BlockEntityRenderer<AbstractChamberBlockEntity> {

    public ChamberRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(AbstractChamberBlockEntity controller, float partialTick, PoseStack pose,
        MultiBufferSource buffers, int light, int overlay) {
        if (controller.role() != ChamberPart.CONTAINMENT_CONTROLLER || !controller.assembled()
            || controller.tank(2)
                .isEmpty())
            return;
        var minimum = controller.visualMin();
        var maximum = controller.visualMax();
        double x = (minimum.getX() + maximum.getX() + 1D) / 2 - controller.getBlockPos()
            .getX();
        double y = (minimum.getY() + maximum.getY() + 1D) / 2 - controller.getBlockPos()
            .getY();
        double z = (minimum.getZ() + maximum.getZ() + 1D) / 2 - controller.getBlockPos()
            .getZ();
        float size = (float) Math.cbrt(
            controller.tank(2)
                .getFluidAmount() / 16000D);
        pose.pushPose();
        pose.translate(x, y, z);
        pose.pushPose();
        float angle = controller.getLevel() == null ? 0
            : (controller.getLevel()
                .getGameTime() + partialTick) * 2.5F % 360F;
        pose.mulPose(Axis.XP.rotationDegrees(angle));
        pose.mulPose(Axis.YP.rotationDegrees(angle));
        pose.mulPose(Axis.ZP.rotationDegrees(angle));
        pose.translate(-size / 2, -size / 2, -size / 2);
        com.nred.nuclearcraft.render.RenderHelper.renderFluid(
            pose,
            buffers,
            LightTexture.FULL_BRIGHT,
            controller.tank(2)
                .getFluid(),
            controller.tank(2)
                .getFluidAmount(),
            size,
            size,
            size,
            Direction.UP,
            fluid -> false);
        pose.popPose();
        float half = (controller.laserAlongX() ? maximum.getX() - minimum.getX() - 1
            : maximum.getZ() - minimum.getZ() - 1) / 2F + .25F;
        if (!controller.laserAlongX()) pose.mulPose(Axis.YP.rotationDegrees(90));
        VertexConsumer beam = buffers.getBuffer(RenderType.lightning());
        box(beam, pose, -half, -.1F, -.1F, half, .1F, .1F);
        pose.popPose();
    }

    private static void box(VertexConsumer b, PoseStack p, float x0, float y0, float z0, float x1, float y1, float z1) {
        quad(b, p, x0, y0, z0, x1, y0, z0, x1, y1, z0, x0, y1, z0);
        quad(b, p, x0, y0, z1, x0, y1, z1, x1, y1, z1, x1, y0, z1);
        quad(b, p, x0, y0, z0, x0, y0, z1, x1, y0, z1, x1, y0, z0);
        quad(b, p, x0, y1, z0, x1, y1, z0, x1, y1, z1, x0, y1, z1);
        quad(b, p, x0, y0, z0, x0, y1, z0, x0, y1, z1, x0, y0, z1);
        quad(b, p, x1, y0, z0, x1, y0, z1, x1, y1, z1, x1, y1, z0);
    }

    private static void quad(VertexConsumer b, PoseStack pose, float... vertices) {
        for (int i = 0; i < vertices.length; i += 3) b.addVertex(
            pose.last()
                .pose(),
            vertices[i],
            vertices[i + 1],
            vertices[i + 2])
            .setColor(255, 0, 0, 153);
    }

    @Override
    public AABB getRenderBoundingBox(AbstractChamberBlockEntity tile) {
        return tile.role() == ChamberPart.CONTAINMENT_CONTROLLER ? new AABB(
            tile.visualMin()
                .getX(),
            tile.visualMin()
                .getY(),
            tile.visualMin()
                .getZ(),
            tile.visualMax()
                .getX() + 1,
            tile.visualMax()
                .getY() + 1,
            tile.visualMax()
                .getZ() + 1)
            : BlockEntityRenderer.super.getRenderBoundingBox(tile);
    }

    @Override
    public boolean shouldRenderOffScreen(AbstractChamberBlockEntity tile) {
        return tile.role() == ChamberPart.CONTAINMENT_CONTROLLER && tile.assembled();
    }

    @Override
    public int getViewDistance() {
        return 128;
    }
}
