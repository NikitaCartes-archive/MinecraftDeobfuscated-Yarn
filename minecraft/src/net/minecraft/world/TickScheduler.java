package net.minecraft.world;

import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public interface TickScheduler<T> {
	boolean isScheduled(BlockPos pos, T object);

	default void schedule(BlockPos pos, T object, int delay) {
		this.schedule(pos, object, delay, TickPriority.NORMAL);
	}

	void schedule(BlockPos pos, T object, int delay, TickPriority priority);

	boolean isTicking(BlockPos pos, T object);

	void scheduleAll(Stream<ScheduledTick<T>> stream);
}
