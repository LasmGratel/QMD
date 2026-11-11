package lach_01298.qmd.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import lach_01298.qmd.entity.GammaFlashEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

/** Deterministic expanding, rotating white rays from the legacy gamma flash. */
public final class GammaFlashRenderer extends EntityRenderer<GammaFlashEntity> {

    public GammaFlashRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(GammaFlashEntity entity) {
        return ResourceLocation.withDefaultNamespace("textures/misc/white.png");
    }

    @Override
    public void render(GammaFlashEntity entity, float yaw, float partialTicks, PoseStack pose,
        MultiBufferSource buffers, int light) {
        float time = (entity.tickCount + partialTicks) / 20F;
        float scale = Math.min(time, 4F) * entity.size();
        RandomSource random = RandomSource.create(432L);
        VertexConsumer vertices = buffers.getBuffer(RenderType.dragonRays());
        pose.pushPose();
        pose.scale(scale, scale, scale);
        for (int i = 0; i < 50; i++) {
            pose.mulPose(Axis.XP.rotationDegrees(random.nextFloat() * 360F));
            pose.mulPose(Axis.YP.rotationDegrees(random.nextFloat() * 360F));
            pose.mulPose(Axis.ZP.rotationDegrees(random.nextFloat() * 360F));
            pose.mulPose(Axis.ZP.rotationDegrees(random.nextFloat() * 10F * time));
            float width = random.nextFloat() * 2.5F + 1F;
            var matrix = pose.last()
                .pose();
            vertices.addVertex(matrix, 0, 0, 0)
                .setColor(255, 255, 255, 255);
            vertices.addVertex(matrix, -width, 0, -10)
                .setColor(0, 0, 0, 0);
            vertices.addVertex(matrix, width, 0, -10)
                .setColor(0, 0, 0, 0);
        }
        pose.popPose();
        super.render(entity, yaw, partialTicks, pose, buffers, light);
    }
}
