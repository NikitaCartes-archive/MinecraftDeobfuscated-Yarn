package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public class TemptationCooldownTask extends Task<LivingEntity> {
	public TemptationCooldownTask() {
		super(ImmutableMap.of(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleState.VALUE_PRESENT));
	}

	private Optional<Integer> getTemptationCooldownTicks(LivingEntity entity) {
		return entity.getBrain().getOptionalMemory(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS);
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
		Optional<Integer> optional = this.getTemptationCooldownTicks(entity);
		return optional.isPresent() && (Integer)optional.get() > 0;
	}

	@Override
	protected void keepRunning(ServerWorld world, LivingEntity entity, long time) {
		Optional<Integer> optional = this.getTemptationCooldownTicks(entity);
		entity.getBrain().remember(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, (Integer)optional.get() - 1);
	}

	@Override
	protected void finishRunning(ServerWorld world, LivingEntity entity, long time) {
		entity.getBrain().forget(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS);
	}
}
