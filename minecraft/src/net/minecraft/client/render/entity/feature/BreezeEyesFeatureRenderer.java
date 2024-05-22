package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.BreezeEntityRenderer;
import net.minecraft.client.render.entity.model.BreezeEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.BreezeEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BreezeEyesFeatureRenderer extends FeatureRenderer<BreezeEntity, BreezeEntityModel<BreezeEntity>> {
	private static final RenderLayer TEXTURE = RenderLayer.getEntityTranslucentEmissiveNoOutline(Identifier.ofVanilla("textures/entity/breeze/breeze_eyes.png"));

	public BreezeEyesFeatureRenderer(FeatureRendererContext<BreezeEntity, BreezeEntityModel<BreezeEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void render(
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider,
		int i,
		BreezeEntity breezeEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l
	) {
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(TEXTURE);
		BreezeEntityModel<BreezeEntity> breezeEntityModel = this.getContextModel();
		BreezeEntityRenderer.updatePartVisibility(breezeEntityModel, breezeEntityModel.getHead(), breezeEntityModel.getEyes())
			.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
	}
}
