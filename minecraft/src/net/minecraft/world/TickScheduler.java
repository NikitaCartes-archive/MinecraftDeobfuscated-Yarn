package net.minecraft.world;

import net.minecraft.util.TaskPriority;
import net.minecraft.util.math.BlockPos;

public interface TickScheduler<T> {
	boolean method_8674(BlockPos blockPos, T object);

	default void method_8676(BlockPos blockPos, T object, int i) {
		this.method_8675(blockPos, object, i, TaskPriority.field_9314);
	}

	void method_8675(BlockPos blockPos, T object, int i, TaskPriority taskPriority);

	boolean method_8677(BlockPos blockPos, T object);
}
