package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class MemoryTransferTask<E extends MobEntity, T> extends Task<E> {
	private final Predicate<E> runPredicate;
	private final MemoryModuleType<? extends T> sourceType;
	private final MemoryModuleType<T> targetType;
	private final UniformIntProvider duration;

	public MemoryTransferTask(Predicate<E> runPredicate, MemoryModuleType<? extends T> sourceType, MemoryModuleType<T> targetType, UniformIntProvider duration) {
		super(ImmutableMap.of(sourceType, MemoryModuleState.VALUE_PRESENT, targetType, MemoryModuleState.VALUE_ABSENT));
		this.runPredicate = runPredicate;
		this.sourceType = sourceType;
		this.targetType = targetType;
		this.duration = duration;
	}

	protected boolean shouldRun(ServerWorld serverWorld, E mobEntity) {
		return this.runPredicate.test(mobEntity);
	}

	protected void run(ServerWorld serverWorld, E mobEntity, long l) {
		Brain<?> brain = mobEntity.getBrain();
		brain.remember(this.targetType, (T)brain.getOptionalMemory(this.sourceType).get(), (long)this.duration.get(serverWorld.random));
	}
}
