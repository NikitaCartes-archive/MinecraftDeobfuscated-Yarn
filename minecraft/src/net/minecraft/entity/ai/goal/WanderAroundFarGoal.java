package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.Vec3d;

public class WanderAroundFarGoal extends WanderAroundGoal {
	protected final float probability;

	public WanderAroundFarGoal(MobEntityWithAi mobEntityWithAi, double d) {
		this(mobEntityWithAi, d, 0.001F);
	}

	public WanderAroundFarGoal(MobEntityWithAi mobEntityWithAi, double d, float f) {
		super(mobEntityWithAi, d);
		this.probability = f;
	}

	@Nullable
	@Override
	protected Vec3d getWanderTarget() {
		if (this.mob.isInsideWaterOrBubbleColumn()) {
			Vec3d vec3d = PathfindingUtil.findTargetStraight(this.mob, 15, 7);
			return vec3d == null ? super.getWanderTarget() : vec3d;
		} else {
			return this.mob.getRand().nextFloat() >= this.probability ? PathfindingUtil.findTargetStraight(this.mob, 10, 7) : super.getWanderTarget();
		}
	}
}
