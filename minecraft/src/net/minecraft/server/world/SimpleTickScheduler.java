package net.minecraft.server.world;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ScheduledTick;
import net.minecraft.world.TickPriority;
import net.minecraft.world.TickScheduler;

public class SimpleTickScheduler<T> implements TickScheduler<T> {
	private final Set<ScheduledTick<T>> scheduledTicks;
	private final Function<T, Identifier> identifierProvider;

	public SimpleTickScheduler(Function<T, Identifier> function, List<ScheduledTick<T>> list) {
		this(function, Sets.<ScheduledTick<T>>newHashSet(list));
	}

	private SimpleTickScheduler(Function<T, Identifier> function, Set<ScheduledTick<T>> set) {
		this.scheduledTicks = set;
		this.identifierProvider = function;
	}

	@Override
	public boolean isScheduled(BlockPos pos, T object) {
		return false;
	}

	@Override
	public void schedule(BlockPos pos, T object, int delay, TickPriority priority) {
		this.scheduledTicks.add(new ScheduledTick(pos, object, (long)delay, priority));
	}

	@Override
	public boolean isTicking(BlockPos pos, T object) {
		return false;
	}

	@Override
	public void scheduleAll(Stream<ScheduledTick<T>> stream) {
		stream.forEach(this.scheduledTicks::add);
	}

	public Stream<ScheduledTick<T>> stream() {
		return this.scheduledTicks.stream();
	}

	public ListTag toNbt(long time) {
		return ServerTickScheduler.serializeScheduledTicks(this.identifierProvider, this.scheduledTicks, time);
	}

	public static <T> SimpleTickScheduler<T> fromNbt(ListTag ticks, Function<T, Identifier> function, Function<Identifier, T> function2) {
		Set<ScheduledTick<T>> set = Sets.<ScheduledTick<T>>newHashSet();

		for (int i = 0; i < ticks.size(); i++) {
			CompoundTag compoundTag = ticks.getCompound(i);
			T object = (T)function2.apply(new Identifier(compoundTag.getString("i")));
			if (object != null) {
				set.add(
					new ScheduledTick(
						new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z")),
						object,
						(long)compoundTag.getInt("t"),
						TickPriority.byIndex(compoundTag.getInt("p"))
					)
				);
			}
		}

		return new SimpleTickScheduler<>(function, set);
	}
}
