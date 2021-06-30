package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class StrollTask extends Task<PathAwareEntity> {
	private static final int MIN_RUN_TIME = 10;
	private static final int MAX_RUN_TIME = 7;
	private final float speed;
	protected final int horizontalRadius;
	protected final int verticalRadius;
	private final boolean strollInsideWater;

	public StrollTask(float speed) {
		this(speed, true);
	}

	public StrollTask(float speed, boolean strollInsideWater) {
		this(speed, 10, 7, strollInsideWater);
	}

	public StrollTask(float speed, int horizontalRadius, int verticalRadius) {
		this(speed, horizontalRadius, verticalRadius, true);
	}

	public StrollTask(float speed, int horizontalRadius, int verticalRadius, boolean strollInsideWater) {
		super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT));
		this.speed = speed;
		this.horizontalRadius = horizontalRadius;
		this.verticalRadius = verticalRadius;
		this.strollInsideWater = strollInsideWater;
	}

	protected boolean shouldRun(ServerWorld serverWorld, PathAwareEntity pathAwareEntity) {
		return this.strollInsideWater || !pathAwareEntity.isInsideWaterOrBubbleColumn();
	}

	protected void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
		Optional<Vec3d> optional = Optional.ofNullable(this.findWalkTarget(pathAwareEntity));
		pathAwareEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, optional.map(pos -> new WalkTarget(pos, this.speed, 0)));
	}

	@Nullable
	protected Vec3d findWalkTarget(PathAwareEntity entity) {
		return FuzzyTargeting.find(entity, this.horizontalRadius, this.verticalRadius);
	}
}
