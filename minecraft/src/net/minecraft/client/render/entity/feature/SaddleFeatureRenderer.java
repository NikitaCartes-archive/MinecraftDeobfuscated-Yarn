package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.SaddleableRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SaddleFeatureRenderer<S extends LivingEntityRenderState & SaddleableRenderState, M extends EntityModel<? super S>> extends FeatureRenderer<S, M> {
	private final Identifier texture;
	private final M model;
	private final M babyModel;

	public SaddleFeatureRenderer(FeatureRendererContext<S, M> context, M model, M babyModel, Identifier texture) {
		super(context);
		this.model = model;
		this.babyModel = babyModel;
		this.texture = texture;
	}

	public SaddleFeatureRenderer(FeatureRendererContext<S, M> context, M model, Identifier texture) {
		this(context, model, model, texture);
	}

	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, S livingEntityRenderState, float f, float g) {
		if (livingEntityRenderState.isSaddled()) {
			M entityModel = livingEntityRenderState.baby ? this.babyModel : this.model;
			entityModel.setAngles(livingEntityRenderState);
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(this.texture));
			entityModel.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
		}
	}
}
