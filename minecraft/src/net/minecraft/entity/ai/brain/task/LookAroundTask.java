package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class LookAroundTask extends MultiTickTask<MobEntity> {
	public LookAroundTask(int minRunTime, int maxRunTime) {
		super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.VALUE_PRESENT), minRunTime, maxRunTime);
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		return mobEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.LOOK_TARGET).filter(lookTarget -> lookTarget.isSeenBy(mobEntity)).isPresent();
	}

	protected void finishRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		mobEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
	}

	protected void keepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		mobEntity.getBrain()
			.getOptionalRegisteredMemory(MemoryModuleType.LOOK_TARGET)
			.ifPresent(lookTarget -> mobEntity.getLookControl().lookAt(lookTarget.getPos()));
	}
}
