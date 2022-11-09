package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public class TemptationCooldownTask extends MultiTickTask<LivingEntity> {
	private final MemoryModuleType<Integer> moduleType;

	public TemptationCooldownTask(MemoryModuleType<Integer> moduleType) {
		super(ImmutableMap.of(moduleType, MemoryModuleState.VALUE_PRESENT));
		this.moduleType = moduleType;
	}

	private Optional<Integer> getTemptationCooldownTicks(LivingEntity entity) {
		return entity.getBrain().getOptionalRegisteredMemory(this.moduleType);
	}

	@Override
	protected boolean isTimeLimitExceeded(long time) {
		return false;
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
		Optional<Integer> optional = this.getTemptationCooldownTicks(entity);
		return optional.isPresent() && (Integer)optional.get() > 0;
	}

	@Override
	protected void keepRunning(ServerWorld world, LivingEntity entity, long time) {
		Optional<Integer> optional = this.getTemptationCooldownTicks(entity);
		entity.getBrain().remember(this.moduleType, (Integer)optional.get() - 1);
	}

	@Override
	protected void finishRunning(ServerWorld world, LivingEntity entity, long time) {
		entity.getBrain().forget(this.moduleType);
	}
}
