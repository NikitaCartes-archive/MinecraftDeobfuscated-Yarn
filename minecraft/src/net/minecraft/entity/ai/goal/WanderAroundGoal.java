package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.Vec3d;

public class WanderAroundGoal extends Goal {
	protected final MobEntityWithAi mob;
	protected double targetX;
	protected double targetY;
	protected double targetZ;
	protected final double speed;
	protected int chance;
	protected boolean ignoringChance;

	public WanderAroundGoal(MobEntityWithAi mobEntityWithAi, double d) {
		this(mobEntityWithAi, d, 120);
	}

	public WanderAroundGoal(MobEntityWithAi mobEntityWithAi, double d, int i) {
		this.mob = mobEntityWithAi;
		this.speed = d;
		this.chance = i;
		this.setControls(EnumSet.of(Goal.Control.field_18405));
	}

	@Override
	public boolean canStart() {
		if (this.mob.hasPassengers()) {
			return false;
		} else {
			if (!this.ignoringChance) {
				if (this.mob.getDespawnCounter() >= 100) {
					return false;
				}

				if (this.mob.getRand().nextInt(this.chance) != 0) {
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
		return PathfindingUtil.findTarget(this.mob, 10, 7);
	}

	@Override
	public boolean shouldContinue() {
		return !this.mob.getNavigation().isIdle();
	}

	@Override
	public void start() {
		this.mob.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
	}

	public void ignoreChanceOnce() {
		this.ignoringChance = true;
	}

	public void setChance(int i) {
		this.chance = i;
	}
}
