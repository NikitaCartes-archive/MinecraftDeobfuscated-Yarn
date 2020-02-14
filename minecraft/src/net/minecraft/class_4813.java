package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class class_4813<E extends LivingEntity> extends Task<E> {
	private final Predicate<E> field_22302;
	private final MemoryModuleType<?> field_22303;

	public class_4813(Predicate<E> predicate, MemoryModuleType<?> memoryModuleType) {
		super(ImmutableMap.of(memoryModuleType, MemoryModuleState.VALUE_PRESENT));
		this.field_22302 = predicate;
		this.field_22303 = memoryModuleType;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E entity) {
		return this.field_22302.test(entity);
	}

	@Override
	protected void run(ServerWorld world, E entity, long time) {
		entity.getBrain().forget(this.field_22303);
	}
}
