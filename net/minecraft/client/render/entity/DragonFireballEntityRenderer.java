/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class DragonFireballEntityRenderer
extends EntityRenderer<DragonFireballEntity> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/enderdragon/dragon_fireball.png");
    private static final RenderLayer LAYER = RenderLayer.getEntityCutoutNoCull(TEXTURE);

    public DragonFireballEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    protected int getBlockLight(DragonFireballEntity dragonFireballEntity, BlockPos blockPos) {
        return 15;
    }

    @Override
    public void render(DragonFireballEntity dragonFireballEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.scale(2.0f, 2.0f, 2.0f);
        matrixStack.multiply(this.dispatcher.getRotation());
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f));
        MatrixStack.Entry entry = matrixStack.peek();
        Matrix4f matrix4f = entry.getModel();
        Matrix3f matrix3f = entry.getNormal();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);
        DragonFireballEntityRenderer.produceVertex(vertexConsumer, matrix4f, matrix3f, i, 0.0f, 0, 0, 1);
        DragonFireballEntityRenderer.produceVertex(vertexConsumer, matrix4f, matrix3f, i, 1.0f, 0, 1, 1);
        DragonFireballEntityRenderer.produceVertex(vertexConsumer, matrix4f, matrix3f, i, 1.0f, 1, 1, 0);
        DragonFireballEntityRenderer.produceVertex(vertexConsumer, matrix4f, matrix3f, i, 0.0f, 1, 0, 0);
        matrixStack.pop();
        super.render(dragonFireballEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    private static void produceVertex(VertexConsumer vertexConsumer, Matrix4f modelMatrix, Matrix3f normalMatrix, int light, float x, int y, int textureU, int textureV) {
        vertexConsumer.vertex(modelMatrix, x - 0.5f, (float)y - 0.25f, 0.0f).color(255, 255, 255, 255).texture(textureU, textureV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
    }

    @Override
    public Identifier getTexture(DragonFireballEntity dragonFireballEntity) {
        return TEXTURE;
    }
}

