package lach_01298.qmd.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import lach_01298.qmd.entity.GluonBeamEntity;
import lach_01298.qmd.entity.WeaponBeamEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;

/** Fullbright crossed beam planes with the original tint, width and muzzle offset. */
public final class WeaponBeamRenderer<T extends WeaponBeamEntity> extends EntityRenderer<T> {

    public WeaponBeamRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return ResourceLocation.fromNamespaceAndPath(
            "qmd",
            "textures/entities/" + (entity instanceof GluonBeamEntity ? "gluon_beam" : "lepton_beam") + ".png");
    }

    @Override
    public boolean shouldRender(T entity, net.minecraft.client.renderer.culling.Frustum frustum, double x, double y,
        double z) {
        return entity.shouldRenderAtSqrDistance(entity.distanceToSqr(x, y, z));
    }

    @Override
    public void render(T entity, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) {
        boolean gluon = entity instanceof GluonBeamEntity;
        pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(-entity.getYRot()));
        pose.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));
        float side = entity.hand() == InteractionHand.OFF_HAND ? 1 : -1;
        var owner = entity.owner();
        if (owner != null && owner.getMainArm() == net.minecraft.world.entity.HumanoidArm.LEFT) side = -side;
        pose.translate((gluon ? 0.3F : 0.2F) * side, gluon ? -0.24F : -0.18F, gluon ? 0.45F : 0.6F);
        if (gluon) pose.mulPose(Axis.ZP.rotationDegrees((entity.tickCount + partialTick) * 20));
        var consumer = buffers.getBuffer(RenderType.entityTranslucent(getTextureLocation(entity)));
        float width = gluon ? 0.15F : 0.1F;
        float length = Math.max(0, entity.length() - (gluon ? 0.45F : 0.6F));
        float scroll = gluon ? -(entity.tickCount + partialTick) / 4 : 0;
        for (int i = 0; i < 4; i++) {
            pose.mulPose(Axis.ZP.rotationDegrees(90));
            vertex(consumer, pose, -width, 0, 0, 0, scroll, entity.color());
            vertex(consumer, pose, width, 0, 0, 1, scroll, entity.color());
            vertex(consumer, pose, width, 0, length, 1, length + scroll, entity.color());
            vertex(consumer, pose, -width, 0, length, 0, length + scroll, entity.color());
        }
        pose.popPose();
        super.render(entity, yaw, partialTick, pose, buffers, light);
    }

    static void vertex(VertexConsumer vertices, PoseStack pose, float x, float y, float z, float u, float v,
        int color) {
        vertices.addVertex(pose.last(), x, y, z)
            .setColor(0xFF000000 | color)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(LightTexture.FULL_BRIGHT)
            .setNormal(pose.last(), 0, 1, 0);
    }
}
