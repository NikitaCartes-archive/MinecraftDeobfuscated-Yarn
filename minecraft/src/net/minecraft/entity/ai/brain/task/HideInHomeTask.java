package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestTypes;

public class HideInHomeTask {
	public static SingleTickTask<LivingEntity> create(int maxDistance, float walkSpeed, int preferredDistance) {
		return TaskTriggerer.task(
			context -> context.group(
						context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET),
						context.queryMemoryOptional(MemoryModuleType.HOME),
						context.queryMemoryOptional(MemoryModuleType.HIDING_PLACE),
						context.queryMemoryOptional(MemoryModuleType.PATH),
						context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET),
						context.queryMemoryOptional(MemoryModuleType.BREED_TARGET),
						context.queryMemoryOptional(MemoryModuleType.INTERACTION_TARGET)
					)
					.apply(
						context,
						(walkTarget, home, hidingPlace, path, lookTarget, breedTarget, interactionTarget) -> (world, entity, time) -> {
								world.getPointOfInterestStorage()
									.getPosition(
										poiType -> poiType.matchesKey(PointOfInterestTypes.HOME),
										pos -> true,
										entity.getBlockPos(),
										preferredDistance + 1,
										PointOfInterestStorage.OccupationStatus.ANY
									)
									.filter(pos -> pos.isWithinDistance(entity.getPos(), (double)preferredDistance))
									.or(
										() -> world.getPointOfInterestStorage()
												.getPosition(
													poiType -> poiType.matchesKey(PointOfInterestTypes.HOME),
													pos -> true,
													PointOfInterestStorage.OccupationStatus.ANY,
													entity.getBlockPos(),
													maxDistance,
													entity.getRandom()
												)
									)
									.or(() -> context.getOptionalValue(home).map(GlobalPos::getPos))
									.ifPresent(pos -> {
										path.forget();
										lookTarget.forget();
										breedTarget.forget();
										interactionTarget.forget();
										hidingPlace.remember(GlobalPos.create(world.getRegistryKey(), pos));
										if (!pos.isWithinDistance(entity.getPos(), (double)preferredDistance)) {
											walkTarget.remember(new WalkTarget(pos, walkSpeed, preferredDistance));
										}
									});
								return true;
							}
					)
		);
	}
}
