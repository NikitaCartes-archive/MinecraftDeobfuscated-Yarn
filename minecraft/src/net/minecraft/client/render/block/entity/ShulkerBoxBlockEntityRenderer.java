package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4576;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Quaternion;

@Environment(EnvType.CLIENT)
public class ShulkerBoxBlockEntityRenderer extends class_4576<ShulkerBoxBlockEntity> {
	private final ShulkerEntityModel<?> model;

	public ShulkerBoxBlockEntityRenderer(ShulkerEntityModel<?> shulkerEntityModel) {
		this.model = shulkerEntityModel;
	}

	protected void method_3574(
		ShulkerBoxBlockEntity shulkerBoxBlockEntity,
		double d,
		double e,
		double f,
		float g,
		int i,
		BlockRenderLayer blockRenderLayer,
		BufferBuilder bufferBuilder,
		int j,
		int k
	) {
		Direction direction = Direction.UP;
		if (shulkerBoxBlockEntity.hasWorld()) {
			BlockState blockState = this.getWorld().getBlockState(shulkerBoxBlockEntity.getPos());
			if (blockState.getBlock() instanceof ShulkerBoxBlock) {
				direction = blockState.get(ShulkerBoxBlock.FACING);
			}
		}

		Identifier identifier;
		if (i >= 0) {
			identifier = (Identifier)ModelLoader.field_20848.get(i);
		} else {
			DyeColor dyeColor = shulkerBoxBlockEntity.getColor();
			if (dyeColor == null) {
				identifier = ModelLoader.field_20845;
			} else {
				identifier = (Identifier)ModelLoader.field_20846.get(dyeColor.getId());
			}
		}

		Sprite sprite = this.method_22739(identifier);
		bufferBuilder.method_22629();
		bufferBuilder.method_22626(0.5, 1.5, 0.5);
		bufferBuilder.method_22627(1.0F, -1.0F, -1.0F);
		bufferBuilder.method_22626(0.0, 1.0, 0.0);
		float h = 0.9995F;
		bufferBuilder.method_22627(0.9995F, 0.9995F, 0.9995F);
		bufferBuilder.method_22626(0.0, -1.0, 0.0);
		switch (direction) {
			case DOWN:
				bufferBuilder.method_22626(0.0, 2.0, 0.0);
				bufferBuilder.method_22622(new Quaternion(Vector3f.field_20703, 180.0F, true));
			case UP:
			default:
				break;
			case NORTH:
				bufferBuilder.method_22626(0.0, 1.0, 1.0);
				bufferBuilder.method_22622(new Quaternion(Vector3f.field_20703, 90.0F, true));
				bufferBuilder.method_22622(new Quaternion(Vector3f.field_20707, 180.0F, true));
				break;
			case SOUTH:
				bufferBuilder.method_22626(0.0, 1.0, -1.0);
				bufferBuilder.method_22622(new Quaternion(Vector3f.field_20703, 90.0F, true));
				break;
			case WEST:
				bufferBuilder.method_22626(-1.0, 1.0, 0.0);
				bufferBuilder.method_22622(new Quaternion(Vector3f.field_20703, 90.0F, true));
				bufferBuilder.method_22622(new Quaternion(Vector3f.field_20707, -90.0F, true));
				break;
			case EAST:
				bufferBuilder.method_22626(1.0, 1.0, 0.0);
				bufferBuilder.method_22622(new Quaternion(Vector3f.field_20703, 90.0F, true));
				bufferBuilder.method_22622(new Quaternion(Vector3f.field_20707, 90.0F, true));
		}

		this.model.method_2831().method_22698(bufferBuilder, 0.0625F, j, k, sprite);
		bufferBuilder.method_22626(0.0, (double)(-shulkerBoxBlockEntity.getAnimationProgress(g) * 0.5F), 0.0);
		bufferBuilder.method_22622(new Quaternion(Vector3f.field_20705, 270.0F * shulkerBoxBlockEntity.getAnimationProgress(g), true));
		this.model.method_2829().method_22698(bufferBuilder, 0.0625F, j, k, sprite);
		bufferBuilder.method_22630();
	}
}
