package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public abstract class SkinOverlayFeatureRenderer<T extends Entity & SkinOverlayOwner, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	public SkinOverlayFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
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
		if (entity.shouldRenderOverlay()) {
			float n = (float)entity.age + h;
			EntityModel<T> entityModel = this.method_23203();
			entityModel.animateModel(entity, f, g, h);
			this.getModel().copyStateTo(entityModel);
			VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.getPowerSwirl(this.method_23201(), this.method_23202(n), n * 0.01F));
			entityModel.setAngles(entity, f, g, j, k, l, m);
			entityModel.renderItem(matrixStack, vertexConsumer, i, OverlayTexture.field_21444, 0.5F, 0.5F, 0.5F);
		}
	}

	protected abstract float method_23202(float f);

	protected abstract Identifier method_23201();

	protected abstract EntityModel<T> method_23203();
}
