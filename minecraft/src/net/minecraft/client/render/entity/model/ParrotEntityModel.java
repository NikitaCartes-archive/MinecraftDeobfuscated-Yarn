package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ParrotEntityModel extends SinglePartEntityModel<ParrotEntity> {
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
		this.torso = modelPart.getChild("body");
		this.tail = modelPart.getChild("tail");
		this.field_27459 = modelPart.getChild("left_wing");
		this.field_27460 = modelPart.getChild("right_wing");
		this.head = modelPart.getChild("head");
		this.headFeathers = this.head.getChild("feather");
		this.field_27461 = modelPart.getChild("left_leg");
		this.field_27462 = modelPart.getChild("right_leg");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("body", ModelPartBuilder.create().uv(2, 8).cuboid(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F), ModelTransform.pivot(0.0F, 16.5F, -3.0F));
		modelPartData.addChild("tail", ModelPartBuilder.create().uv(22, 1).cuboid(-1.5F, -1.0F, -1.0F, 3.0F, 4.0F, 1.0F), ModelTransform.pivot(0.0F, 21.07F, 1.16F));
		modelPartData.addChild(
			"left_wing", ModelPartBuilder.create().uv(19, 8).cuboid(-0.5F, 0.0F, -1.5F, 1.0F, 5.0F, 3.0F), ModelTransform.pivot(1.5F, 16.94F, -2.76F)
		);
		modelPartData.addChild(
			"right_wing", ModelPartBuilder.create().uv(19, 8).cuboid(-0.5F, 0.0F, -1.5F, 1.0F, 5.0F, 3.0F), ModelTransform.pivot(-1.5F, 16.94F, -2.76F)
		);
		ModelPartData modelPartData2 = modelPartData.addChild(
			"head", ModelPartBuilder.create().uv(2, 2).cuboid(-1.0F, -1.5F, -1.0F, 2.0F, 3.0F, 2.0F), ModelTransform.pivot(0.0F, 15.69F, -2.76F)
		);
		modelPartData2.addChild("head2", ModelPartBuilder.create().uv(10, 0).cuboid(-1.0F, -0.5F, -2.0F, 2.0F, 1.0F, 4.0F), ModelTransform.pivot(0.0F, -2.0F, -1.0F));
		modelPartData2.addChild("beak1", ModelPartBuilder.create().uv(11, 7).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F), ModelTransform.pivot(0.0F, -0.5F, -1.5F));
		modelPartData2.addChild("beak2", ModelPartBuilder.create().uv(16, 7).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F), ModelTransform.pivot(0.0F, -1.75F, -2.45F));
		modelPartData2.addChild(
			"feather", ModelPartBuilder.create().uv(2, 18).cuboid(0.0F, -4.0F, -2.0F, 0.0F, 5.0F, 4.0F), ModelTransform.pivot(0.0F, -2.15F, 0.15F)
		);
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(14, 18).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F);
		modelPartData.addChild("left_leg", modelPartBuilder, ModelTransform.pivot(1.0F, 22.0F, -1.05F));
		modelPartData.addChild("right_leg", modelPartBuilder, ModelTransform.pivot(-1.0F, 22.0F, -1.05F));
		return TexturedModelData.of(modelData, 32, 32);
	}

	@Override
	public ModelPart getPart() {
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
