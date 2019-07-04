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
import net.minecraft.util.Actor;
import net.minecraft.util.Mailbox;
import net.minecraft.util.MailboxProcessor;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.Unit;
import net.minecraft.util.math.ChunkPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkTaskPrioritySystem implements AutoCloseable, ChunkHolder.LevelUpdateListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Map<Actor<?>, LevelPrioritizedQueue<? extends Function<Actor<Unit>, ?>>> queues;
	private final Set<Actor<?>> actors;
	private final MailboxProcessor<Mailbox.PrioritizedMessage> sorter;

	public ChunkTaskPrioritySystem(List<Actor<?>> list, Executor executor, int i) {
		this.queues = (Map<Actor<?>, LevelPrioritizedQueue<? extends Function<Actor<Unit>, ?>>>)list.stream()
			.collect(Collectors.toMap(Function.identity(), actor -> new LevelPrioritizedQueue(actor.getName() + "_queue", i)));
		this.actors = Sets.<Actor<?>>newHashSet(list);
		this.sorter = new MailboxProcessor<>(new Mailbox.PrioritizedQueueMailbox(4), executor, "sorter");
	}

	public static ChunkTaskPrioritySystem.RunnableMessage<Runnable> createRunnableMessage(Runnable runnable, long l, IntSupplier intSupplier) {
		return new ChunkTaskPrioritySystem.RunnableMessage<>(actor -> () -> {
				runnable.run();
				actor.send(Unit.INSTANCE);
			}, l, intSupplier);
	}

	public static ChunkTaskPrioritySystem.RunnableMessage<Runnable> createExecutorMessage(ChunkHolder chunkHolder, Runnable runnable) {
		return createRunnableMessage(runnable, chunkHolder.getPos().toLong(), chunkHolder::getCompletedLevel);
	}

	public static ChunkTaskPrioritySystem.SorterMessage createSorterMessage(Runnable runnable, long l, boolean bl) {
		return new ChunkTaskPrioritySystem.SorterMessage(runnable, l, bl);
	}

	public <T> Actor<ChunkTaskPrioritySystem.RunnableMessage<T>> createExecutingActor(Actor<T> actor, boolean bl) {
		return (Actor<ChunkTaskPrioritySystem.RunnableMessage<T>>)this.sorter
			.createAndSendFutureActor(
				actor2 -> new Mailbox.PrioritizedMessage(
						0,
						() -> {
							this.getQueue(actor);
							actor2.send(
								Actor.createConsumerActor(
									"chunk priority sorter around " + actor.getName(),
									runnableMessage -> this.execute(actor, runnableMessage.function, runnableMessage.pos, runnableMessage.lastLevelUpdatedToProvider, bl)
								)
							);
						}
					)
			)
			.join();
	}

	public Actor<ChunkTaskPrioritySystem.SorterMessage> createSortingActor(Actor<Runnable> actor) {
		return (Actor<ChunkTaskPrioritySystem.SorterMessage>)this.sorter
			.createAndSendFutureActor(
				actor2 -> new Mailbox.PrioritizedMessage(
						0,
						() -> actor2.send(
								Actor.createConsumerActor(
									"chunk priority sorter around " + actor.getName(),
									sorterMessage -> this.sort(actor, sorterMessage.pos, sorterMessage.runnable, sorterMessage.field_17451)
								)
							)
					)
			)
			.join();
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
			LevelPrioritizedQueue<Function<Actor<Unit>, T>> levelPrioritizedQueue = this.getQueue(actor);
			levelPrioritizedQueue.clearPosition(l, bl);
			if (this.actors.remove(actor)) {
				this.method_17630(levelPrioritizedQueue, actor);
			}

			runnable.run();
		}));
	}

	private <T> void execute(Actor<T> actor, Function<Actor<Unit>, T> function, long l, IntSupplier intSupplier, boolean bl) {
		this.sorter.send(new Mailbox.PrioritizedMessage(2, () -> {
			LevelPrioritizedQueue<Function<Actor<Unit>, T>> levelPrioritizedQueue = this.getQueue(actor);
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
			Stream<Either<Function<Actor<Unit>, T>, Runnable>> stream = levelPrioritizedQueue.poll();
			if (stream == null) {
				this.actors.add(actor);
			} else {
				SystemUtil.thenCombine((List)stream.map(either -> either.map(actor::createAndSendFutureActor, runnable -> {
						runnable.run();
						return CompletableFuture.completedFuture(Unit.INSTANCE);
					})).collect(Collectors.toList())).thenAccept(list -> this.method_17630(levelPrioritizedQueue, actor));
			}
		}));
	}

	private <T> LevelPrioritizedQueue<Function<Actor<Unit>, T>> getQueue(Actor<T> actor) {
		LevelPrioritizedQueue<? extends Function<Actor<Unit>, ?>> levelPrioritizedQueue = (LevelPrioritizedQueue<? extends Function<Actor<Unit>, ?>>)this.queues
			.get(actor);
		if (levelPrioritizedQueue == null) {
			throw new IllegalArgumentException("No queue for: " + actor);
		} else {
			return (LevelPrioritizedQueue<Function<Actor<Unit>, T>>)levelPrioritizedQueue;
		}
	}

	@VisibleForTesting
	public String method_21680() {
		return (String)this.queues
			.entrySet()
			.stream()
			.map(
				entry -> ((Actor)entry.getKey()).getName()
						+ "=["
						+ (String)((LevelPrioritizedQueue)entry.getValue())
							.method_21679()
							.stream()
							.map(long_ -> long_ + ":" + new ChunkPos(long_))
							.collect(Collectors.joining(","))
						+ "]"
			)
			.collect(Collectors.joining(","));
	}

	public void close() {
		this.queues.keySet().forEach(Actor::close);
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
}
