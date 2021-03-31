package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public class TemptationCooldownTask extends Task<LivingEntity> {
	private final MemoryModuleType<Integer> field_30113;

	public TemptationCooldownTask(MemoryModuleType<Integer> memoryModuleType) {
		super(ImmutableMap.of(memoryModuleType, MemoryModuleState.VALUE_PRESENT));
		this.field_30113 = memoryModuleType;
	}

	private Optional<Integer> getTemptationCooldownTicks(LivingEntity entity) {
		return entity.getBrain().getOptionalMemory(this.field_30113);
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
		entity.getBrain().remember(this.field_30113, (Integer)optional.get() - 1);
	}

	@Override
	protected void finishRunning(ServerWorld world, LivingEntity entity, long time) {
		entity.getBrain().forget(this.field_30113);
	}
}
