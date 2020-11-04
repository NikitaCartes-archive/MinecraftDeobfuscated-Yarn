package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5597;
import net.minecraft.class_5603;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BatEntityModel extends class_5597<BatEntity> {
	private final ModelPart field_27393;
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart rightWing;
	private final ModelPart leftWing;
	private final ModelPart rightWingTip;
	private final ModelPart leftWingTip;

	public BatEntityModel(ModelPart modelPart) {
		this.field_27393 = modelPart;
		this.head = modelPart.method_32086("head");
		this.body = modelPart.method_32086("body");
		this.rightWing = this.body.method_32086("right_wing");
		this.rightWingTip = this.rightWing.method_32086("right_wing_tip");
		this.leftWing = this.body.method_32086("left_wing");
		this.leftWingTip = this.leftWing.method_32086("left_wing_tip");
	}

	public static class_5607 method_31980() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		class_5610 lv3 = lv2.method_32117(
			"head", class_5606.method_32108().method_32101(0, 0).method_32097(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F), class_5603.field_27701
		);
		lv3.method_32117("right_ear", class_5606.method_32108().method_32101(24, 0).method_32097(-4.0F, -6.0F, -2.0F, 3.0F, 4.0F, 1.0F), class_5603.field_27701);
		lv3.method_32117(
			"left_ear", class_5606.method_32108().method_32101(24, 0).method_32096().method_32097(1.0F, -6.0F, -2.0F, 3.0F, 4.0F, 1.0F), class_5603.field_27701
		);
		class_5610 lv4 = lv2.method_32117(
			"body",
			class_5606.method_32108()
				.method_32101(0, 16)
				.method_32097(-3.0F, 4.0F, -3.0F, 6.0F, 12.0F, 6.0F)
				.method_32101(0, 34)
				.method_32097(-5.0F, 16.0F, 0.0F, 10.0F, 6.0F, 1.0F),
			class_5603.field_27701
		);
		class_5610 lv5 = lv4.method_32117(
			"right_wing", class_5606.method_32108().method_32101(42, 0).method_32097(-12.0F, 1.0F, 1.5F, 10.0F, 16.0F, 1.0F), class_5603.field_27701
		);
		lv5.method_32117(
			"right_wing_tip",
			class_5606.method_32108().method_32101(24, 16).method_32097(-8.0F, 1.0F, 0.0F, 8.0F, 12.0F, 1.0F),
			class_5603.method_32090(-12.0F, 1.0F, 1.5F)
		);
		class_5610 lv6 = lv4.method_32117(
			"left_wing", class_5606.method_32108().method_32101(42, 0).method_32096().method_32097(2.0F, 1.0F, 1.5F, 10.0F, 16.0F, 1.0F), class_5603.field_27701
		);
		lv6.method_32117(
			"left_wing_tip",
			class_5606.method_32108().method_32101(24, 16).method_32096().method_32097(0.0F, 1.0F, 0.0F, 8.0F, 12.0F, 1.0F),
			class_5603.method_32090(12.0F, 1.0F, 1.5F)
		);
		return class_5607.method_32110(lv, 64, 64);
	}

	@Override
	public ModelPart method_32008() {
		return this.field_27393;
	}

	public void setAngles(BatEntity batEntity, float f, float g, float h, float i, float j) {
		if (batEntity.isRoosting()) {
			this.head.pitch = j * (float) (Math.PI / 180.0);
			this.head.yaw = (float) Math.PI - i * (float) (Math.PI / 180.0);
			this.head.roll = (float) Math.PI;
			this.head.setPivot(0.0F, -2.0F, 0.0F);
			this.rightWing.setPivot(-3.0F, 0.0F, 3.0F);
			this.leftWing.setPivot(3.0F, 0.0F, 3.0F);
			this.body.pitch = (float) Math.PI;
			this.rightWing.pitch = (float) (-Math.PI / 20);
			this.rightWing.yaw = (float) (-Math.PI * 2.0 / 5.0);
			this.rightWingTip.yaw = -1.7278761F;
			this.leftWing.pitch = this.rightWing.pitch;
			this.leftWing.yaw = -this.rightWing.yaw;
			this.leftWingTip.yaw = -this.rightWingTip.yaw;
		} else {
			this.head.pitch = j * (float) (Math.PI / 180.0);
			this.head.yaw = i * (float) (Math.PI / 180.0);
			this.head.roll = 0.0F;
			this.head.setPivot(0.0F, 0.0F, 0.0F);
			this.rightWing.setPivot(0.0F, 0.0F, 0.0F);
			this.leftWing.setPivot(0.0F, 0.0F, 0.0F);
			this.body.pitch = (float) (Math.PI / 4) + MathHelper.cos(h * 0.1F) * 0.15F;
			this.body.yaw = 0.0F;
			this.rightWing.yaw = MathHelper.cos(h * 1.3F) * (float) Math.PI * 0.25F;
			this.leftWing.yaw = -this.rightWing.yaw;
			this.rightWingTip.yaw = this.rightWing.yaw * 0.5F;
			this.leftWingTip.yaw = -this.rightWing.yaw * 0.5F;
		}
	}
}
