package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.WardenFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.WardenEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class WardenEntityRenderer extends MobEntityRenderer<WardenEntity, WardenEntityModel<WardenEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/warden/warden.png");
	private static final Identifier BIOLUMINESCENT_LAYER_TEXTURE = new Identifier("textures/entity/warden/warden_bioluminescent_layer.png");
	private static final Identifier EARS_TEXTURE = new Identifier("textures/entity/warden/warden_ears.png");
	private static final Identifier HEART_TEXTURE = new Identifier("textures/entity/warden/warden_heart.png");
	private static final Identifier PULSATING_SPOTS_1_TEXTURE = new Identifier("textures/entity/warden/warden_pulsating_spots_1.png");
	private static final Identifier PULSATING_SPOTS_2_TEXTURE = new Identifier("textures/entity/warden/warden_pulsating_spots_2.png");

	public WardenEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new WardenEntityModel<>(context.getPart(EntityModelLayers.WARDEN)), 0.5F);
		this.addFeature(new WardenFeatureRenderer<>(this, BIOLUMINESCENT_LAYER_TEXTURE, (warden, tickDelta, animationProgress) -> 1.0F));
		this.addFeature(
			new WardenFeatureRenderer<>(
				this, PULSATING_SPOTS_1_TEXTURE, (warden, tickDelta, animationProgress) -> Math.max(0.0F, MathHelper.cos(animationProgress * 0.045F) * 0.25F)
			)
		);
		this.addFeature(
			new WardenFeatureRenderer<>(
				this,
				PULSATING_SPOTS_2_TEXTURE,
				(warden, tickDelta, animationProgress) -> Math.max(0.0F, MathHelper.cos(animationProgress * 0.045F + (float) Math.PI) * 0.25F)
			)
		);
		this.addFeature(new WardenFeatureRenderer<>(this, EARS_TEXTURE, (warden, tickDelta, animationProgress) -> warden.getEarPitch(tickDelta)));
		this.addFeature(new WardenFeatureRenderer<>(this, HEART_TEXTURE, (warden, tickDelta, animationProgress) -> warden.getHeartPitch(tickDelta)));
	}

	public void render(WardenEntity wardenEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		if (wardenEntity.age > 2 || wardenEntity.isInPose(EntityPose.EMERGING)) {
			super.render(wardenEntity, f, g, matrixStack, vertexConsumerProvider, i);
		}
	}

	public Identifier getTexture(WardenEntity wardenEntity) {
		return TEXTURE;
	}
}
