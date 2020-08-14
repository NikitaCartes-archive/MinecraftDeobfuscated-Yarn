package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class SwimAroundGoal extends WanderAroundGoal {
	public SwimAroundGoal(PathAwareEntity pathAwareEntity, double d, int i) {
		super(pathAwareEntity, d, i);
	}

	@Nullable
	@Override
	protected Vec3d getWanderTarget() {
		Vec3d vec3d = TargetFinder.findTarget(this.mob, 10, 7);
		int i = 0;

		while (
			vec3d != null
				&& !this.mob.world.getBlockState(new BlockPos(vec3d)).canPathfindThrough(this.mob.world, new BlockPos(vec3d), NavigationType.WATER)
				&& i++ < 10
		) {
			vec3d = TargetFinder.findTarget(this.mob, 10, 7);
		}

		return vec3d;
	}
}
