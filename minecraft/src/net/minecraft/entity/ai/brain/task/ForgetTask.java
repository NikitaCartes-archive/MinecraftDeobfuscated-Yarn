package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public class ForgetTask<E extends LivingEntity> extends Task<E> {
	private final Predicate<E> condition;
	private final MemoryModuleType<?> memory;

	public ForgetTask(Predicate<E> condition, MemoryModuleType<?> memory) {
		super(ImmutableMap.of(memory, MemoryModuleState.VALUE_PRESENT));
		this.condition = condition;
		this.memory = memory;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E entity) {
		return this.condition.test(entity);
	}

	@Override
	protected void run(ServerWorld world, E entity, long time) {
		entity.getBrain().forget(this.memory);
	}
}
