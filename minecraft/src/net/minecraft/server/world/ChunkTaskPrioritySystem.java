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
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.thread.MessageListener;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.util.thread.TaskQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkTaskPrioritySystem implements ChunkHolder.LevelUpdateListener, AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Map<MessageListener<?>, LevelPrioritizedQueue<? extends Function<MessageListener<Unit>, ?>>> queues;
	private final Set<MessageListener<?>> idleActors;
	private final TaskExecutor<TaskQueue.PrioritizedTask> controlActor;

	public ChunkTaskPrioritySystem(List<MessageListener<?>> actors, Executor executor, int maxQueues) {
		this.queues = (Map<MessageListener<?>, LevelPrioritizedQueue<? extends Function<MessageListener<Unit>, ?>>>)actors.stream()
			.collect(Collectors.toMap(Function.identity(), actor -> new LevelPrioritizedQueue(actor.getName() + "_queue", maxQueues)));
		this.idleActors = Sets.<MessageListener<?>>newHashSet(actors);
		this.controlActor = new TaskExecutor<>(new TaskQueue.Prioritized(4), executor, "sorter");
	}

	public static <T> ChunkTaskPrioritySystem.Task<T> method_34871(Function<MessageListener<Unit>, T> function, long l, IntSupplier intSupplier) {
		return new ChunkTaskPrioritySystem.Task<>(function, l, intSupplier);
	}

	public static ChunkTaskPrioritySystem.Task<Runnable> createMessage(Runnable task, long pos, IntSupplier lastLevelUpdatedToProvider) {
		return new ChunkTaskPrioritySystem.Task<>(yield -> () -> {
				task.run();
				yield.send(Unit.INSTANCE);
			}, pos, lastLevelUpdatedToProvider);
	}

	public static ChunkTaskPrioritySystem.Task<Runnable> createMessage(ChunkHolder holder, Runnable task) {
		return createMessage(task, holder.getPos().toLong(), holder::getCompletedLevel);
	}

	public static <T> ChunkTaskPrioritySystem.Task<T> method_34870(ChunkHolder chunkHolder, Function<MessageListener<Unit>, T> function) {
		return method_34871(function, chunkHolder.getPos().toLong(), chunkHolder::getCompletedLevel);
	}

	public static ChunkTaskPrioritySystem.UnblockingMessage createUnblockingMessage(Runnable task, long pos, boolean removeTask) {
		return new ChunkTaskPrioritySystem.UnblockingMessage(task, pos, removeTask);
	}

	public <T> MessageListener<ChunkTaskPrioritySystem.Task<T>> createExecutor(MessageListener<T> executor, boolean addBlocker) {
		return (MessageListener<ChunkTaskPrioritySystem.Task<T>>)this.controlActor
			.ask(
				yield -> new TaskQueue.PrioritizedTask(
						0,
						() -> {
							this.getQueue(executor);
							yield.send(
								MessageListener.create(
									"chunk priority sorter around " + executor.getName(),
									task -> this.enqueueChunk(executor, task.taskFunction, task.pos, task.lastLevelUpdatedToProvider, addBlocker)
								)
							);
						}
					)
			)
			.join();
	}

	public MessageListener<ChunkTaskPrioritySystem.UnblockingMessage> createUnblockingExecutor(MessageListener<Runnable> executor) {
		return (MessageListener<ChunkTaskPrioritySystem.UnblockingMessage>)this.controlActor
			.ask(
				yield -> new TaskQueue.PrioritizedTask(
						0,
						() -> yield.send(
								MessageListener.create(
									"chunk priority sorter around " + executor.getName(),
									unblockingMessage -> this.removeChunk(executor, unblockingMessage.pos, unblockingMessage.callback, unblockingMessage.removeTask)
								)
							)
					)
			)
			.join();
	}

	@Override
	public void updateLevel(ChunkPos pos, IntSupplier levelGetter, int targetLevel, IntConsumer levelSetter) {
		this.controlActor.send(new TaskQueue.PrioritizedTask(0, () -> {
			int j = levelGetter.getAsInt();
			this.queues.values().forEach(queue -> queue.updateLevel(j, pos, targetLevel));
			levelSetter.accept(targetLevel);
		}));
	}

	private <T> void removeChunk(MessageListener<T> actor, long chunkPos, Runnable callback, boolean clearTask) {
		this.controlActor.send(new TaskQueue.PrioritizedTask(1, () -> {
			LevelPrioritizedQueue<Function<MessageListener<Unit>, T>> levelPrioritizedQueue = this.getQueue(actor);
			levelPrioritizedQueue.remove(chunkPos, clearTask);
			if (this.idleActors.remove(actor)) {
				this.enqueueExecution(levelPrioritizedQueue, actor);
			}

			callback.run();
		}));
	}

	private <T> void enqueueChunk(
		MessageListener<T> actor, Function<MessageListener<Unit>, T> task, long chunkPos, IntSupplier lastLevelUpdatedToProvider, boolean addBlocker
	) {
		this.controlActor.send(new TaskQueue.PrioritizedTask(2, () -> {
			LevelPrioritizedQueue<Function<MessageListener<Unit>, T>> levelPrioritizedQueue = this.getQueue(actor);
			int i = lastLevelUpdatedToProvider.getAsInt();
			levelPrioritizedQueue.add(Optional.of(task), chunkPos, i);
			if (addBlocker) {
				levelPrioritizedQueue.add(Optional.empty(), chunkPos, i);
			}

			if (this.idleActors.remove(actor)) {
				this.enqueueExecution(levelPrioritizedQueue, actor);
			}
		}));
	}

	private <T> void enqueueExecution(LevelPrioritizedQueue<Function<MessageListener<Unit>, T>> queue, MessageListener<T> actor) {
		this.controlActor.send(new TaskQueue.PrioritizedTask(3, () -> {
			Stream<Either<Function<MessageListener<Unit>, T>, Runnable>> stream = queue.poll();
			if (stream == null) {
				this.idleActors.add(actor);
			} else {
				Util.combineSafe((List)stream.map(executeOrAddBlocking -> executeOrAddBlocking.map(actor::ask, addBlocking -> {
						addBlocking.run();
						return CompletableFuture.completedFuture(Unit.INSTANCE);
					})).collect(Collectors.toList())).thenAccept(list -> this.enqueueExecution(queue, actor));
			}
		}));
	}

	private <T> LevelPrioritizedQueue<Function<MessageListener<Unit>, T>> getQueue(MessageListener<T> actor) {
		LevelPrioritizedQueue<? extends Function<MessageListener<Unit>, ?>> levelPrioritizedQueue = (LevelPrioritizedQueue<? extends Function<MessageListener<Unit>, ?>>)this.queues
			.get(actor);
		if (levelPrioritizedQueue == null) {
			throw (IllegalArgumentException)Util.throwOrPause((T)(new IllegalArgumentException("No queue for: " + actor)));
		} else {
			return (LevelPrioritizedQueue<Function<MessageListener<Unit>, T>>)levelPrioritizedQueue;
		}
	}

	@VisibleForTesting
	public String getDebugString() {
		return (String)this.queues
				.entrySet()
				.stream()
				.map(
					entry -> ((MessageListener)entry.getKey()).getName()
							+ "=["
							+ (String)((LevelPrioritizedQueue)entry.getValue())
								.getBlockingChunks()
								.stream()
								.map(long_ -> long_ + ":" + new ChunkPos(long_))
								.collect(Collectors.joining(","))
							+ "]"
				)
				.collect(Collectors.joining(","))
			+ ", s="
			+ this.idleActors.size();
	}

	public void close() {
		this.queues.keySet().forEach(MessageListener::close);
	}

	public static final class Task<T> {
		private final Function<MessageListener<Unit>, T> taskFunction;
		private final long pos;
		private final IntSupplier lastLevelUpdatedToProvider;

		private Task(Function<MessageListener<Unit>, T> function, long pos, IntSupplier lastLevelUpdatedToProvider) {
			this.taskFunction = function;
			this.pos = pos;
			this.lastLevelUpdatedToProvider = lastLevelUpdatedToProvider;
		}
	}

	public static final class UnblockingMessage {
		private final Runnable callback;
		private final long pos;
		private final boolean removeTask;

		private UnblockingMessage(Runnable callback, long pos, boolean removeTask) {
			this.callback = callback;
			this.pos = pos;
			this.removeTask = removeTask;
		}
	}
}
