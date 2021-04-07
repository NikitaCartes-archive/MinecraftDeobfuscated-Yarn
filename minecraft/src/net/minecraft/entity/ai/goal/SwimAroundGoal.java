package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;

public class SwimAroundGoal extends WanderAroundGoal {
	public SwimAroundGoal(PathAwareEntity pathAwareEntity, double d, int i) {
		super(pathAwareEntity, d, i);
	}

	@Nullable
	@Override
	protected Vec3d getWanderTarget() {
		return LookTargetUtil.find(this.mob, 10, 7);
	}
}
