package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BrushableBlockEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class BrushableBlockEntityRenderer implements BlockEntityRenderer<BrushableBlockEntity> {
	private final ItemRenderer itemRenderer;

	public BrushableBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
		this.itemRenderer = context.getItemRenderer();
	}

	public void render(BrushableBlockEntity brushableBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		if (brushableBlockEntity.getWorld() != null) {
			int k = (Integer)brushableBlockEntity.getCachedState().get(Properties.DUSTED);
			if (k > 0) {
				Direction direction = brushableBlockEntity.getHitDirection();
				if (direction != null) {
					ItemStack itemStack = brushableBlockEntity.getItem();
					if (!itemStack.isEmpty()) {
						matrixStack.push();
						matrixStack.translate(0.0F, 0.5F, 0.0F);
						float[] fs = this.getTranslation(direction, k);
						matrixStack.translate(fs[0], fs[1], fs[2]);
						matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(75.0F));
						boolean bl = direction == Direction.EAST || direction == Direction.WEST;
						matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)((bl ? 90 : 0) + 11)));
						matrixStack.scale(0.5F, 0.5F, 0.5F);
						int l = WorldRenderer.getLightmapCoordinates(
							brushableBlockEntity.getWorld(), brushableBlockEntity.getCachedState(), brushableBlockEntity.getPos().offset(direction)
						);
						this.itemRenderer
							.renderItem(
								itemStack, ModelTransformationMode.FIXED, l, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider, brushableBlockEntity.getWorld(), 0
							);
						matrixStack.pop();
					}
				}
			}
		}
	}

	private float[] getTranslation(Direction direction, int dustedLevel) {
		float[] fs = new float[]{0.5F, 0.0F, 0.5F};
		float f = (float)dustedLevel / 10.0F * 0.75F;
		switch (direction) {
			case EAST:
				fs[0] = 0.73F + f;
				break;
			case WEST:
				fs[0] = 0.25F - f;
				break;
			case UP:
				fs[1] = 0.25F + f;
				break;
			case DOWN:
				fs[1] = -0.23F - f;
				break;
			case NORTH:
				fs[2] = 0.25F - f;
				break;
			case SOUTH:
				fs[2] = 0.73F + f;
		}

		return fs;
	}
}
