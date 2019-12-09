/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ScheduledTick;
import net.minecraft.world.TickPriority;
import net.minecraft.world.TickScheduler;

public class MultiTickScheduler<T>
implements TickScheduler<T> {
    private final Function<BlockPos, TickScheduler<T>> mapper;

    public MultiTickScheduler(Function<BlockPos, TickScheduler<T>> mapper) {
        this.mapper = mapper;
    }

    @Override
    public boolean isScheduled(BlockPos pos, T object) {
        return this.mapper.apply(pos).isScheduled(pos, object);
    }

    @Override
    public void schedule(BlockPos pos, T object, int delay, TickPriority priority) {
        this.mapper.apply(pos).schedule(pos, object, delay, priority);
    }

    @Override
    public boolean isTicking(BlockPos pos, T object) {
        return false;
    }

    @Override
    public void scheduleAll(Stream<ScheduledTick<T>> stream) {
        stream.forEach(scheduledTick -> this.mapper.apply(scheduledTick.pos).scheduleAll(Stream.of(scheduledTick)));
    }
}

