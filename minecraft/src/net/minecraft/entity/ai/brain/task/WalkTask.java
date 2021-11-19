package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;

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
			Vec3d vec3d = this.findTarget(pathAwareEntity, serverWorld);
			if (vec3d != null) {
				pathAwareEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d, this.speed, 0));
			}
		}
	}

	@Nullable
	private Vec3d findTarget(PathAwareEntity entity, ServerWorld world) {
		if (entity.isOnFire()) {
			Optional<Vec3d> optional = this.findClosestWater(world, entity).map(Vec3d::ofBottomCenter);
			if (optional.isPresent()) {
				return (Vec3d)optional.get();
			}
		}

		return FuzzyTargeting.find(entity, 5, 4);
	}

	private Optional<BlockPos> findClosestWater(BlockView world, Entity entity) {
		BlockPos blockPos = entity.getBlockPos();
		return !world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty()
			? Optional.empty()
			: BlockPos.findClosest(blockPos, 5, 1, pos -> world.getFluidState(pos).isIn(FluidTags.WATER));
	}
}
