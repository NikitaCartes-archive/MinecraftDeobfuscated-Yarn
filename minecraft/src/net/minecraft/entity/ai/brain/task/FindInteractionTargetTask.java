package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityPosWrapper;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public class FindInteractionTargetTask extends Task<LivingEntity> {
	private final EntityType<?> entityType;
	private final int maxSquaredDistance;
	private final Predicate<LivingEntity> predicate;
	private final Predicate<LivingEntity> shouldRunPredicate;

	public FindInteractionTargetTask(EntityType<?> entityType, int i, Predicate<LivingEntity> predicate, Predicate<LivingEntity> predicate2) {
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
		this.maxSquaredDistance = i * i;
		this.predicate = predicate2;
		this.shouldRunPredicate = predicate;
	}

	public FindInteractionTargetTask(EntityType<?> entityType, int i) {
		this(entityType, i, livingEntity -> true, livingEntity -> true);
	}

	@Override
	public boolean shouldRun(ServerWorld serverWorld, LivingEntity livingEntity) {
		return this.shouldRunPredicate.test(livingEntity) && this.getVisibleMobs(livingEntity).stream().anyMatch(this::test);
	}

	@Override
	public void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		super.run(serverWorld, livingEntity, l);
		Brain<?> brain = livingEntity.getBrain();
		brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS)
			.ifPresent(
				list -> list.stream()
						.filter(livingEntity2 -> livingEntity2.squaredDistanceTo(livingEntity) <= (double)this.maxSquaredDistance)
						.filter(this::test)
						.findFirst()
						.ifPresent(livingEntityxx -> {
							brain.putMemory(MemoryModuleType.INTERACTION_TARGET, livingEntityxx);
							brain.putMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(livingEntityxx));
						})
			);
	}

	private boolean test(LivingEntity livingEntity) {
		return this.entityType.equals(livingEntity.getType()) && this.predicate.test(livingEntity);
	}

	private List<LivingEntity> getVisibleMobs(LivingEntity livingEntity) {
		return (List<LivingEntity>)livingEntity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).get();
	}
}
