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
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ParrotEntityModel extends class_5597<ParrotEntity> {
	private final ModelPart field_27458;
	private final ModelPart torso;
	private final ModelPart tail;
	private final ModelPart field_27459;
	private final ModelPart field_27460;
	private final ModelPart head;
	private final ModelPart headFeathers;
	private final ModelPart field_27461;
	private final ModelPart field_27462;

	public ParrotEntityModel(ModelPart modelPart) {
		this.field_27458 = modelPart;
		this.torso = modelPart.method_32086("body");
		this.tail = modelPart.method_32086("tail");
		this.field_27459 = modelPart.method_32086("left_wing");
		this.field_27460 = modelPart.method_32086("right_wing");
		this.head = modelPart.method_32086("head");
		this.headFeathers = this.head.method_32086("feather");
		this.field_27461 = modelPart.method_32086("left_leg");
		this.field_27462 = modelPart.method_32086("right_leg");
	}

	public static class_5607 method_32023() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		lv2.method_32117(
			"body", class_5606.method_32108().method_32101(2, 8).method_32097(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F), class_5603.method_32090(0.0F, 16.5F, -3.0F)
		);
		lv2.method_32117(
			"tail", class_5606.method_32108().method_32101(22, 1).method_32097(-1.5F, -1.0F, -1.0F, 3.0F, 4.0F, 1.0F), class_5603.method_32090(0.0F, 21.07F, 1.16F)
		);
		lv2.method_32117(
			"left_wing", class_5606.method_32108().method_32101(19, 8).method_32097(-0.5F, 0.0F, -1.5F, 1.0F, 5.0F, 3.0F), class_5603.method_32090(1.5F, 16.94F, -2.76F)
		);
		lv2.method_32117(
			"right_wing",
			class_5606.method_32108().method_32101(19, 8).method_32097(-0.5F, 0.0F, -1.5F, 1.0F, 5.0F, 3.0F),
			class_5603.method_32090(-1.5F, 16.94F, -2.76F)
		);
		class_5610 lv3 = lv2.method_32117(
			"head", class_5606.method_32108().method_32101(2, 2).method_32097(-1.0F, -1.5F, -1.0F, 2.0F, 3.0F, 2.0F), class_5603.method_32090(0.0F, 15.69F, -2.76F)
		);
		lv3.method_32117(
			"head2", class_5606.method_32108().method_32101(10, 0).method_32097(-1.0F, -0.5F, -2.0F, 2.0F, 1.0F, 4.0F), class_5603.method_32090(0.0F, -2.0F, -1.0F)
		);
		lv3.method_32117(
			"beak1", class_5606.method_32108().method_32101(11, 7).method_32097(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F), class_5603.method_32090(0.0F, -0.5F, -1.5F)
		);
		lv3.method_32117(
			"beak2", class_5606.method_32108().method_32101(16, 7).method_32097(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F), class_5603.method_32090(0.0F, -1.75F, -2.45F)
		);
		lv3.method_32117(
			"feather", class_5606.method_32108().method_32101(2, 18).method_32097(0.0F, -4.0F, -2.0F, 0.0F, 5.0F, 4.0F), class_5603.method_32090(0.0F, -2.15F, 0.15F)
		);
		class_5606 lv4 = class_5606.method_32108().method_32101(14, 18).method_32097(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F);
		lv2.method_32117("left_leg", lv4, class_5603.method_32090(1.0F, 22.0F, -1.05F));
		lv2.method_32117("right_leg", lv4, class_5603.method_32090(-1.0F, 22.0F, -1.05F));
		return class_5607.method_32110(lv, 32, 32);
	}

	@Override
	public ModelPart method_32008() {
		return this.field_27458;
	}

	public void setAngles(ParrotEntity parrotEntity, float f, float g, float h, float i, float j) {
		this.setAngles(getPose(parrotEntity), parrotEntity.age, f, g, h, i, j);
	}

	public void animateModel(ParrotEntity parrotEntity, float f, float g, float h) {
		this.animateModel(getPose(parrotEntity));
	}

	public void poseOnShoulder(
		MatrixStack matrices,
		VertexConsumer vertexConsumer,
		int light,
		int overlay,
		float limbAngle,
		float limbDistance,
		float headYaw,
		float headPitch,
		int danceAngle
	) {
		this.animateModel(ParrotEntityModel.Pose.ON_SHOULDER);
		this.setAngles(ParrotEntityModel.Pose.ON_SHOULDER, danceAngle, limbAngle, limbDistance, 0.0F, headYaw, headPitch);
		this.field_27458.render(matrices, vertexConsumer, light, overlay);
	}

	private void setAngles(ParrotEntityModel.Pose pose, int danceAngle, float limbAngle, float limbDistance, float age, float headYaw, float headPitch) {
		this.head.pitch = headPitch * (float) (Math.PI / 180.0);
		this.head.yaw = headYaw * (float) (Math.PI / 180.0);
		this.head.roll = 0.0F;
		this.head.pivotX = 0.0F;
		this.torso.pivotX = 0.0F;
		this.tail.pivotX = 0.0F;
		this.field_27460.pivotX = -1.5F;
		this.field_27459.pivotX = 1.5F;
		switch (pose) {
			case SITTING:
				break;
			case PARTY:
				float f = MathHelper.cos((float)danceAngle);
				float g = MathHelper.sin((float)danceAngle);
				this.head.pivotX = f;
				this.head.pivotY = 15.69F + g;
				this.head.pitch = 0.0F;
				this.head.yaw = 0.0F;
				this.head.roll = MathHelper.sin((float)danceAngle) * 0.4F;
				this.torso.pivotX = f;
				this.torso.pivotY = 16.5F + g;
				this.field_27459.roll = -0.0873F - age;
				this.field_27459.pivotX = 1.5F + f;
				this.field_27459.pivotY = 16.94F + g;
				this.field_27460.roll = 0.0873F + age;
				this.field_27460.pivotX = -1.5F + f;
				this.field_27460.pivotY = 16.94F + g;
				this.tail.pivotX = f;
				this.tail.pivotY = 21.07F + g;
				break;
			case STANDING:
				this.field_27461.pitch = this.field_27461.pitch + MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
				this.field_27462.pitch = this.field_27462.pitch + MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.4F * limbDistance;
			case FLYING:
			case ON_SHOULDER:
			default:
				float h = age * 0.3F;
				this.head.pivotY = 15.69F + h;
				this.tail.pitch = 1.015F + MathHelper.cos(limbAngle * 0.6662F) * 0.3F * limbDistance;
				this.tail.pivotY = 21.07F + h;
				this.torso.pivotY = 16.5F + h;
				this.field_27459.roll = -0.0873F - age;
				this.field_27459.pivotY = 16.94F + h;
				this.field_27460.roll = 0.0873F + age;
				this.field_27460.pivotY = 16.94F + h;
				this.field_27461.pivotY = 22.0F + h;
				this.field_27462.pivotY = 22.0F + h;
		}
	}

	private void animateModel(ParrotEntityModel.Pose pose) {
		this.headFeathers.pitch = -0.2214F;
		this.torso.pitch = 0.4937F;
		this.field_27459.pitch = -0.6981F;
		this.field_27459.yaw = (float) -Math.PI;
		this.field_27460.pitch = -0.6981F;
		this.field_27460.yaw = (float) -Math.PI;
		this.field_27461.pitch = -0.0299F;
		this.field_27462.pitch = -0.0299F;
		this.field_27461.pivotY = 22.0F;
		this.field_27462.pivotY = 22.0F;
		this.field_27461.roll = 0.0F;
		this.field_27462.roll = 0.0F;
		switch (pose) {
			case SITTING:
				float f = 1.9F;
				this.head.pivotY = 17.59F;
				this.tail.pitch = 1.5388988F;
				this.tail.pivotY = 22.97F;
				this.torso.pivotY = 18.4F;
				this.field_27459.roll = -0.0873F;
				this.field_27459.pivotY = 18.84F;
				this.field_27460.roll = 0.0873F;
				this.field_27460.pivotY = 18.84F;
				this.field_27461.pivotY++;
				this.field_27462.pivotY++;
				this.field_27461.pitch++;
				this.field_27462.pitch++;
				break;
			case PARTY:
				this.field_27461.roll = (float) (-Math.PI / 9);
				this.field_27462.roll = (float) (Math.PI / 9);
			case STANDING:
			case ON_SHOULDER:
			default:
				break;
			case FLYING:
				this.field_27461.pitch += (float) (Math.PI * 2.0 / 9.0);
				this.field_27462.pitch += (float) (Math.PI * 2.0 / 9.0);
		}
	}

	private static ParrotEntityModel.Pose getPose(ParrotEntity parrot) {
		if (parrot.getSongPlaying()) {
			return ParrotEntityModel.Pose.PARTY;
		} else if (parrot.isInSittingPose()) {
			return ParrotEntityModel.Pose.SITTING;
		} else {
			return parrot.isInAir() ? ParrotEntityModel.Pose.FLYING : ParrotEntityModel.Pose.STANDING;
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum Pose {
		FLYING,
		STANDING,
		SITTING,
		PARTY,
		ON_SHOULDER;
	}
}
