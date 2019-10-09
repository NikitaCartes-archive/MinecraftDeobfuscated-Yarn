/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class TntMinecartEntityRenderer
extends MinecartEntityRenderer<TntMinecartEntity> {
    public TntMinecartEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    protected void method_4137(TntMinecartEntity tntMinecartEntity, float f, BlockState blockState, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i) {
        int j = tntMinecartEntity.getFuseTicks();
        if (j > -1 && (float)j - f + 1.0f < 10.0f) {
            float g = 1.0f - ((float)j - f + 1.0f) / 10.0f;
            g = MathHelper.clamp(g, 0.0f, 1.0f);
            g *= g;
            g *= g;
            float h = 1.0f + g * 0.3f;
            matrixStack.scale(h, h, h);
        }
        TntMinecartEntityRenderer.method_23190(blockState, matrixStack, layeredVertexConsumerStorage, i, j > -1 && j / 5 % 2 == 0);
    }

    public static void method_23190(BlockState blockState, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i, boolean bl) {
        int j = bl ? OverlayTexture.method_23625(OverlayTexture.getU(1.0f), 10) : OverlayTexture.field_21444;
        MinecraftClient.getInstance().getBlockRenderManager().renderDynamic(blockState, matrixStack, layeredVertexConsumerStorage, i, j);
    }
}

