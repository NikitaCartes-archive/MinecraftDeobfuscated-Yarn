package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class TimeLimitedTask<E extends LivingEntity> extends Task<E> {
	private boolean needsTimeReset;
	private boolean delegateRunning;
	private final UniformIntProvider timeRange;
	private final Task<? super E> delegate;
	private int timeLeft;

	public TimeLimitedTask(Task<? super E> delegate, UniformIntProvider timeRange) {
		this(delegate, false, timeRange);
	}

	public TimeLimitedTask(Task<? super E> delegate, boolean skipFirstRun, UniformIntProvider timeRange) {
		super(delegate.requiredMemoryStates);
		this.delegate = delegate;
		this.needsTimeReset = !skipFirstRun;
		this.timeRange = timeRange;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E entity) {
		if (!this.delegate.shouldRun(world, entity)) {
			return false;
		} else {
			if (this.needsTimeReset) {
				this.resetTimeLeft(world);
				this.needsTimeReset = false;
			}

			if (this.timeLeft > 0) {
				this.timeLeft--;
			}

			return !this.delegateRunning && this.timeLeft == 0;
		}
	}

	@Override
	protected void run(ServerWorld world, E entity, long time) {
		this.delegate.run(world, entity, time);
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, E entity, long time) {
		return this.delegate.shouldKeepRunning(world, entity, time);
	}

	@Override
	protected void keepRunning(ServerWorld world, E entity, long time) {
		this.delegate.keepRunning(world, entity, time);
		this.delegateRunning = this.delegate.getStatus() == Task.Status.RUNNING;
	}

	@Override
	protected void finishRunning(ServerWorld world, E entity, long time) {
		this.resetTimeLeft(world);
		this.delegate.finishRunning(world, entity, time);
	}

	private void resetTimeLeft(ServerWorld world) {
		this.timeLeft = this.timeRange.get(world.random);
	}

	@Override
	protected boolean isTimeLimitExceeded(long time) {
		return false;
	}

	@Override
	public String toString() {
		return "RunSometimes: " + this.delegate;
	}
}
