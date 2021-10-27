package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;

public class GoToWalkTargetGoal extends Goal {
	private final PathAwareEntity mob;
	private double x;
	private double y;
	private double z;
	private final double speed;

	public GoToWalkTargetGoal(PathAwareEntity mob, double speed) {
		this.mob = mob;
		this.speed = speed;
		this.setControls(EnumSet.of(Goal.Control.MOVE));
	}

	@Override
	public boolean canStart() {
		if (this.mob.isInWalkTargetRange()) {
			return false;
		} else {
			Vec3d vec3d = NoPenaltyTargeting.findTo(this.mob, 16, 7, Vec3d.ofBottomCenter(this.mob.getPositionTarget()), (float) (Math.PI / 2));
			if (vec3d == null) {
				return false;
			} else {
				this.x = vec3d.x;
				this.y = vec3d.y;
				this.z = vec3d.z;
				return true;
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		return !this.mob.getNavigation().isIdle();
	}

	@Override
	public void start() {
		this.mob.getNavigation().startMovingTo(this.x, this.y, this.z, this.speed);
	}
}
