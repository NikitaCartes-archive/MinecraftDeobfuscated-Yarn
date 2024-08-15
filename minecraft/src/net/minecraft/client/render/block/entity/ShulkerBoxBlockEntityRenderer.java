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
	private final ShulkerBoxBlockEntityRenderer.class_9984 model;

	public ShulkerBoxBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
		this.model = new ShulkerBoxBlockEntityRenderer.class_9984(ctx.getLayerModelPart(EntityModelLayers.SHULKER_BOX));
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
		this.model.method_62341(shulkerBoxBlockEntity, f);
		VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumerProvider, this.model::getLayer);
		this.model.render(matrixStack, vertexConsumer, i, j);
		matrixStack.pop();
	}

	@Environment(EnvType.CLIENT)
	static class class_9984 extends Model {
		private final ModelPart field_53168;
		private final ModelPart field_53169;

		public class_9984(ModelPart modelPart) {
			super(RenderLayer::getEntityCutoutNoCull);
			this.field_53168 = modelPart;
			this.field_53169 = modelPart.getChild("lid");
		}

		public void method_62341(ShulkerBoxBlockEntity shulkerBoxBlockEntity, float f) {
			this.field_53169.setPivot(0.0F, 24.0F - shulkerBoxBlockEntity.getAnimationProgress(f) * 0.5F * 16.0F, 0.0F);
			this.field_53169.yaw = 270.0F * shulkerBoxBlockEntity.getAnimationProgress(f) * (float) (Math.PI / 180.0);
		}

		@Override
		public ModelPart getPart() {
			return this.field_53168;
		}
	}
}
