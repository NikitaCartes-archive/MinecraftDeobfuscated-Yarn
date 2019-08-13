package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class LookAroundTask extends Task<MobEntity> {
	public LookAroundTask(int i, int j) {
		super(ImmutableMap.of(MemoryModuleType.field_18446, MemoryModuleState.field_18456), i, j);
	}

	protected boolean method_18967(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		return mobEntity.getBrain().getOptionalMemory(MemoryModuleType.field_18446).filter(lookTarget -> lookTarget.isSeenBy(mobEntity)).isPresent();
	}

	protected void method_18968(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		mobEntity.getBrain().forget(MemoryModuleType.field_18446);
	}

	protected void method_18969(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		mobEntity.getBrain().getOptionalMemory(MemoryModuleType.field_18446).ifPresent(lookTarget -> mobEntity.getLookControl().lookAt(lookTarget.getPos()));
	}
}
