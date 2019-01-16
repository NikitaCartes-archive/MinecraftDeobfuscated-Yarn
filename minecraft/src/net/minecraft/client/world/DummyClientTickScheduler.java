package net.minecraft.client.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.TaskPriority;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickScheduler;

@Environment(EnvType.CLIENT)
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
}
