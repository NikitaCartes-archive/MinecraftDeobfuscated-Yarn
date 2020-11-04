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
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class EndermanEntityModel<T extends LivingEntity> extends BipedEntityModel<T> {
	public boolean carryingBlock;
	public boolean angry;

	public EndermanEntityModel(ModelPart modelPart) {
		super(modelPart);
	}

	public static class_5607 method_31995() {
		float f = -14.0F;
		class_5609 lv = BipedEntityModel.method_32011(class_5605.field_27715, -14.0F);
		class_5610 lv2 = lv.method_32111();
		class_5603 lv3 = class_5603.method_32090(0.0F, -13.0F, 0.0F);
		lv2.method_32117("hat", class_5606.method_32108().method_32101(0, 16).method_32098(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new class_5605(-0.5F)), lv3);
		lv2.method_32117("head", class_5606.method_32108().method_32101(0, 0).method_32097(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), lv3);
		lv2.method_32117(
			"body", class_5606.method_32108().method_32101(32, 16).method_32097(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F), class_5603.method_32090(0.0F, -14.0F, 0.0F)
		);
		lv2.method_32117(
			"right_arm",
			class_5606.method_32108().method_32101(56, 0).method_32097(-1.0F, -2.0F, -1.0F, 2.0F, 30.0F, 2.0F),
			class_5603.method_32090(-5.0F, -12.0F, 0.0F)
		);
		lv2.method_32117(
			"left_arm",
			class_5606.method_32108().method_32101(56, 0).method_32096().method_32097(-1.0F, -2.0F, -1.0F, 2.0F, 30.0F, 2.0F),
			class_5603.method_32090(5.0F, -12.0F, 0.0F)
		);
		lv2.method_32117(
			"right_leg", class_5606.method_32108().method_32101(56, 0).method_32097(-1.0F, 0.0F, -1.0F, 2.0F, 30.0F, 2.0F), class_5603.method_32090(-2.0F, -5.0F, 0.0F)
		);
		lv2.method_32117(
			"left_leg",
			class_5606.method_32108().method_32101(56, 0).method_32096().method_32097(-1.0F, 0.0F, -1.0F, 2.0F, 30.0F, 2.0F),
			class_5603.method_32090(2.0F, -5.0F, 0.0F)
		);
		return class_5607.method_32110(lv, 64, 32);
	}

	@Override
	public void setAngles(T livingEntity, float f, float g, float h, float i, float j) {
		super.setAngles(livingEntity, f, g, h, i, j);
		this.head.visible = true;
		int k = -14;
		this.torso.pitch = 0.0F;
		this.torso.pivotY = -14.0F;
		this.torso.pivotZ = -0.0F;
		this.rightLeg.pitch -= 0.0F;
		this.leftLeg.pitch -= 0.0F;
		this.rightArm.pitch = (float)((double)this.rightArm.pitch * 0.5);
		this.field_27433.pitch = (float)((double)this.field_27433.pitch * 0.5);
		this.rightLeg.pitch = (float)((double)this.rightLeg.pitch * 0.5);
		this.leftLeg.pitch = (float)((double)this.leftLeg.pitch * 0.5);
		float l = 0.4F;
		if (this.rightArm.pitch > 0.4F) {
			this.rightArm.pitch = 0.4F;
		}

		if (this.field_27433.pitch > 0.4F) {
			this.field_27433.pitch = 0.4F;
		}

		if (this.rightArm.pitch < -0.4F) {
			this.rightArm.pitch = -0.4F;
		}

		if (this.field_27433.pitch < -0.4F) {
			this.field_27433.pitch = -0.4F;
		}

		if (this.rightLeg.pitch > 0.4F) {
			this.rightLeg.pitch = 0.4F;
		}

		if (this.leftLeg.pitch > 0.4F) {
			this.leftLeg.pitch = 0.4F;
		}

		if (this.rightLeg.pitch < -0.4F) {
			this.rightLeg.pitch = -0.4F;
		}

		if (this.leftLeg.pitch < -0.4F) {
			this.leftLeg.pitch = -0.4F;
		}

		if (this.carryingBlock) {
			this.rightArm.pitch = -0.5F;
			this.field_27433.pitch = -0.5F;
			this.rightArm.roll = 0.05F;
			this.field_27433.roll = -0.05F;
		}

		this.rightLeg.pivotZ = 0.0F;
		this.leftLeg.pivotZ = 0.0F;
		this.rightLeg.pivotY = -5.0F;
		this.leftLeg.pivotY = -5.0F;
		this.head.pivotZ = -0.0F;
		this.head.pivotY = -13.0F;
		this.helmet.pivotX = this.head.pivotX;
		this.helmet.pivotY = this.head.pivotY;
		this.helmet.pivotZ = this.head.pivotZ;
		this.helmet.pitch = this.head.pitch;
		this.helmet.yaw = this.head.yaw;
		this.helmet.roll = this.head.roll;
		if (this.angry) {
			float m = 1.0F;
			this.head.pivotY -= 5.0F;
		}

		int n = -14;
		this.rightArm.setPivot(-5.0F, -12.0F, 0.0F);
		this.field_27433.setPivot(5.0F, -12.0F, 0.0F);
	}
}
