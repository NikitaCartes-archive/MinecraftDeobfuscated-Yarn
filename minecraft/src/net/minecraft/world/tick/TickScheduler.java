package net.minecraft.world.tick;

import net.minecraft.util.math.BlockPos;

public interface TickScheduler<T> {
	void scheduleTick(OrderedTick<T> orderedTick);

	boolean isQueued(BlockPos pos, T type);

	int getTickCount();
}
