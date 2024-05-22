package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class FeatureRenderer<T extends Entity, M extends EntityModel<T>> {
	private final FeatureRendererContext<T, M> context;

	public FeatureRenderer(FeatureRendererContext<T, M> context) {
		this.context = context;
	}

	protected static <T extends LivingEntity> void render(
		EntityModel<T> contextModel,
		EntityModel<T> model,
		Identifier texture,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		T entity,
		float limbAngle,
		float limbDistance,
		float age,
		float headYaw,
		float headPitch,
		float tickDelta,
		int i
	) {
		if (!entity.isInvisible()) {
			contextModel.copyStateTo(model);
			model.animateModel(entity, limbAngle, limbDistance, tickDelta);
			model.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch);
			renderModel(model, texture, matrices, vertexConsumers, light, entity, i);
		}
	}

	protected static <T extends LivingEntity> void renderModel(
		EntityModel<T> model, Identifier texture, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, int i
	) {
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(texture));
		model.render(matrices, vertexConsumer, light, LivingEntityRenderer.getOverlay(entity, 0.0F), i);
	}

	public M getContextModel() {
		return this.context.getModel();
	}

	protected Identifier getTexture(T entity) {
		return this.context.getTexture(entity);
	}

	public abstract void render(
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
	);
}
