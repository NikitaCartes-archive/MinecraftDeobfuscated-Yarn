package net.minecraft;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class class_6028 extends Task<PathAwareEntity> {
	private static final int field_30107 = 100;
	private static final int field_30108 = 120;
	private static final int field_30109 = 5;
	private static final int field_30110 = 4;
	private final float field_30111;

	public class_6028(float f) {
		super(ImmutableMap.of(MemoryModuleType.HURT_BY, MemoryModuleState.VALUE_PRESENT), 100, 120);
		this.field_30111 = f;
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
				pathAwareEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d, this.field_30111, 0));
			}
		}
	}
}
