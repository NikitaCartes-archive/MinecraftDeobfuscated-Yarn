package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class HeldItemFeatureRenderer<T extends LivingEntity, M extends EntityModel<T> & ModelWithArms> extends FeatureRenderer<T, M> {
	private final HeldItemRenderer heldItemRenderer;

	public HeldItemFeatureRenderer(FeatureRendererContext<T, M> context, HeldItemRenderer heldItemRenderer) {
		super(context);
		this.heldItemRenderer = heldItemRenderer;
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l
	) {
		boolean bl = livingEntity.getMainArm() == Arm.RIGHT;
		ItemStack itemStack = bl ? livingEntity.getOffHandStack() : livingEntity.getMainHandStack();
		ItemStack itemStack2 = bl ? livingEntity.getMainHandStack() : livingEntity.getOffHandStack();
		if (!itemStack.isEmpty() || !itemStack2.isEmpty()) {
			matrixStack.push();
			if (this.getContextModel().child) {
				float m = 0.5F;
				matrixStack.translate(0.0F, 0.75F, 0.0F);
				matrixStack.scale(0.5F, 0.5F, 0.5F);
			}

			this.renderItem(livingEntity, itemStack2, ModelTransformationMode.THIRD_PERSON_RIGHT_HAND, Arm.RIGHT, matrixStack, vertexConsumerProvider, i);
			this.renderItem(livingEntity, itemStack, ModelTransformationMode.THIRD_PERSON_LEFT_HAND, Arm.LEFT, matrixStack, vertexConsumerProvider, i);
			matrixStack.pop();
		}
	}

	protected void renderItem(
		LivingEntity entity,
		ItemStack stack,
		ModelTransformationMode transformationMode,
		Arm arm,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light
	) {
		if (!stack.isEmpty()) {
			matrices.push();
			this.getContextModel().setArmAngle(arm, matrices);
			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
			boolean bl = arm == Arm.LEFT;
			matrices.translate((float)(bl ? -1 : 1) / 16.0F, 0.125F, -0.625F);
			this.heldItemRenderer.renderItem(entity, stack, transformationMode, bl, matrices, vertexConsumers, light);
			matrices.pop();
		}
	}
}
