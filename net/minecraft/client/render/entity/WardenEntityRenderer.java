/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.WardenFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.WardenEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WardenEntityRenderer
extends MobEntityRenderer<WardenEntity, WardenEntityModel<WardenEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/warden/warden.png");
    private static final Identifier BIOLUMINESCENT_LAYER_TEXTURE = new Identifier("textures/entity/warden/warden_bioluminescent_layer.png");
    private static final Identifier HEART_TEXTURE = new Identifier("textures/entity/warden/warden_heart.png");
    private static final Identifier PULSATING_SPOTS_1_TEXTURE = new Identifier("textures/entity/warden/warden_pulsating_spots_1.png");
    private static final Identifier PULSATING_SPOTS_2_TEXTURE = new Identifier("textures/entity/warden/warden_pulsating_spots_2.png");

    public WardenEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new WardenEntityModel(context.getPart(EntityModelLayers.WARDEN)), 0.5f);
        this.addFeature(new WardenFeatureRenderer<WardenEntity, WardenEntityModel>(this, BIOLUMINESCENT_LAYER_TEXTURE, (warden, tickDelta, animationProgress) -> 1.0f, WardenEntityModel::method_42740));
        this.addFeature(new WardenFeatureRenderer<WardenEntity, WardenEntityModel>(this, PULSATING_SPOTS_1_TEXTURE, (warden, tickDelta, animationProgress) -> Math.max(0.0f, MathHelper.cos(animationProgress * 0.045f) * 0.25f), WardenEntityModel::method_42741));
        this.addFeature(new WardenFeatureRenderer<WardenEntity, WardenEntityModel>(this, PULSATING_SPOTS_2_TEXTURE, (warden, tickDelta, animationProgress) -> Math.max(0.0f, MathHelper.cos(animationProgress * 0.045f + (float)Math.PI) * 0.25f), WardenEntityModel::method_42741));
        this.addFeature(new WardenFeatureRenderer<WardenEntity, WardenEntityModel>(this, TEXTURE, (warden, tickDelta, animationProgress) -> warden.getEarPitch(tickDelta), WardenEntityModel::method_42738));
        this.addFeature(new WardenFeatureRenderer<WardenEntity, WardenEntityModel>(this, HEART_TEXTURE, (warden, tickDelta, animationProgress) -> warden.getHeartPitch(tickDelta), WardenEntityModel::method_42739));
    }

    @Override
    public void render(WardenEntity wardenEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        if (wardenEntity.age <= 2 && !wardenEntity.isInPose(EntityPose.EMERGING)) {
            return;
        }
        super.render(wardenEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(WardenEntity wardenEntity) {
        return TEXTURE;
    }
}

