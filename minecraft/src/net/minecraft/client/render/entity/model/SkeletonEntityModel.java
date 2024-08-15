package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.state.SkeletonEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SkeletonEntityModel<S extends SkeletonEntityRenderState> extends BipedEntityModel<S> {
	public SkeletonEntityModel(ModelPart modelPart) {
		super(modelPart);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = BipedEntityModel.getModelData(Dilation.NONE, 0.0F);
		ModelPartData modelPartData = modelData.getRoot();
		addLimbs(modelPartData);
		return TexturedModelData.of(modelData, 64, 32);
	}

	protected static void addLimbs(ModelPartData data) {
		data.addChild(
			EntityModelPartNames.RIGHT_ARM, ModelPartBuilder.create().uv(40, 16).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F), ModelTransform.pivot(-5.0F, 2.0F, 0.0F)
		);
		data.addChild(
			EntityModelPartNames.LEFT_ARM,
			ModelPartBuilder.create().uv(40, 16).mirrored().cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F),
			ModelTransform.pivot(5.0F, 2.0F, 0.0F)
		);
		data.addChild(
			EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create().uv(0, 16).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F), ModelTransform.pivot(-2.0F, 12.0F, 0.0F)
		);
		data.addChild(
			EntityModelPartNames.LEFT_LEG,
			ModelPartBuilder.create().uv(0, 16).mirrored().cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F),
			ModelTransform.pivot(2.0F, 12.0F, 0.0F)
		);
	}

	protected BipedEntityModel.ArmPose getArmPose(S skeletonEntityRenderState, Arm arm) {
		return skeletonEntityRenderState.getMainHandStack().isOf(Items.BOW) && skeletonEntityRenderState.attacking && skeletonEntityRenderState.mainArm == arm
			? BipedEntityModel.ArmPose.BOW_AND_ARROW
			: BipedEntityModel.ArmPose.EMPTY;
	}

	public void setAngles(S skeletonEntityRenderState) {
		super.setAngles(skeletonEntityRenderState);
		ItemStack itemStack = skeletonEntityRenderState.getMainHandStack();
		if (skeletonEntityRenderState.attacking && !itemStack.isOf(Items.BOW)) {
			float f = skeletonEntityRenderState.handSwingProgress;
			float g = MathHelper.sin(f * (float) Math.PI);
			float h = MathHelper.sin((1.0F - (1.0F - f) * (1.0F - f)) * (float) Math.PI);
			this.rightArm.roll = 0.0F;
			this.leftArm.roll = 0.0F;
			this.rightArm.yaw = -(0.1F - g * 0.6F);
			this.leftArm.yaw = 0.1F - g * 0.6F;
			this.rightArm.pitch = (float) (-Math.PI / 2);
			this.leftArm.pitch = (float) (-Math.PI / 2);
			this.rightArm.pitch -= g * 1.2F - h * 0.4F;
			this.leftArm.pitch -= g * 1.2F - h * 0.4F;
			CrossbowPosing.swingArms(this.rightArm, this.leftArm, skeletonEntityRenderState.age);
		}
	}

	@Override
	public void setArmAngle(Arm arm, MatrixStack matrices) {
		this.getPart().rotate(matrices);
		float f = arm == Arm.RIGHT ? 1.0F : -1.0F;
		ModelPart modelPart = this.getArm(arm);
		modelPart.pivotX += f;
		modelPart.rotate(matrices);
		modelPart.pivotX -= f;
	}
}
