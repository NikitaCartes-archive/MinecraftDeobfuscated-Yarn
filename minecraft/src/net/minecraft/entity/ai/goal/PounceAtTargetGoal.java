package net.minecraft.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class PounceAtTargetGoal extends Goal {
	private final MobEntity owner;
	private LivingEntity target;
	private final float field_6475;

	public PounceAtTargetGoal(MobEntity mobEntity, float f) {
		this.owner = mobEntity;
		this.field_6475 = f;
		this.setControlBits(5);
	}

	@Override
	public boolean canStart() {
		if (this.owner.hasPassengers()) {
			return false;
		} else {
			this.target = this.owner.getTarget();
			if (this.target == null) {
				return false;
			} else {
				double d = this.owner.squaredDistanceTo(this.target);
				if (d < 4.0 || d > 16.0) {
					return false;
				} else {
					return !this.owner.onGround ? false : this.owner.getRand().nextInt(5) == 0;
				}
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		return !this.owner.onGround;
	}

	@Override
	public void start() {
		double d = this.target.x - this.owner.x;
		double e = this.target.z - this.owner.z;
		float f = MathHelper.sqrt(d * d + e * e);
		if ((double)f >= 1.0E-4) {
			this.owner.velocityX = this.owner.velocityX + d / (double)f * 0.5 * 0.8F + this.owner.velocityX * 0.2F;
			this.owner.velocityZ = this.owner.velocityZ + e / (double)f * 0.5 * 0.8F + this.owner.velocityZ * 0.2F;
		}

		this.owner.velocityY = (double)this.field_6475;
	}
}
