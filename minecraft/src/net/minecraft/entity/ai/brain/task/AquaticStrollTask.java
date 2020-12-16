package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AquaticStrollTask extends StrollTask {
	public AquaticStrollTask(float f) {
		super(f);
	}

	protected boolean shouldRun(ServerWorld serverWorld, PathAwareEntity pathAwareEntity) {
		return pathAwareEntity.isInsideWaterOrBubbleColumn();
	}

	@Override
	protected Vec3d method_33201(PathAwareEntity pathAwareEntity) {
		Vec3d vec3d = LookTargetUtil.method_33193(pathAwareEntity, this.horizontalRadius, this.verticalRadius);
		return vec3d != null && pathAwareEntity.world.getFluidState(new BlockPos(vec3d)).isEmpty() ? null : vec3d;
	}
}
