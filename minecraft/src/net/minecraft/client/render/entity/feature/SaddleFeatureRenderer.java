package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Saddleable;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SaddleFeatureRenderer<T extends Entity & Saddleable, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	private final Identifier texture;
	private final M model;

	public SaddleFeatureRenderer(FeatureRendererContext<T, M> context, M model, Identifier texture) {
		super(context);
		this.model = model;
		this.texture = texture;
	}

	@Override
	public void render(
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		T entity,
		float limbAngle,
		float limbDistance,
		float tickDelta,
		float animationProgress,
		float headYaw,
		float headPitch
	) {
		if (entity.isSaddled()) {
			this.getContextModel().copyStateTo(this.model);
			this.model.animateModel(entity, limbAngle, limbDistance, tickDelta);
			this.model.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
			VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(this.texture));
			this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
		}
	}
}
