package net.minecraft.world;

import java.util.function.Function;
import net.minecraft.util.TaskPriority;
import net.minecraft.util.math.BlockPos;

public class MultiTickScheduler<T> implements TickScheduler<T> {
	private final Function<BlockPos, TickScheduler<T>> mapper;

	public MultiTickScheduler(Function<BlockPos, TickScheduler<T>> function) {
		this.mapper = function;
	}

	@Override
	public boolean method_8674(BlockPos blockPos, T object) {
		return ((TickScheduler)this.mapper.apply(blockPos)).method_8674(blockPos, object);
	}

	@Override
	public void method_8675(BlockPos blockPos, T object, int i, TaskPriority taskPriority) {
		((TickScheduler)this.mapper.apply(blockPos)).method_8675(blockPos, object, i, taskPriority);
	}

	@Override
	public boolean method_8677(BlockPos blockPos, T object) {
		return false;
	}
}
