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
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ExperienceOrbEntityRenderer
extends EntityRenderer<ExperienceOrbEntity> {
    private static final Identifier SKIN = new Identifier("textures/entity/experience_orb.png");

    public ExperienceOrbEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.field_4673 = 0.15f;
        this.field_4672 = 0.75f;
    }

    public void method_3966(ExperienceOrbEntity experienceOrbEntity, double d, double e, double f, float g, float h, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider) {
        matrixStack.push();
        int i = experienceOrbEntity.getOrbSize();
        float j = (float)(i % 4 * 16 + 0) / 64.0f;
        float k = (float)(i % 4 * 16 + 16) / 64.0f;
        float l = (float)(i / 4 * 16 + 0) / 64.0f;
        float m = (float)(i / 4 * 16 + 16) / 64.0f;
        float n = 1.0f;
        float o = 0.5f;
        float p = 0.25f;
        float q = 255.0f;
        float r = ((float)experienceOrbEntity.renderTicks + h) / 2.0f;
        int s = (int)((MathHelper.sin(r + 0.0f) + 1.0f) * 0.5f * 255.0f);
        int t = 255;
        int u = (int)((MathHelper.sin(r + 4.1887903f) + 1.0f) * 0.1f * 255.0f);
        matrixStack.translate(0.0, 0.1f, 0.0);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0f - this.renderManager.cameraYaw));
        float v = (float)(this.renderManager.gameOptions.perspective == 2 ? -1 : 1) * -this.renderManager.cameraPitch;
        matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(v));
        float w = 0.3f;
        matrixStack.scale(0.3f, 0.3f, 0.3f);
        int x = experienceOrbEntity.getLightmapCoordinates();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutout(SKIN));
        Matrix4f matrix4f = matrixStack.peekModel();
        ExperienceOrbEntityRenderer.method_23171(vertexConsumer, matrix4f, -0.5f, -0.25f, s, 255, u, j, m, x);
        ExperienceOrbEntityRenderer.method_23171(vertexConsumer, matrix4f, 0.5f, -0.25f, s, 255, u, k, m, x);
        ExperienceOrbEntityRenderer.method_23171(vertexConsumer, matrix4f, 0.5f, 0.75f, s, 255, u, k, l, x);
        ExperienceOrbEntityRenderer.method_23171(vertexConsumer, matrix4f, -0.5f, 0.75f, s, 255, u, j, l, x);
        matrixStack.pop();
        super.render(experienceOrbEntity, d, e, f, g, h, matrixStack, vertexConsumerProvider);
    }

    private static void method_23171(VertexConsumer vertexConsumer, Matrix4f matrix4f, float f, float g, int i, int j, int k, float h, float l, int m) {
        vertexConsumer.vertex(matrix4f, f, g, 0.0f).color(i, j, k, 128).texture(h, l).overlay(OverlayTexture.DEFAULT_UV).light(m).normal(0.0f, 1.0f, 0.0f).next();
    }

    public Identifier method_3967(ExperienceOrbEntity experienceOrbEntity) {
        return SKIN;
    }
}

