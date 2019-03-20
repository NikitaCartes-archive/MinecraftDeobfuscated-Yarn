package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.Vec3d;

public class WanderAroundFarGoal extends WanderAroundGoal {
	protected final float farWanderProbability;

	public WanderAroundFarGoal(MobEntityWithAi mobEntityWithAi, double d) {
		this(mobEntityWithAi, d, 0.001F);
	}

	public WanderAroundFarGoal(MobEntityWithAi mobEntityWithAi, double d, float f) {
		super(mobEntityWithAi, d);
		this.farWanderProbability = f;
	}

	@Nullable
	@Override
	protected Vec3d getWanderTarget() {
		if (this.owner.isInsideWaterOrBubbleColumn()) {
			Vec3d vec3d = PathfindingUtil.findTargetStraight(this.owner, 15, 7);
			return vec3d == null ? super.getWanderTarget() : vec3d;
		} else {
			return this.owner.getRand().nextFloat() >= this.farWanderProbability ? PathfindingUtil.findTargetStraight(this.owner, 10, 7) : super.getWanderTarget();
		}
	}
}
