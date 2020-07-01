/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class TntMinecartEntityRenderer
extends MinecartEntityRenderer<TntMinecartEntity> {
    public TntMinecartEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Override
    protected void renderBlock(TntMinecartEntity tntMinecartEntity, float f, BlockState blockState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        int j = tntMinecartEntity.getFuseTicks();
        if (j > -1 && (float)j - f + 1.0f < 10.0f) {
            float g = 1.0f - ((float)j - f + 1.0f) / 10.0f;
            g = MathHelper.clamp(g, 0.0f, 1.0f);
            g *= g;
            g *= g;
            float h = 1.0f + g * 0.3f;
            matrixStack.scale(h, h, h);
        }
        TntMinecartEntityRenderer.renderFlashingBlock(blockState, matrixStack, vertexConsumerProvider, i, j > -1 && j / 5 % 2 == 0);
    }

    /**
     * Renders a given block state into the given buffers either normally or with a bright white overlay.
     * Used for rendering primed TNT either standalone or as part of a TNT minecart.
     * 
     * @param drawFlash whether a white semi-transparent overlay is added to the block to indicate the flash
     */
    public static void renderFlashingBlock(BlockState blockState, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, boolean drawFlash) {
        int i = drawFlash ? OverlayTexture.packUv(OverlayTexture.getU(1.0f), 10) : OverlayTexture.DEFAULT_UV;
        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(blockState, matrices, vertexConsumers, light, i);
    }
}

