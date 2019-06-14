/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.TaskPriority;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ScheduledTick;
import net.minecraft.world.TickScheduler;

public class MultiTickScheduler<T>
implements TickScheduler<T> {
    private final Function<BlockPos, TickScheduler<T>> mapper;

    public MultiTickScheduler(Function<BlockPos, TickScheduler<T>> function) {
        this.mapper = function;
    }

    @Override
    public boolean isScheduled(BlockPos blockPos, T object) {
        return this.mapper.apply(blockPos).isScheduled(blockPos, object);
    }

    @Override
    public void schedule(BlockPos blockPos, T object, int i, TaskPriority taskPriority) {
        this.mapper.apply(blockPos).schedule(blockPos, object, i, taskPriority);
    }

    @Override
    public boolean isTicking(BlockPos blockPos, T object) {
        return false;
    }

    @Override
    public void method_20470(Stream<ScheduledTick<T>> stream) {
        stream.forEach(scheduledTick -> this.mapper.apply(scheduledTick.pos).method_20470(Stream.of(scheduledTick)));
    }
}

