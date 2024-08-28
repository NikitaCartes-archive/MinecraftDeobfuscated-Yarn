package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public abstract class EyesFeatureRenderer<S extends EntityRenderState, M extends EntityModel<S>> extends FeatureRenderer<S, M> {
	public EyesFeatureRenderer(FeatureRendererContext<S, M> featureRendererContext) {
		super(featureRendererContext);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, S state, float limbAngle, float limbDistance) {
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.getEyesTexture());
		this.getContextModel().render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
	}

	public abstract RenderLayer getEyesTexture();
}
