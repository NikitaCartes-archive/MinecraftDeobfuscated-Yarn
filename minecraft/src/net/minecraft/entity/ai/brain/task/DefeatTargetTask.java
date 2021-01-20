package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.function.BiPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;

public class DefeatTargetTask extends Task<LivingEntity> {
	private final int duration;
	private final BiPredicate<LivingEntity, LivingEntity> predicate;

	public DefeatTargetTask(int duration, BiPredicate<LivingEntity, LivingEntity> predicate) {
		super(
			ImmutableMap.of(
				MemoryModuleType.ATTACK_TARGET,
				MemoryModuleState.VALUE_PRESENT,
				MemoryModuleType.ANGRY_AT,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.CELEBRATE_LOCATION,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.DANCING,
				MemoryModuleState.REGISTERED
			)
		);
		this.duration = duration;
		this.predicate = predicate;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
		return this.getAttackTarget(entity).isDead();
	}

	@Override
	protected void run(ServerWorld world, LivingEntity entity, long time) {
		LivingEntity livingEntity = this.getAttackTarget(entity);
		if (this.predicate.test(entity, livingEntity)) {
			entity.getBrain().remember(MemoryModuleType.DANCING, true, (long)this.duration);
		}

		entity.getBrain().remember(MemoryModuleType.CELEBRATE_LOCATION, livingEntity.getBlockPos(), (long)this.duration);
		if (livingEntity.getType() != EntityType.PLAYER || world.getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS)) {
			entity.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
			entity.getBrain().forget(MemoryModuleType.ANGRY_AT);
		}
	}

	private LivingEntity getAttackTarget(LivingEntity entity) {
		return (LivingEntity)entity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
	}
}
