package net.minecraft;

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
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class class_4106<E extends LivingEntity, T extends LivingEntity> extends Task<E> {
	private final int field_18355;
	private final float field_18356;
	private final EntityType<? extends T> field_18357;
	private final int field_18358;
	private final Predicate<T> field_18359;
	private final Predicate<E> field_18360;
	private final MemoryModuleType<T> field_18361;

	public class_4106(
		EntityType<? extends T> entityType, int i, Predicate<E> predicate, Predicate<T> predicate2, MemoryModuleType<T> memoryModuleType, float f, int j
	) {
		this.field_18357 = entityType;
		this.field_18356 = f;
		this.field_18358 = i * i;
		this.field_18355 = j;
		this.field_18359 = predicate2;
		this.field_18360 = predicate;
		this.field_18361 = memoryModuleType;
	}

	public static <T extends LivingEntity> class_4106<LivingEntity, T> method_18941(
		EntityType<? extends T> entityType, int i, MemoryModuleType<T> memoryModuleType, float f, int j
	) {
		return new class_4106<>(entityType, i, livingEntity -> true, livingEntity -> true, memoryModuleType, f, j);
	}

	@Override
	protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
		return ImmutableSet.of(
			Pair.of(MemoryModuleType.field_18446, MemoryModuleState.field_18458),
			Pair.of(MemoryModuleType.field_18445, MemoryModuleState.field_18457),
			Pair.of(this.field_18361, MemoryModuleState.field_18457),
			Pair.of(MemoryModuleType.field_18442, MemoryModuleState.field_18456)
		);
	}

	@Override
	protected boolean shouldRun(ServerWorld serverWorld, E livingEntity) {
		return this.field_18360.test(livingEntity)
			&& ((List)livingEntity.getBrain().getMemory(MemoryModuleType.field_18442).get())
				.stream()
				.anyMatch(livingEntityx -> this.field_18357.equals(livingEntityx.getType()) && this.field_18359.test(livingEntityx));
	}

	@Override
	protected void run(ServerWorld serverWorld, E livingEntity, long l) {
		Brain<?> brain = livingEntity.getBrain();
		brain.getMemory(MemoryModuleType.field_18442)
			.ifPresent(
				list -> list.stream()
						.filter(livingEntityxx -> this.field_18357.equals(livingEntityxx.getType()))
						.map(livingEntityxx -> livingEntityxx)
						.filter(livingEntity2 -> livingEntity2.squaredDistanceTo(livingEntity) <= (double)this.field_18358)
						.filter(this.field_18359)
						.findFirst()
						.ifPresent(livingEntityxx -> {
							brain.putMemory(this.field_18361, (T)livingEntityxx);
							brain.putMemory(MemoryModuleType.field_18446, new EntityPosWrapper(livingEntityxx));
							brain.putMemory(MemoryModuleType.field_18445, new WalkTarget(new EntityPosWrapper(livingEntityxx), this.field_18356, this.field_18355));
						})
			);
	}
}
