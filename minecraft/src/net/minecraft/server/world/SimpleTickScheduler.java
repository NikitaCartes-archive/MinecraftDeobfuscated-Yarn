package net.minecraft.server.world;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.TaskPriority;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ScheduledTick;
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
	public void scheduleAll(Stream<ScheduledTick<T>> stream) {
		stream.forEach(this.scheduledTicks::add);
	}

	public Stream<ScheduledTick<T>> stream() {
		return this.scheduledTicks.stream();
	}

	public ListTag toNbt(long l) {
		return ServerTickScheduler.serializeScheduledTicks(this.identifierProvider, this.scheduledTicks, l);
	}

	public static <T> SimpleTickScheduler<T> fromNbt(ListTag listTag, Function<T, Identifier> function, Function<Identifier, T> function2) {
		Set<ScheduledTick<T>> set = Sets.<ScheduledTick<T>>newHashSet();

		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag = listTag.getCompoundTag(i);
			T object = (T)function2.apply(new Identifier(compoundTag.getString("i")));
			if (object != null) {
				set.add(
					new ScheduledTick(
						new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z")),
						object,
						(long)compoundTag.getInt("t"),
						TaskPriority.getByIndex(compoundTag.getInt("p"))
					)
				);
			}
		}

		return new SimpleTickScheduler<>(function, set);
	}
}
