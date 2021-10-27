/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.tick;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.tick.BasicTickScheduler;
import net.minecraft.world.tick.OrderedTick;
import net.minecraft.world.tick.SerializableTickScheduler;
import net.minecraft.world.tick.Tick;
import org.jetbrains.annotations.Nullable;

public class ChunkTickScheduler<T>
implements SerializableTickScheduler<T>,
BasicTickScheduler<T> {
    private final Queue<OrderedTick<T>> tickQueue = new PriorityQueue(OrderedTick.TRIGGER_TICK_COMPARATOR);
    @Nullable
    private List<Tick<T>> ticks;
    private final Set<OrderedTick<?>> queuedTicks = new ObjectOpenCustomHashSet(OrderedTick.HASH_STRATEGY);
    @Nullable
    private BiConsumer<ChunkTickScheduler<T>, OrderedTick<T>> tickConsumer;

    public ChunkTickScheduler() {
    }

    public ChunkTickScheduler(List<Tick<T>> ticks) {
        this.ticks = ticks;
        for (Tick<T> tick : ticks) {
            this.queuedTicks.add(OrderedTick.create(tick.type(), tick.pos()));
        }
    }

    public void setTickConsumer(@Nullable BiConsumer<ChunkTickScheduler<T>, OrderedTick<T>> tickConsumer) {
        this.tickConsumer = tickConsumer;
    }

    @Nullable
    public OrderedTick<T> peekNextTick() {
        return this.tickQueue.peek();
    }

    @Nullable
    public OrderedTick<T> pollNextTick() {
        OrderedTick<T> orderedTick = this.tickQueue.poll();
        if (orderedTick != null) {
            this.queuedTicks.remove(orderedTick);
        }
        return orderedTick;
    }

    @Override
    public void scheduleTick(OrderedTick<T> orderedTick) {
        if (this.queuedTicks.add(orderedTick)) {
            this.queueTick(orderedTick);
        }
    }

    private void queueTick(OrderedTick<T> orderedTick) {
        this.tickQueue.add(orderedTick);
        if (this.tickConsumer != null) {
            this.tickConsumer.accept(this, orderedTick);
        }
    }

    @Override
    public boolean isQueued(BlockPos pos, T type) {
        return this.queuedTicks.contains(OrderedTick.create(type, pos));
    }

    public void removeTicksIf(Predicate<OrderedTick<T>> predicate) {
        Iterator iterator = this.tickQueue.iterator();
        while (iterator.hasNext()) {
            OrderedTick orderedTick = (OrderedTick)iterator.next();
            if (!predicate.test(orderedTick)) continue;
            iterator.remove();
            this.queuedTicks.remove(orderedTick);
        }
    }

    public Stream<OrderedTick<T>> getQueueAsStream() {
        return this.tickQueue.stream();
    }

    @Override
    public int getTickCount() {
        return this.tickQueue.size() + (this.ticks != null ? this.ticks.size() : 0);
    }

    @Override
    public NbtList toNbt(long l, Function<T, String> function) {
        NbtList nbtList = new NbtList();
        if (this.ticks != null) {
            for (Tick<Object> tick : this.ticks) {
                nbtList.add(tick.toNbt(function));
            }
        }
        for (OrderedTick orderedTick : this.tickQueue) {
            nbtList.add(Tick.orderedTickToNbt(orderedTick, function, l));
        }
        return nbtList;
    }

    public void disable(long time) {
        if (this.ticks != null) {
            int i = -this.ticks.size();
            for (Tick<T> tick : this.ticks) {
                this.queueTick(tick.createOrderedTick(time, i++));
            }
        }
        this.ticks = null;
    }

    public static <T> ChunkTickScheduler<T> create(NbtList tickQueue, Function<String, Optional<T>> nameToTypeFunction, ChunkPos pos) {
        ImmutableList.Builder builder = ImmutableList.builder();
        Tick.tick(tickQueue, nameToTypeFunction, pos, builder::add);
        return new ChunkTickScheduler<T>(builder.build());
    }

    @Override
    public /* synthetic */ NbtElement toNbt(long time, Function typeToNameFunction) {
        return this.toNbt(time, typeToNameFunction);
    }
}

