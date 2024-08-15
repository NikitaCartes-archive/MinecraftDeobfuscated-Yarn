package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.client.render.entity.state.WolfEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class WolfCollarFeatureRenderer extends FeatureRenderer<WolfEntityRenderState, WolfEntityModel> {
	private static final Identifier SKIN = Identifier.ofVanilla("textures/entity/wolf/wolf_collar.png");

	public WolfCollarFeatureRenderer(FeatureRendererContext<WolfEntityRenderState, WolfEntityModel> featureRendererContext) {
		super(featureRendererContext);
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, WolfEntityRenderState wolfEntityRenderState, float f, float g
	) {
		DyeColor dyeColor = wolfEntityRenderState.collarColor;
		if (dyeColor != null && !wolfEntityRenderState.invisible) {
			int j = dyeColor.getEntityColor();
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(SKIN));
			this.getContextModel().render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, j);
		}
	}
}
