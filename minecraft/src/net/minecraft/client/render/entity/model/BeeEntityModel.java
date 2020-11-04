package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5605;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelUtil;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BeeEntityModel<T extends BeeEntity> extends AnimalModel<T> {
	private final ModelPart body;
	private final ModelPart rightWing;
	private final ModelPart leftWing;
	private final ModelPart frontLegs;
	private final ModelPart middleLegs;
	private final ModelPart backLegs;
	private final ModelPart stinger;
	private final ModelPart leftAntenna;
	private final ModelPart rightAntenna;
	private float bodyPitch;

	public BeeEntityModel(ModelPart modelPart) {
		super(false, 24.0F, 0.0F);
		this.body = modelPart.method_32086("bone");
		ModelPart modelPart2 = this.body.method_32086("body");
		this.stinger = modelPart2.method_32086("stinger");
		this.leftAntenna = modelPart2.method_32086("left_antenna");
		this.rightAntenna = modelPart2.method_32086("right_antenna");
		this.rightWing = this.body.method_32086("right_wing");
		this.leftWing = this.body.method_32086("left_wing");
		this.frontLegs = this.body.method_32086("front_legs");
		this.middleLegs = this.body.method_32086("middle_legs");
		this.backLegs = this.body.method_32086("back_legs");
	}

	public static class_5607 method_31981() {
		float f = 19.0F;
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		class_5610 lv3 = lv2.method_32117("bone", class_5606.method_32108(), class_5603.method_32090(0.0F, 19.0F, 0.0F));
		class_5610 lv4 = lv3.method_32117(
			"body", class_5606.method_32108().method_32101(0, 0).method_32097(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F), class_5603.field_27701
		);
		lv4.method_32117("stinger", class_5606.method_32108().method_32101(26, 7).method_32097(0.0F, -1.0F, 5.0F, 0.0F, 1.0F, 2.0F), class_5603.field_27701);
		lv4.method_32117(
			"left_antenna", class_5606.method_32108().method_32101(2, 0).method_32097(1.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F), class_5603.method_32090(0.0F, -2.0F, -5.0F)
		);
		lv4.method_32117(
			"right_antenna",
			class_5606.method_32108().method_32101(2, 3).method_32097(-2.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F),
			class_5603.method_32090(0.0F, -2.0F, -5.0F)
		);
		class_5605 lv5 = new class_5605(0.001F);
		lv3.method_32117(
			"right_wing",
			class_5606.method_32108().method_32101(0, 18).method_32098(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, lv5),
			class_5603.method_32091(-1.5F, -4.0F, -3.0F, 0.0F, -0.2618F, 0.0F)
		);
		lv3.method_32117(
			"left_wing",
			class_5606.method_32108().method_32101(0, 18).method_32096().method_32098(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, lv5),
			class_5603.method_32091(1.5F, -4.0F, -3.0F, 0.0F, 0.2618F, 0.0F)
		);
		lv3.method_32117(
			"front_legs", class_5606.method_32108().method_32104("front_legs", -5.0F, 0.0F, 0.0F, 7, 2, 0, 26, 1), class_5603.method_32090(1.5F, 3.0F, -2.0F)
		);
		lv3.method_32117(
			"middle_legs", class_5606.method_32108().method_32104("middle_legs", -5.0F, 0.0F, 0.0F, 7, 2, 0, 26, 3), class_5603.method_32090(1.5F, 3.0F, 0.0F)
		);
		lv3.method_32117(
			"back_legs", class_5606.method_32108().method_32104("back_legs", -5.0F, 0.0F, 0.0F, 7, 2, 0, 26, 5), class_5603.method_32090(1.5F, 3.0F, 2.0F)
		);
		return class_5607.method_32110(lv, 64, 64);
	}

	public void animateModel(T beeEntity, float f, float g, float h) {
		super.animateModel(beeEntity, f, g, h);
		this.bodyPitch = beeEntity.getBodyPitch(h);
		this.stinger.visible = !beeEntity.hasStung();
	}

	public void setAngles(T beeEntity, float f, float g, float h, float i, float j) {
		this.rightWing.pitch = 0.0F;
		this.leftAntenna.pitch = 0.0F;
		this.rightAntenna.pitch = 0.0F;
		this.body.pitch = 0.0F;
		boolean bl = beeEntity.isOnGround() && beeEntity.getVelocity().lengthSquared() < 1.0E-7;
		if (bl) {
			this.rightWing.yaw = -0.2618F;
			this.rightWing.roll = 0.0F;
			this.leftWing.pitch = 0.0F;
			this.leftWing.yaw = 0.2618F;
			this.leftWing.roll = 0.0F;
			this.frontLegs.pitch = 0.0F;
			this.middleLegs.pitch = 0.0F;
			this.backLegs.pitch = 0.0F;
		} else {
			float k = h * 2.1F;
			this.rightWing.yaw = 0.0F;
			this.rightWing.roll = MathHelper.cos(k) * (float) Math.PI * 0.15F;
			this.leftWing.pitch = this.rightWing.pitch;
			this.leftWing.yaw = this.rightWing.yaw;
			this.leftWing.roll = -this.rightWing.roll;
			this.frontLegs.pitch = (float) (Math.PI / 4);
			this.middleLegs.pitch = (float) (Math.PI / 4);
			this.backLegs.pitch = (float) (Math.PI / 4);
			this.body.pitch = 0.0F;
			this.body.yaw = 0.0F;
			this.body.roll = 0.0F;
		}

		if (!beeEntity.hasAngerTime()) {
			this.body.pitch = 0.0F;
			this.body.yaw = 0.0F;
			this.body.roll = 0.0F;
			if (!bl) {
				float k = MathHelper.cos(h * 0.18F);
				this.body.pitch = 0.1F + k * (float) Math.PI * 0.025F;
				this.leftAntenna.pitch = k * (float) Math.PI * 0.03F;
				this.rightAntenna.pitch = k * (float) Math.PI * 0.03F;
				this.frontLegs.pitch = -k * (float) Math.PI * 0.1F + (float) (Math.PI / 8);
				this.backLegs.pitch = -k * (float) Math.PI * 0.05F + (float) (Math.PI / 4);
				this.body.pivotY = 19.0F - MathHelper.cos(h * 0.18F) * 0.9F;
			}
		}

		if (this.bodyPitch > 0.0F) {
			this.body.pitch = ModelUtil.interpolateAngle(this.body.pitch, 3.0915928F, this.bodyPitch);
		}
	}

	@Override
	protected Iterable<ModelPart> getHeadParts() {
		return ImmutableList.<ModelPart>of();
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.<ModelPart>of(this.body);
	}
}
