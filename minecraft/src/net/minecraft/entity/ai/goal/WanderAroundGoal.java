package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;

public class WanderAroundGoal extends Goal {
	protected final PathAwareEntity mob;
	protected double targetX;
	protected double targetY;
	protected double targetZ;
	protected final double speed;
	protected int chance;
	protected boolean ignoringChance;
	private boolean field_24463;

	public WanderAroundGoal(PathAwareEntity mob, double speed) {
		this(mob, speed, 120);
	}

	public WanderAroundGoal(PathAwareEntity mob, double speed, int chance) {
		this(mob, speed, chance, true);
	}

	public WanderAroundGoal(PathAwareEntity pathAwareEntity, double d, int i, boolean bl) {
		this.mob = pathAwareEntity;
		this.speed = d;
		this.chance = i;
		this.field_24463 = bl;
		this.setControls(EnumSet.of(Goal.Control.MOVE));
	}

	@Override
	public boolean canStart() {
		if (this.mob.hasPassengers()) {
			return false;
		} else {
			if (!this.ignoringChance) {
				if (this.field_24463 && this.mob.getDespawnCounter() >= 100) {
					return false;
				}

				if (this.mob.getRandom().nextInt(this.chance) != 0) {
					return false;
				}
			}

			Vec3d vec3d = this.getWanderTarget();
			if (vec3d == null) {
				return false;
			} else {
				this.targetX = vec3d.x;
				this.targetY = vec3d.y;
				this.targetZ = vec3d.z;
				this.ignoringChance = false;
				return true;
			}
		}
	}

	@Nullable
	protected Vec3d getWanderTarget() {
		return TargetFinder.findTarget(this.mob, 10, 7);
	}

	@Override
	public boolean shouldContinue() {
		return !this.mob.getNavigation().isIdle() && !this.mob.hasPassengers();
	}

	@Override
	public void start() {
		this.mob.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
	}

	@Override
	public void stop() {
		this.mob.getNavigation().stop();
		super.stop();
	}

	public void ignoreChanceOnce() {
		this.ignoringChance = true;
	}

	public void setChance(int chance) {
		this.chance = chance;
	}
}
