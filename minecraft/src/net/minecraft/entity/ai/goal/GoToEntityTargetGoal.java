package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.Vec3d;

public class GoToEntityTargetGoal extends Goal {
	private final MobEntityWithAi owner;
	private LivingEntity target;
	private double field_6527;
	private double field_6526;
	private double field_6531;
	private final double field_6530;
	private final float maxDistance;

	public GoToEntityTargetGoal(MobEntityWithAi mobEntityWithAi, double d, float f) {
		this.owner = mobEntityWithAi;
		this.field_6530 = d;
		this.maxDistance = f;
		this.setControlBits(EnumSet.of(Goal.ControlBit.field_18405));
	}

	@Override
	public boolean canStart() {
		this.target = this.owner.getTarget();
		if (this.target == null) {
			return false;
		} else if (this.target.squaredDistanceTo(this.owner) > (double)(this.maxDistance * this.maxDistance)) {
			return false;
		} else {
			Vec3d vec3d = PathfindingUtil.method_6373(this.owner, 16, 7, new Vec3d(this.target.x, this.target.y, this.target.z));
			if (vec3d == null) {
				return false;
			} else {
				this.field_6527 = vec3d.x;
				this.field_6526 = vec3d.y;
				this.field_6531 = vec3d.z;
				return true;
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		return !this.owner.getNavigation().isIdle()
			&& this.target.isValid()
			&& this.target.squaredDistanceTo(this.owner) < (double)(this.maxDistance * this.maxDistance);
	}

	@Override
	public void onRemove() {
		this.target = null;
	}

	@Override
	public void start() {
		this.owner.getNavigation().startMovingTo(this.field_6527, this.field_6526, this.field_6531, this.field_6530);
	}
}
