package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class CampfireBlockEntityRenderer extends BlockEntityRenderer<CampfireBlockEntity> {
	public CampfireBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
	}

	public void method_17581(CampfireBlockEntity campfireBlockEntity, double d, double e, double f, float g, class_4587 arg, class_4597 arg2, int i) {
		Direction direction = campfireBlockEntity.getCachedState().get(CampfireBlock.FACING);
		DefaultedList<ItemStack> defaultedList = campfireBlockEntity.getItemsBeingCooked();

		for (int j = 0; j < defaultedList.size(); j++) {
			ItemStack itemStack = defaultedList.get(j);
			if (itemStack != ItemStack.EMPTY) {
				arg.method_22903();
				arg.method_22904(0.5, 0.44921875, 0.5);
				Direction direction2 = Direction.fromHorizontal((j + direction.getHorizontal()) % 4);
				arg.method_22907(Vector3f.field_20705.method_23214(-direction2.asRotation(), true));
				arg.method_22907(Vector3f.field_20703.method_23214(90.0F, true));
				arg.method_22904(-0.3125, -0.3125, 0.0);
				arg.method_22905(0.375F, 0.375F, 0.375F);
				MinecraftClient.getInstance().getItemRenderer().method_23178(itemStack, ModelTransformation.Type.FIXED, i, arg, arg2);
				arg.method_22909();
			}
		}
	}
}
