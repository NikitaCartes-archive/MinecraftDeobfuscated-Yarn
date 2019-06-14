package net.minecraft.world;

import java.util.Comparator;
import net.minecraft.util.TaskPriority;
import net.minecraft.util.math.BlockPos;

public class ScheduledTick<T> {
	private static long idCounter;
	private final T object;
	public final BlockPos pos;
	public final long time;
	public final TaskPriority field_9320;
	private final long id;

	public ScheduledTick(BlockPos blockPos, T object) {
		this(blockPos, object, 0L, TaskPriority.field_9314);
	}

	public ScheduledTick(BlockPos blockPos, T object, long l, TaskPriority taskPriority) {
		this.id = idCounter++;
		this.pos = blockPos.toImmutable();
		this.object = object;
		this.time = l;
		this.field_9320 = taskPriority;
	}

	public boolean equals(Object object) {
		if (!(object instanceof ScheduledTick)) {
			return false;
		} else {
			ScheduledTick<?> scheduledTick = (ScheduledTick<?>)object;
			return this.pos.equals(scheduledTick.pos) && this.object == scheduledTick.object;
		}
	}

	public int hashCode() {
		return this.pos.hashCode();
	}

	public static <T> Comparator<ScheduledTick<T>> method_20597() {
		return (scheduledTick, scheduledTick2) -> {
			int i = Long.compare(scheduledTick.time, scheduledTick2.time);
			if (i != 0) {
				return i;
			} else {
				i = scheduledTick.field_9320.compareTo(scheduledTick2.field_9320);
				return i != 0 ? i : Long.compare(scheduledTick.id, scheduledTick2.id);
			}
		};
	}

	public String toString() {
		return this.object + ": " + this.pos + ", " + this.time + ", " + this.field_9320 + ", " + this.id;
	}

	public T getObject() {
		return this.object;
	}
}
