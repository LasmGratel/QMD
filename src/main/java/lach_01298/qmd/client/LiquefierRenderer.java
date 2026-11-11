package lach_01298.qmd.client;

import com.mojang.blaze3d.vertex.PoseStack;
import lach_01298.qmd.block.entity.liquefier.LiquefierControllerBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import static com.nred.nuclearcraft.render.RenderHelper.renderFluid;
import static it.zerono.mods.zerocore.lib.client.render.ModRenderHelper.ONE_PIXEL;

/** Retains the transparent upper coolant volume and the bottom liquid reservoir. */
public record LiquefierRenderer(BlockEntityRendererProvider.Context context)
    implements BlockEntityRenderer<LiquefierControllerBlockEntity> {

    @Override
    public void render(LiquefierControllerBlockEntity tile, float partialTick, PoseStack pose,
        MultiBufferSource buffers, int light, int overlay) {
        var logic = tile.liquefier();
        if (!tile.isRenderer() || !tile.isMachineAssembled() || logic == null) return;
        var hx = logic.multiblock;
        var offset = hx.getExtremeInteriorCoord(false, false, false)
            .subtract(tile.getBlockPos());
        float x = hx.getInteriorLengthX() + 2F * ONE_PIXEL, z = hx.getInteriorLengthZ() + 2F * ONE_PIXEL;
        pose.pushPose();
        pose.translate(offset.getX() - ONE_PIXEL, offset.getY() - ONE_PIXEL, offset.getZ() - ONE_PIXEL);
        pose.pushPose();
        pose.translate(0, 2, 0);
        for (var tank : hx.shellTanks) renderFluid(
            pose,
            buffers,
            light,
            tank.getFluid(),
            tank.getCapacity(),
            x,
            hx.getInteriorLengthY() - 2 + 2F * ONE_PIXEL,
            z,
            Direction.UP,
            fluid -> true);
        pose.popPose();
        var liquid = logic.gasTanks.get(1);
        renderFluid(
            pose,
            buffers,
            light,
            liquid.getFluid(),
            liquid.getCapacity(),
            x,
            1 + 2F * ONE_PIXEL,
            z,
            fluid -> fluid.getFluidType()
                .isLighterThanAir(),
            amount -> amount + liquid.getCapacity() * 0.2D);
        pose.popPose();
    }

    @Override
    public AABB getRenderBoundingBox(LiquefierControllerBlockEntity tile) {
        return tile.getMultiblockController()
            .map(
                hx -> hx.getBoundingBox()
                    .getAABB())
            .orElseGet(() -> BlockEntityRenderer.super.getRenderBoundingBox(tile));
    }

    @Override
    public boolean shouldRenderOffScreen(LiquefierControllerBlockEntity tile) {
        return tile.isRenderer() && tile.isMachineAssembled();
    }
}
