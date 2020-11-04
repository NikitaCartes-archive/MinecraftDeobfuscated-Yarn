package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5597;
import net.minecraft.class_5603;
import net.minecraft.class_5605;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class IllagerEntityModel<T extends IllagerEntity> extends class_5597<T> implements ModelWithArms, ModelWithHead {
	private final ModelPart field_27435;
	private final ModelPart head;
	private final ModelPart hat;
	private final ModelPart arms;
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;
	private final ModelPart rightAttackingArm;
	private final ModelPart leftAttackingArm;

	public IllagerEntityModel(ModelPart modelPart) {
		this.field_27435 = modelPart;
		this.head = modelPart.method_32086("head");
		this.hat = this.head.method_32086("hat");
		this.hat.visible = false;
		this.arms = modelPart.method_32086("arms");
		this.rightLeg = modelPart.method_32086("left_leg");
		this.leftLeg = modelPart.method_32086("right_leg");
		this.leftAttackingArm = modelPart.method_32086("left_arm");
		this.rightAttackingArm = modelPart.method_32086("right_arm");
	}

	public static class_5607 method_32012() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		class_5610 lv3 = lv2.method_32117(
			"head", class_5606.method_32108().method_32101(0, 0).method_32097(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F), class_5603.method_32090(0.0F, 0.0F, 0.0F)
		);
		lv3.method_32117(
			"hat", class_5606.method_32108().method_32101(32, 0).method_32098(-4.0F, -10.0F, -4.0F, 8.0F, 12.0F, 8.0F, new class_5605(0.45F)), class_5603.field_27701
		);
		lv3.method_32117(
			"nose", class_5606.method_32108().method_32101(24, 0).method_32097(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F), class_5603.method_32090(0.0F, -2.0F, 0.0F)
		);
		lv2.method_32117(
			"body",
			class_5606.method_32108()
				.method_32101(16, 20)
				.method_32097(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F)
				.method_32101(0, 38)
				.method_32098(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, new class_5605(0.5F)),
			class_5603.method_32090(0.0F, 0.0F, 0.0F)
		);
		class_5610 lv4 = lv2.method_32117(
			"arms",
			class_5606.method_32108()
				.method_32101(44, 22)
				.method_32097(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F)
				.method_32101(40, 38)
				.method_32097(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F),
			class_5603.method_32091(0.0F, 3.0F, -1.0F, -0.75F, 0.0F, 0.0F)
		);
		lv4.method_32117(
			"left_shoulder", class_5606.method_32108().method_32101(44, 22).method_32096().method_32097(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F), class_5603.field_27701
		);
		lv2.method_32117(
			"right_leg", class_5606.method_32108().method_32101(0, 22).method_32097(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), class_5603.method_32090(-2.0F, 12.0F, 0.0F)
		);
		lv2.method_32117(
			"left_leg",
			class_5606.method_32108().method_32101(0, 22).method_32096().method_32097(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
			class_5603.method_32090(2.0F, 12.0F, 0.0F)
		);
		lv2.method_32117(
			"right_arm", class_5606.method_32108().method_32101(40, 46).method_32097(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F), class_5603.method_32090(-5.0F, 2.0F, 0.0F)
		);
		lv2.method_32117(
			"left_arm",
			class_5606.method_32108().method_32101(40, 46).method_32096().method_32097(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F),
			class_5603.method_32090(5.0F, 2.0F, 0.0F)
		);
		return class_5607.method_32110(lv, 64, 64);
	}

	@Override
	public ModelPart method_32008() {
		return this.field_27435;
	}

	public void setAngles(T illagerEntity, float f, float g, float h, float i, float j) {
		this.head.yaw = i * (float) (Math.PI / 180.0);
		this.head.pitch = j * (float) (Math.PI / 180.0);
		if (this.riding) {
			this.rightAttackingArm.pitch = (float) (-Math.PI / 5);
			this.rightAttackingArm.yaw = 0.0F;
			this.rightAttackingArm.roll = 0.0F;
			this.leftAttackingArm.pitch = (float) (-Math.PI / 5);
			this.leftAttackingArm.yaw = 0.0F;
			this.leftAttackingArm.roll = 0.0F;
			this.leftLeg.pitch = -1.4137167F;
			this.leftLeg.yaw = (float) (Math.PI / 10);
			this.leftLeg.roll = 0.07853982F;
			this.rightLeg.pitch = -1.4137167F;
			this.rightLeg.yaw = (float) (-Math.PI / 10);
			this.rightLeg.roll = -0.07853982F;
		} else {
			this.rightAttackingArm.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 2.0F * g * 0.5F;
			this.rightAttackingArm.yaw = 0.0F;
			this.rightAttackingArm.roll = 0.0F;
			this.leftAttackingArm.pitch = MathHelper.cos(f * 0.6662F) * 2.0F * g * 0.5F;
			this.leftAttackingArm.yaw = 0.0F;
			this.leftAttackingArm.roll = 0.0F;
			this.leftLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g * 0.5F;
			this.leftLeg.yaw = 0.0F;
			this.leftLeg.roll = 0.0F;
			this.rightLeg.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g * 0.5F;
			this.rightLeg.yaw = 0.0F;
			this.rightLeg.roll = 0.0F;
		}

		IllagerEntity.State state = illagerEntity.getState();
		if (state == IllagerEntity.State.ATTACKING) {
			if (illagerEntity.getMainHandStack().isEmpty()) {
				CrossbowPosing.method_29352(this.leftAttackingArm, this.rightAttackingArm, true, this.handSwingProgress, h);
			} else {
				CrossbowPosing.method_29351(this.rightAttackingArm, this.leftAttackingArm, illagerEntity, this.handSwingProgress, h);
			}
		} else if (state == IllagerEntity.State.SPELLCASTING) {
			this.rightAttackingArm.pivotZ = 0.0F;
			this.rightAttackingArm.pivotX = -5.0F;
			this.leftAttackingArm.pivotZ = 0.0F;
			this.leftAttackingArm.pivotX = 5.0F;
			this.rightAttackingArm.pitch = MathHelper.cos(h * 0.6662F) * 0.25F;
			this.leftAttackingArm.pitch = MathHelper.cos(h * 0.6662F) * 0.25F;
			this.rightAttackingArm.roll = (float) (Math.PI * 3.0 / 4.0);
			this.leftAttackingArm.roll = (float) (-Math.PI * 3.0 / 4.0);
			this.rightAttackingArm.yaw = 0.0F;
			this.leftAttackingArm.yaw = 0.0F;
		} else if (state == IllagerEntity.State.BOW_AND_ARROW) {
			this.rightAttackingArm.yaw = -0.1F + this.head.yaw;
			this.rightAttackingArm.pitch = (float) (-Math.PI / 2) + this.head.pitch;
			this.leftAttackingArm.pitch = -0.9424779F + this.head.pitch;
			this.leftAttackingArm.yaw = this.head.yaw - 0.4F;
			this.leftAttackingArm.roll = (float) (Math.PI / 2);
		} else if (state == IllagerEntity.State.CROSSBOW_HOLD) {
			CrossbowPosing.hold(this.rightAttackingArm, this.leftAttackingArm, this.head, true);
		} else if (state == IllagerEntity.State.CROSSBOW_CHARGE) {
			CrossbowPosing.charge(this.rightAttackingArm, this.leftAttackingArm, illagerEntity, true);
		} else if (state == IllagerEntity.State.CELEBRATING) {
			this.rightAttackingArm.pivotZ = 0.0F;
			this.rightAttackingArm.pivotX = -5.0F;
			this.rightAttackingArm.pitch = MathHelper.cos(h * 0.6662F) * 0.05F;
			this.rightAttackingArm.roll = 2.670354F;
			this.rightAttackingArm.yaw = 0.0F;
			this.leftAttackingArm.pivotZ = 0.0F;
			this.leftAttackingArm.pivotX = 5.0F;
			this.leftAttackingArm.pitch = MathHelper.cos(h * 0.6662F) * 0.05F;
			this.leftAttackingArm.roll = (float) (-Math.PI * 3.0 / 4.0);
			this.leftAttackingArm.yaw = 0.0F;
		}

		boolean bl = state == IllagerEntity.State.CROSSED;
		this.arms.visible = bl;
		this.leftAttackingArm.visible = !bl;
		this.rightAttackingArm.visible = !bl;
	}

	private ModelPart getAttackingArm(Arm arm) {
		return arm == Arm.LEFT ? this.leftAttackingArm : this.rightAttackingArm;
	}

	public ModelPart getHat() {
		return this.hat;
	}

	@Override
	public ModelPart getHead() {
		return this.head;
	}

	@Override
	public void setArmAngle(Arm arm, MatrixStack matrices) {
		this.getAttackingArm(arm).rotate(matrices);
	}
}
