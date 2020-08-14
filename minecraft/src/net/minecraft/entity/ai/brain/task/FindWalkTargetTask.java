package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;

public class FindWalkTargetTask extends Task<PathAwareEntity> {
	private final float walkSpeed;
	private final int maxHorizontalDistance;
	private final int maxVerticalDistance;

	public FindWalkTargetTask(float walkSpeed) {
		this(walkSpeed, 10, 7);
	}

	public FindWalkTargetTask(float walkSpeed, int maxHorizontalDistance, int maxVerticalDistance) {
		super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT));
		this.walkSpeed = walkSpeed;
		this.maxHorizontalDistance = maxHorizontalDistance;
		this.maxVerticalDistance = maxVerticalDistance;
	}

	protected void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
		BlockPos blockPos = pathAwareEntity.getBlockPos();
		if (serverWorld.isNearOccupiedPointOfInterest(blockPos)) {
			this.updateWalkTarget(pathAwareEntity);
		} else {
			ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(blockPos);
			ChunkSectionPos chunkSectionPos2 = LookTargetUtil.getPosClosestToOccupiedPointOfInterest(serverWorld, chunkSectionPos, 2);
			if (chunkSectionPos2 != chunkSectionPos) {
				this.updateWalkTarget(pathAwareEntity, chunkSectionPos2);
			} else {
				this.updateWalkTarget(pathAwareEntity);
			}
		}
	}

	private void updateWalkTarget(PathAwareEntity entity, ChunkSectionPos pos) {
		Optional<Vec3d> optional = Optional.ofNullable(
			TargetFinder.findTargetTowards(entity, this.maxHorizontalDistance, this.maxVerticalDistance, Vec3d.ofBottomCenter(pos.getCenterPos()))
		);
		entity.getBrain().remember(MemoryModuleType.WALK_TARGET, optional.map(vec3d -> new WalkTarget(vec3d, this.walkSpeed, 0)));
	}

	private void updateWalkTarget(PathAwareEntity entity) {
		Optional<Vec3d> optional = Optional.ofNullable(TargetFinder.findGroundTarget(entity, this.maxHorizontalDistance, this.maxVerticalDistance));
		entity.getBrain().remember(MemoryModuleType.WALK_TARGET, optional.map(vec3d -> new WalkTarget(vec3d, this.walkSpeed, 0)));
	}
}
