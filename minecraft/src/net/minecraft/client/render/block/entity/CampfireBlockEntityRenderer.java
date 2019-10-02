package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class CampfireBlockEntityRenderer extends BlockEntityRenderer<CampfireBlockEntity> {
	public CampfireBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
	}

	public void method_17581(
		CampfireBlockEntity campfireBlockEntity,
		double d,
		double e,
		double f,
		float g,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i
	) {
		Direction direction = campfireBlockEntity.getCachedState().get(CampfireBlock.FACING);
		DefaultedList<ItemStack> defaultedList = campfireBlockEntity.getItemsBeingCooked();

		for (int j = 0; j < defaultedList.size(); j++) {
			ItemStack itemStack = defaultedList.get(j);
			if (itemStack != ItemStack.EMPTY) {
				matrixStack.push();
				matrixStack.translate(0.5, 0.44921875, 0.5);
				Direction direction2 = Direction.fromHorizontal((j + direction.getHorizontal()) % 4);
				matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(-direction2.asRotation(), true));
				matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(90.0F, true));
				matrixStack.translate(-0.3125, -0.3125, 0.0);
				matrixStack.scale(0.375F, 0.375F, 0.375F);
				MinecraftClient.getInstance().getItemRenderer().method_23178(itemStack, ModelTransformation.Type.FIXED, i, matrixStack, layeredVertexConsumerStorage);
				matrixStack.pop();
			}
		}
	}
}
