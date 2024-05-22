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
import net.minecraft.entity.SkinOverlayOwner;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class EnergySwirlOverlayFeatureRenderer<T extends Entity & SkinOverlayOwner, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	public EnergySwirlOverlayFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
		super(featureRendererContext);
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
		if (entity.shouldRenderOverlay()) {
			float f = (float)entity.age + tickDelta;
			EntityModel<T> entityModel = this.getEnergySwirlModel();
			entityModel.animateModel(entity, limbAngle, limbDistance, tickDelta);
			this.getContextModel().copyStateTo(entityModel);
			VertexConsumer vertexConsumer = vertexConsumers.getBuffer(
				RenderLayer.getEnergySwirl(this.getEnergySwirlTexture(), this.getEnergySwirlX(f) % 1.0F, f * 0.01F % 1.0F)
			);
			entityModel.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
			entityModel.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, -8355712);
		}
	}

	protected abstract float getEnergySwirlX(float partialAge);

	protected abstract Identifier getEnergySwirlTexture();

	protected abstract EntityModel<T> getEnergySwirlModel();
}
