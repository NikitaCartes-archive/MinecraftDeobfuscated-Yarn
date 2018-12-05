package net.minecraft.world;

import net.minecraft.util.TaskPriority;
import net.minecraft.util.math.BlockPos;

public interface TickScheduler<T> {
	boolean isScheduled(BlockPos blockPos, T object);

	default void schedule(BlockPos blockPos, T object, int i) {
		this.schedule(blockPos, object, i, TaskPriority.field_9314);
	}

	void schedule(BlockPos blockPos, T object, int i, TaskPriority taskPriority);

	boolean isTicking(BlockPos blockPos, T object);
}
