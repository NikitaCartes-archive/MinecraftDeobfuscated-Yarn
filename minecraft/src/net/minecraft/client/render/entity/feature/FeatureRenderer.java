package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
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

	protected static <T extends LivingEntity> void method_23195(
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
		float m
	) {
		method_23196(entityModel, entityModel2, identifier, matrixStack, layeredVertexConsumerStorage, i, livingEntity, f, g, h, j, k, l, m, 1.0F, 1.0F, 1.0F);
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

	protected static <T extends LivingEntity> void method_23198(
		EntityModel<T> entityModel, Identifier identifier, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i, T livingEntity
	) {
		method_23199(entityModel, identifier, matrixStack, layeredVertexConsumerStorage, i, livingEntity, 1.0F, 1.0F, 1.0F);
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
		VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.method_23017(identifier));
		LivingEntityRenderer.method_23184(livingEntity, vertexConsumer, 0.0F);
		entityModel.method_17116(matrixStack, vertexConsumer, i, f, g, h);
		vertexConsumer.clearDefaultOverlay();
	}

	protected static <T extends LivingEntity> void method_23197(
		EntityModel<T> entityModel,
		Identifier identifier,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i,
		float f,
		float g,
		float h
	) {
		VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.method_23017(identifier));
		OverlayTexture.clearDefaultOverlay(vertexConsumer);
		entityModel.method_17116(matrixStack, vertexConsumer, i, f, g, h);
		vertexConsumer.clearDefaultOverlay();
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
