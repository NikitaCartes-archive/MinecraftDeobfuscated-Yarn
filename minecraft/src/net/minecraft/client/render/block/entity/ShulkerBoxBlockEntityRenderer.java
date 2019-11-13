package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class ShulkerBoxBlockEntityRenderer extends BlockEntityRenderer<ShulkerBoxBlockEntity> {
	private final ShulkerEntityModel<?> model;

	public ShulkerBoxBlockEntityRenderer(ShulkerEntityModel<?> shulkerEntityModel, BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
		this.model = shulkerEntityModel;
	}

	public void method_3574(
		ShulkerBoxBlockEntity shulkerBoxBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j
	) {
		Direction direction = Direction.UP;
		if (shulkerBoxBlockEntity.hasWorld()) {
			BlockState blockState = shulkerBoxBlockEntity.getWorld().getBlockState(shulkerBoxBlockEntity.getPos());
			if (blockState.getBlock() instanceof ShulkerBoxBlock) {
				direction = blockState.get(ShulkerBoxBlock.FACING);
			}
		}

		DyeColor dyeColor = shulkerBoxBlockEntity.getColor();
		Identifier identifier;
		if (dyeColor == null) {
			identifier = ModelLoader.field_20845;
		} else {
			identifier = (Identifier)ModelLoader.field_20846.get(dyeColor.getId());
		}

		Sprite sprite = this.getSprite(identifier);
		matrixStack.push();
		matrixStack.translate(0.5, 0.5, 0.5);
		float g = 0.9995F;
		matrixStack.scale(0.9995F, 0.9995F, 0.9995F);
		matrixStack.multiply(direction.getRotationQuaternion());
		matrixStack.scale(1.0F, -1.0F, -1.0F);
		matrixStack.translate(0.0, -1.0, 0.0);
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.method_23948());
		this.model.getBottomShell().render(matrixStack, vertexConsumer, i, j, sprite);
		matrixStack.translate(0.0, (double)(-shulkerBoxBlockEntity.getAnimationProgress(f) * 0.5F), 0.0);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(270.0F * shulkerBoxBlockEntity.getAnimationProgress(f)));
		this.model.getTopShell().render(matrixStack, vertexConsumer, i, j, sprite);
		matrixStack.pop();
	}
}
