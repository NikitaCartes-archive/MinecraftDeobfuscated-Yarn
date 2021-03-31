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
	private static final int field_30157 = 10;
	private static final int field_30158 = 7;
	private final float speed;
	protected final int horizontalRadius;
	protected final int verticalRadius;

	public StrollTask(float speed) {
		this(speed, 10, 7);
	}

	public StrollTask(float speed, int horizontalRadius, int verticalRadius) {
		super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT));
		this.speed = speed;
		this.horizontalRadius = horizontalRadius;
		this.verticalRadius = verticalRadius;
	}

	protected void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
		Optional<Vec3d> optional = Optional.ofNullable(this.method_33201(pathAwareEntity));
		pathAwareEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, optional.map(vec3d -> new WalkTarget(vec3d, this.speed, 0)));
	}

	@Nullable
	protected Vec3d method_33201(PathAwareEntity pathAwareEntity) {
		return FuzzyTargeting.find(pathAwareEntity, this.horizontalRadius, this.verticalRadius);
	}
}
