/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ParrotEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;

@Environment(value=EnvType.CLIENT)
public class ShoulderParrotFeatureRenderer<T extends PlayerEntity>
extends FeatureRenderer<T, PlayerEntityModel<T>> {
    private final ParrotEntityModel model = new ParrotEntityModel();

    public ShoulderParrotFeatureRenderer(FeatureRendererContext<T, PlayerEntityModel<T>> featureRendererContext) {
        super(featureRendererContext);
    }

    public void method_4185(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T playerEntity, float f, float g, float h, float j, float k, float l) {
        this.renderShoulderParrot(matrixStack, vertexConsumerProvider, i, playerEntity, f, g, k, l, true);
        this.renderShoulderParrot(matrixStack, vertexConsumerProvider, i, playerEntity, f, g, k, l, false);
    }

    private void renderShoulderParrot(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T playerEntity, float f, float g, float h, float j, boolean bl) {
        CompoundTag compoundTag = bl ? ((PlayerEntity)playerEntity).getShoulderEntityLeft() : ((PlayerEntity)playerEntity).getShoulderEntityRight();
        EntityType.get(compoundTag.getString("id")).filter(entityType -> entityType == EntityType.PARROT).ifPresent(entityType -> {
            matrixStack.push();
            matrixStack.translate(bl ? (double)0.4f : (double)-0.4f, playerEntity.isInSneakingPose() ? (double)-1.3f : -1.5, 0.0);
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(ParrotEntityRenderer.SKINS[compoundTag.getInt("Variant")]));
            this.model.method_17106(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, f, g, h, j, playerEntity.age);
            matrixStack.pop();
        });
    }
}

