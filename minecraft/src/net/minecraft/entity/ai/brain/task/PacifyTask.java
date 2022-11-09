package net.minecraft.entity.ai.brain.task;

import com.mojang.datafixers.util.Function3;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.MemoryQueryResult;
import net.minecraft.server.world.ServerWorld;

public class PacifyTask {
	public static Task<LivingEntity> create(MemoryModuleType<?> requiredMemory, int duration) {
		return TaskTriggerer.task(
			context -> context.group(
						context.queryMemoryOptional(MemoryModuleType.ATTACK_TARGET),
						context.queryMemoryAbsent(MemoryModuleType.PACIFIED),
						context.queryMemoryValue(requiredMemory)
					)
					.apply(
						context,
						context.supply(
							() -> "[BecomePassive if " + requiredMemory + " present]",
							(Function3<MemoryQueryResult, MemoryQueryResult, MemoryQueryResult, TaskRunnable<LivingEntity>>)(attackTarget, pacified, requiredMemoryResult) -> (world, entity, time) -> {
									pacified.remember(true, (long)duration);
									attackTarget.forget();
									return true;
								}
						)
					)
		);
	}
}
