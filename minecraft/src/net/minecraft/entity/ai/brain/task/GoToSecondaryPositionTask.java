package net.minecraft.entity.ai.brain.task;

import java.util.List;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.math.GlobalPos;
import org.apache.commons.lang3.mutable.MutableLong;

public class GoToSecondaryPositionTask {
	public static Task<VillagerEntity> create(
		MemoryModuleType<List<GlobalPos>> secondaryPositions,
		float speed,
		int completionRange,
		int primaryPositionActivationDistance,
		MemoryModuleType<GlobalPos> primaryPosition
	) {
		MutableLong mutableLong = new MutableLong(0L);
		return TaskTriggerer.task(
			context -> context.group(
						context.queryMemoryOptional(MemoryModuleType.WALK_TARGET), context.queryMemoryValue(secondaryPositions), context.queryMemoryValue(primaryPosition)
					)
					.apply(
						context,
						(walkTarget, secondary, primary) -> (world, entity, time) -> {
								List<GlobalPos> list = context.getValue(secondary);
								GlobalPos globalPos = context.getValue(primary);
								if (list.isEmpty()) {
									return false;
								} else {
									GlobalPos globalPos2 = (GlobalPos)list.get(world.getRandom().nextInt(list.size()));
									if (globalPos2 != null
										&& world.getRegistryKey() == globalPos2.getDimension()
										&& globalPos.getPos().isWithinDistance(entity.getPos(), (double)primaryPositionActivationDistance)) {
										if (time > mutableLong.getValue()) {
											walkTarget.remember(new WalkTarget(globalPos2.getPos(), speed, completionRange));
											mutableLong.setValue(time + 100L);
										}

										return true;
									} else {
										return false;
									}
								}
							}
					)
		);
	}
}
