package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5605;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SkeletonEntityModel<T extends MobEntity & RangedAttackMob> extends BipedEntityModel<T> {
	public SkeletonEntityModel(ModelPart modelPart) {
		super(modelPart);
	}

	public static class_5607 method_32047() {
		class_5609 lv = BipedEntityModel.method_32011(class_5605.field_27715, 0.0F);
		class_5610 lv2 = lv.method_32111();
		lv2.method_32117(
			"right_arm", class_5606.method_32108().method_32101(40, 16).method_32097(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F), class_5603.method_32090(-5.0F, 2.0F, 0.0F)
		);
		lv2.method_32117(
			"left_arm",
			class_5606.method_32108().method_32101(40, 16).method_32096().method_32097(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F),
			class_5603.method_32090(5.0F, 2.0F, 0.0F)
		);
		lv2.method_32117(
			"right_leg", class_5606.method_32108().method_32101(0, 16).method_32097(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F), class_5603.method_32090(-2.0F, 12.0F, 0.0F)
		);
		lv2.method_32117(
			"left_leg",
			class_5606.method_32108().method_32101(0, 16).method_32096().method_32097(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F),
			class_5603.method_32090(2.0F, 12.0F, 0.0F)
		);
		return class_5607.method_32110(lv, 64, 32);
	}

	public void animateModel(T mobEntity, float f, float g, float h) {
		this.rightArmPose = BipedEntityModel.ArmPose.EMPTY;
		this.leftArmPose = BipedEntityModel.ArmPose.EMPTY;
		ItemStack itemStack = mobEntity.getStackInHand(Hand.MAIN_HAND);
		if (itemStack.isOf(Items.BOW) && mobEntity.isAttacking()) {
			if (mobEntity.getMainArm() == Arm.RIGHT) {
				this.rightArmPose = BipedEntityModel.ArmPose.BOW_AND_ARROW;
			} else {
				this.leftArmPose = BipedEntityModel.ArmPose.BOW_AND_ARROW;
			}
		}

		super.animateModel(mobEntity, f, g, h);
	}

	public void setAngles(T mobEntity, float f, float g, float h, float i, float j) {
		super.setAngles(mobEntity, f, g, h, i, j);
		ItemStack itemStack = mobEntity.getMainHandStack();
		if (mobEntity.isAttacking() && (itemStack.isEmpty() || !itemStack.isOf(Items.BOW))) {
			float k = MathHelper.sin(this.handSwingProgress * (float) Math.PI);
			float l = MathHelper.sin((1.0F - (1.0F - this.handSwingProgress) * (1.0F - this.handSwingProgress)) * (float) Math.PI);
			this.rightArm.roll = 0.0F;
			this.field_27433.roll = 0.0F;
			this.rightArm.yaw = -(0.1F - k * 0.6F);
			this.field_27433.yaw = 0.1F - k * 0.6F;
			this.rightArm.pitch = (float) (-Math.PI / 2);
			this.field_27433.pitch = (float) (-Math.PI / 2);
			this.rightArm.pitch -= k * 1.2F - l * 0.4F;
			this.field_27433.pitch -= k * 1.2F - l * 0.4F;
			CrossbowPosing.method_29350(this.rightArm, this.field_27433, h);
		}
	}

	@Override
	public void setArmAngle(Arm arm, MatrixStack matrices) {
		float f = arm == Arm.RIGHT ? 1.0F : -1.0F;
		ModelPart modelPart = this.getArm(arm);
		modelPart.pivotX += f;
		modelPart.rotate(matrices);
		modelPart.pivotX -= f;
	}
}
