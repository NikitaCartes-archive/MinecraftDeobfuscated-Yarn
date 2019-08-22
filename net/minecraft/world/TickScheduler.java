/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import java.util.stream.Stream;
import net.minecraft.util.TaskPriority;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ScheduledTick;

public interface TickScheduler<T> {
    public boolean isScheduled(BlockPos var1, T var2);

    default public void schedule(BlockPos blockPos, T object, int i) {
        this.schedule(blockPos, object, i, TaskPriority.NORMAL);
    }

    public void schedule(BlockPos var1, T var2, int var3, TaskPriority var4);

    public boolean isTicking(BlockPos var1, T var2);

    public void scheduleAll(Stream<ScheduledTick<T>> var1);
}

