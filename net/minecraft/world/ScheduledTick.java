/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
    private final long id = idCounter++;

    public ScheduledTick(BlockPos blockPos, T object) {
        this(blockPos, object, 0L, TaskPriority.NORMAL);
    }

    public ScheduledTick(BlockPos blockPos, T object, long l, TaskPriority taskPriority) {
        this.pos = blockPos.toImmutable();
        this.object = object;
        this.time = l;
        this.priority = taskPriority;
    }

    public boolean equals(Object object) {
        if (object instanceof ScheduledTick) {
            ScheduledTick scheduledTick = (ScheduledTick)object;
            return this.pos.equals(scheduledTick.pos) && this.object == scheduledTick.object;
        }
        return false;
    }

    public int hashCode() {
        return this.pos.hashCode();
    }

    public static <T> Comparator<ScheduledTick<T>> method_20597() {
        return (scheduledTick, scheduledTick2) -> {
            int i = Long.compare(scheduledTick.time, scheduledTick2.time);
            if (i != 0) {
                return i;
            }
            i = scheduledTick.priority.compareTo(scheduledTick2.priority);
            if (i != 0) {
                return i;
            }
            return Long.compare(scheduledTick.id, scheduledTick2.id);
        };
    }

    public String toString() {
        return this.object + ": " + this.pos + ", " + this.time + ", " + (Object)((Object)this.priority) + ", " + this.id;
    }

    public T getObject() {
        return this.object;
    }
}

