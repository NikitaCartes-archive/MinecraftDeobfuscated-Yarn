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
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class DragonFireballEntityRenderer
extends EntityRenderer<DragonFireballEntity> {
    private static final Identifier SKIN = new Identifier("textures/entity/enderdragon/dragon_fireball.png");
    private static final RenderLayer field_21735 = RenderLayer.getEntityCutoutNoCull(SKIN);

    public DragonFireballEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Override
    protected int method_24087(DragonFireballEntity dragonFireballEntity, float f) {
        return 15;
    }

    @Override
    public void render(DragonFireballEntity dragonFireballEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.scale(2.0f, 2.0f, 2.0f);
        matrixStack.multiply(this.renderManager.method_24197());
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f));
        MatrixStack.Entry entry = matrixStack.peek();
        Matrix4f matrix4f = entry.getModel();
        Matrix3f matrix3f = entry.getNormal();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(field_21735);
        DragonFireballEntityRenderer.method_23837(vertexConsumer, matrix4f, matrix3f, i, 0.0f, 0, 0, 1);
        DragonFireballEntityRenderer.method_23837(vertexConsumer, matrix4f, matrix3f, i, 1.0f, 0, 1, 1);
        DragonFireballEntityRenderer.method_23837(vertexConsumer, matrix4f, matrix3f, i, 1.0f, 1, 1, 0);
        DragonFireballEntityRenderer.method_23837(vertexConsumer, matrix4f, matrix3f, i, 0.0f, 1, 0, 0);
        matrixStack.pop();
        super.render(dragonFireballEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    private static void method_23837(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, int i, float f, int j, int k, int l) {
        vertexConsumer.vertex(matrix4f, f - 0.5f, (float)j - 0.25f, 0.0f).color(255, 255, 255, 255).texture(k, l).overlay(OverlayTexture.DEFAULT_UV).light(i).normal(matrix3f, 0.0f, 1.0f, 0.0f).next();
    }

    @Override
    public Identifier getTexture(DragonFireballEntity dragonFireballEntity) {
        return SKIN;
    }
}

