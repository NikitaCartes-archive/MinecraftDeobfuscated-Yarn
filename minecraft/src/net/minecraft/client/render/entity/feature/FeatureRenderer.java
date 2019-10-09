package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public abstract class FeatureRenderer<T extends Entity, M extends EntityModel<T>> {
	private final FeatureRendererContext<T, M> context;

	public FeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
		this.context = featureRendererContext;
	}

	protected static <T extends LivingEntity> void method_23196(
		EntityModel<T> entityModel,
		EntityModel<T> entityModel2,
		Identifier identifier,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i,
		T livingEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l,
		float m,
		float n,
		float o,
		float p
	) {
		if (!livingEntity.isInvisible()) {
			entityModel.copyStateTo(entityModel2);
			entityModel2.animateModel(livingEntity, f, g, m);
			entityModel2.setAngles(livingEntity, f, g, h, j, k, l);
			method_23199(entityModel2, identifier, matrixStack, layeredVertexConsumerStorage, i, livingEntity, n, o, p);
		}
	}

	protected static <T extends LivingEntity> void method_23199(
		EntityModel<T> entityModel,
		Identifier identifier,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i,
		T livingEntity,
		float f,
		float g,
		float h
	) {
		VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.getEntityCutoutNoCull(identifier));
		entityModel.renderItem(matrixStack, vertexConsumer, i, LivingEntityRenderer.method_23622(livingEntity, 0.0F), f, g, h);
	}

	public M getModel() {
		return this.context.getModel();
	}

	protected Identifier method_23194(T entity) {
		return this.context.getTexture(entity);
	}

	public abstract void render(
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i,
		T entity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l,
		float m
	);
}
