/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

@Environment(value=EnvType.CLIENT)
public class BoatEntityRenderer
extends EntityRenderer<BoatEntity> {
    private static final Identifier[] SKIN = new Identifier[]{new Identifier("textures/entity/boat/oak.png"), new Identifier("textures/entity/boat/spruce.png"), new Identifier("textures/entity/boat/birch.png"), new Identifier("textures/entity/boat/jungle.png"), new Identifier("textures/entity/boat/acacia.png"), new Identifier("textures/entity/boat/dark_oak.png")};
    protected final BoatEntityModel model = new BoatEntityModel();

    public BoatEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.field_4673 = 0.8f;
    }

    public void method_3888(BoatEntity boatEntity, double d, double e, double f, float g, float h, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage) {
        float k;
        matrixStack.push();
        matrixStack.translate(0.0, 0.375, 0.0);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0f - g));
        float i = (float)boatEntity.getDamageWobbleTicks() - h;
        float j = boatEntity.getDamageWobbleStrength() - h;
        if (j < 0.0f) {
            j = 0.0f;
        }
        if (i > 0.0f) {
            matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(MathHelper.sin(i) * i * j / 10.0f * (float)boatEntity.getDamageWobbleSide()));
        }
        if (!MathHelper.approximatelyEquals(k = boatEntity.interpolateBubbleWobble(h), 0.0f)) {
            matrixStack.multiply(new Quaternion(new Vector3f(1.0f, 0.0f, 1.0f), boatEntity.interpolateBubbleWobble(h), true));
        }
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        int l = boatEntity.getLightmapCoordinates();
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(90.0f));
        this.model.method_22952(boatEntity, h, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
        VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(this.model.getLayer(this.method_3891(boatEntity)));
        this.model.render(matrixStack, vertexConsumer, l, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f);
        VertexConsumer vertexConsumer2 = layeredVertexConsumerStorage.getBuffer(RenderLayer.getWaterMask());
        this.model.getBottom().render(matrixStack, vertexConsumer2, 0.0625f, l, OverlayTexture.DEFAULT_UV, null);
        matrixStack.pop();
        super.render(boatEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
    }

    public Identifier method_3891(BoatEntity boatEntity) {
        return SKIN[boatEntity.getBoatType().ordinal()];
    }
}

