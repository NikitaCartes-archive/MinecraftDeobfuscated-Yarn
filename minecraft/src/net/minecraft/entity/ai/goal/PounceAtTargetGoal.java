package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Vec3d;

public class PounceAtTargetGoal extends Goal {
	private final MobEntity mob;
	private LivingEntity target;
	private final float velocity;

	public PounceAtTargetGoal(MobEntity mobEntity, float f) {
		this.mob = mobEntity;
		this.velocity = f;
		this.setControls(EnumSet.of(Goal.Control.field_18407, Goal.Control.field_18405));
	}

	@Override
	public boolean canStart() {
		if (this.mob.hasPassengers()) {
			return false;
		} else {
			this.target = this.mob.getTarget();
			if (this.target == null) {
				return false;
			} else {
				double d = this.mob.squaredDistanceTo(this.target);
				if (d < 4.0 || d > 16.0) {
					return false;
				} else {
					return !this.mob.onGround ? false : this.mob.getRand().nextInt(5) == 0;
				}
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		return !this.mob.onGround;
	}

	@Override
	public void start() {
		Vec3d vec3d = this.mob.getVelocity();
		Vec3d vec3d2 = new Vec3d(this.target.x - this.mob.x, 0.0, this.target.z - this.mob.z);
		if (vec3d2.lengthSquared() > 1.0E-7) {
			vec3d2 = vec3d2.normalize().multiply(0.4).add(vec3d.multiply(0.2));
		}

		this.mob.setVelocity(vec3d2.x, (double)this.velocity, vec3d2.z);
	}
}
