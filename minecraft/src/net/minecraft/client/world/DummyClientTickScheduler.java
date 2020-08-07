package net.minecraft.client.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;
import net.minecraft.world.TickScheduler;

public class DummyClientTickScheduler<T> implements TickScheduler<T> {
	private static final DummyClientTickScheduler<Object> INSTANCE = new DummyClientTickScheduler<>();

	public static <T> DummyClientTickScheduler<T> get() {
		return (DummyClientTickScheduler<T>)INSTANCE;
	}

	@Override
	public boolean isScheduled(BlockPos pos, T object) {
		return false;
	}

	@Override
	public void schedule(BlockPos pos, T object, int delay) {
	}

	@Override
	public void schedule(BlockPos pos, T object, int delay, TickPriority priority) {
	}

	@Override
	public boolean isTicking(BlockPos pos, T object) {
		return false;
	}
}
