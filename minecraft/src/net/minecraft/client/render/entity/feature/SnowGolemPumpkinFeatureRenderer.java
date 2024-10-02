package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.SnowGolemEntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class SnowGolemPumpkinFeatureRenderer extends FeatureRenderer<LivingEntityRenderState, SnowGolemEntityModel> {
	private final BlockRenderManager blockRenderManager;
	private final ItemRenderer itemRenderer;

	public SnowGolemPumpkinFeatureRenderer(
		FeatureRendererContext<LivingEntityRenderState, SnowGolemEntityModel> context, BlockRenderManager blockRenderManager, ItemRenderer itemRenderer
	) {
		super(context);
		this.blockRenderManager = blockRenderManager;
		this.itemRenderer = itemRenderer;
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, LivingEntityRenderState livingEntityRenderState, float f, float g
	) {
		BakedModel bakedModel = livingEntityRenderState.equippedHeadItemModel;
		if (bakedModel != null) {
			boolean bl = livingEntityRenderState.hasOutline && livingEntityRenderState.invisible;
			if (!livingEntityRenderState.invisible || bl) {
				matrixStack.push();
				this.getContextModel().getHead().rotate(matrixStack);
				float h = 0.625F;
				matrixStack.translate(0.0F, -0.34375F, 0.0F);
				matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
				matrixStack.scale(0.625F, -0.625F, -0.625F);
				ItemStack itemStack = livingEntityRenderState.equippedHeadStack;
				if (bl && itemStack.getItem() instanceof BlockItem blockItem) {
					BlockState blockState = blockItem.getBlock().getDefaultState();
					BakedModel bakedModel2 = this.blockRenderManager.getModel(blockState);
					int j = LivingEntityRenderer.getOverlay(livingEntityRenderState, 0.0F);
					matrixStack.translate(-0.5F, -0.5F, -0.5F);
					this.blockRenderManager
						.getModelRenderer()
						.render(
							matrixStack.peek(),
							vertexConsumerProvider.getBuffer(RenderLayer.getOutline(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)),
							blockState,
							bakedModel2,
							0.0F,
							0.0F,
							0.0F,
							i,
							j
						);
				} else {
					this.itemRenderer
						.renderItem(
							itemStack,
							ModelTransformationMode.HEAD,
							false,
							matrixStack,
							vertexConsumerProvider,
							i,
							LivingEntityRenderer.getOverlay(livingEntityRenderState, 0.0F),
							bakedModel
						);
				}

				matrixStack.pop();
			}
		}
	}
}
