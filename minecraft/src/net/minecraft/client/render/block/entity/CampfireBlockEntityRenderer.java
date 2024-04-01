package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class CampfireBlockEntityRenderer implements BlockEntityRenderer<CampfireBlockEntity> {
	private static final float SCALE = 0.375F;
	private final ItemRenderer itemRenderer;

	public CampfireBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
		this.itemRenderer = ctx.getItemRenderer();
	}

	public void render(CampfireBlockEntity campfireBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		boolean bl = campfireBlockEntity.field_50912;
		Direction direction = campfireBlockEntity.getCachedState().get(CampfireBlock.FACING);
		DefaultedList<ItemStack> defaultedList = campfireBlockEntity.getItemsBeingCooked();
		int k = (int)campfireBlockEntity.getPos().asLong();
		float g = bl ? -2.5F : -5.0F;
		float h = bl ? 11.0F : 7.0F;

		for (int l = 0; l < defaultedList.size(); l++) {
			ItemStack itemStack = defaultedList.get(l);
			if (itemStack != ItemStack.EMPTY) {
				matrixStack.push();
				matrixStack.translate(0.5F, (h + 0.1875F) / 16.0F, 0.5F);
				Direction direction2 = Direction.fromHorizontal((l + direction.getHorizontal()) % 4);
				float m = -direction2.asRotation();
				matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(m));
				matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
				matrixStack.translate(g / 16.0F, g / 16.0F, 0.0F);
				matrixStack.scale(0.375F, 0.375F, 0.375F);
				this.itemRenderer.renderItem(itemStack, ModelTransformationMode.FIXED, i, j, matrixStack, vertexConsumerProvider, campfireBlockEntity.getWorld(), k + l);
				matrixStack.pop();
			}
		}
	}
}
