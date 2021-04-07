package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class WalkTask extends Task<PathAwareEntity> {
	private static final int MIN_RUN_TIME = 100;
	private static final int MAX_RUN_TIME = 120;
	private static final int HORIZONTAL_RANGE = 5;
	private static final int VERTICAL_RANGE = 4;
	private final float speed;

	public WalkTask(float speed) {
		super(ImmutableMap.of(MemoryModuleType.HURT_BY, MemoryModuleState.VALUE_PRESENT), 100, 120);
		this.speed = speed;
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
		return true;
	}

	protected void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
		pathAwareEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);
	}

	protected void keepRunning(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
		if (pathAwareEntity.getNavigation().isIdle()) {
			Vec3d vec3d = FuzzyTargeting.find(pathAwareEntity, 5, 4);
			if (vec3d != null) {
				pathAwareEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d, this.speed, 0));
			}
		}
	}
}
