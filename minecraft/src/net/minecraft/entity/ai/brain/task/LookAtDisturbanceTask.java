package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.util.math.BlockPos;

public class LookAtDisturbanceTask {
	public static Task<LivingEntity> create() {
		return TaskTriggerer.task(
			context -> context.group(
						context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET),
						context.queryMemoryOptional(MemoryModuleType.DISTURBANCE_LOCATION),
						context.queryMemoryOptional(MemoryModuleType.ROAR_TARGET),
						context.queryMemoryAbsent(MemoryModuleType.ATTACK_TARGET)
					)
					.apply(context, (lookTarget, disturbanceLocation, roarTarget, attackTarget) -> (world, entity, time) -> {
							Optional<BlockPos> optional = context.getOptionalValue(roarTarget).map(Entity::getBlockPos).or(() -> context.getOptionalValue(disturbanceLocation));
							if (optional.isEmpty()) {
								return false;
							} else {
								lookTarget.remember(new BlockPosLookTarget((BlockPos)optional.get()));
								return true;
							}
						})
		);
	}
}
