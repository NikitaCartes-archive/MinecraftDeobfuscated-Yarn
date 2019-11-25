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
import net.minecraft.client.util.math.Matrix3f;
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
    private static final RenderLayer field_21741 = RenderLayer.getEntityTranslucent(SKIN);

    public ExperienceOrbEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.field_4673 = 0.15f;
        this.field_4672 = 0.75f;
    }

    @Override
    protected int method_24087(ExperienceOrbEntity experienceOrbEntity, float f) {
        return MathHelper.clamp(super.method_24087(experienceOrbEntity, f) + 7, 0, 15);
    }

    @Override
    public void render(ExperienceOrbEntity experienceOrbEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        int j = experienceOrbEntity.getOrbSize();
        float h = (float)(j % 4 * 16 + 0) / 64.0f;
        float k = (float)(j % 4 * 16 + 16) / 64.0f;
        float l = (float)(j / 4 * 16 + 0) / 64.0f;
        float m = (float)(j / 4 * 16 + 16) / 64.0f;
        float n = 1.0f;
        float o = 0.5f;
        float p = 0.25f;
        float q = 255.0f;
        float r = ((float)experienceOrbEntity.renderTicks + g) / 2.0f;
        int s = (int)((MathHelper.sin(r + 0.0f) + 1.0f) * 0.5f * 255.0f);
        int t = 255;
        int u = (int)((MathHelper.sin(r + 4.1887903f) + 1.0f) * 0.1f * 255.0f);
        matrixStack.translate(0.0, 0.1f, 0.0);
        matrixStack.multiply(this.renderManager.method_24197());
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f));
        float v = 0.3f;
        matrixStack.scale(0.3f, 0.3f, 0.3f);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(field_21741);
        MatrixStack.Entry entry = matrixStack.peek();
        Matrix4f matrix4f = entry.getModel();
        Matrix3f matrix3f = entry.getNormal();
        ExperienceOrbEntityRenderer.method_23171(vertexConsumer, matrix4f, matrix3f, -0.5f, -0.25f, s, 255, u, h, m, i);
        ExperienceOrbEntityRenderer.method_23171(vertexConsumer, matrix4f, matrix3f, 0.5f, -0.25f, s, 255, u, k, m, i);
        ExperienceOrbEntityRenderer.method_23171(vertexConsumer, matrix4f, matrix3f, 0.5f, 0.75f, s, 255, u, k, l, i);
        ExperienceOrbEntityRenderer.method_23171(vertexConsumer, matrix4f, matrix3f, -0.5f, 0.75f, s, 255, u, h, l, i);
        matrixStack.pop();
        super.render(experienceOrbEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    private static void method_23171(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, float f, float g, int i, int j, int k, float h, float l, int m) {
        vertexConsumer.vertex(matrix4f, f, g, 0.0f).color(i, j, k, 128).texture(h, l).overlay(OverlayTexture.DEFAULT_UV).light(m).normal(matrix3f, 0.0f, 1.0f, 0.0f).next();
    }

    @Override
    public Identifier getTexture(ExperienceOrbEntity experienceOrbEntity) {
        return SKIN;
    }
}

