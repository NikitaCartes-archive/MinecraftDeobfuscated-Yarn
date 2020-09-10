package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class WanderAroundTask extends Task<MobEntity> {
	private int pathUpdateCountdownTicks;
	@Nullable
	private Path path;
	@Nullable
	private BlockPos lookTargetPos;
	private float speed;

	public WanderAroundTask() {
		this(150, 250);
	}

	public WanderAroundTask(int i, int j) {
		super(
			ImmutableMap.of(
				MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.PATH,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.WALK_TARGET,
				MemoryModuleState.VALUE_PRESENT
			),
			i,
			j
		);
	}

	protected boolean shouldRun(ServerWorld serverWorld, MobEntity mobEntity) {
		if (this.pathUpdateCountdownTicks > 0) {
			this.pathUpdateCountdownTicks--;
			return false;
		} else {
			Brain<?> brain = mobEntity.getBrain();
			WalkTarget walkTarget = (WalkTarget)brain.getOptionalMemory(MemoryModuleType.WALK_TARGET).get();
			boolean bl = this.hasReached(mobEntity, walkTarget);
			if (!bl && this.hasFinishedPath(mobEntity, walkTarget, serverWorld.getTime())) {
				this.lookTargetPos = walkTarget.getLookTarget().getBlockPos();
				return true;
			} else {
				brain.forget(MemoryModuleType.WALK_TARGET);
				if (bl) {
					brain.forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
				}

				return false;
			}
		}
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		if (this.path != null && this.lookTargetPos != null) {
			Optional<WalkTarget> optional = mobEntity.getBrain().getOptionalMemory(MemoryModuleType.WALK_TARGET);
			EntityNavigation entityNavigation = mobEntity.getNavigation();
			return !entityNavigation.isIdle() && optional.isPresent() && !this.hasReached(mobEntity, (WalkTarget)optional.get());
		} else {
			return false;
		}
	}

	protected void finishRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		if (mobEntity.getBrain().hasMemoryModule(MemoryModuleType.WALK_TARGET)
			&& !this.hasReached(mobEntity, (WalkTarget)mobEntity.getBrain().getOptionalMemory(MemoryModuleType.WALK_TARGET).get())
			&& mobEntity.getNavigation().isNearPathStartPos()) {
			this.pathUpdateCountdownTicks = serverWorld.getRandom().nextInt(40);
		}

		mobEntity.getNavigation().stop();
		mobEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);
		mobEntity.getBrain().forget(MemoryModuleType.PATH);
		this.path = null;
	}

	protected void run(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		mobEntity.getBrain().remember(MemoryModuleType.PATH, this.path);
		mobEntity.getNavigation().startMovingAlong(this.path, (double)this.speed);
	}

	protected void keepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		Path path = mobEntity.getNavigation().getCurrentPath();
		Brain<?> brain = mobEntity.getBrain();
		if (this.path != path) {
			this.path = path;
			brain.remember(MemoryModuleType.PATH, path);
		}

		if (path != null && this.lookTargetPos != null) {
			WalkTarget walkTarget = (WalkTarget)brain.getOptionalMemory(MemoryModuleType.WALK_TARGET).get();
			if (walkTarget.getLookTarget().getBlockPos().getSquaredDistance(this.lookTargetPos) > 4.0
				&& this.hasFinishedPath(mobEntity, walkTarget, serverWorld.getTime())) {
				this.lookTargetPos = walkTarget.getLookTarget().getBlockPos();
				this.run(serverWorld, mobEntity, l);
			}
		}
	}

	private boolean hasFinishedPath(MobEntity mobEntity, WalkTarget walkTarget, long time) {
		BlockPos blockPos = walkTarget.getLookTarget().getBlockPos();
		this.path = mobEntity.getNavigation().findPathTo(blockPos, 0);
		this.speed = walkTarget.getSpeed();
		Brain<?> brain = mobEntity.getBrain();
		if (this.hasReached(mobEntity, walkTarget)) {
			brain.forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
		} else {
			boolean bl = this.path != null && this.path.reachesTarget();
			if (bl) {
				brain.forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
			} else if (!brain.hasMemoryModule(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)) {
				brain.remember(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, time);
			}

			if (this.path != null) {
				return true;
			}

			Vec3d vec3d = TargetFinder.findTargetTowards((PathAwareEntity)mobEntity, 10, 7, Vec3d.ofBottomCenter(blockPos));
			if (vec3d != null) {
				this.path = mobEntity.getNavigation().findPathTo(vec3d.x, vec3d.y, vec3d.z, 0);
				return this.path != null;
			}
		}

		return false;
	}

	private boolean hasReached(MobEntity entity, WalkTarget walkTarget) {
		return walkTarget.getLookTarget().getBlockPos().getManhattanDistance(entity.getBlockPos()) <= walkTarget.getCompletionRange();
	}
}
