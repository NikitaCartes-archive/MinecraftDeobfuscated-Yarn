package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.WitchEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@Environment(EnvType.CLIENT)
public class WitchHeldItemFeatureRenderer<T extends LivingEntity> extends FeatureRenderer<T, WitchEntityModel<T>> {
	public WitchHeldItemFeatureRenderer(FeatureRendererContext<T, WitchEntityModel<T>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4208(class_4587 arg, class_4597 arg2, int i, T livingEntity, float f, float g, float h, float j, float k, float l, float m) {
		ItemStack itemStack = livingEntity.getMainHandStack();
		if (!itemStack.isEmpty()) {
			arg.method_22903();
			if (this.getModel().isChild) {
				arg.method_22904(0.0, 0.625, 0.0);
				arg.method_22907(Vector3f.field_20703.method_23214(20.0F, true));
				float n = 0.5F;
				arg.method_22905(0.5F, 0.5F, 0.5F);
			}

			this.getModel().method_2839().method_22703(arg, 0.0625F);
			arg.method_22904(-0.0625, 0.53125, 0.21875);
			Item item = itemStack.getItem();
			if (Block.getBlockFromItem(item).getDefaultState().getRenderType() == BlockRenderType.ENTITYBLOCK_ANIMATED) {
				arg.method_22904(0.0, 0.0625, -0.25);
				arg.method_22907(Vector3f.field_20703.method_23214(30.0F, true));
				arg.method_22907(Vector3f.field_20705.method_23214(-5.0F, true));
				float o = 0.375F;
				arg.method_22905(0.375F, -0.375F, 0.375F);
			} else if (item == Items.BOW) {
				arg.method_22904(0.0, 0.125, -0.125);
				arg.method_22907(Vector3f.field_20705.method_23214(-45.0F, true));
				float o = 0.625F;
				arg.method_22905(0.625F, -0.625F, 0.625F);
				arg.method_22907(Vector3f.field_20703.method_23214(-100.0F, true));
				arg.method_22907(Vector3f.field_20705.method_23214(-20.0F, true));
			} else {
				arg.method_22904(0.1875, 0.1875, 0.0);
				float o = 0.875F;
				arg.method_22905(0.875F, 0.875F, 0.875F);
				arg.method_22907(Vector3f.field_20707.method_23214(-20.0F, true));
				arg.method_22907(Vector3f.field_20703.method_23214(-60.0F, true));
				arg.method_22907(Vector3f.field_20707.method_23214(-30.0F, true));
			}

			arg.method_22907(Vector3f.field_20703.method_23214(-15.0F, true));
			arg.method_22907(Vector3f.field_20707.method_23214(40.0F, true));
			MinecraftClient.getInstance()
				.getFirstPersonRenderer()
				.renderItem(livingEntity, itemStack, ModelTransformation.Type.THIRD_PERSON_RIGHT_HAND, false, arg, arg2);
			arg.method_22909();
		}
	}
}
