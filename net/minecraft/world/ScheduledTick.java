/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import net.minecraft.util.TaskPriority;
import net.minecraft.util.math.BlockPos;

public class ScheduledTick<T>
implements Comparable<ScheduledTick<?>> {
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

    public int method_8682(ScheduledTick<?> scheduledTick) {
        int i = Long.compare(this.time, scheduledTick.time);
        if (i != 0) {
            return i;
        }
        i = Integer.compare(this.priority.ordinal(), scheduledTick.priority.ordinal());
        if (i != 0) {
            return i;
        }
        return Long.compare(this.id, scheduledTick.id);
    }

    public String toString() {
        return this.object + ": " + this.pos + ", " + this.time + ", " + (Object)((Object)this.priority) + ", " + this.id;
    }

    public T getObject() {
        return this.object;
    }

    @Override
    public /* synthetic */ int compareTo(Object object) {
        return this.method_8682((ScheduledTick)object);
    }
}

