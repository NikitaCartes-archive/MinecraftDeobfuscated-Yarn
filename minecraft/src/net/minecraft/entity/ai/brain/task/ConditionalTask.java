package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public class ConditionalTask<E extends LivingEntity> extends Task<E> {
	private final Predicate<E> condition;
	private final Task<? super E> delegate;
	private final boolean allowsContinuation;

	public ConditionalTask(
		Map<MemoryModuleType<?>, MemoryModuleState> requiredMemoryStates, Predicate<E> condition, Task<? super E> delegate, boolean allowsContinuation
	) {
		super(merge(requiredMemoryStates, delegate.requiredMemoryStates));
		this.condition = condition;
		this.delegate = delegate;
		this.allowsContinuation = allowsContinuation;
	}

	private static Map<MemoryModuleType<?>, MemoryModuleState> merge(
		Map<MemoryModuleType<?>, MemoryModuleState> first, Map<MemoryModuleType<?>, MemoryModuleState> second
	) {
		Map<MemoryModuleType<?>, MemoryModuleState> map = Maps.newHashMap();
		map.putAll(first);
		map.putAll(second);
		return map;
	}

	public ConditionalTask(Predicate<E> condition, Task<? super E> delegate) {
		this(ImmutableMap.of(), condition, delegate, false);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E entity) {
		return this.condition.test(entity) && this.delegate.shouldRun(world, entity);
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, E entity, long time) {
		return this.allowsContinuation && this.condition.test(entity) && this.delegate.shouldKeepRunning(world, entity, time);
	}

	@Override
	protected boolean isTimeLimitExceeded(long time) {
		return false;
	}

	@Override
	protected void run(ServerWorld world, E entity, long time) {
		this.delegate.run(world, entity, time);
	}

	@Override
	protected void keepRunning(ServerWorld world, E entity, long time) {
		this.delegate.keepRunning(world, entity, time);
	}

	@Override
	protected void finishRunning(ServerWorld world, E entity, long time) {
		this.delegate.finishRunning(world, entity, time);
	}

	@Override
	public String toString() {
		return "RunIf: " + this.delegate;
	}
}
