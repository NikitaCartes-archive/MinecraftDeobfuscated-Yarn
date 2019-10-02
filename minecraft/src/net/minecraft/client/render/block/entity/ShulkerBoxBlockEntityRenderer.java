package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class ShulkerBoxBlockEntityRenderer extends BlockEntityRenderer<ShulkerBoxBlockEntity> {
	private final ShulkerEntityModel<?> model;

	public ShulkerBoxBlockEntityRenderer(ShulkerEntityModel<?> shulkerEntityModel, BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
		this.model = shulkerEntityModel;
	}

	public void method_3574(
		ShulkerBoxBlockEntity shulkerBoxBlockEntity,
		double d,
		double e,
		double f,
		float g,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i
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
		matrixStack.translate(0.5, 1.5, 0.5);
		matrixStack.scale(1.0F, -1.0F, -1.0F);
		matrixStack.translate(0.0, 1.0, 0.0);
		float h = 0.9995F;
		matrixStack.scale(0.9995F, 0.9995F, 0.9995F);
		matrixStack.multiply(direction.method_23224());
		matrixStack.translate(0.0, -1.0, 0.0);
		VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.CUTOUT_MIPPED);
		this.model.method_2831().render(matrixStack, vertexConsumer, 0.0625F, i, sprite);
		matrixStack.translate(0.0, (double)(-shulkerBoxBlockEntity.getAnimationProgress(g) * 0.5F), 0.0);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(270.0F * shulkerBoxBlockEntity.getAnimationProgress(g), true));
		this.model.method_2829().render(matrixStack, vertexConsumer, 0.0625F, i, sprite);
		matrixStack.pop();
	}
}
