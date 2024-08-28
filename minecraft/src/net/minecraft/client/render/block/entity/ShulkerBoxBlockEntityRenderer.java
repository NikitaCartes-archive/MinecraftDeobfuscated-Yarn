package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class ShulkerBoxBlockEntityRenderer implements BlockEntityRenderer<ShulkerBoxBlockEntity> {
	private final ShulkerBoxBlockEntityRenderer.ShulkerBoxBlockModel model;

	public ShulkerBoxBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
		this.model = new ShulkerBoxBlockEntityRenderer.ShulkerBoxBlockModel(ctx.getLayerModelPart(EntityModelLayers.SHULKER_BOX));
	}

	public void render(ShulkerBoxBlockEntity shulkerBoxBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		Direction direction = Direction.UP;
		if (shulkerBoxBlockEntity.hasWorld()) {
			BlockState blockState = shulkerBoxBlockEntity.getWorld().getBlockState(shulkerBoxBlockEntity.getPos());
			if (blockState.getBlock() instanceof ShulkerBoxBlock) {
				direction = blockState.get(ShulkerBoxBlock.FACING);
			}
		}

		DyeColor dyeColor = shulkerBoxBlockEntity.getColor();
		SpriteIdentifier spriteIdentifier;
		if (dyeColor == null) {
			spriteIdentifier = TexturedRenderLayers.SHULKER_TEXTURE_ID;
		} else {
			spriteIdentifier = (SpriteIdentifier)TexturedRenderLayers.COLORED_SHULKER_BOXES_TEXTURES.get(dyeColor.getId());
		}

		matrixStack.push();
		matrixStack.translate(0.5F, 0.5F, 0.5F);
		float g = 0.9995F;
		matrixStack.scale(0.9995F, 0.9995F, 0.9995F);
		matrixStack.multiply(direction.getRotationQuaternion());
		matrixStack.scale(1.0F, -1.0F, -1.0F);
		matrixStack.translate(0.0F, -1.0F, 0.0F);
		this.model.animateLid(shulkerBoxBlockEntity, f);
		VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumerProvider, this.model::getLayer);
		this.model.render(matrixStack, vertexConsumer, i, j);
		matrixStack.pop();
	}

	@Environment(EnvType.CLIENT)
	static class ShulkerBoxBlockModel extends Model {
		private final ModelPart lid;

		public ShulkerBoxBlockModel(ModelPart root) {
			super(root, RenderLayer::getEntityCutoutNoCull);
			this.lid = root.getChild("lid");
		}

		public void animateLid(ShulkerBoxBlockEntity blockEntity, float delta) {
			this.lid.setPivot(0.0F, 24.0F - blockEntity.getAnimationProgress(delta) * 0.5F * 16.0F, 0.0F);
			this.lid.yaw = 270.0F * blockEntity.getAnimationProgress(delta) * (float) (Math.PI / 180.0);
		}
	}
}
