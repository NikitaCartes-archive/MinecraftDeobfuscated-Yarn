/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

@Environment(value=EnvType.CLIENT)
public class EnderCrystalEntityRenderer
extends EntityRenderer<EnderCrystalEntity> {
    private static final Identifier SKIN = new Identifier("textures/entity/end_crystal/end_crystal.png");
    public static final float field_21002 = (float)Math.sin(0.7853981633974483);
    private final ModelPart field_21003;
    private final ModelPart field_21004;
    private final ModelPart bottom;

    public EnderCrystalEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.field_4673 = 0.5f;
        this.field_21004 = new ModelPart(64, 32, 0, 0);
        this.field_21004.addCuboid(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f);
        this.field_21003 = new ModelPart(64, 32, 32, 0);
        this.field_21003.addCuboid(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f);
        this.bottom = new ModelPart(64, 32, 0, 16);
        this.bottom.addCuboid(-6.0f, 0.0f, -6.0f, 12.0f, 4.0f, 12.0f);
    }

    public void method_3908(EnderCrystalEntity enderCrystalEntity, double d, double e, double f, float g, float h, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage) {
        matrixStack.push();
        float i = EnderCrystalEntityRenderer.method_23155(enderCrystalEntity, h);
        float j = 0.0625f;
        float k = ((float)enderCrystalEntity.field_7034 + h) * 3.0f;
        int l = enderCrystalEntity.getLightmapCoordinates();
        VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.getEntityCutoutNoCull(this.method_3909(enderCrystalEntity)));
        matrixStack.push();
        matrixStack.scale(2.0f, 2.0f, 2.0f);
        matrixStack.translate(0.0, -0.5, 0.0);
        int m = OverlayTexture.DEFAULT_UV;
        if (enderCrystalEntity.getShowBottom()) {
            this.bottom.render(matrixStack, vertexConsumer, 0.0625f, l, m, null);
        }
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(k));
        matrixStack.translate(0.0, 1.5f + i / 2.0f, 0.0);
        matrixStack.multiply(new Quaternion(new Vector3f(field_21002, 0.0f, field_21002), 60.0f, true));
        this.field_21004.render(matrixStack, vertexConsumer, 0.0625f, l, m, null);
        float n = 0.875f;
        matrixStack.scale(0.875f, 0.875f, 0.875f);
        matrixStack.multiply(new Quaternion(new Vector3f(field_21002, 0.0f, field_21002), 60.0f, true));
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(k));
        this.field_21004.render(matrixStack, vertexConsumer, 0.0625f, l, m, null);
        matrixStack.scale(0.875f, 0.875f, 0.875f);
        matrixStack.multiply(new Quaternion(new Vector3f(field_21002, 0.0f, field_21002), 60.0f, true));
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(k));
        this.field_21003.render(matrixStack, vertexConsumer, 0.0625f, l, m, null);
        matrixStack.pop();
        matrixStack.pop();
        BlockPos blockPos = enderCrystalEntity.getBeamTarget();
        if (blockPos != null) {
            float o = (float)blockPos.getX() + 0.5f;
            float p = (float)blockPos.getY() + 0.5f;
            float q = (float)blockPos.getZ() + 0.5f;
            float r = (float)((double)o - enderCrystalEntity.getX());
            float s = (float)((double)p - enderCrystalEntity.getY());
            float t = (float)((double)q - enderCrystalEntity.getZ());
            matrixStack.translate(r, s, t);
            EnderDragonEntityRenderer.renderCrystalBeam(-r, -s + i, -t, h, enderCrystalEntity.field_7034, matrixStack, layeredVertexConsumerStorage, l);
        }
        super.render(enderCrystalEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
    }

    public static float method_23155(EnderCrystalEntity enderCrystalEntity, float f) {
        float g = (float)enderCrystalEntity.field_7034 + f;
        float h = MathHelper.sin(g * 0.2f) / 2.0f + 0.5f;
        h = (h * h + h) * 0.4f;
        return h - 1.4f;
    }

    public Identifier method_3909(EnderCrystalEntity enderCrystalEntity) {
        return SKIN;
    }

    public boolean method_3907(EnderCrystalEntity enderCrystalEntity, Frustum frustum, double d, double e, double f) {
        return super.isVisible(enderCrystalEntity, frustum, d, e, f) || enderCrystalEntity.getBeamTarget() != null;
    }
}

