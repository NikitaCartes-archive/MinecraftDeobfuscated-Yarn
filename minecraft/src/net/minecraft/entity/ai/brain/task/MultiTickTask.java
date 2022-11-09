package net.minecraft.entity.ai.brain.task;

import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public abstract class MultiTickTask<E extends LivingEntity> implements Task<E> {
	public static final int DEFAULT_RUN_TIME = 60;
	protected final Map<MemoryModuleType<?>, MemoryModuleState> requiredMemoryStates;
	private MultiTickTask.Status status = MultiTickTask.Status.STOPPED;
	private long endTime;
	private final int minRunTime;
	private final int maxRunTime;

	public MultiTickTask(Map<MemoryModuleType<?>, MemoryModuleState> requiredMemoryState) {
		this(requiredMemoryState, 60);
	}

	public MultiTickTask(Map<MemoryModuleType<?>, MemoryModuleState> requiredMemoryState, int runTime) {
		this(requiredMemoryState, runTime, runTime);
	}

	public MultiTickTask(Map<MemoryModuleType<?>, MemoryModuleState> requiredMemoryState, int minRunTime, int maxRunTime) {
		this.minRunTime = minRunTime;
		this.maxRunTime = maxRunTime;
		this.requiredMemoryStates = requiredMemoryState;
	}

	@Override
	public MultiTickTask.Status getStatus() {
		return this.status;
	}

	@Override
	public final boolean tryStarting(ServerWorld world, E entity, long time) {
		if (this.hasRequiredMemoryState(entity) && this.shouldRun(world, entity)) {
			this.status = MultiTickTask.Status.RUNNING;
			int i = this.minRunTime + world.getRandom().nextInt(this.maxRunTime + 1 - this.minRunTime);
			this.endTime = time + (long)i;
			this.run(world, entity, time);
			return true;
		} else {
			return false;
		}
	}

	protected void run(ServerWorld world, E entity, long time) {
	}

	@Override
	public final void tick(ServerWorld world, E entity, long time) {
		if (!this.isTimeLimitExceeded(time) && this.shouldKeepRunning(world, entity, time)) {
			this.keepRunning(world, entity, time);
		} else {
			this.stop(world, entity, time);
		}
	}

	protected void keepRunning(ServerWorld world, E entity, long time) {
	}

	@Override
	public final void stop(ServerWorld world, E entity, long time) {
		this.status = MultiTickTask.Status.STOPPED;
		this.finishRunning(world, entity, time);
	}

	protected void finishRunning(ServerWorld world, E entity, long time) {
	}

	protected boolean shouldKeepRunning(ServerWorld world, E entity, long time) {
		return false;
	}

	protected boolean isTimeLimitExceeded(long time) {
		return time > this.endTime;
	}

	protected boolean shouldRun(ServerWorld world, E entity) {
		return true;
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	protected boolean hasRequiredMemoryState(E entity) {
		for (Entry<MemoryModuleType<?>, MemoryModuleState> entry : this.requiredMemoryStates.entrySet()) {
			MemoryModuleType<?> memoryModuleType = (MemoryModuleType<?>)entry.getKey();
			MemoryModuleState memoryModuleState = (MemoryModuleState)entry.getValue();
			if (!entity.getBrain().isMemoryInState(memoryModuleType, memoryModuleState)) {
				return false;
			}
		}

		return true;
	}

	public static enum Status {
		STOPPED,
		RUNNING;
	}
}
