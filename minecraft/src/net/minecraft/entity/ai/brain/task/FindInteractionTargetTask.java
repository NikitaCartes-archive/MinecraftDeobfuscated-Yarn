package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public class FindInteractionTargetTask extends Task<LivingEntity> {
	private final EntityType<?> entityType;
	private final int maxSquaredDistance;
	private final Predicate<LivingEntity> predicate;
	private final Predicate<LivingEntity> shouldRunPredicate;

	public FindInteractionTargetTask(EntityType<?> entityType, int maxDistance, Predicate<LivingEntity> shouldRunPredicate, Predicate<LivingEntity> predicate) {
		super(
			ImmutableMap.of(
				MemoryModuleType.LOOK_TARGET,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.INTERACTION_TARGET,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.VISIBLE_MOBS,
				MemoryModuleState.VALUE_PRESENT
			)
		);
		this.entityType = entityType;
		this.maxSquaredDistance = maxDistance * maxDistance;
		this.predicate = predicate;
		this.shouldRunPredicate = shouldRunPredicate;
	}

	public FindInteractionTargetTask(EntityType<?> entityType, int maxDistance) {
		this(entityType, maxDistance, livingEntity -> true, livingEntity -> true);
	}

	@Override
	public boolean shouldRun(ServerWorld world, LivingEntity entity) {
		return this.shouldRunPredicate.test(entity) && this.getVisibleMobs(entity).stream().anyMatch(this::test);
	}

	@Override
	public void run(ServerWorld world, LivingEntity entity, long time) {
		super.run(world, entity, time);
		Brain<?> brain = entity.getBrain();
		brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS)
			.ifPresent(
				list -> list.stream()
						.filter(livingEntity2 -> livingEntity2.squaredDistanceTo(entity) <= (double)this.maxSquaredDistance)
						.filter(this::test)
						.findFirst()
						.ifPresent(livingEntityx -> {
							brain.remember(MemoryModuleType.INTERACTION_TARGET, livingEntityx);
							brain.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(livingEntityx, true));
						})
			);
	}

	private boolean test(LivingEntity entity) {
		return this.entityType.equals(entity.getType()) && this.predicate.test(entity);
	}

	private List<LivingEntity> getVisibleMobs(LivingEntity entity) {
		return (List<LivingEntity>)entity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).get();
	}
}
