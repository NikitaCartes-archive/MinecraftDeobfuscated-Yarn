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

    public ScheduledTick(BlockPos blockPos, T object) {
        this(blockPos, object, 0L, TickPriority.NORMAL);
    }

    public ScheduledTick(BlockPos blockPos, T object, long l, TickPriority tickPriority) {
        this.pos = blockPos.toImmutable();
        this.object = object;
        this.time = l;
        this.priority = tickPriority;
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

