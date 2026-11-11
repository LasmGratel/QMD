package lach_01298.qmd.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import lach_01298.qmd.entity.AntimatterProjectileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public final class AntimatterProjectileRenderer extends EntityRenderer<AntimatterProjectileEntity> {

    public AntimatterProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(AntimatterProjectileEntity entity) {
        return ResourceLocation.fromNamespaceAndPath("qmd", "textures/entities/antimatter_projectile.png");
    }

    @Override
    public void render(AntimatterProjectileEntity entity, float yaw, float partialTick, PoseStack pose,
        MultiBufferSource buffers, int light) {
        pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(-entity.getYRot()));
        pose.mulPose(Axis.XP.rotationDegrees(-entity.getXRot()));
        pose.mulPose(Axis.ZP.rotationDegrees((entity.tickCount + partialTick) * 20));
        pose.scale(0.5F, 0.5F, 0.5F);
        var vertices = buffers.getBuffer(RenderType.entityTranslucent(getTextureLocation(entity)));
        for (int i = 0; i < 4; i++) {
            pose.mulPose(Axis.ZP.rotationDegrees(90));
            WeaponBeamRenderer.vertex(vertices, pose, -0.5F, 0, -0.5F, 0, 0, entity.color());
            WeaponBeamRenderer.vertex(vertices, pose, 0.5F, 0, -0.5F, 1, 0, entity.color());
            WeaponBeamRenderer.vertex(vertices, pose, 0.5F, 0, 0.5F, 1, 1, entity.color());
            WeaponBeamRenderer.vertex(vertices, pose, -0.5F, 0, 0.5F, 0, 1, entity.color());
        }
        for (int i = 0; i < 2; i++) {
            pose.mulPose(Axis.YP.rotationDegrees(180));
            WeaponBeamRenderer.vertex(vertices, pose, -0.5F, -0.5F, 0.5F, 0, 0, entity.color());
            WeaponBeamRenderer.vertex(vertices, pose, 0.5F, -0.5F, 0.5F, 1, 0, entity.color());
            WeaponBeamRenderer.vertex(vertices, pose, 0.5F, 0.5F, 0.5F, 1, 1, entity.color());
            WeaponBeamRenderer.vertex(vertices, pose, -0.5F, 0.5F, 0.5F, 0, 1, entity.color());
        }
        pose.popPose();
        super.render(entity, yaw, partialTick, pose, buffers, light);
    }
}
