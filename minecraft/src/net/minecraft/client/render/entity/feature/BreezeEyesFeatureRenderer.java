package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.BreezeEntityRenderer;
import net.minecraft.client.render.entity.model.BreezeEntityModel;
import net.minecraft.client.render.entity.state.BreezeEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BreezeEyesFeatureRenderer extends FeatureRenderer<BreezeEntityRenderState, BreezeEntityModel> {
	private static final RenderLayer TEXTURE = RenderLayer.getEntityTranslucentEmissiveNoOutline(Identifier.ofVanilla("textures/entity/breeze/breeze_eyes.png"));

	public BreezeEyesFeatureRenderer(FeatureRendererContext<BreezeEntityRenderState, BreezeEntityModel> featureRendererContext) {
		super(featureRendererContext);
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, BreezeEntityRenderState breezeEntityRenderState, float f, float g
	) {
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(TEXTURE);
		BreezeEntityModel breezeEntityModel = this.getContextModel();
		BreezeEntityRenderer.updatePartVisibility(breezeEntityModel, breezeEntityModel.getHead(), breezeEntityModel.getEyes())
			.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
	}
}
