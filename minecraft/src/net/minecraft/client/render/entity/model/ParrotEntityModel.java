package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ParrotEntityModel extends CompositeEntityModel<ParrotEntity> {
	private final ModelPart torso;
	private final ModelPart tail;
	private final ModelPart leftWing;
	private final ModelPart rightWing;
	private final ModelPart head;
	private final ModelPart forehead;
	private final ModelPart innerBeak;
	private final ModelPart outerBeak;
	private final ModelPart headFeathers;
	private final ModelPart leftLeg;
	private final ModelPart rightLeg;

	public ParrotEntityModel() {
		this.textureWidth = 32;
		this.textureHeight = 32;
		this.torso = new ModelPart(this, 2, 8);
		this.torso.addCuboid(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F);
		this.torso.setPivot(0.0F, 16.5F, -3.0F);
		this.tail = new ModelPart(this, 22, 1);
		this.tail.addCuboid(-1.5F, -1.0F, -1.0F, 3.0F, 4.0F, 1.0F);
		this.tail.setPivot(0.0F, 21.07F, 1.16F);
		this.leftWing = new ModelPart(this, 19, 8);
		this.leftWing.addCuboid(-0.5F, 0.0F, -1.5F, 1.0F, 5.0F, 3.0F);
		this.leftWing.setPivot(1.5F, 16.94F, -2.76F);
		this.rightWing = new ModelPart(this, 19, 8);
		this.rightWing.addCuboid(-0.5F, 0.0F, -1.5F, 1.0F, 5.0F, 3.0F);
		this.rightWing.setPivot(-1.5F, 16.94F, -2.76F);
		this.head = new ModelPart(this, 2, 2);
		this.head.addCuboid(-1.0F, -1.5F, -1.0F, 2.0F, 3.0F, 2.0F);
		this.head.setPivot(0.0F, 15.69F, -2.76F);
		this.forehead = new ModelPart(this, 10, 0);
		this.forehead.addCuboid(-1.0F, -0.5F, -2.0F, 2.0F, 1.0F, 4.0F);
		this.forehead.setPivot(0.0F, -2.0F, -1.0F);
		this.head.addChild(this.forehead);
		this.innerBeak = new ModelPart(this, 11, 7);
		this.innerBeak.addCuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F);
		this.innerBeak.setPivot(0.0F, -0.5F, -1.5F);
		this.head.addChild(this.innerBeak);
		this.outerBeak = new ModelPart(this, 16, 7);
		this.outerBeak.addCuboid(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F);
		this.outerBeak.setPivot(0.0F, -1.75F, -2.45F);
		this.head.addChild(this.outerBeak);
		this.headFeathers = new ModelPart(this, 2, 18);
		this.headFeathers.addCuboid(0.0F, -4.0F, -2.0F, 0.0F, 5.0F, 4.0F);
		this.headFeathers.setPivot(0.0F, -2.15F, 0.15F);
		this.head.addChild(this.headFeathers);
		this.leftLeg = new ModelPart(this, 14, 18);
		this.leftLeg.addCuboid(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F);
		this.leftLeg.setPivot(1.0F, 22.0F, -1.05F);
		this.rightLeg = new ModelPart(this, 14, 18);
		this.rightLeg.addCuboid(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F);
		this.rightLeg.setPivot(-1.0F, 22.0F, -1.05F);
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.<ModelPart>of(this.torso, this.leftWing, this.rightWing, this.tail, this.head, this.leftLeg, this.rightLeg);
	}

	public void setAngles(ParrotEntity parrotEntity, float f, float g, float h, float i, float j) {
		this.method_17111(getPose(parrotEntity), parrotEntity.age, f, g, h, i, j);
	}

	public void animateModel(ParrotEntity parrotEntity, float f, float g, float h) {
		this.method_17110(getPose(parrotEntity));
	}

	public void method_17106(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, float f, float g, float h, float k, int l) {
		this.method_17110(ParrotEntityModel.Pose.ON_SHOULDER);
		this.method_17111(ParrotEntityModel.Pose.ON_SHOULDER, l, f, g, 0.0F, h, k);
		this.getParts().forEach(modelPart -> modelPart.render(matrixStack, vertexConsumer, i, j));
	}

	private void method_17111(ParrotEntityModel.Pose pose, int age, float limbAngle, float limbDistance, float age, float headYaw, float headPitch) {
		this.head.pitch = headPitch * (float) (Math.PI / 180.0);
		this.head.yaw = headYaw * (float) (Math.PI / 180.0);
		this.head.roll = 0.0F;
		this.head.pivotX = 0.0F;
		this.torso.pivotX = 0.0F;
		this.tail.pivotX = 0.0F;
		this.rightWing.pivotX = -1.5F;
		this.leftWing.pivotX = 1.5F;
		switch (pose) {
			case SITTING:
				break;
			case PARTY:
				float f = MathHelper.cos((float)age);
				float g = MathHelper.sin((float)age);
				this.head.pivotX = f;
				this.head.pivotY = 15.69F + g;
				this.head.pitch = 0.0F;
				this.head.yaw = 0.0F;
				this.head.roll = MathHelper.sin((float)age) * 0.4F;
				this.torso.pivotX = f;
				this.torso.pivotY = 16.5F + g;
				this.leftWing.roll = -0.0873F - age;
				this.leftWing.pivotX = 1.5F + f;
				this.leftWing.pivotY = 16.94F + g;
				this.rightWing.roll = 0.0873F + age;
				this.rightWing.pivotX = -1.5F + f;
				this.rightWing.pivotY = 16.94F + g;
				this.tail.pivotX = f;
				this.tail.pivotY = 21.07F + g;
				break;
			case STANDING:
				this.leftLeg.pitch = this.leftLeg.pitch + MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
				this.rightLeg.pitch = this.rightLeg.pitch + MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.4F * limbDistance;
			case FLYING:
			case ON_SHOULDER:
			default:
				float h = age * 0.3F;
				this.head.pivotY = 15.69F + h;
				this.tail.pitch = 1.015F + MathHelper.cos(limbAngle * 0.6662F) * 0.3F * limbDistance;
				this.tail.pivotY = 21.07F + h;
				this.torso.pivotY = 16.5F + h;
				this.leftWing.roll = -0.0873F - age;
				this.leftWing.pivotY = 16.94F + h;
				this.rightWing.roll = 0.0873F + age;
				this.rightWing.pivotY = 16.94F + h;
				this.leftLeg.pivotY = 22.0F + h;
				this.rightLeg.pivotY = 22.0F + h;
		}
	}

	private void method_17110(ParrotEntityModel.Pose pose) {
		this.headFeathers.pitch = -0.2214F;
		this.torso.pitch = 0.4937F;
		this.leftWing.pitch = -0.6981F;
		this.leftWing.yaw = (float) -Math.PI;
		this.rightWing.pitch = -0.6981F;
		this.rightWing.yaw = (float) -Math.PI;
		this.leftLeg.pitch = -0.0299F;
		this.rightLeg.pitch = -0.0299F;
		this.leftLeg.pivotY = 22.0F;
		this.rightLeg.pivotY = 22.0F;
		this.leftLeg.roll = 0.0F;
		this.rightLeg.roll = 0.0F;
		switch (pose) {
			case SITTING:
				float f = 1.9F;
				this.head.pivotY = 17.59F;
				this.tail.pitch = 1.5388988F;
				this.tail.pivotY = 22.97F;
				this.torso.pivotY = 18.4F;
				this.leftWing.roll = -0.0873F;
				this.leftWing.pivotY = 18.84F;
				this.rightWing.roll = 0.0873F;
				this.rightWing.pivotY = 18.84F;
				this.leftLeg.pivotY++;
				this.rightLeg.pivotY++;
				this.leftLeg.pitch++;
				this.rightLeg.pitch++;
				break;
			case PARTY:
				this.leftLeg.roll = (float) (-Math.PI / 9);
				this.rightLeg.roll = (float) (Math.PI / 9);
			case STANDING:
			case ON_SHOULDER:
			default:
				break;
			case FLYING:
				this.leftLeg.pitch += (float) (Math.PI * 2.0 / 9.0);
				this.rightLeg.pitch += (float) (Math.PI * 2.0 / 9.0);
		}
	}

	private static ParrotEntityModel.Pose getPose(ParrotEntity parrotEntity) {
		if (parrotEntity.getSongPlaying()) {
			return ParrotEntityModel.Pose.PARTY;
		} else if (parrotEntity.isSitting()) {
			return ParrotEntityModel.Pose.SITTING;
		} else {
			return parrotEntity.isInAir() ? ParrotEntityModel.Pose.FLYING : ParrotEntityModel.Pose.STANDING;
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
