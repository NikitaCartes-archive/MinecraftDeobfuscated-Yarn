package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;

@Environment(EnvType.CLIENT)
public class HeldItemFeatureRenderer<T extends LivingEntity, M extends EntityModel<T> & ModelWithArms> extends FeatureRenderer<T, M> {
	public HeldItemFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_17162(class_4587 arg, class_4597 arg2, int i, T livingEntity, float f, float g, float h, float j, float k, float l, float m) {
		boolean bl = livingEntity.getMainArm() == Arm.RIGHT;
		ItemStack itemStack = bl ? livingEntity.getOffHandStack() : livingEntity.getMainHandStack();
		ItemStack itemStack2 = bl ? livingEntity.getMainHandStack() : livingEntity.getOffHandStack();
		if (!itemStack.isEmpty() || !itemStack2.isEmpty()) {
			arg.method_22903();
			if (this.getModel().isChild) {
				float n = 0.5F;
				arg.method_22904(0.0, 0.75, 0.0);
				arg.method_22905(0.5F, 0.5F, 0.5F);
			}

			this.method_4192(livingEntity, itemStack2, ModelTransformation.Type.THIRD_PERSON_RIGHT_HAND, Arm.RIGHT, arg, arg2);
			this.method_4192(livingEntity, itemStack, ModelTransformation.Type.THIRD_PERSON_LEFT_HAND, Arm.LEFT, arg, arg2);
			arg.method_22909();
		}
	}

	private void method_4192(LivingEntity livingEntity, ItemStack itemStack, ModelTransformation.Type type, Arm arm, class_4587 arg, class_4597 arg2) {
		if (!itemStack.isEmpty()) {
			arg.method_22903();
			this.getModel().setArmAngle(0.0625F, arm, arg);
			if (livingEntity.isInSneakingPose()) {
				arg.method_22904(0.0, 0.2F, 0.0);
			}

			arg.method_22907(Vector3f.field_20703.method_23214(-90.0F, true));
			arg.method_22907(Vector3f.field_20705.method_23214(180.0F, true));
			boolean bl = arm == Arm.LEFT;
			arg.method_22904((double)((float)(bl ? -1 : 1) / 16.0F), 0.125, -0.625);
			MinecraftClient.getInstance().getFirstPersonRenderer().renderItem(livingEntity, itemStack, type, bl, arg, arg2);
			arg.method_22909();
		}
	}
}
