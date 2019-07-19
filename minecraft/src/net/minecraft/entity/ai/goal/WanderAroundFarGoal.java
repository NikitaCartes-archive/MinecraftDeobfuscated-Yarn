package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.Vec3d;

public class WanderAroundFarGoal extends WanderAroundGoal {
	protected final float probability;

	public WanderAroundFarGoal(MobEntityWithAi mobEntityWithAi, double d) {
		this(mobEntityWithAi, d, 0.001F);
	}

	public WanderAroundFarGoal(MobEntityWithAi mob, double speed, float probabiliity) {
		super(mob, speed);
		this.probability = probabiliity;
	}

	@Nullable
	@Override
	protected Vec3d getWanderTarget() {
		if (this.mob.isInsideWaterOrBubbleColumn()) {
			Vec3d vec3d = TargetFinder.findGroundTarget(this.mob, 15, 7);
			return vec3d == null ? super.getWanderTarget() : vec3d;
		} else {
			return this.mob.getRandom().nextFloat() >= this.probability ? TargetFinder.findGroundTarget(this.mob, 10, 7) : super.getWanderTarget();
		}
	}
}
