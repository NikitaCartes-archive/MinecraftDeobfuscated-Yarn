package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class VillagerHeldItemFeatureRenderer<S extends LivingEntityRenderState, M extends EntityModel<S>> extends FeatureRenderer<S, M> {
	private final ItemRenderer itemRenderer;

	public VillagerHeldItemFeatureRenderer(FeatureRendererContext<S, M> context, ItemRenderer itemRenderer) {
		super(context);
		this.itemRenderer = itemRenderer;
	}

	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, S livingEntityRenderState, float f, float g) {
		BakedModel bakedModel = livingEntityRenderState.getMainHandItemModel();
		if (bakedModel != null) {
			matrixStack.push();
			matrixStack.translate(0.0F, 0.4F, -0.4F);
			matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
			ItemStack itemStack = livingEntityRenderState.getMainHandStack();
			this.itemRenderer
				.renderItem(itemStack, ModelTransformationMode.GROUND, false, matrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV, bakedModel);
			matrixStack.pop();
		}
	}
}
