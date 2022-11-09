package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import org.apache.commons.lang3.mutable.MutableInt;

public class ForgetBellRingTask {
	private static final int MIN_HEARD_BELL_TIME = 300;

	public static Task<LivingEntity> create(int maxHiddenSeconds, int distance) {
		int i = maxHiddenSeconds * 20;
		MutableInt mutableInt = new MutableInt(0);
		return TaskTriggerer.task(
			context -> context.group(context.queryMemoryValue(MemoryModuleType.HIDING_PLACE), context.queryMemoryValue(MemoryModuleType.HEARD_BELL_TIME))
					.apply(context, (hidingPlace, heardBellTime) -> (world, entity, time) -> {
							long l = context.<Long>getValue(heardBellTime);
							boolean bl = l + 300L <= time;
							if (mutableInt.getValue() <= i && !bl) {
								BlockPos blockPos = context.<GlobalPos>getValue(hidingPlace).getPos();
								if (blockPos.isWithinDistance(entity.getBlockPos(), (double)distance)) {
									mutableInt.increment();
								}

								return true;
							} else {
								heardBellTime.forget();
								hidingPlace.forget();
								entity.getBrain().refreshActivities(world.getTimeOfDay(), world.getTime());
								mutableInt.setValue(0);
								return true;
							}
						})
		);
	}
}
