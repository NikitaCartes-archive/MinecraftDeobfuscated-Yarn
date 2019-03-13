package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class EndermanEntityModel<T extends LivingEntity> extends BipedEntityModel<T> {
	public boolean carryingBlock;
	public boolean angry;

	public EndermanEntityModel(float f) {
		super(0.0F, -14.0F, 64, 32);
		float g = -14.0F;
		this.field_3394 = new Cuboid(this, 0, 16);
		this.field_3394.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, f - 0.5F);
		this.field_3394.setRotationPoint(0.0F, -14.0F, 0.0F);
		this.field_3391 = new Cuboid(this, 32, 16);
		this.field_3391.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, f);
		this.field_3391.setRotationPoint(0.0F, -14.0F, 0.0F);
		this.field_3401 = new Cuboid(this, 56, 0);
		this.field_3401.addBox(-1.0F, -2.0F, -1.0F, 2, 30, 2, f);
		this.field_3401.setRotationPoint(-3.0F, -12.0F, 0.0F);
		this.field_3390 = new Cuboid(this, 56, 0);
		this.field_3390.mirror = true;
		this.field_3390.addBox(-1.0F, -2.0F, -1.0F, 2, 30, 2, f);
		this.field_3390.setRotationPoint(5.0F, -12.0F, 0.0F);
		this.field_3392 = new Cuboid(this, 56, 0);
		this.field_3392.addBox(-1.0F, 0.0F, -1.0F, 2, 30, 2, f);
		this.field_3392.setRotationPoint(-2.0F, -2.0F, 0.0F);
		this.field_3397 = new Cuboid(this, 56, 0);
		this.field_3397.mirror = true;
		this.field_3397.addBox(-1.0F, 0.0F, -1.0F, 2, 30, 2, f);
		this.field_3397.setRotationPoint(2.0F, -2.0F, 0.0F);
	}

	@Override
	public void method_17087(T livingEntity, float f, float g, float h, float i, float j, float k) {
		super.method_17087(livingEntity, f, g, h, i, j, k);
		this.field_3398.visible = true;
		float l = -14.0F;
		this.field_3391.pitch = 0.0F;
		this.field_3391.rotationPointY = -14.0F;
		this.field_3391.rotationPointZ = -0.0F;
		this.field_3392.pitch -= 0.0F;
		this.field_3397.pitch -= 0.0F;
		this.field_3401.pitch = (float)((double)this.field_3401.pitch * 0.5);
		this.field_3390.pitch = (float)((double)this.field_3390.pitch * 0.5);
		this.field_3392.pitch = (float)((double)this.field_3392.pitch * 0.5);
		this.field_3397.pitch = (float)((double)this.field_3397.pitch * 0.5);
		float m = 0.4F;
		if (this.field_3401.pitch > 0.4F) {
			this.field_3401.pitch = 0.4F;
		}

		if (this.field_3390.pitch > 0.4F) {
			this.field_3390.pitch = 0.4F;
		}

		if (this.field_3401.pitch < -0.4F) {
			this.field_3401.pitch = -0.4F;
		}

		if (this.field_3390.pitch < -0.4F) {
			this.field_3390.pitch = -0.4F;
		}

		if (this.field_3392.pitch > 0.4F) {
			this.field_3392.pitch = 0.4F;
		}

		if (this.field_3397.pitch > 0.4F) {
			this.field_3397.pitch = 0.4F;
		}

		if (this.field_3392.pitch < -0.4F) {
			this.field_3392.pitch = -0.4F;
		}

		if (this.field_3397.pitch < -0.4F) {
			this.field_3397.pitch = -0.4F;
		}

		if (this.carryingBlock) {
			this.field_3401.pitch = -0.5F;
			this.field_3390.pitch = -0.5F;
			this.field_3401.roll = 0.05F;
			this.field_3390.roll = -0.05F;
		}

		this.field_3401.rotationPointZ = 0.0F;
		this.field_3390.rotationPointZ = 0.0F;
		this.field_3392.rotationPointZ = 0.0F;
		this.field_3397.rotationPointZ = 0.0F;
		this.field_3392.rotationPointY = -5.0F;
		this.field_3397.rotationPointY = -5.0F;
		this.field_3398.rotationPointZ = -0.0F;
		this.field_3398.rotationPointY = -13.0F;
		this.field_3394.rotationPointX = this.field_3398.rotationPointX;
		this.field_3394.rotationPointY = this.field_3398.rotationPointY;
		this.field_3394.rotationPointZ = this.field_3398.rotationPointZ;
		this.field_3394.pitch = this.field_3398.pitch;
		this.field_3394.yaw = this.field_3398.yaw;
		this.field_3394.roll = this.field_3398.roll;
		if (this.angry) {
			float n = 1.0F;
			this.field_3398.rotationPointY -= 5.0F;
		}
	}
}
