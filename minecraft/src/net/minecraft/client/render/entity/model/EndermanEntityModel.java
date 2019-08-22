package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class EndermanEntityModel<T extends LivingEntity> extends BipedEntityModel<T> {
	public boolean carryingBlock;
	public boolean angry;

	public EndermanEntityModel(float f) {
		super(0.0F, -14.0F, 64, 32);
		float g = -14.0F;
		this.headwear = new ModelPart(this, 0, 16);
		this.headwear.addCuboid(-4.0F, -8.0F, -4.0F, 8, 8, 8, f - 0.5F);
		this.headwear.setRotationPoint(0.0F, -14.0F, 0.0F);
		this.body = new ModelPart(this, 32, 16);
		this.body.addCuboid(-4.0F, 0.0F, -2.0F, 8, 12, 4, f);
		this.body.setRotationPoint(0.0F, -14.0F, 0.0F);
		this.rightArm = new ModelPart(this, 56, 0);
		this.rightArm.addCuboid(-1.0F, -2.0F, -1.0F, 2, 30, 2, f);
		this.rightArm.setRotationPoint(-3.0F, -12.0F, 0.0F);
		this.leftArm = new ModelPart(this, 56, 0);
		this.leftArm.mirror = true;
		this.leftArm.addCuboid(-1.0F, -2.0F, -1.0F, 2, 30, 2, f);
		this.leftArm.setRotationPoint(5.0F, -12.0F, 0.0F);
		this.rightLeg = new ModelPart(this, 56, 0);
		this.rightLeg.addCuboid(-1.0F, 0.0F, -1.0F, 2, 30, 2, f);
		this.rightLeg.setRotationPoint(-2.0F, -2.0F, 0.0F);
		this.leftLeg = new ModelPart(this, 56, 0);
		this.leftLeg.mirror = true;
		this.leftLeg.addCuboid(-1.0F, 0.0F, -1.0F, 2, 30, 2, f);
		this.leftLeg.setRotationPoint(2.0F, -2.0F, 0.0F);
	}

	@Override
	public void method_17087(T livingEntity, float f, float g, float h, float i, float j, float k) {
		super.method_17087(livingEntity, f, g, h, i, j, k);
		this.head.visible = true;
		float l = -14.0F;
		this.body.pitch = 0.0F;
		this.body.rotationPointY = -14.0F;
		this.body.rotationPointZ = -0.0F;
		this.rightLeg.pitch -= 0.0F;
		this.leftLeg.pitch -= 0.0F;
		this.rightArm.pitch = (float)((double)this.rightArm.pitch * 0.5);
		this.leftArm.pitch = (float)((double)this.leftArm.pitch * 0.5);
		this.rightLeg.pitch = (float)((double)this.rightLeg.pitch * 0.5);
		this.leftLeg.pitch = (float)((double)this.leftLeg.pitch * 0.5);
		float m = 0.4F;
		if (this.rightArm.pitch > 0.4F) {
			this.rightArm.pitch = 0.4F;
		}

		if (this.leftArm.pitch > 0.4F) {
			this.leftArm.pitch = 0.4F;
		}

		if (this.rightArm.pitch < -0.4F) {
			this.rightArm.pitch = -0.4F;
		}

		if (this.leftArm.pitch < -0.4F) {
			this.leftArm.pitch = -0.4F;
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
			this.leftArm.pitch = -0.5F;
			this.rightArm.roll = 0.05F;
			this.leftArm.roll = -0.05F;
		}

		this.rightArm.rotationPointZ = 0.0F;
		this.leftArm.rotationPointZ = 0.0F;
		this.rightLeg.rotationPointZ = 0.0F;
		this.leftLeg.rotationPointZ = 0.0F;
		this.rightLeg.rotationPointY = -5.0F;
		this.leftLeg.rotationPointY = -5.0F;
		this.head.rotationPointZ = -0.0F;
		this.head.rotationPointY = -13.0F;
		this.headwear.rotationPointX = this.head.rotationPointX;
		this.headwear.rotationPointY = this.head.rotationPointY;
		this.headwear.rotationPointZ = this.head.rotationPointZ;
		this.headwear.pitch = this.head.pitch;
		this.headwear.yaw = this.head.yaw;
		this.headwear.roll = this.head.roll;
		if (this.angry) {
			float n = 1.0F;
			this.head.rotationPointY -= 5.0F;
		}
	}
}
