/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.TntMinecartEntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.TntEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class TntEntityRenderer
extends EntityRenderer<TntEntity> {
    public TntEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.field_4673 = 0.5f;
    }

    public void method_4135(TntEntity tntEntity, double d, double e, double f, float g, float h, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage) {
        matrixStack.push();
        matrixStack.translate(0.0, 0.5, 0.0);
        if ((float)tntEntity.getFuseTimer() - h + 1.0f < 10.0f) {
            float i = 1.0f - ((float)tntEntity.getFuseTimer() - h + 1.0f) / 10.0f;
            i = MathHelper.clamp(i, 0.0f, 1.0f);
            i *= i;
            i *= i;
            float j = 1.0f + i * 0.3f;
            matrixStack.scale(j, j, j);
        }
        int k = tntEntity.getLightmapCoordinates();
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(-90.0f, true));
        matrixStack.translate(-0.5, -0.5, 0.5);
        if (tntEntity.getFuseTimer() / 5 % 2 == 0) {
            TntMinecartEntityRenderer.method_23190(Blocks.TNT.getDefaultState(), matrixStack, layeredVertexConsumerStorage, k);
        } else {
            MinecraftClient.getInstance().getBlockRenderManager().renderDynamic(Blocks.TNT.getDefaultState(), matrixStack, layeredVertexConsumerStorage, k, 0, 10);
        }
        matrixStack.pop();
        super.render(tntEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
    }

    public Identifier method_4136(TntEntity tntEntity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
    }
}

