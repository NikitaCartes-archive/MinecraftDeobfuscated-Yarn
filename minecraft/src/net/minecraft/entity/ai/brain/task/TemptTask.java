package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
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
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class TemptTask extends MultiTickTask<PathAwareEntity> {
	public static final int TEMPTATION_COOLDOWN_TICKS = 100;
	public static final double DEFAULT_STOP_DISTANCE = 2.5;
	private final Function<LivingEntity, Float> speed;
	private final Function<LivingEntity, Double> stopDistanceGetter;

	public TemptTask(Function<LivingEntity, Float> speed) {
		this(speed, entity -> 2.5);
	}

	public TemptTask(Function<LivingEntity, Float> speed, Function<LivingEntity, Double> stopDistanceGetter) {
		super(Util.make(() -> {
			Builder<MemoryModuleType<?>, MemoryModuleState> builder = ImmutableMap.builder();
			builder.put(MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED);
			builder.put(MemoryModuleType.WALK_TARGET, MemoryModuleState.REGISTERED);
			builder.put(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleState.VALUE_ABSENT);
			builder.put(MemoryModuleType.IS_TEMPTED, MemoryModuleState.REGISTERED);
			builder.put(MemoryModuleType.TEMPTING_PLAYER, MemoryModuleState.VALUE_PRESENT);
			builder.put(MemoryModuleType.BREED_TARGET, MemoryModuleState.VALUE_ABSENT);
			builder.put(MemoryModuleType.IS_PANICKING, MemoryModuleState.VALUE_ABSENT);
			return builder.build();
		}));
		this.speed = speed;
		this.stopDistanceGetter = stopDistanceGetter;
	}

	protected float getSpeed(PathAwareEntity entity) {
		return (Float)this.speed.apply(entity);
	}

	private Optional<PlayerEntity> getTemptingPlayer(PathAwareEntity entity) {
		return entity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.TEMPTING_PLAYER);
	}

	@Override
	protected boolean isTimeLimitExceeded(long time) {
		return false;
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
		return this.getTemptingPlayer(pathAwareEntity).isPresent()
			&& !pathAwareEntity.getBrain().hasMemoryModule(MemoryModuleType.BREED_TARGET)
			&& !pathAwareEntity.getBrain().hasMemoryModule(MemoryModuleType.IS_PANICKING);
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
		double d = (Double)this.stopDistanceGetter.apply(pathAwareEntity);
		if (pathAwareEntity.squaredDistanceTo(playerEntity) < MathHelper.square(d)) {
			brain.forget(MemoryModuleType.WALK_TARGET);
		} else {
			brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityLookTarget(playerEntity, false), this.getSpeed(pathAwareEntity), 2));
		}
	}
}
