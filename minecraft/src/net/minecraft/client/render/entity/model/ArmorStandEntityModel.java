package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
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
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ArmorStandEntityModel extends ArmorStandArmorEntityModel {
	private final ModelPart field_27391;
	private final ModelPart field_27392;
	private final ModelPart hip;
	private final ModelPart plate;

	public ArmorStandEntityModel(ModelPart modelPart) {
		super(modelPart);
		this.field_27391 = modelPart.method_32086("right_body_stick");
		this.field_27392 = modelPart.method_32086("left_body_stick");
		this.hip = modelPart.method_32086("shoulder_stick");
		this.plate = modelPart.method_32086("base_plate");
		this.helmet.visible = false;
	}

	public static class_5607 method_31979() {
		class_5609 lv = BipedEntityModel.method_32011(class_5605.field_27715, 0.0F);
		class_5610 lv2 = lv.method_32111();
		lv2.method_32117("head", class_5606.method_32108().method_32101(0, 0).method_32097(-1.0F, -7.0F, -1.0F, 2.0F, 7.0F, 2.0F), class_5603.field_27701);
		lv2.method_32117("body", class_5606.method_32108().method_32101(0, 26).method_32097(-6.0F, 0.0F, -1.5F, 12.0F, 3.0F, 3.0F), class_5603.field_27701);
		lv2.method_32117(
			"right_arm", class_5606.method_32108().method_32101(24, 0).method_32097(-2.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F), class_5603.method_32090(-5.0F, 2.0F, 0.0F)
		);
		lv2.method_32117(
			"left_arm",
			class_5606.method_32108().method_32101(32, 16).method_32096().method_32097(0.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F),
			class_5603.method_32090(5.0F, 2.0F, 0.0F)
		);
		lv2.method_32117(
			"right_leg", class_5606.method_32108().method_32101(8, 0).method_32097(-1.0F, 0.0F, -1.0F, 2.0F, 11.0F, 2.0F), class_5603.method_32090(-1.9F, 12.0F, 0.0F)
		);
		lv2.method_32117(
			"left_leg",
			class_5606.method_32108().method_32101(40, 16).method_32096().method_32097(-1.0F, 0.0F, -1.0F, 2.0F, 11.0F, 2.0F),
			class_5603.method_32090(1.9F, 12.0F, 0.0F)
		);
		lv2.method_32117("right_body_stick", class_5606.method_32108().method_32101(16, 0).method_32097(-3.0F, 3.0F, -1.0F, 2.0F, 7.0F, 2.0F), class_5603.field_27701);
		lv2.method_32117("left_body_stick", class_5606.method_32108().method_32101(48, 16).method_32097(1.0F, 3.0F, -1.0F, 2.0F, 7.0F, 2.0F), class_5603.field_27701);
		lv2.method_32117("shoulder_stick", class_5606.method_32108().method_32101(0, 48).method_32097(-4.0F, 10.0F, -1.0F, 8.0F, 2.0F, 2.0F), class_5603.field_27701);
		lv2.method_32117(
			"base_plate",
			class_5606.method_32108().method_32101(0, 32).method_32097(-6.0F, 11.0F, -6.0F, 12.0F, 1.0F, 12.0F),
			class_5603.method_32090(0.0F, 12.0F, 0.0F)
		);
		return class_5607.method_32110(lv, 64, 64);
	}

	public void animateModel(ArmorStandEntity armorStandEntity, float f, float g, float h) {
		this.plate.pitch = 0.0F;
		this.plate.yaw = (float) (Math.PI / 180.0) * -MathHelper.lerpAngleDegrees(h, armorStandEntity.prevYaw, armorStandEntity.yaw);
		this.plate.roll = 0.0F;
	}

	@Override
	public void setAngles(ArmorStandEntity armorStandEntity, float f, float g, float h, float i, float j) {
		super.setAngles(armorStandEntity, f, g, h, i, j);
		this.field_27433.visible = armorStandEntity.shouldShowArms();
		this.rightArm.visible = armorStandEntity.shouldShowArms();
		this.plate.visible = !armorStandEntity.shouldHideBasePlate();
		this.field_27391.pitch = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getPitch();
		this.field_27391.yaw = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getYaw();
		this.field_27391.roll = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getRoll();
		this.field_27392.pitch = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getPitch();
		this.field_27392.yaw = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getYaw();
		this.field_27392.roll = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getRoll();
		this.hip.pitch = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getPitch();
		this.hip.yaw = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getYaw();
		this.hip.roll = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getRoll();
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return Iterables.concat(super.getBodyParts(), ImmutableList.of(this.field_27391, this.field_27392, this.hip, this.plate));
	}

	@Override
	public void setArmAngle(Arm arm, MatrixStack matrices) {
		ModelPart modelPart = this.getArm(arm);
		boolean bl = modelPart.visible;
		modelPart.visible = true;
		super.setArmAngle(arm, matrices);
		modelPart.visible = bl;
	}
}
