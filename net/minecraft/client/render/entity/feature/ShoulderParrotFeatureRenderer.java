/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5599;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ParrotEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;

@Environment(value=EnvType.CLIENT)
public class ShoulderParrotFeatureRenderer<T extends PlayerEntity>
extends FeatureRenderer<T, PlayerEntityModel<T>> {
    private final ParrotEntityModel model;

    public ShoulderParrotFeatureRenderer(FeatureRendererContext<T, PlayerEntityModel<T>> featureRendererContext, class_5599 arg) {
        super(featureRendererContext);
        this.model = new ParrotEntityModel(arg.method_32072(EntityModelLayers.PARROT));
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T playerEntity, float f, float g, float h, float j, float k, float l) {
        this.renderShoulderParrot(matrixStack, vertexConsumerProvider, i, playerEntity, f, g, k, l, true);
        this.renderShoulderParrot(matrixStack, vertexConsumerProvider, i, playerEntity, f, g, k, l, false);
    }

    private void renderShoulderParrot(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T player, float limbAngle, float limbDistance, float headYaw, float headPitch, boolean leftShoulder) {
        CompoundTag compoundTag = leftShoulder ? ((PlayerEntity)player).getShoulderEntityLeft() : ((PlayerEntity)player).getShoulderEntityRight();
        EntityType.get(compoundTag.getString("id")).filter(entityType -> entityType == EntityType.PARROT).ifPresent(entityType -> {
            matrices.push();
            matrices.translate(leftShoulder ? (double)0.4f : (double)-0.4f, player.isInSneakingPose() ? (double)-1.3f : -1.5, 0.0);
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.model.getLayer(ParrotEntityRenderer.TEXTURES[compoundTag.getInt("Variant")]));
            this.model.poseOnShoulder(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, limbAngle, limbDistance, headYaw, headPitch, playerEntity.age);
            matrices.pop();
        });
    }
}

