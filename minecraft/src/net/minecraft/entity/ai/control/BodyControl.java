package net.minecraft.entity.ai.control;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class BodyControl {
	private final MobEntity entity;
	private int field_6355;
	private float field_6354;

	public BodyControl(MobEntity mobEntity) {
		this.entity = mobEntity;
	}

	public void method_6224() {
		if (this.method_20247()) {
			this.entity.field_6283 = this.entity.yaw;
			this.method_20244();
			this.field_6354 = this.entity.headYaw;
			this.field_6355 = 0;
		} else {
			if (this.method_20246()) {
				if (Math.abs(this.entity.headYaw - this.field_6354) > 15.0F) {
					this.field_6355 = 0;
					this.field_6354 = this.entity.headYaw;
					this.method_20243();
				} else {
					this.field_6355++;
					if (this.field_6355 > 10) {
						this.method_20245();
					}
				}
			}
		}
	}

	private void method_20243() {
		this.entity.field_6283 = MathHelper.method_20306(this.entity.field_6283, this.entity.headYaw, (float)this.entity.method_5986());
	}

	private void method_20244() {
		this.entity.headYaw = MathHelper.method_20306(this.entity.headYaw, this.entity.field_6283, (float)this.entity.method_5986());
	}

	private void method_20245() {
		int i = this.field_6355 - 10;
		float f = MathHelper.clamp((float)i / 10.0F, 0.0F, 1.0F);
		float g = (float)this.entity.method_5986() * (1.0F - f);
		this.entity.field_6283 = MathHelper.method_20306(this.entity.field_6283, this.entity.headYaw, g);
	}

	private boolean method_20246() {
		return this.entity.getPassengerList().isEmpty() || !(this.entity.getPassengerList().get(0) instanceof MobEntity);
	}

	private boolean method_20247() {
		double d = this.entity.x - this.entity.prevX;
		double e = this.entity.z - this.entity.prevZ;
		return d * d + e * e > 2.5000003E-7F;
	}
}
