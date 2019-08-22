package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class LookAroundTask extends Task<MobEntity> {
	public LookAroundTask(int i, int j) {
		super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.VALUE_PRESENT), i, j);
	}

	protected boolean method_18967(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		return mobEntity.getBrain().getOptionalMemory(MemoryModuleType.LOOK_TARGET).filter(lookTarget -> lookTarget.isSeenBy(mobEntity)).isPresent();
	}

	protected void method_18968(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		mobEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
	}

	protected void method_18969(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		mobEntity.getBrain().getOptionalMemory(MemoryModuleType.LOOK_TARGET).ifPresent(lookTarget -> mobEntity.getLookControl().lookAt(lookTarget.getPos()));
	}
}
