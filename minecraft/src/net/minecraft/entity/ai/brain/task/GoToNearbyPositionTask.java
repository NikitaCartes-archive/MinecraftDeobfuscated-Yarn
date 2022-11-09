package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.GlobalPos;
import org.apache.commons.lang3.mutable.MutableLong;

public class GoToNearbyPositionTask {
	public static Task<PathAwareEntity> create(MemoryModuleType<GlobalPos> posModule, float walkSpeed, int completionRange, int maxDistance) {
		MutableLong mutableLong = new MutableLong(0L);
		return TaskTriggerer.task(
			context -> context.group(context.queryMemoryOptional(MemoryModuleType.WALK_TARGET), context.queryMemoryValue(posModule))
					.apply(context, (walkTarget, pos) -> (world, entity, time) -> {
							GlobalPos globalPos = context.getValue(pos);
							if (world.getRegistryKey() != globalPos.getDimension() || !globalPos.getPos().isWithinDistance(entity.getPos(), (double)maxDistance)) {
								return false;
							} else if (time <= mutableLong.getValue()) {
								return true;
							} else {
								walkTarget.remember(new WalkTarget(globalPos.getPos(), walkSpeed, completionRange));
								mutableLong.setValue(time + 80L);
								return true;
							}
						})
		);
	}
}
