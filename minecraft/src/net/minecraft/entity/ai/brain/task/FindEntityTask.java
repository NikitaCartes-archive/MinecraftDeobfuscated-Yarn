package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Set;
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
	private final int field_18355;
	private final float field_18356;
	private final EntityType<? extends T> mobType;
	private final int maxDistanceSq;
	private final Predicate<T> filter;
	private final Predicate<E> field_18360;
	private final MemoryModuleType<T> memoryModuleType;

	public FindEntityTask(
		EntityType<? extends T> entityType, int i, Predicate<E> predicate, Predicate<T> predicate2, MemoryModuleType<T> memoryModuleType, float f, int j
	) {
		this.mobType = entityType;
		this.field_18356 = f;
		this.maxDistanceSq = i * i;
		this.field_18355 = j;
		this.filter = predicate2;
		this.field_18360 = predicate;
		this.memoryModuleType = memoryModuleType;
	}

	public static <T extends LivingEntity> FindEntityTask<LivingEntity, T> method_18941(
		EntityType<? extends T> entityType, int i, MemoryModuleType<T> memoryModuleType, float f, int j
	) {
		return new FindEntityTask<>(entityType, i, livingEntity -> true, livingEntity -> true, memoryModuleType, f, j);
	}

	@Override
	protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
		return ImmutableSet.of(
			Pair.of(MemoryModuleType.field_18446, MemoryModuleState.field_18458),
			Pair.of(MemoryModuleType.field_18445, MemoryModuleState.field_18457),
			Pair.of(this.memoryModuleType, MemoryModuleState.field_18457),
			Pair.of(MemoryModuleType.field_18442, MemoryModuleState.field_18456)
		);
	}

	@Override
	protected boolean shouldRun(ServerWorld serverWorld, E livingEntity) {
		return this.field_18360.test(livingEntity)
			&& ((List)livingEntity.getBrain().getMemory(MemoryModuleType.field_18442).get())
				.stream()
				.anyMatch(livingEntityx -> this.mobType.equals(livingEntityx.getType()) && this.filter.test(livingEntityx));
	}

	@Override
	protected void run(ServerWorld serverWorld, E livingEntity, long l) {
		Brain<?> brain = livingEntity.getBrain();
		brain.getMemory(MemoryModuleType.field_18442)
			.ifPresent(
				list -> list.stream()
						.filter(livingEntityxx -> this.mobType.equals(livingEntityxx.getType()))
						.map(livingEntityxx -> livingEntityxx)
						.filter(livingEntity2 -> livingEntity2.squaredDistanceTo(livingEntity) <= (double)this.maxDistanceSq)
						.filter(this.filter)
						.findFirst()
						.ifPresent(livingEntityxx -> {
							brain.putMemory(this.memoryModuleType, (T)livingEntityxx);
							brain.putMemory(MemoryModuleType.field_18446, new EntityPosWrapper(livingEntityxx));
							brain.putMemory(MemoryModuleType.field_18445, new WalkTarget(new EntityPosWrapper(livingEntityxx), this.field_18356, this.field_18355));
						})
			);
	}
}
