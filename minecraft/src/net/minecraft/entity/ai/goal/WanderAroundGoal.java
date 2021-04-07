package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;

public class WanderAroundGoal extends Goal {
	public static final int DEFAULT_CHANCE = 120;
	protected final PathAwareEntity mob;
	protected double targetX;
	protected double targetY;
	protected double targetZ;
	protected final double speed;
	protected int chance;
	protected boolean ignoringChance;
	private final boolean canDespawn;

	public WanderAroundGoal(PathAwareEntity mob, double speed) {
		this(mob, speed, 120);
	}

	public WanderAroundGoal(PathAwareEntity mob, double speed, int chance) {
		this(mob, speed, chance, true);
	}

	public WanderAroundGoal(PathAwareEntity entity, double speed, int chance, boolean canDespawn) {
		this.mob = entity;
		this.speed = speed;
		this.chance = chance;
		this.canDespawn = canDespawn;
		this.setControls(EnumSet.of(Goal.Control.MOVE));
	}

	@Override
	public boolean canStart() {
		if (this.mob.hasPassengers()) {
			return false;
		} else {
			if (!this.ignoringChance) {
				if (this.canDespawn && this.mob.getDespawnCounter() >= 100) {
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
		return NoPenaltyTargeting.find(this.mob, 10, 7);
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
