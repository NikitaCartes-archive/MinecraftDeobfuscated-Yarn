/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import java.util.Comparator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;

public class ScheduledTick<T> {
    private static long idCounter;
    private final T object;
    public final BlockPos pos;
    public final long time;
    public final TickPriority priority;
    private final long id = idCounter++;

    public ScheduledTick(BlockPos pos, T t) {
        this(pos, t, 0L, TickPriority.NORMAL);
    }

    public ScheduledTick(BlockPos pos, T t, long time, TickPriority priority) {
        this.pos = pos.toImmutable();
        this.object = t;
        this.time = time;
        this.priority = priority;
    }

    public boolean equals(Object o) {
        if (o instanceof ScheduledTick) {
            ScheduledTick scheduledTick = (ScheduledTick)o;
            return this.pos.equals(scheduledTick.pos) && this.object == scheduledTick.object;
        }
        return false;
    }

    public int hashCode() {
        return this.pos.hashCode();
    }

    public static <T> Comparator<ScheduledTick<T>> getComparator() {
        return Comparator.comparingLong(scheduledTick -> scheduledTick.time).thenComparing(scheduledTick -> scheduledTick.priority).thenComparingLong(scheduledTick -> scheduledTick.id);
    }

    public String toString() {
        return this.object + ": " + this.pos + ", " + this.time + ", " + (Object)((Object)this.priority) + ", " + this.id;
    }

    public T getObject() {
        return this.object;
    }
}

