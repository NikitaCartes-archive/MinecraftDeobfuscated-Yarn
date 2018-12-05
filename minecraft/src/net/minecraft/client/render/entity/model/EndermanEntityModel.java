package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class EndermanEntityModel extends BipedEntityModel {
	public boolean carryingBlock;
	public boolean angry;

	public EndermanEntityModel(float f) {
		super(0.0F, -14.0F, 64, 32);
		float g = -14.0F;
		this.headwear = new Cuboid(this, 0, 16);
		this.headwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, f - 0.5F);
		this.headwear.setRotationPoint(0.0F, -14.0F, 0.0F);
		this.body = new Cuboid(this, 32, 16);
		this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, f);
		this.body.setRotationPoint(0.0F, -14.0F, 0.0F);
		this.armRight = new Cuboid(this, 56, 0);
		this.armRight.addBox(-1.0F, -2.0F, -1.0F, 2, 30, 2, f);
		this.armRight.setRotationPoint(-3.0F, -12.0F, 0.0F);
		this.armLeft = new Cuboid(this, 56, 0);
		this.armLeft.mirror = true;
		this.armLeft.addBox(-1.0F, -2.0F, -1.0F, 2, 30, 2, f);
		this.armLeft.setRotationPoint(5.0F, -12.0F, 0.0F);
		this.legRight = new Cuboid(this, 56, 0);
		this.legRight.addBox(-1.0F, 0.0F, -1.0F, 2, 30, 2, f);
		this.legRight.setRotationPoint(-2.0F, -2.0F, 0.0F);
		this.legLeft = new Cuboid(this, 56, 0);
		this.legLeft.mirror = true;
		this.legLeft.addBox(-1.0F, 0.0F, -1.0F, 2, 30, 2, f);
		this.legLeft.setRotationPoint(2.0F, -2.0F, 0.0F);
	}

	@Override
	public void setRotationAngles(float f, float g, float h, float i, float j, float k, Entity entity) {
		super.setRotationAngles(f, g, h, i, j, k, entity);
		this.head.visible = true;
		float l = -14.0F;
		this.body.pitch = 0.0F;
		this.body.rotationPointY = -14.0F;
		this.body.rotationPointZ = -0.0F;
		this.legRight.pitch -= 0.0F;
		this.legLeft.pitch -= 0.0F;
		this.armRight.pitch = (float)((double)this.armRight.pitch * 0.5);
		this.armLeft.pitch = (float)((double)this.armLeft.pitch * 0.5);
		this.legRight.pitch = (float)((double)this.legRight.pitch * 0.5);
		this.legLeft.pitch = (float)((double)this.legLeft.pitch * 0.5);
		float m = 0.4F;
		if (this.armRight.pitch > 0.4F) {
			this.armRight.pitch = 0.4F;
		}

		if (this.armLeft.pitch > 0.4F) {
			this.armLeft.pitch = 0.4F;
		}

		if (this.armRight.pitch < -0.4F) {
			this.armRight.pitch = -0.4F;
		}

		if (this.armLeft.pitch < -0.4F) {
			this.armLeft.pitch = -0.4F;
		}

		if (this.legRight.pitch > 0.4F) {
			this.legRight.pitch = 0.4F;
		}

		if (this.legLeft.pitch > 0.4F) {
			this.legLeft.pitch = 0.4F;
		}

		if (this.legRight.pitch < -0.4F) {
			this.legRight.pitch = -0.4F;
		}

		if (this.legLeft.pitch < -0.4F) {
			this.legLeft.pitch = -0.4F;
		}

		if (this.carryingBlock) {
			this.armRight.pitch = -0.5F;
			this.armLeft.pitch = -0.5F;
			this.armRight.roll = 0.05F;
			this.armLeft.roll = -0.05F;
		}

		this.armRight.rotationPointZ = 0.0F;
		this.armLeft.rotationPointZ = 0.0F;
		this.legRight.rotationPointZ = 0.0F;
		this.legLeft.rotationPointZ = 0.0F;
		this.legRight.rotationPointY = -5.0F;
		this.legLeft.rotationPointY = -5.0F;
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
