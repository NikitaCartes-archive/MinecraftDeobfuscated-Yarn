package net.minecraft.entity.ai.control;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class BodyControl {
	private final LivingEntity entity;
	private int field_6355;
	private float field_6354;

	public BodyControl(LivingEntity livingEntity) {
		this.entity = livingEntity;
	}

	public void method_6224() {
		double d = this.entity.x - this.entity.prevX;
		double e = this.entity.z - this.entity.prevZ;
		if (d * d + e * e > 2.5000003E-7F) {
			this.entity.field_6283 = this.entity.yaw;
			this.entity.headYaw = this.method_6223(this.entity.field_6283, this.entity.headYaw, (float)((MobEntity)this.entity).method_5986());
			this.field_6354 = this.entity.headYaw;
			this.field_6355 = 0;
		} else {
			if (this.entity.getPassengerList().isEmpty() || !(this.entity.getPassengerList().get(0) instanceof MobEntity)) {
				float f = 75.0F;
				if (Math.abs(this.entity.headYaw - this.field_6354) > 15.0F) {
					this.field_6355 = 0;
					this.field_6354 = this.entity.headYaw;
				} else {
					this.field_6355++;
					int i = 10;
					if (this.field_6355 > 10) {
						f = Math.max(1.0F - (float)(this.field_6355 - 10) / 10.0F, 0.0F) * (float)((MobEntity)this.entity).method_5986();
					}
				}

				this.entity.field_6283 = this.method_6223(this.entity.headYaw, this.entity.field_6283, f);
			}
		}
	}

	private float method_6223(float f, float g, float h) {
		float i = MathHelper.wrapDegrees(f - g);
		if (i < -h) {
			i = -h;
		}

		if (i >= h) {
			i = h;
		}

		return f - i;
	}
}
