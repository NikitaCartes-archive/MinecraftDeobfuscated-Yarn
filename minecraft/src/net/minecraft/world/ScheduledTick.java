package net.minecraft.world;

import net.minecraft.util.TaskPriority;
import net.minecraft.util.math.BlockPos;

public class ScheduledTick<T> implements Comparable<ScheduledTick<?>> {
	private static long idCounter;
	private final T object;
	public final BlockPos field_9322;
	public final long time;
	public final TaskPriority field_9320;
	private final long id;

	public ScheduledTick(BlockPos blockPos, T object) {
		this(blockPos, object, 0L, TaskPriority.field_9314);
	}

	public ScheduledTick(BlockPos blockPos, T object, long l, TaskPriority taskPriority) {
		this.id = idCounter++;
		this.field_9322 = blockPos.toImmutable();
		this.object = object;
		this.time = l;
		this.field_9320 = taskPriority;
	}

	public boolean equals(Object object) {
		if (!(object instanceof ScheduledTick)) {
			return false;
		} else {
			ScheduledTick<?> scheduledTick = (ScheduledTick<?>)object;
			return this.field_9322.equals(scheduledTick.field_9322) && this.object == scheduledTick.object;
		}
	}

	public int hashCode() {
		return this.field_9322.hashCode();
	}

	public int method_8682(ScheduledTick<?> scheduledTick) {
		int i = Long.compare(this.time, scheduledTick.time);
		if (i != 0) {
			return i;
		} else {
			i = Integer.compare(this.field_9320.ordinal(), scheduledTick.field_9320.ordinal());
			return i != 0 ? i : Long.compare(this.id, scheduledTick.id);
		}
	}

	public String toString() {
		return this.object + ": " + this.field_9322 + ", " + this.time + ", " + this.field_9320 + ", " + this.id;
	}

	public T getObject() {
		return this.object;
	}
}
