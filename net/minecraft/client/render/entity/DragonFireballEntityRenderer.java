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
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class DragonFireballEntityRenderer
extends EntityRenderer<DragonFireballEntity> {
    private static final Identifier SKIN = new Identifier("textures/entity/enderdragon/dragon_fireball.png");

    public DragonFireballEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    public void method_3906(DragonFireballEntity dragonFireballEntity, double d, double e, double f, float g, float h, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider) {
        matrixStack.push();
        matrixStack.scale(2.0f, 2.0f, 2.0f);
        float i = 1.0f;
        float j = 0.5f;
        float k = 0.25f;
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0f - this.renderManager.cameraYaw));
        float l = (float)(this.renderManager.gameOptions.perspective == 2 ? -1 : 1) * -this.renderManager.cameraPitch;
        matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(l));
        Matrix4f matrix4f = matrixStack.peekModel();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(SKIN));
        int m = dragonFireballEntity.getLightmapCoordinates();
        vertexConsumer.vertex(matrix4f, -0.5f, -0.25f, 0.0f).color(255, 255, 255, 255).texture(0.0f, 1.0f).overlay(OverlayTexture.DEFAULT_UV).light(m).normal(0.0f, 1.0f, 0.0f).next();
        vertexConsumer.vertex(matrix4f, 0.5f, -0.25f, 0.0f).color(255, 255, 255, 255).texture(1.0f, 1.0f).overlay(OverlayTexture.DEFAULT_UV).light(m).normal(0.0f, 1.0f, 0.0f).next();
        vertexConsumer.vertex(matrix4f, 0.5f, 0.75f, 0.0f).color(255, 255, 255, 255).texture(1.0f, 0.0f).overlay(OverlayTexture.DEFAULT_UV).light(m).normal(0.0f, 1.0f, 0.0f).next();
        vertexConsumer.vertex(matrix4f, -0.5f, 0.75f, 0.0f).color(255, 255, 255, 255).texture(0.0f, 0.0f).overlay(OverlayTexture.DEFAULT_UV).light(m).normal(0.0f, 1.0f, 0.0f).next();
        matrixStack.pop();
        super.render(dragonFireballEntity, d, e, f, g, h, matrixStack, vertexConsumerProvider);
    }

    public Identifier method_3905(DragonFireballEntity dragonFireballEntity) {
        return SKIN;
    }
}

