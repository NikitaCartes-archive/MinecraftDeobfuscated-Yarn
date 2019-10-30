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
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class EnergySwirlOverlayFeatureRenderer<T extends Entity & SkinOverlayOwner, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	public EnergySwirlOverlayFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
		super(featureRendererContext);
	}

	@Override
	public void render(
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider,
		int i,
		T entity,
		float f,
		float g,
		float tickDelta,
		float h,
		float j,
		float k,
		float l
	) {
		if (entity.shouldRenderOverlay()) {
			float m = (float)entity.age + tickDelta;
			EntityModel<T> entityModel = this.getEnergySwirlModel();
			entityModel.animateModel(entity, f, g, tickDelta);
			this.getModel().copyStateTo(entityModel);
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(
				RenderLayer.getEnergySwirl(this.getEnergySwirlTexture(), this.getEnergySwirlX(m), m * 0.01F)
			);
			entityModel.setAngles(entity, f, g, h, j, k, l);
			entityModel.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 0.5F, 0.5F, 0.5F);
		}
	}

	protected abstract float getEnergySwirlX(float partialAge);

	protected abstract Identifier getEnergySwirlTexture();

	protected abstract EntityModel<T> getEnergySwirlModel();
}
