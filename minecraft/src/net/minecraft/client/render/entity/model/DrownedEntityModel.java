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
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class DrownedEntityModel extends ZombieEntityModel<ZombieEntityRenderState> {
	public DrownedEntityModel(ModelPart modelPart) {
		super(modelPart);
	}

	public static TexturedModelData getTexturedModelData(Dilation dilation) {
		ModelData modelData = BipedEntityModel.getModelData(dilation, 0.0F);
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(
			EntityModelPartNames.LEFT_ARM,
			ModelPartBuilder.create().uv(32, 48).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation),
			ModelTransform.pivot(5.0F, 2.0F, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_LEG,
			ModelPartBuilder.create().uv(16, 48).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation),
			ModelTransform.pivot(1.9F, 12.0F, 0.0F)
		);
		return TexturedModelData.of(modelData, 64, 64);
	}

	protected BipedEntityModel.ArmPose getArmPose(ZombieEntityRenderState zombieEntityRenderState, Arm arm) {
		ItemStack itemStack = arm == Arm.RIGHT ? zombieEntityRenderState.rightHandStack : zombieEntityRenderState.leftHandStack;
		return itemStack.isOf(Items.TRIDENT) && zombieEntityRenderState.attacking && zombieEntityRenderState.mainArm == arm
			? BipedEntityModel.ArmPose.THROW_SPEAR
			: BipedEntityModel.ArmPose.EMPTY;
	}

	@Override
	public void setAngles(ZombieEntityRenderState zombieEntityRenderState) {
		super.setAngles(zombieEntityRenderState);
		if (this.getArmPose(zombieEntityRenderState, Arm.LEFT) == BipedEntityModel.ArmPose.THROW_SPEAR) {
			this.leftArm.pitch = this.leftArm.pitch * 0.5F - (float) Math.PI;
			this.leftArm.yaw = 0.0F;
		}

		if (this.getArmPose(zombieEntityRenderState, Arm.RIGHT) == BipedEntityModel.ArmPose.THROW_SPEAR) {
			this.rightArm.pitch = this.rightArm.pitch * 0.5F - (float) Math.PI;
			this.rightArm.yaw = 0.0F;
		}

		float f = zombieEntityRenderState.leaningPitch;
		if (f > 0.0F) {
			this.rightArm.pitch = MathHelper.lerpAngleRadians(f, this.rightArm.pitch, (float) (-Math.PI * 4.0 / 5.0))
				+ f * 0.35F * MathHelper.sin(0.1F * zombieEntityRenderState.age);
			this.leftArm.pitch = MathHelper.lerpAngleRadians(f, this.leftArm.pitch, (float) (-Math.PI * 4.0 / 5.0))
				- f * 0.35F * MathHelper.sin(0.1F * zombieEntityRenderState.age);
			this.rightArm.roll = MathHelper.lerpAngleRadians(f, this.rightArm.roll, -0.15F);
			this.leftArm.roll = MathHelper.lerpAngleRadians(f, this.leftArm.roll, 0.15F);
			this.leftLeg.pitch = this.leftLeg.pitch - f * 0.55F * MathHelper.sin(0.1F * zombieEntityRenderState.age);
			this.rightLeg.pitch = this.rightLeg.pitch + f * 0.55F * MathHelper.sin(0.1F * zombieEntityRenderState.age);
			this.head.pitch = 0.0F;
		}
	}
}
