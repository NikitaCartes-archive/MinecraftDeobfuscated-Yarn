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
		EntityType<? extends T> entityType, int i, Predicate<E> predicate, Predicate<T> predicate2, MemoryModuleType<T> memoryModuleType, float f, int j
	) {
		super(
			ImmutableMap.of(
				MemoryModuleType.LOOK_TARGET,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.WALK_TARGET,
				MemoryModuleState.VALUE_ABSENT,
				memoryModuleType,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.VISIBLE_MOBS,
				MemoryModuleState.VALUE_PRESENT
			)
		);
		this.entityType = entityType;
		this.speed = f;
		this.maxSquaredDistance = i * i;
		this.completionRange = j;
		this.predicate = predicate2;
		this.shouldRunPredicate = predicate;
		this.targetModule = memoryModuleType;
	}

	public static <T extends LivingEntity> FindEntityTask<LivingEntity, T> create(
		EntityType<? extends T> entityType, int i, MemoryModuleType<T> memoryModuleType, float f, int j
	) {
		return new FindEntityTask<>(entityType, i, livingEntity -> true, livingEntity -> true, memoryModuleType, f, j);
	}

	@Override
	protected boolean shouldRun(ServerWorld serverWorld, E livingEntity) {
		return this.shouldRunPredicate.test(livingEntity)
			&& ((List)livingEntity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).get())
				.stream()
				.anyMatch(livingEntityx -> this.entityType.equals(livingEntityx.getType()) && this.predicate.test(livingEntityx));
	}

	@Override
	protected void run(ServerWorld serverWorld, E livingEntity, long l) {
		Brain<?> brain = livingEntity.getBrain();
		brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS)
			.ifPresent(
				list -> list.stream()
						.filter(livingEntityxx -> this.entityType.equals(livingEntityxx.getType()))
						.map(livingEntityxx -> livingEntityxx)
						.filter(livingEntity2 -> livingEntity2.squaredDistanceTo(livingEntity) <= (double)this.maxSquaredDistance)
						.filter(this.predicate)
						.findFirst()
						.ifPresent(livingEntityxx -> {
							brain.putMemory(this.targetModule, (T)livingEntityxx);
							brain.putMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(livingEntityxx));
							brain.putMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityPosWrapper(livingEntityxx), this.speed, this.completionRange));
						})
			);
	}
}
