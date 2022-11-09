package net.minecraft.entity.ai.brain.task;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;

public class WanderIndoorsTask {
	public static Task<PathAwareEntity> create(float speed) {
		return TaskTriggerer.task(
			context -> context.group(context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET))
					.apply(
						context,
						walkTarget -> (world, entity, time) -> {
								if (world.isSkyVisible(entity.getBlockPos())) {
									return false;
								} else {
									BlockPos blockPos = entity.getBlockPos();
									List<BlockPos> list = (List<BlockPos>)BlockPos.stream(blockPos.add(-1, -1, -1), blockPos.add(1, 1, 1))
										.map(BlockPos::toImmutable)
										.collect(Collectors.toList());
									Collections.shuffle(list);
									list.stream()
										.filter(pos -> !world.isSkyVisible(pos))
										.filter(pos -> world.isTopSolid(pos, entity))
										.filter(pos -> world.isSpaceEmpty(entity))
										.findFirst()
										.ifPresent(pos -> walkTarget.remember(new WalkTarget(pos, speed, 0)));
									return true;
								}
							}
					)
		);
	}
}
