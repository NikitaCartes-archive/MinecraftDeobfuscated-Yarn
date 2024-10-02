package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public class TickCooldownTask extends MultiTickTask<LivingEntity> {
	private final MemoryModuleType<Integer> cooldownModule;

	public TickCooldownTask(MemoryModuleType<Integer> cooldownModule) {
		super(ImmutableMap.of(cooldownModule, MemoryModuleState.VALUE_PRESENT));
		this.cooldownModule = cooldownModule;
	}

	private Optional<Integer> getRemainingCooldownTicks(LivingEntity entity) {
		return entity.getBrain().getOptionalRegisteredMemory(this.cooldownModule);
	}

	@Override
	protected boolean isTimeLimitExceeded(long time) {
		return false;
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
		Optional<Integer> optional = this.getRemainingCooldownTicks(entity);
		return optional.isPresent() && (Integer)optional.get() > 0;
	}

	@Override
	protected void keepRunning(ServerWorld world, LivingEntity entity, long time) {
		Optional<Integer> optional = this.getRemainingCooldownTicks(entity);
		entity.getBrain().remember(this.cooldownModule, (Integer)optional.get() - 1);
	}

	@Override
	protected void finishRunning(ServerWorld world, LivingEntity entity, long time) {
		entity.getBrain().forget(this.cooldownModule);
	}
}
