package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;

public class VillagerWalkTowardsTask {
	public static SingleTickTask<VillagerEntity> create(MemoryModuleType<GlobalPos> destination, float speed, int completionRange, int maxDistance, int maxRunTime) {
		return TaskTriggerer.task(
			context -> context.group(
						context.queryMemoryOptional(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE),
						context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET),
						context.queryMemoryValue(destination)
					)
					.apply(context, (cantReachWalkTargetSince, walkTarget, destinationResult) -> (world, entity, time) -> {
							GlobalPos globalPos = context.getValue(destinationResult);
							Optional<Long> optional = context.getOptionalValue(cantReachWalkTargetSince);
							if (globalPos.getDimension() == world.getRegistryKey() && (!optional.isPresent() || world.getTime() - (Long)optional.get() <= (long)maxRunTime)) {
								if (globalPos.getPos().getManhattanDistance(entity.getBlockPos()) > maxDistance) {
									Vec3d vec3d = null;
									int l = 0;
									int m = 1000;

									while (vec3d == null || new BlockPos(vec3d).getManhattanDistance(entity.getBlockPos()) > maxDistance) {
										vec3d = NoPenaltyTargeting.findTo(entity, 15, 7, Vec3d.ofBottomCenter(globalPos.getPos()), (float) (Math.PI / 2));
										if (++l == 1000) {
											entity.releaseTicketFor(destination);
											destinationResult.forget();
											cantReachWalkTargetSince.remember(time);
											return true;
										}
									}

									walkTarget.remember(new WalkTarget(vec3d, speed, completionRange));
								} else if (globalPos.getPos().getManhattanDistance(entity.getBlockPos()) > completionRange) {
									walkTarget.remember(new WalkTarget(globalPos.getPos(), speed, completionRange));
								}
							} else {
								entity.releaseTicketFor(destination);
								destinationResult.forget();
								cantReachWalkTargetSince.remember(time);
							}

							return true;
						})
		);
	}
}
