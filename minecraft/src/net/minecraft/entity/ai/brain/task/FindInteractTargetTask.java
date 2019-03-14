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
import net.minecraft.server.world.ServerWorld;

public class FindInteractTargetTask extends Task<LivingEntity> {
	private final EntityType<?> field_18363;
	private final int field_18364;
	private final Predicate<LivingEntity> field_18365;
	private final Predicate<LivingEntity> field_18366;

	public FindInteractTargetTask(EntityType<?> entityType, int i, Predicate<LivingEntity> predicate, Predicate<LivingEntity> predicate2) {
		this.field_18363 = entityType;
		this.field_18364 = i * i;
		this.field_18365 = predicate2;
		this.field_18366 = predicate;
	}

	public FindInteractTargetTask(EntityType<?> entityType, int i) {
		this(entityType, i, livingEntity -> true, livingEntity -> true);
	}

	@Override
	public Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
		return ImmutableSet.of(
			Pair.of(MemoryModuleType.field_18446, MemoryModuleState.field_18458),
			Pair.of(MemoryModuleType.field_18447, MemoryModuleState.field_18457),
			Pair.of(MemoryModuleType.field_18442, MemoryModuleState.field_18456)
		);
	}

	@Override
	public boolean shouldRun(ServerWorld serverWorld, LivingEntity livingEntity) {
		return this.field_18366.test(livingEntity)
			&& ((List)livingEntity.getBrain().getMemory(MemoryModuleType.field_18442).get())
				.stream()
				.anyMatch(livingEntityx -> this.field_18363.equals(livingEntityx.getType()) && this.field_18365.test(livingEntityx));
	}

	@Override
	public void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		super.run(serverWorld, livingEntity, l);
		Brain<?> brain = livingEntity.getBrain();
		brain.getMemory(MemoryModuleType.field_18442)
			.ifPresent(
				list -> list.stream()
						.filter(livingEntityxx -> this.field_18363.equals(livingEntityxx.getType()))
						.filter(livingEntity2 -> livingEntity2.squaredDistanceTo(livingEntity) <= (double)this.field_18364)
						.filter(this.field_18365)
						.findFirst()
						.ifPresent(livingEntityxx -> {
							brain.putMemory(MemoryModuleType.field_18447, livingEntityxx);
							brain.putMemory(MemoryModuleType.field_18446, new EntityPosWrapper(livingEntityxx));
						})
			);
	}
}
