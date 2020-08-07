package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.server.world.ServerWorld;

public class AdmireItemTimeLimitTask<E extends PiglinEntity> extends Task<E> {
	private final int timeLimit;
	private final int cooldown;

	public AdmireItemTimeLimitTask(int timeLimit, int cooldown) {
		super(
			ImmutableMap.of(
				MemoryModuleType.field_22334,
				MemoryModuleState.field_18456,
				MemoryModuleType.field_22332,
				MemoryModuleState.field_18456,
				MemoryModuleType.field_25813,
				MemoryModuleState.field_18458,
				MemoryModuleType.field_25814,
				MemoryModuleState.field_18458
			)
		);
		this.timeLimit = timeLimit;
		this.cooldown = cooldown;
	}

	protected boolean method_30337(ServerWorld serverWorld, E piglinEntity) {
		return piglinEntity.getOffHandStack().isEmpty();
	}

	protected void method_30338(ServerWorld serverWorld, E piglinEntity, long l) {
		Brain<PiglinEntity> brain = piglinEntity.getBrain();
		Optional<Integer> optional = brain.getOptionalMemory(MemoryModuleType.field_25813);
		if (!optional.isPresent()) {
			brain.remember(MemoryModuleType.field_25813, 0);
		} else {
			int i = (Integer)optional.get();
			if (i > this.timeLimit) {
				brain.forget(MemoryModuleType.field_22334);
				brain.forget(MemoryModuleType.field_25813);
				brain.remember(MemoryModuleType.field_25814, true, (long)this.cooldown);
			} else {
				brain.remember(MemoryModuleType.field_25813, i + 1);
			}
		}
	}
}
