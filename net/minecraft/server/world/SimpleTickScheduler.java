/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.world;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ScheduledTick;
import net.minecraft.world.TickPriority;
import net.minecraft.world.TickScheduler;

public class SimpleTickScheduler<T>
implements TickScheduler<T> {
    private final List<Tick<T>> scheduledTicks;
    private final Function<T, Identifier> identifierProvider;

    public SimpleTickScheduler(Function<T, Identifier> identifierProvider, List<ScheduledTick<T>> scheduledTicks, long startTime) {
        this(identifierProvider, scheduledTicks.stream().map(scheduledTick -> new Tick(scheduledTick.getObject(), scheduledTick.pos, (int)(scheduledTick.time - startTime), scheduledTick.priority)).collect(Collectors.toList()));
    }

    private SimpleTickScheduler(Function<T, Identifier> identifierProvider, List<Tick<T>> scheduledTicks) {
        this.scheduledTicks = scheduledTicks;
        this.identifierProvider = identifierProvider;
    }

    @Override
    public boolean isScheduled(BlockPos pos, T object) {
        return false;
    }

    @Override
    public void schedule(BlockPos pos, T object, int delay, TickPriority priority) {
        this.scheduledTicks.add(new Tick(object, pos, delay, priority));
    }

    @Override
    public boolean isTicking(BlockPos pos, T object) {
        return false;
    }

    public ListTag toNbt() {
        ListTag listTag = new ListTag();
        for (Tick<T> tick : this.scheduledTicks) {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putString("i", this.identifierProvider.apply(((Tick)tick).object).toString());
            compoundTag.putInt("x", tick.pos.getX());
            compoundTag.putInt("y", tick.pos.getY());
            compoundTag.putInt("z", tick.pos.getZ());
            compoundTag.putInt("t", tick.delay);
            compoundTag.putInt("p", tick.priority.getIndex());
            listTag.add(compoundTag);
        }
        return listTag;
    }

    public static <T> SimpleTickScheduler<T> fromNbt(ListTag ticks, Function<T, Identifier> function, Function<Identifier, T> function2) {
        ArrayList<Tick<T>> list = Lists.newArrayList();
        for (int i = 0; i < ticks.size(); ++i) {
            CompoundTag compoundTag = ticks.getCompound(i);
            T object = function2.apply(new Identifier(compoundTag.getString("i")));
            if (object == null) continue;
            BlockPos blockPos = new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z"));
            list.add(new Tick(object, blockPos, compoundTag.getInt("t"), TickPriority.byIndex(compoundTag.getInt("p"))));
        }
        return new SimpleTickScheduler<T>(function, list);
    }

    public void scheduleTo(TickScheduler<T> scheduler) {
        this.scheduledTicks.forEach(tick -> scheduler.schedule(tick.pos, ((Tick)tick).object, tick.delay, tick.priority));
    }

    static class Tick<T> {
        private final T object;
        public final BlockPos pos;
        public final int delay;
        public final TickPriority priority;

        private Tick(T object, BlockPos pos, int delay, TickPriority priority) {
            this.object = object;
            this.pos = pos;
            this.delay = delay;
            this.priority = priority;
        }

        public String toString() {
            return this.object + ": " + this.pos + ", " + this.delay + ", " + (Object)((Object)this.priority);
        }
    }
}

