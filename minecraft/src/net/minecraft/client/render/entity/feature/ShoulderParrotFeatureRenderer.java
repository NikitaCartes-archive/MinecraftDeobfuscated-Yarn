package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ParrotEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.ParrotEntity;

@Environment(EnvType.CLIENT)
public class ShoulderParrotFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
	private final ParrotEntityModel model;

	public ShoulderParrotFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context, EntityModelLoader loader) {
		super(context);
		this.model = new ParrotEntityModel(loader.getModelPart(EntityModelLayers.PARROT));
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, PlayerEntityRenderState playerEntityRenderState, float f, float g
	) {
		ParrotEntity.Variant variant = playerEntityRenderState.leftShoulderParrotVariant;
		if (variant != null) {
			this.render(matrixStack, vertexConsumerProvider, i, playerEntityRenderState, variant, f, g, true);
		}

		ParrotEntity.Variant variant2 = playerEntityRenderState.rightShoulderParrotVariant;
		if (variant2 != null) {
			this.render(matrixStack, vertexConsumerProvider, i, playerEntityRenderState, variant2, f, g, false);
		}
	}

	private void render(
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		PlayerEntityRenderState playerRenderState,
		ParrotEntity.Variant variant,
		float headYaw,
		float headPitch,
		boolean left
	) {
		matrices.push();
		matrices.translate(left ? 0.4F : -0.4F, playerRenderState.isInSneakingPose ? -1.3F : -1.5F, 0.0F);
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.model.getLayer(ParrotEntityRenderer.getTexture(variant)));
		this.model
			.poseOnShoulder(
				matrices,
				vertexConsumer,
				light,
				OverlayTexture.DEFAULT_UV,
				playerRenderState.limbFrequency,
				playerRenderState.limbAmplitudeMultiplier,
				headYaw,
				headPitch,
				playerRenderState.age
			);
		matrices.pop();
	}
}
