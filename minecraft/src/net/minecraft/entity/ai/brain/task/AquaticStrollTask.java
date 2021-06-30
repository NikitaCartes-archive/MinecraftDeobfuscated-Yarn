package net.minecraft.entity.ai.brain.task;

import javax.annotation.Nullable;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AquaticStrollTask extends StrollTask {
	public static final int[][] NORMALIZED_POS_MULTIPLIERS = new int[][]{{1, 1}, {3, 3}, {5, 5}, {6, 5}, {7, 7}, {10, 7}};

	public AquaticStrollTask(float f) {
		super(f);
	}

	@Override
	protected boolean shouldRun(ServerWorld serverWorld, PathAwareEntity pathAwareEntity) {
		return pathAwareEntity.isInsideWaterOrBubbleColumn();
	}

	@Nullable
	@Override
	protected Vec3d findWalkTarget(PathAwareEntity entity) {
		Vec3d vec3d = null;
		Vec3d vec3d2 = null;

		for (int[] is : NORMALIZED_POS_MULTIPLIERS) {
			if (vec3d == null) {
				vec3d2 = LookTargetUtil.find(entity, is[0], is[1]);
			} else {
				vec3d2 = entity.getPos().add(entity.getPos().relativize(vec3d).normalize().multiply((double)is[0], (double)is[1], (double)is[0]));
			}

			if (vec3d2 == null || entity.world.getFluidState(new BlockPos(vec3d2)).isEmpty()) {
				return vec3d;
			}

			vec3d = vec3d2;
		}

		return vec3d2;
	}
}
