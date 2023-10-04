package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.SnowGolemEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class SnowGolemPumpkinFeatureRenderer extends FeatureRenderer<SnowGolemEntity, SnowGolemEntityModel<SnowGolemEntity>> {
	private final BlockRenderManager blockRenderManager;
	private final ItemRenderer itemRenderer;

	public SnowGolemPumpkinFeatureRenderer(
		FeatureRendererContext<SnowGolemEntity, SnowGolemEntityModel<SnowGolemEntity>> context, BlockRenderManager blockRenderManager, ItemRenderer itemRenderer
	) {
		super(context);
		this.blockRenderManager = blockRenderManager;
		this.itemRenderer = itemRenderer;
	}

	public void render(
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider,
		int i,
		SnowGolemEntity snowGolemEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l
	) {
		if (snowGolemEntity.hasPumpkin()) {
			boolean bl = MinecraftClient.getInstance().hasOutline(snowGolemEntity) && snowGolemEntity.isInvisible();
			if (!snowGolemEntity.isInvisible() || bl) {
				matrixStack.push();
				this.getContextModel().getHead().rotate(matrixStack);
				float m = 0.625F;
				matrixStack.translate(0.0F, -0.34375F, 0.0F);
				matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
				matrixStack.scale(0.625F, -0.625F, -0.625F);
				ItemStack itemStack = new ItemStack(Blocks.CARVED_PUMPKIN);
				if (bl) {
					BlockState blockState = Blocks.CARVED_PUMPKIN.getDefaultState();
					BakedModel bakedModel = this.blockRenderManager.getModel(blockState);
					int n = LivingEntityRenderer.getOverlay(snowGolemEntity, 0.0F);
					matrixStack.translate(-0.5F, -0.5F, -0.5F);
					this.blockRenderManager
						.getModelRenderer()
						.render(
							matrixStack.peek(),
							vertexConsumerProvider.getBuffer(RenderLayer.getOutline(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)),
							blockState,
							bakedModel,
							0.0F,
							0.0F,
							0.0F,
							i,
							n
						);
				} else {
					this.itemRenderer
						.renderItem(
							snowGolemEntity,
							itemStack,
							ModelTransformationMode.HEAD,
							false,
							matrixStack,
							vertexConsumerProvider,
							snowGolemEntity.getWorld(),
							i,
							LivingEntityRenderer.getOverlay(snowGolemEntity, 0.0F),
							snowGolemEntity.getId()
						);
				}

				matrixStack.pop();
			}
		}
	}
}
