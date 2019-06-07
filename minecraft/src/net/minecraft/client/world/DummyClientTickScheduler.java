package net.minecraft.client.world;

import java.util.stream.Stream;
import net.minecraft.util.TaskPriority;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ScheduledTick;
import net.minecraft.world.TickScheduler;

public class DummyClientTickScheduler<T> implements TickScheduler<T> {
	private static final DummyClientTickScheduler<Object> INSTANCE = new DummyClientTickScheduler<>();

	public static <T> DummyClientTickScheduler<T> get() {
		return (DummyClientTickScheduler<T>)INSTANCE;
	}

	@Override
	public boolean isScheduled(BlockPos blockPos, T object) {
		return false;
	}

	@Override
	public void schedule(BlockPos blockPos, T object, int i) {
	}

	@Override
	public void schedule(BlockPos blockPos, T object, int i, TaskPriority taskPriority) {
	}

	@Override
	public boolean isTicking(BlockPos blockPos, T object) {
		return false;
	}

	@Override
	public void scheduleAll(Stream<ScheduledTick<T>> stream) {
	}
}
