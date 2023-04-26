package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class WalkTowardsPosTask {
	private static BlockPos fuzz(MobEntity mob, BlockPos pos) {
		Random random = mob.getWorld().random;
		return pos.add(fuzz(random), 0, fuzz(random));
	}

	private static int fuzz(Random random) {
		return random.nextInt(3) - 1;
	}

	public static <E extends MobEntity> SingleTickTask<E> create(MemoryModuleType<BlockPos> posModule, int completionRange, float speed) {
		return TaskTriggerer.task(
			context -> context.group(
						context.queryMemoryValue(posModule),
						context.queryMemoryAbsent(MemoryModuleType.ATTACK_TARGET),
						context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET),
						context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET)
					)
					.apply(context, (pos, attackTarget, walkTarget, lookTarget) -> (world, entity, time) -> {
							BlockPos blockPos = context.getValue(pos);
							boolean bl = blockPos.isWithinDistance(entity.getBlockPos(), (double)completionRange);
							if (!bl) {
								LookTargetUtil.walkTowards(entity, fuzz(entity, blockPos), speed, completionRange);
							}

							return true;
						})
		);
	}
}
