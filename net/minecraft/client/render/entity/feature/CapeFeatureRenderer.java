/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class CapeFeatureRenderer
extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public CapeFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    public void method_4177(MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i, AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h, float j, float k, float l, float m) {
        if (!abstractClientPlayerEntity.canRenderCapeTexture() || abstractClientPlayerEntity.isInvisible() || !abstractClientPlayerEntity.isSkinOverlayVisible(PlayerModelPart.CAPE) || abstractClientPlayerEntity.getCapeTexture() == null) {
            return;
        }
        ItemStack itemStack = abstractClientPlayerEntity.getEquippedStack(EquipmentSlot.CHEST);
        if (itemStack.getItem() == Items.ELYTRA) {
            return;
        }
        matrixStack.push();
        matrixStack.translate(0.0, 0.0, 0.125);
        double d = MathHelper.lerp((double)h, abstractClientPlayerEntity.field_7524, abstractClientPlayerEntity.field_7500) - MathHelper.lerp((double)h, abstractClientPlayerEntity.prevX, abstractClientPlayerEntity.getX());
        double e = MathHelper.lerp((double)h, abstractClientPlayerEntity.field_7502, abstractClientPlayerEntity.field_7521) - MathHelper.lerp((double)h, abstractClientPlayerEntity.prevY, abstractClientPlayerEntity.getY());
        double n = MathHelper.lerp((double)h, abstractClientPlayerEntity.field_7522, abstractClientPlayerEntity.field_7499) - MathHelper.lerp((double)h, abstractClientPlayerEntity.prevZ, abstractClientPlayerEntity.getZ());
        float o = abstractClientPlayerEntity.prevBodyYaw + (abstractClientPlayerEntity.bodyYaw - abstractClientPlayerEntity.prevBodyYaw);
        double p = MathHelper.sin(o * ((float)Math.PI / 180));
        double q = -MathHelper.cos(o * ((float)Math.PI / 180));
        float r = (float)e * 10.0f;
        r = MathHelper.clamp(r, -6.0f, 32.0f);
        float s = (float)(d * p + n * q) * 100.0f;
        s = MathHelper.clamp(s, 0.0f, 150.0f);
        float t = (float)(d * q - n * p) * 100.0f;
        t = MathHelper.clamp(t, -20.0f, 20.0f);
        if (s < 0.0f) {
            s = 0.0f;
        }
        float u = MathHelper.lerp(h, abstractClientPlayerEntity.field_7505, abstractClientPlayerEntity.field_7483);
        r += MathHelper.sin(MathHelper.lerp(h, abstractClientPlayerEntity.prevHorizontalSpeed, abstractClientPlayerEntity.horizontalSpeed) * 6.0f) * 32.0f * u;
        if (abstractClientPlayerEntity.isInSneakingPose()) {
            r += 25.0f;
        }
        matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(6.0f + s / 2.0f + r));
        matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(t / 2.0f));
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0f - t / 2.0f));
        VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.getEntitySolid(abstractClientPlayerEntity.getCapeTexture()));
        ((PlayerEntityModel)this.getModel()).renderCape(matrixStack, vertexConsumer, 0.0625f, i, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
    }
}

