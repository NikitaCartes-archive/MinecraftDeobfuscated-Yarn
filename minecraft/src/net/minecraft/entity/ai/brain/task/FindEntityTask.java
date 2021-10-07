package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.class_6670;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.server.world.ServerWorld;

public class FindEntityTask<E extends LivingEntity, T extends LivingEntity> extends Task<E> {
	private final int completionRange;
	private final float speed;
	private final EntityType<? extends T> entityType;
	private final int maxSquaredDistance;
	private final Predicate<T> predicate;
	private final Predicate<E> shouldRunPredicate;
	private final MemoryModuleType<T> targetModule;

	public FindEntityTask(
		EntityType<? extends T> entityType,
		int maxDistance,
		Predicate<E> shouldRunPredicate,
		Predicate<T> predicate,
		MemoryModuleType<T> targetModule,
		float speed,
		int completionRange
	) {
		super(
			ImmutableMap.of(
				MemoryModuleType.LOOK_TARGET,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.WALK_TARGET,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.VISIBLE_MOBS,
				MemoryModuleState.VALUE_PRESENT
			)
		);
		this.entityType = entityType;
		this.speed = speed;
		this.maxSquaredDistance = maxDistance * maxDistance;
		this.completionRange = completionRange;
		this.predicate = predicate;
		this.shouldRunPredicate = shouldRunPredicate;
		this.targetModule = targetModule;
	}

	public static <T extends LivingEntity> FindEntityTask<LivingEntity, T> create(
		EntityType<? extends T> entityType, int maxDistance, MemoryModuleType<T> targetModule, float speed, int completionRange
	) {
		return new FindEntityTask<>(entityType, maxDistance, entity -> true, entity -> true, targetModule, speed, completionRange);
	}

	public static <T extends LivingEntity> FindEntityTask<LivingEntity, T> create(
		EntityType<? extends T> entityType, int maxDistance, Predicate<T> condition, MemoryModuleType<T> moduleType, float speed, int completionRange
	) {
		return new FindEntityTask<>(entityType, maxDistance, entity -> true, condition, moduleType, speed, completionRange);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E entity) {
		return this.shouldRunPredicate.test(entity) && this.anyVisibleTo(entity);
	}

	private boolean anyVisibleTo(E entity) {
		class_6670 lv = (class_6670)entity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).get();
		return lv.method_38981(this::testPredicate);
	}

	private boolean testPredicate(LivingEntity entity) {
		return this.entityType.equals(entity.getType()) && this.predicate.test(entity);
	}

	@Override
	protected void run(ServerWorld world, E entity, long time) {
		Brain<?> brain = entity.getBrain();
		Optional<class_6670> optional = brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS);
		if (!optional.isEmpty()) {
			class_6670 lv = (class_6670)optional.get();
			lv.method_38975(target -> this.shouldTarget(entity, target)).ifPresent(target -> {
				brain.remember(this.targetModule, (T)target);
				brain.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(target, true));
				brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityLookTarget(target, false), this.speed, this.completionRange));
			});
		}
	}

	private boolean shouldTarget(E self, LivingEntity target) {
		return this.entityType.equals(target.getType()) && target.squaredDistanceTo(self) <= (double)this.maxSquaredDistance && this.predicate.test(target);
	}
}
