package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class CampfireBlockEntityRenderer extends BlockEntityRenderer<CampfireBlockEntity> {
	public void method_17581(CampfireBlockEntity campfireBlockEntity, double d, double e, double f, float g, int i) {
		Direction direction = campfireBlockEntity.method_11010().method_11654(CampfireBlock.field_17564);
		DefaultedList<ItemStack> defaultedList = campfireBlockEntity.getItemsBeingCooked();

		for (int j = 0; j < defaultedList.size(); j++) {
			ItemStack itemStack = defaultedList.get(j);
			if (itemStack != ItemStack.EMPTY) {
				GlStateManager.pushMatrix();
				GlStateManager.translatef((float)d + 0.5F, (float)e + 0.44921875F, (float)f + 0.5F);
				Direction direction2 = Direction.fromHorizontal((j + direction.getHorizontal()) % 4);
				GlStateManager.rotatef(-direction2.asRotation(), 0.0F, 1.0F, 0.0F);
				GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.translatef(-0.3125F, -0.3125F, 0.0F);
				GlStateManager.scalef(0.375F, 0.375F, 0.375F);
				MinecraftClient.getInstance().method_1480().renderItem(itemStack, ModelTransformation.Type.field_4319);
				GlStateManager.popMatrix();
			}
		}
	}
}
