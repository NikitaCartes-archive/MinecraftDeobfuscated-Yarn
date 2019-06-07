package net.minecraft.world;

import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.TaskPriority;
import net.minecraft.util.math.BlockPos;

public class MultiTickScheduler<T> implements TickScheduler<T> {
	private final Function<BlockPos, TickScheduler<T>> mapper;

	public MultiTickScheduler(Function<BlockPos, TickScheduler<T>> function) {
		this.mapper = function;
	}

	@Override
	public boolean isScheduled(BlockPos blockPos, T object) {
		return ((TickScheduler)this.mapper.apply(blockPos)).isScheduled(blockPos, object);
	}

	@Override
	public void schedule(BlockPos blockPos, T object, int i, TaskPriority taskPriority) {
		((TickScheduler)this.mapper.apply(blockPos)).schedule(blockPos, object, i, taskPriority);
	}

	@Override
	public boolean isTicking(BlockPos blockPos, T object) {
		return false;
	}

	@Override
	public void scheduleAll(Stream<ScheduledTick<T>> stream) {
		stream.forEach(scheduledTick -> ((TickScheduler)this.mapper.apply(scheduledTick.pos)).scheduleAll(Stream.of(scheduledTick)));
	}
}
