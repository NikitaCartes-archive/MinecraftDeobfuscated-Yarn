package net.minecraft.server.world;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.TaskPriority;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ScheduledTick;
import net.minecraft.world.TickScheduler;

public class SimpleTickScheduler<T> implements TickScheduler<T> {
	protected final Set<ScheduledTick<T>> scheduledTicks = Sets.<ScheduledTick<T>>newHashSet();
	private final Function<T, Identifier> identifierProvider;

	public SimpleTickScheduler(Function<T, Identifier> function, List<ScheduledTick<T>> list) {
		this.identifierProvider = function;
		this.scheduledTicks.addAll(list);
	}

	@Override
	public boolean isScheduled(BlockPos blockPos, T object) {
		return false;
	}

	@Override
	public void schedule(BlockPos blockPos, T object, int i, TaskPriority taskPriority) {
		this.scheduledTicks.add(new ScheduledTick(blockPos, object, (long)i, taskPriority));
	}

	@Override
	public boolean isTicking(BlockPos blockPos, T object) {
		return false;
	}

	@Override
	public void method_20470(Stream<ScheduledTick<T>> stream) {
		stream.forEach(this.scheduledTicks::add);
	}

	public Stream<ScheduledTick<T>> stream() {
		return this.scheduledTicks.stream();
	}

	public ListTag toTag(long l) {
		return ServerTickScheduler.serializeScheduledTicks(this.identifierProvider, this.scheduledTicks, l);
	}
}
