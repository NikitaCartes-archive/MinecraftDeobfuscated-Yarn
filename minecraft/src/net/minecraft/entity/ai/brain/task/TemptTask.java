package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class TemptTask extends Task<PathAwareEntity> {
	private final Function<LivingEntity, Float> field_28316;

	public TemptTask(Function<LivingEntity, Float> function) {
		super(
			ImmutableMap.of(
				MemoryModuleType.LOOK_TARGET,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.WALK_TARGET,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.TEMPTATION_COOLDOWN_TICKS,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.IS_TEMPTED,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.TEMPTING_PLAYER,
				MemoryModuleState.VALUE_PRESENT
			)
		);
		this.field_28316 = function;
	}

	protected float method_33196(PathAwareEntity entity) {
		return (Float)this.field_28316.apply(entity);
	}

	private Optional<PlayerEntity> getTemptingPlayer(PathAwareEntity entity) {
		return entity.getBrain().getOptionalMemory(MemoryModuleType.TEMPTING_PLAYER);
	}

	@Override
	protected boolean isTimeLimitExceeded(long time) {
		return false;
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
		return this.getTemptingPlayer(pathAwareEntity).isPresent();
	}

	protected void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
		pathAwareEntity.getBrain().remember(MemoryModuleType.IS_TEMPTED, true);
	}

	protected void finishRunning(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
		Brain<?> brain = pathAwareEntity.getBrain();
		brain.remember(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, 100);
		brain.remember(MemoryModuleType.IS_TEMPTED, false);
		brain.forget(MemoryModuleType.WALK_TARGET);
		brain.forget(MemoryModuleType.LOOK_TARGET);
	}

	protected void keepRunning(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
		PlayerEntity playerEntity = (PlayerEntity)this.getTemptingPlayer(pathAwareEntity).get();
		Brain<?> brain = pathAwareEntity.getBrain();
		brain.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(playerEntity, true));
		if (pathAwareEntity.squaredDistanceTo(playerEntity) < 6.25) {
			brain.forget(MemoryModuleType.WALK_TARGET);
		} else {
			brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityLookTarget(playerEntity, false), this.method_33196(pathAwareEntity), 2));
		}
	}
}
