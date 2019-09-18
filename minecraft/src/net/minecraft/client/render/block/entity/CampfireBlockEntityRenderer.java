package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class CampfireBlockEntityRenderer extends BlockEntityRenderer<CampfireBlockEntity> {
	public void method_17581(CampfireBlockEntity campfireBlockEntity, double d, double e, double f, float g, int i, BlockRenderLayer blockRenderLayer) {
		Direction direction = campfireBlockEntity.getCachedState().get(CampfireBlock.FACING);
		DefaultedList<ItemStack> defaultedList = campfireBlockEntity.getItemsBeingCooked();

		for (int j = 0; j < defaultedList.size(); j++) {
			ItemStack itemStack = defaultedList.get(j);
			if (itemStack != ItemStack.EMPTY) {
				RenderSystem.pushMatrix();
				RenderSystem.translatef((float)d + 0.5F, (float)e + 0.44921875F, (float)f + 0.5F);
				Direction direction2 = Direction.fromHorizontal((j + direction.getHorizontal()) % 4);
				RenderSystem.rotatef(-direction2.asRotation(), 0.0F, 1.0F, 0.0F);
				RenderSystem.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
				RenderSystem.translatef(-0.3125F, -0.3125F, 0.0F);
				RenderSystem.scalef(0.375F, 0.375F, 0.375F);
				MinecraftClient.getInstance().getItemRenderer().renderItem(itemStack, ModelTransformation.Type.FIXED);
				RenderSystem.popMatrix();
			}
		}
	}
}
