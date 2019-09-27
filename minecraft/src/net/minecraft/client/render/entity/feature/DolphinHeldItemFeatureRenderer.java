package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.DolphinEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class DolphinHeldItemFeatureRenderer extends FeatureRenderer<DolphinEntity, DolphinEntityModel<DolphinEntity>> {
	public DolphinHeldItemFeatureRenderer(FeatureRendererContext<DolphinEntity, DolphinEntityModel<DolphinEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_17160(class_4587 arg, class_4597 arg2, int i, DolphinEntity dolphinEntity, float f, float g, float h, float j, float k, float l, float m) {
		boolean bl = dolphinEntity.getMainArm() == Arm.RIGHT;
		arg.method_22903();
		float n = 1.0F;
		float o = -1.0F;
		float p = MathHelper.abs(dolphinEntity.pitch) / 60.0F;
		if (dolphinEntity.pitch < 0.0F) {
			arg.method_22904(0.0, (double)(1.0F - p * 0.5F), (double)(-1.0F + p * 0.5F));
		} else {
			arg.method_22904(0.0, (double)(1.0F + p * 0.8F), (double)(-1.0F + p * 0.2F));
		}

		ItemStack itemStack = bl ? dolphinEntity.getMainHandStack() : dolphinEntity.getOffHandStack();
		MinecraftClient.getInstance().getFirstPersonRenderer().renderItem(dolphinEntity, itemStack, ModelTransformation.Type.GROUND, false, arg, arg2);
		arg.method_22909();
	}
}
