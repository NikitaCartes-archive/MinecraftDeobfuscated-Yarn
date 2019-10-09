package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public abstract class class_4606<T extends Entity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	public class_4606(FeatureRendererContext<T, M> featureRendererContext) {
		super(featureRendererContext);
	}

	@Override
	public void render(
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
	) {
		VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.getEyes(this.method_23193()));
		this.getModel().renderItem(matrixStack, vertexConsumer, 15728640, OverlayTexture.field_21444, 1.0F, 1.0F, 1.0F);
	}

	public abstract Identifier method_23193();
}
