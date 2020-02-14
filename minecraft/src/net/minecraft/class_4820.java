package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class class_4820<E extends LivingEntity> extends Task<E> {
	private final Predicate<E> field_22313;
	private final Task<? super E> field_22314;
	private final boolean field_22315;

	public class_4820(Map<MemoryModuleType<?>, MemoryModuleState> map, Predicate<E> predicate, Task<? super E> task, boolean bl) {
		super(method_24597(map, task.requiredMemoryState));
		this.field_22313 = predicate;
		this.field_22314 = task;
		this.field_22315 = bl;
	}

	private static Map<MemoryModuleType<?>, MemoryModuleState> method_24597(
		Map<MemoryModuleType<?>, MemoryModuleState> map, Map<MemoryModuleType<?>, MemoryModuleState> map2
	) {
		Map<MemoryModuleType<?>, MemoryModuleState> map3 = Maps.<MemoryModuleType<?>, MemoryModuleState>newHashMap();
		map3.putAll(map);
		map3.putAll(map2);
		return map3;
	}

	public class_4820(Predicate<E> predicate, Task<? super E> task) {
		this(ImmutableMap.of(), predicate, task, false);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E entity) {
		return this.field_22313.test(entity) && this.field_22314.shouldRun(world, entity);
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, E entity, long time) {
		return this.field_22315 && this.field_22313.test(entity) && this.field_22314.shouldKeepRunning(world, entity, time);
	}

	@Override
	protected boolean isTimeLimitExceeded(long time) {
		return false;
	}

	@Override
	protected void run(ServerWorld world, E entity, long time) {
		this.field_22314.run(world, entity, time);
	}

	@Override
	protected void keepRunning(ServerWorld world, E entity, long time) {
		this.field_22314.keepRunning(world, entity, time);
	}

	@Override
	protected void finishRunning(ServerWorld world, E entity, long time) {
		this.field_22314.finishRunning(world, entity, time);
	}

	@Override
	public String toString() {
		return "RunIf: " + this.field_22314;
	}
}
