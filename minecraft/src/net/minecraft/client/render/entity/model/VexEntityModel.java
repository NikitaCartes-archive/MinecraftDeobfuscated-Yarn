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
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class VexEntityModel extends BipedEntityModel<VexEntity> {
	private final ModelPart leftWing;
	private final ModelPart rightWing;

	public VexEntityModel(ModelPart modelPart) {
		super(modelPart);
		this.leftLeg.visible = false;
		this.helmet.visible = false;
		this.rightWing = modelPart.method_32086("right_wing");
		this.leftWing = modelPart.method_32086("left_wing");
	}

	public static class_5607 method_32063() {
		class_5609 lv = BipedEntityModel.method_32011(class_5605.field_27715, 0.0F);
		class_5610 lv2 = lv.method_32111();
		lv2.method_32117(
			"right_leg", class_5606.method_32108().method_32101(32, 0).method_32097(-1.0F, -1.0F, -2.0F, 6.0F, 10.0F, 4.0F), class_5603.method_32090(-1.9F, 12.0F, 0.0F)
		);
		lv2.method_32117("right_wing", class_5606.method_32108().method_32101(0, 32).method_32097(-20.0F, 0.0F, 0.0F, 20.0F, 12.0F, 1.0F), class_5603.field_27701);
		lv2.method_32117(
			"left_wing", class_5606.method_32108().method_32101(0, 32).method_32096().method_32097(0.0F, 0.0F, 0.0F, 20.0F, 12.0F, 1.0F), class_5603.field_27701
		);
		return class_5607.method_32110(lv, 64, 64);
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return Iterables.concat(super.getBodyParts(), ImmutableList.of(this.rightWing, this.leftWing));
	}

	public void setAngles(VexEntity vexEntity, float f, float g, float h, float i, float j) {
		super.setAngles(vexEntity, f, g, h, i, j);
		if (vexEntity.isCharging()) {
			if (vexEntity.getMainHandStack().isEmpty()) {
				this.rightArm.pitch = (float) (Math.PI * 3.0 / 2.0);
				this.field_27433.pitch = (float) (Math.PI * 3.0 / 2.0);
			} else if (vexEntity.getMainArm() == Arm.RIGHT) {
				this.rightArm.pitch = 3.7699115F;
			} else {
				this.field_27433.pitch = 3.7699115F;
			}
		}

		this.rightLeg.pitch += (float) (Math.PI / 5);
		this.rightWing.pivotZ = 2.0F;
		this.leftWing.pivotZ = 2.0F;
		this.rightWing.pivotY = 1.0F;
		this.leftWing.pivotY = 1.0F;
		this.rightWing.yaw = 0.47123894F + MathHelper.cos(h * 0.8F) * (float) Math.PI * 0.05F;
		this.leftWing.yaw = -this.rightWing.yaw;
		this.leftWing.roll = -0.47123894F;
		this.leftWing.pitch = 0.47123894F;
		this.rightWing.pitch = 0.47123894F;
		this.rightWing.roll = 0.47123894F;
	}
}
