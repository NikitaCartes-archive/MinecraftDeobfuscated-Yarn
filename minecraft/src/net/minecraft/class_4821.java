package net.minecraft;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class class_4821<E extends LivingEntity> extends Task<E> {
	private boolean field_22316;
	private boolean field_22317;
	private final class_4801 field_22318;
	private final Task<? super E> field_22319;
	private int field_22320;

	public class_4821(Task<? super E> task, class_4801 arg) {
		this(task, false, arg);
	}

	public class_4821(Task<? super E> task, boolean bl, class_4801 arg) {
		super(task.requiredMemoryState);
		this.field_22319 = task;
		this.field_22316 = !bl;
		this.field_22318 = arg;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E entity) {
		if (!this.field_22319.shouldRun(world, entity)) {
			return false;
		} else {
			if (this.field_22316) {
				this.method_24598(world);
				this.field_22316 = false;
			}

			if (this.field_22320 > 0) {
				this.field_22320--;
			}

			return !this.field_22317 && this.field_22320 == 0;
		}
	}

	@Override
	protected void run(ServerWorld world, E entity, long time) {
		this.field_22319.run(world, entity, time);
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, E entity, long time) {
		return this.field_22319.shouldKeepRunning(world, entity, time);
	}

	@Override
	protected void keepRunning(ServerWorld world, E entity, long time) {
		this.field_22319.keepRunning(world, entity, time);
		this.field_22317 = this.field_22319.getStatus() == Task.Status.RUNNING;
	}

	@Override
	protected void finishRunning(ServerWorld world, E entity, long time) {
		this.method_24598(world);
		this.field_22319.finishRunning(world, entity, time);
	}

	private void method_24598(ServerWorld serverWorld) {
		this.field_22320 = this.field_22318.method_24503(serverWorld.random);
	}

	@Override
	protected boolean isTimeLimitExceeded(long time) {
		return false;
	}

	@Override
	public String toString() {
		return "RunSometimes: " + this.field_22319;
	}
}
