package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.WitchEntityModel;
import net.minecraft.client.render.entity.state.WitchEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class WitchHeldItemFeatureRenderer extends VillagerHeldItemFeatureRenderer<WitchEntityRenderState, WitchEntityModel> {
	public WitchHeldItemFeatureRenderer(FeatureRendererContext<WitchEntityRenderState, WitchEntityModel> featureRendererContext, ItemRenderer itemRenderer) {
		super(featureRendererContext, itemRenderer);
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, WitchEntityRenderState witchEntityRenderState, float f, float g
	) {
		matrixStack.push();
		if (witchEntityRenderState.rightHandStack.isOf(Items.POTION)) {
			this.getContextModel().getRootPart().rotate(matrixStack);
			this.getContextModel().getHead().rotate(matrixStack);
			this.getContextModel().getNose().rotate(matrixStack);
			matrixStack.translate(0.0625F, 0.25F, 0.0F);
			matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
			matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(140.0F));
			matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(10.0F));
			matrixStack.translate(0.0F, -0.4F, 0.4F);
		}

		super.render(matrixStack, vertexConsumerProvider, i, witchEntityRenderState, f, g);
		matrixStack.pop();
	}
}
