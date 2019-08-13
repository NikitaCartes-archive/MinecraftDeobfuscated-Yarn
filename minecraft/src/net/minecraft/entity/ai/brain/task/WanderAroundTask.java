package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class WanderAroundTask extends Task<MobEntity> {
	@Nullable
	private Path field_18369;
	@Nullable
	private BlockPos field_18370;
	private float field_18371;
	private int field_18964;

	public WanderAroundTask(int i) {
		super(ImmutableMap.of(MemoryModuleType.field_18449, MemoryModuleState.field_18457, MemoryModuleType.field_18445, MemoryModuleState.field_18456), i);
	}

	protected boolean method_18978(ServerWorld serverWorld, MobEntity mobEntity) {
		Brain<?> brain = mobEntity.getBrain();
		WalkTarget walkTarget = (WalkTarget)brain.getOptionalMemory(MemoryModuleType.field_18445).get();
		if (!this.method_18980(mobEntity, walkTarget) && this.method_18977(mobEntity, walkTarget, serverWorld.getTime())) {
			this.field_18370 = walkTarget.getLookTarget().getBlockPos();
			return true;
		} else {
			brain.forget(MemoryModuleType.field_18445);
			return false;
		}
	}

	protected boolean method_18979(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		if (this.field_18369 != null && this.field_18370 != null) {
			Optional<WalkTarget> optional = mobEntity.getBrain().getOptionalMemory(MemoryModuleType.field_18445);
			EntityNavigation entityNavigation = mobEntity.getNavigation();
			return !entityNavigation.isIdle() && optional.isPresent() && !this.method_18980(mobEntity, (WalkTarget)optional.get());
		} else {
			return false;
		}
	}

	protected void method_18981(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		mobEntity.getNavigation().stop();
		mobEntity.getBrain().forget(MemoryModuleType.field_18445);
		mobEntity.getBrain().forget(MemoryModuleType.field_18449);
		this.field_18369 = null;
	}

	protected void method_18982(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		mobEntity.getBrain().putMemory(MemoryModuleType.field_18449, this.field_18369);
		mobEntity.getNavigation().startMovingAlong(this.field_18369, (double)this.field_18371);
		this.field_18964 = serverWorld.getRandom().nextInt(10);
	}

	protected void method_18983(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		this.field_18964--;
		if (this.field_18964 <= 0) {
			Path path = mobEntity.getNavigation().getCurrentPath();
			Brain<?> brain = mobEntity.getBrain();
			if (this.field_18369 != path) {
				this.field_18369 = path;
				brain.putMemory(MemoryModuleType.field_18449, path);
			}

			if (path != null && this.field_18370 != null) {
				WalkTarget walkTarget = (WalkTarget)brain.getOptionalMemory(MemoryModuleType.field_18445).get();
				if (walkTarget.getLookTarget().getBlockPos().getSquaredDistance(this.field_18370) > 4.0 && this.method_18977(mobEntity, walkTarget, serverWorld.getTime())) {
					this.field_18370 = walkTarget.getLookTarget().getBlockPos();
					this.method_18982(serverWorld, mobEntity, l);
				}
			}
		}
	}

	private boolean method_18977(MobEntity mobEntity, WalkTarget walkTarget, long l) {
		BlockPos blockPos = walkTarget.getLookTarget().getBlockPos();
		this.field_18369 = mobEntity.getNavigation().findPathTo(blockPos, 0);
		this.field_18371 = walkTarget.getSpeed();
		if (!this.method_18980(mobEntity, walkTarget)) {
			Brain<?> brain = mobEntity.getBrain();
			boolean bl = this.field_18369 != null && this.field_18369.method_21655();
			if (bl) {
				brain.setMemory(MemoryModuleType.field_19293, Optional.empty());
			} else if (!brain.hasMemoryModule(MemoryModuleType.field_19293)) {
				brain.putMemory(MemoryModuleType.field_19293, l);
			}

			if (this.field_18369 != null) {
				return true;
			}

			Vec3d vec3d = PathfindingUtil.method_6373((MobEntityWithAi)mobEntity, 10, 7, new Vec3d(blockPos));
			if (vec3d != null) {
				this.field_18369 = mobEntity.getNavigation().findPathTo(vec3d.x, vec3d.y, vec3d.z, 0);
				return this.field_18369 != null;
			}
		}

		return false;
	}

	private boolean method_18980(MobEntity mobEntity, WalkTarget walkTarget) {
		return walkTarget.getLookTarget().getBlockPos().getManhattanDistance(new BlockPos(mobEntity)) <= walkTarget.getCompletionRange();
	}
}
