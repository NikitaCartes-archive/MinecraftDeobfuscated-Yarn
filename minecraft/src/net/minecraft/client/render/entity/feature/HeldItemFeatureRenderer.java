package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;

@Environment(EnvType.CLIENT)
public class HeldItemFeatureRenderer<T extends LivingEntity, M extends EntityModel<T> & ModelWithArms> extends FeatureRenderer<T, M> {
	public HeldItemFeatureRenderer(FeatureRendererContext<T, M> context) {
		super(context);
	}

	public void method_17162(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, float m
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

			this.renderItem(livingEntity, itemStack2, ModelTransformation.Type.THIRD_PERSON_RIGHT_HAND, Arm.RIGHT, matrixStack, vertexConsumerProvider);
			this.renderItem(livingEntity, itemStack, ModelTransformation.Type.THIRD_PERSON_LEFT_HAND, Arm.LEFT, matrixStack, vertexConsumerProvider);
			matrixStack.pop();
		}
	}

	private void renderItem(
		LivingEntity livingEntity,
		ItemStack itemStack,
		ModelTransformation.Type type,
		Arm arm,
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider
	) {
		if (!itemStack.isEmpty()) {
			matrixStack.push();
			this.getModel().setArmAngle(0.0625F, arm, matrixStack);
			matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(-90.0F));
			matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0F));
			boolean bl = arm == Arm.LEFT;
			matrixStack.translate((double)((float)(bl ? -1 : 1) / 16.0F), 0.125, -0.625);
			MinecraftClient.getInstance().getFirstPersonRenderer().renderItem(livingEntity, itemStack, type, bl, matrixStack, vertexConsumerProvider);
			matrixStack.pop();
		}
	}
}
