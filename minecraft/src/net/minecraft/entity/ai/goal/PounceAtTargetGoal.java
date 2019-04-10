package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Vec3d;

public class PounceAtTargetGoal extends Goal {
	private final MobEntity owner;
	private LivingEntity target;
	private final float field_6475;

	public PounceAtTargetGoal(MobEntity mobEntity, float f) {
		this.owner = mobEntity;
		this.field_6475 = f;
		this.setControls(EnumSet.of(Goal.Control.field_18407, Goal.Control.field_18405));
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
		Vec3d vec3d = this.owner.getVelocity();
		Vec3d vec3d2 = new Vec3d(this.target.x - this.owner.x, 0.0, this.target.z - this.owner.z);
		if (vec3d2.lengthSquared() > 1.0E-7) {
			vec3d2 = vec3d2.normalize().multiply(0.4).add(vec3d.multiply(0.2));
		}

		this.owner.setVelocity(vec3d2.x, (double)this.field_6475, vec3d2.y);
	}
}
