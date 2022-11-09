package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.mutable.MutableLong;

public class GoToIfNearbyTask {
	private static final int UPDATE_INTERVAL = 180;
	private static final int HORIZONTAL_RANGE = 8;
	private static final int VERTICAL_RANGE = 6;

	public static SingleTickTask<PathAwareEntity> create(MemoryModuleType<GlobalPos> posModule, float walkSpeed, int maxDistance) {
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
								Optional<Vec3d> optional = Optional.ofNullable(FuzzyTargeting.find(entity, 8, 6));
								walkTarget.remember(optional.map(targetPos -> new WalkTarget(targetPos, walkSpeed, 1)));
								mutableLong.setValue(time + 180L);
								return true;
							}
						})
		);
	}
}
