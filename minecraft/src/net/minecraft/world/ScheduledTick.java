package net.minecraft.world;

import java.util.Comparator;
import net.minecraft.util.TaskPriority;
import net.minecraft.util.math.BlockPos;

public class ScheduledTick<T> {
	private static long idCounter;
	private final T object;
	public final BlockPos pos;
	public final long time;
	public final TaskPriority priority;
	private final long id;

	public ScheduledTick(BlockPos blockPos, T object) {
		this(blockPos, object, 0L, TaskPriority.NORMAL);
	}

	public ScheduledTick(BlockPos blockPos, T object, long l, TaskPriority taskPriority) {
		this.id = idCounter++;
		this.pos = blockPos.toImmutable();
		this.object = object;
		this.time = l;
		this.priority = taskPriority;
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

	public static <T> Comparator<ScheduledTick<T>> getComparator() {
		return Comparator.comparingLong(scheduledTick -> scheduledTick.time)
			.thenComparing(scheduledTick -> scheduledTick.priority)
			.thenComparingLong(scheduledTick -> scheduledTick.id);
	}

	public String toString() {
		return this.object + ": " + this.pos + ", " + this.time + ", " + this.priority + ", " + this.id;
	}

	public T getObject() {
		return this.object;
	}
}
