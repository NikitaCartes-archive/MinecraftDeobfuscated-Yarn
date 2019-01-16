package net.minecraft;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class class_1359 extends Goal {
	private final MobEntity field_6476;
	private LivingEntity field_6477;
	private final float field_6475;

	public class_1359(MobEntity mobEntity, float f) {
		this.field_6476 = mobEntity;
		this.field_6475 = f;
		this.setControlBits(5);
	}

	@Override
	public boolean canStart() {
		if (this.field_6476.hasPassengers()) {
			return false;
		} else {
			this.field_6477 = this.field_6476.getTarget();
			if (this.field_6477 == null) {
				return false;
			} else {
				double d = this.field_6476.squaredDistanceTo(this.field_6477);
				if (d < 4.0 || d > 16.0) {
					return false;
				} else {
					return !this.field_6476.onGround ? false : this.field_6476.getRand().nextInt(5) == 0;
				}
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		return !this.field_6476.onGround;
	}

	@Override
	public void start() {
		double d = this.field_6477.x - this.field_6476.x;
		double e = this.field_6477.z - this.field_6476.z;
		float f = MathHelper.sqrt(d * d + e * e);
		if ((double)f >= 1.0E-4) {
			this.field_6476.velocityX = this.field_6476.velocityX + d / (double)f * 0.5 * 0.8F + this.field_6476.velocityX * 0.2F;
			this.field_6476.velocityZ = this.field_6476.velocityZ + e / (double)f * 0.5 * 0.8F + this.field_6476.velocityZ * 0.2F;
		}

		this.field_6476.velocityY = (double)this.field_6475;
	}
}
