package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.MemoryQueryResult;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.server.world.ServerWorld;

public class WalkTowardsLookTargetTask {
	public static Task<LivingEntity> create(Function<LivingEntity, Optional<LookTarget>> lookTargetFunction, int completionRange, int searchRange, float speed) {
		return TaskTriggerer.task(
			context -> context.group(context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET), context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET))
					.apply(context, (lookTarget, walkTarget) -> (world, entity, time) -> {
							Optional<LookTarget> optional = (Optional)lookTargetFunction.apply(entity);
							if (optional.isEmpty()) {
								return false;
							} else {
								LookTarget lookTargetxx = (LookTarget)optional.get();
								if (entity.getPos().isInRange(lookTargetxx.getPos(), (double)searchRange)) {
									return false;
								} else {
									LookTarget lookTarget2 = (LookTarget)optional.get();
									lookTarget.remember(lookTarget2);
									walkTarget.remember(new WalkTarget(lookTarget2, speed, completionRange));
									return true;
								}
							}
						})
		);
	}
}
