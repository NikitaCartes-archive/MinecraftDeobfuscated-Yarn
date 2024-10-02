package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.PandaEntityModel;
import net.minecraft.client.render.entity.state.PandaEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PandaHeldItemFeatureRenderer extends FeatureRenderer<PandaEntityRenderState, PandaEntityModel> {
	private final ItemRenderer itemRenderer;

	public PandaHeldItemFeatureRenderer(FeatureRendererContext<PandaEntityRenderState, PandaEntityModel> context, ItemRenderer itemRenderer) {
		super(context);
		this.itemRenderer = itemRenderer;
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, PandaEntityRenderState pandaEntityRenderState, float f, float g
	) {
		BakedModel bakedModel = pandaEntityRenderState.getMainHandItemModel();
		if (bakedModel != null && pandaEntityRenderState.sitting && !pandaEntityRenderState.scaredByThunderstorm) {
			float h = -0.6F;
			float j = 1.4F;
			if (pandaEntityRenderState.eating) {
				h -= 0.2F * MathHelper.sin(pandaEntityRenderState.age * 0.6F) + 0.2F;
				j -= 0.09F * MathHelper.sin(pandaEntityRenderState.age * 0.6F);
			}

			matrixStack.push();
			matrixStack.translate(0.1F, j, h);
			ItemStack itemStack = pandaEntityRenderState.getMainHandStack();
			this.itemRenderer
				.renderItem(itemStack, ModelTransformationMode.GROUND, false, matrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV, bakedModel);
			matrixStack.pop();
		}
	}
}
