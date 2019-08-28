/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.world;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.LevelPrioritizedQueue;
import net.minecraft.util.Actor;
import net.minecraft.util.Mailbox;
import net.minecraft.util.MailboxProcessor;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.Unit;
import net.minecraft.util.math.ChunkPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkTaskPrioritySystem
implements AutoCloseable,
ChunkHolder.LevelUpdateListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<Actor<?>, LevelPrioritizedQueue<? extends Function<Actor<Unit>, ?>>> queues;
    private final Set<Actor<?>> actors;
    private final MailboxProcessor<Mailbox.PrioritizedMessage> sorter;

    public ChunkTaskPrioritySystem(List<Actor<?>> list, Executor executor, int i) {
        this.queues = list.stream().collect(Collectors.toMap(Function.identity(), actor -> new LevelPrioritizedQueue(actor.getName() + "_queue", i)));
        this.actors = Sets.newHashSet(list);
        this.sorter = new MailboxProcessor<Mailbox.PrioritizedMessage>(new Mailbox.PrioritizedQueueMailbox(4), executor, "sorter");
    }

    public static RunnableMessage<Runnable> createRunnableMessage(Runnable runnable, long l, IntSupplier intSupplier) {
        return new RunnableMessage<Runnable>(actor -> () -> {
            runnable.run();
            actor.send(Unit.INSTANCE);
        }, l, intSupplier);
    }

    public static RunnableMessage<Runnable> createExecutorMessage(ChunkHolder chunkHolder, Runnable runnable) {
        return ChunkTaskPrioritySystem.createRunnableMessage(runnable, chunkHolder.getPos().toLong(), chunkHolder::getCompletedLevel);
    }

    public static SorterMessage createSorterMessage(Runnable runnable, long l, boolean bl) {
        return new SorterMessage(runnable, l, bl);
    }

    public <T> Actor<RunnableMessage<T>> createExecutingActor(Actor<T> actor, boolean bl) {
        return (Actor)this.sorter.createAndSendFutureActor(actor2 -> new Mailbox.PrioritizedMessage(0, () -> {
            this.getQueue(actor);
            actor2.send(Actor.createConsumerActor("chunk priority sorter around " + actor.getName(), runnableMessage -> this.execute(actor, ((RunnableMessage)runnableMessage).function, ((RunnableMessage)runnableMessage).pos, ((RunnableMessage)runnableMessage).lastLevelUpdatedToProvider, bl)));
        })).join();
    }

    public Actor<SorterMessage> createSortingActor(Actor<Runnable> actor) {
        return (Actor)this.sorter.createAndSendFutureActor(actor2 -> new Mailbox.PrioritizedMessage(0, () -> actor2.send(Actor.createConsumerActor("chunk priority sorter around " + actor.getName(), sorterMessage -> this.sort(actor, ((SorterMessage)sorterMessage).pos, ((SorterMessage)sorterMessage).runnable, ((SorterMessage)sorterMessage).field_17451))))).join();
    }

    @Override
    public void updateLevel(ChunkPos chunkPos, IntSupplier intSupplier, int i, IntConsumer intConsumer) {
        this.sorter.send(new Mailbox.PrioritizedMessage(0, () -> {
            int j = intSupplier.getAsInt();
            this.queues.values().forEach(levelPrioritizedQueue -> levelPrioritizedQueue.updateLevel(j, chunkPos, i));
            intConsumer.accept(i);
        }));
    }

    private <T> void sort(Actor<T> actor, long l, Runnable runnable, boolean bl) {
        this.sorter.send(new Mailbox.PrioritizedMessage(1, () -> {
            LevelPrioritizedQueue levelPrioritizedQueue = this.getQueue(actor);
            levelPrioritizedQueue.clearPosition(l, bl);
            if (this.actors.remove(actor)) {
                this.method_17630(levelPrioritizedQueue, actor);
            }
            runnable.run();
        }));
    }

    private <T> void execute(Actor<T> actor, Function<Actor<Unit>, T> function, long l, IntSupplier intSupplier, boolean bl) {
        this.sorter.send(new Mailbox.PrioritizedMessage(2, () -> {
            LevelPrioritizedQueue levelPrioritizedQueue = this.getQueue(actor);
            int i = intSupplier.getAsInt();
            levelPrioritizedQueue.add(Optional.of(function), l, i);
            if (bl) {
                levelPrioritizedQueue.add(Optional.empty(), l, i);
            }
            if (this.actors.remove(actor)) {
                this.method_17630(levelPrioritizedQueue, actor);
            }
        }));
    }

    private <T> void method_17630(LevelPrioritizedQueue<Function<Actor<Unit>, T>> levelPrioritizedQueue, Actor<T> actor) {
        this.sorter.send(new Mailbox.PrioritizedMessage(3, () -> {
            Stream<Either<Either, Runnable>> stream = levelPrioritizedQueue.poll();
            if (stream == null) {
                this.actors.add(actor);
            } else {
                SystemUtil.thenCombine(stream.map(either -> either.map(actor::createAndSendFutureActor, runnable -> {
                    runnable.run();
                    return CompletableFuture.completedFuture(Unit.INSTANCE);
                })).collect(Collectors.toList())).thenAccept(list -> this.method_17630(levelPrioritizedQueue, actor));
            }
        }));
    }

    private <T> LevelPrioritizedQueue<Function<Actor<Unit>, T>> getQueue(Actor<T> actor) {
        LevelPrioritizedQueue<Function<Actor<Unit>, T>> levelPrioritizedQueue = this.queues.get(actor);
        if (levelPrioritizedQueue == null) {
            throw SystemUtil.throwOrPause(new IllegalArgumentException("No queue for: " + actor));
        }
        return levelPrioritizedQueue;
    }

    @VisibleForTesting
    public String method_21680() {
        return this.queues.entrySet().stream().map(entry -> ((Actor)entry.getKey()).getName() + "=[" + ((LevelPrioritizedQueue)entry.getValue()).method_21679().stream().map(long_ -> long_ + ":" + new ChunkPos((long)long_)).collect(Collectors.joining(",")) + "]").collect(Collectors.joining(",")) + ", s=" + this.actors.size();
    }

    @Override
    public void close() {
        this.queues.keySet().forEach(Actor::close);
    }

    public static final class SorterMessage {
        private final Runnable runnable;
        private final long pos;
        private final boolean field_17451;

        private SorterMessage(Runnable runnable, long l, boolean bl) {
            this.runnable = runnable;
            this.pos = l;
            this.field_17451 = bl;
        }
    }

    public static final class RunnableMessage<T> {
        private final Function<Actor<Unit>, T> function;
        private final long pos;
        private final IntSupplier lastLevelUpdatedToProvider;

        private RunnableMessage(Function<Actor<Unit>, T> function, long l, IntSupplier intSupplier) {
            this.function = function;
            this.pos = l;
            this.lastLevelUpdatedToProvider = intSupplier;
        }
    }
}

