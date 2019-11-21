package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4722;
import net.minecraft.class_4730;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class ShulkerBoxBlockEntityRenderer extends BlockEntityRenderer<ShulkerBoxBlockEntity> {
	private final ShulkerEntityModel<?> model;

	public ShulkerBoxBlockEntityRenderer(ShulkerEntityModel<?> shulkerEntityModel, BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
		this.model = shulkerEntityModel;
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
		class_4730 lv;
		if (dyeColor == null) {
			lv = class_4722.field_21710;
		} else {
			lv = (class_4730)class_4722.field_21711.get(dyeColor.getId());
		}

		matrixStack.push();
		matrixStack.translate(0.5, 0.5, 0.5);
		float g = 0.9995F;
		matrixStack.scale(0.9995F, 0.9995F, 0.9995F);
		matrixStack.multiply(direction.getRotationQuaternion());
		matrixStack.scale(1.0F, -1.0F, -1.0F);
		matrixStack.translate(0.0, -1.0, 0.0);
		VertexConsumer vertexConsumer = lv.method_24145(vertexConsumerProvider, RenderLayer::getEntityCutoutNoCull);
		this.model.getBottomShell().render(matrixStack, vertexConsumer, i, j);
		matrixStack.translate(0.0, (double)(-shulkerBoxBlockEntity.getAnimationProgress(f) * 0.5F), 0.0);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(270.0F * shulkerBoxBlockEntity.getAnimationProgress(f)));
		this.model.getTopShell().render(matrixStack, vertexConsumer, i, j);
		matrixStack.pop();
	}
}
