package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.DolphinEntityModel;
import net.minecraft.client.render.entity.state.DolphinEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class DolphinHeldItemFeatureRenderer extends FeatureRenderer<DolphinEntityRenderState, DolphinEntityModel> {
	private final ItemRenderer itemRenderer;

	public DolphinHeldItemFeatureRenderer(FeatureRendererContext<DolphinEntityRenderState, DolphinEntityModel> context, ItemRenderer itemRenderer) {
		super(context);
		this.itemRenderer = itemRenderer;
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, DolphinEntityRenderState dolphinEntityRenderState, float f, float g
	) {
		ItemStack itemStack = dolphinEntityRenderState.getMainHandStack();
		BakedModel bakedModel = dolphinEntityRenderState.getMainHandItemModel();
		if (bakedModel != null) {
			matrixStack.push();
			float h = 1.0F;
			float j = -1.0F;
			float k = MathHelper.abs(dolphinEntityRenderState.pitch) / 60.0F;
			if (dolphinEntityRenderState.pitch < 0.0F) {
				matrixStack.translate(0.0F, 1.0F - k * 0.5F, -1.0F + k * 0.5F);
			} else {
				matrixStack.translate(0.0F, 1.0F + k * 0.8F, -1.0F + k * 0.2F);
			}

			this.itemRenderer
				.renderItem(itemStack, ModelTransformationMode.GROUND, false, matrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV, bakedModel);
			matrixStack.pop();
		}
	}
}
