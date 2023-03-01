package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;

public class LookAtMobTask {
	public static Task<LivingEntity> create(SpawnGroup spawnGroup, float maxDistance) {
		return create(entity -> spawnGroup.equals(entity.getType().getSpawnGroup()), maxDistance);
	}

	public static SingleTickTask<LivingEntity> create(EntityType<?> type, float maxDistance) {
		return create(entity -> type.equals(entity.getType()), maxDistance);
	}

	public static SingleTickTask<LivingEntity> create(float maxDistance) {
		return create(entity -> true, maxDistance);
	}

	public static SingleTickTask<LivingEntity> create(Predicate<LivingEntity> predicate, float maxDistance) {
		float f = maxDistance * maxDistance;
		return TaskTriggerer.task(
			context -> context.group(context.queryMemoryAbsent(MemoryModuleType.LOOK_TARGET), context.queryMemoryValue(MemoryModuleType.VISIBLE_MOBS))
					.apply(
						context,
						(lookTarget, visibleMobs) -> (world, entity, time) -> {
								Optional<LivingEntity> optional = context.<LivingTargetCache>getValue(visibleMobs)
									.findFirst(predicate.and(target -> target.squaredDistanceTo(entity) <= (double)f && !entity.hasPassenger(target)));
								if (optional.isEmpty()) {
									return false;
								} else {
									lookTarget.remember(new EntityLookTarget((Entity)optional.get(), true));
									return true;
								}
							}
					)
		);
	}
}
