package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class HeldItemFeatureRenderer<T extends LivingEntity, M extends EntityModel<T> & ModelWithArms> extends FeatureRenderer<T, M> {
	public HeldItemFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_17162(
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i,
		T livingEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l,
		float m
	) {
		boolean bl = livingEntity.getMainArm() == Arm.RIGHT;
		ItemStack itemStack = bl ? livingEntity.getOffHandStack() : livingEntity.getMainHandStack();
		ItemStack itemStack2 = bl ? livingEntity.getMainHandStack() : livingEntity.getOffHandStack();
		if (!itemStack.isEmpty() || !itemStack2.isEmpty()) {
			matrixStack.push();
			if (this.getModel().isChild) {
				float n = 0.5F;
				matrixStack.translate(0.0, 0.75, 0.0);
				matrixStack.scale(0.5F, 0.5F, 0.5F);
			}

			this.method_4192(livingEntity, itemStack2, ModelTransformation.Type.THIRD_PERSON_RIGHT_HAND, Arm.RIGHT, matrixStack, layeredVertexConsumerStorage);
			this.method_4192(livingEntity, itemStack, ModelTransformation.Type.THIRD_PERSON_LEFT_HAND, Arm.LEFT, matrixStack, layeredVertexConsumerStorage);
			matrixStack.pop();
		}
	}

	private void method_4192(
		LivingEntity livingEntity,
		ItemStack itemStack,
		ModelTransformation.Type type,
		Arm arm,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage
	) {
		if (!itemStack.isEmpty()) {
			matrixStack.push();
			this.getModel().setArmAngle(0.0625F, arm, matrixStack);
			if (livingEntity.isInSneakingPose()) {
				matrixStack.translate(0.0, 0.2F, 0.0);
			}

			matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(-90.0F, true));
			matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0F, true));
			boolean bl = arm == Arm.LEFT;
			matrixStack.translate((double)((float)(bl ? -1 : 1) / 16.0F), 0.125, -0.625);
			MinecraftClient.getInstance().getFirstPersonRenderer().renderItem(livingEntity, itemStack, type, bl, matrixStack, layeredVertexConsumerStorage);
			matrixStack.pop();
		}
	}
}
