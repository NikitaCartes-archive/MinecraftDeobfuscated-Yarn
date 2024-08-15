package net.minecraft.world.tick;

import java.util.List;

public interface SerializableTickScheduler<T> {
	List<Tick<T>> collectTicks(long time);
}
