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
	private final Identifier TEXTURE;
	private final M model;

	public SaddleFeatureRenderer(FeatureRendererContext<T, M> context, M model, Identifier texture) {
		super(context);
		this.model = model;
		this.TEXTURE = texture;
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
		float customAngle,
		float headYaw,
		float headPitch
	) {
		if (entity.isSaddled()) {
			this.getContextModel().copyStateTo(this.model);
			this.model.animateModel(entity, limbAngle, limbDistance, tickDelta);
			this.model.setAngles(entity, limbAngle, limbDistance, customAngle, headYaw, headPitch);
			VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(this.TEXTURE));
			this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
		}
	}
}
