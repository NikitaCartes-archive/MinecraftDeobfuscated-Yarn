package net.minecraft.util.thread;

import com.google.common.collect.Queues;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;

public interface TaskQueue<T, F> {
	@Nullable
	F poll();

	boolean add(T message);

	boolean isEmpty();

	int getSize();

	public static final class Prioritized implements TaskQueue<TaskQueue.PrioritizedTask, Runnable> {
		private final Queue<Runnable>[] queue;
		private final AtomicInteger queueSize = new AtomicInteger();

		public Prioritized(int priorityCount) {
			this.queue = new Queue[priorityCount];

			for (int i = 0; i < priorityCount; i++) {
				this.queue[i] = Queues.<Runnable>newConcurrentLinkedQueue();
			}
		}

		@Nullable
		public Runnable poll() {
			for (Queue<Runnable> queue : this.queue) {
				Runnable runnable = (Runnable)queue.poll();
				if (runnable != null) {
					this.queueSize.decrementAndGet();
					return runnable;
				}
			}

			return null;
		}

		public boolean add(TaskQueue.PrioritizedTask prioritizedTask) {
			int i = prioritizedTask.priority;
			if (i < this.queue.length && i >= 0) {
				this.queue[i].add(prioritizedTask);
				this.queueSize.incrementAndGet();
				return true;
			} else {
				throw new IndexOutOfBoundsException("Priority %d not supported. Expected range [0-%d]".formatted(i, this.queue.length - 1));
			}
		}

		@Override
		public boolean isEmpty() {
			return this.queueSize.get() == 0;
		}

		@Override
		public int getSize() {
			return this.queueSize.get();
		}
	}

	public static final class PrioritizedTask implements Runnable {
		final int priority;
		private final Runnable runnable;

		public PrioritizedTask(int priority, Runnable runnable) {
			this.priority = priority;
			this.runnable = runnable;
		}

		public void run() {
			this.runnable.run();
		}

		public int getPriority() {
			return this.priority;
		}
	}

	public static final class Simple<T> implements TaskQueue<T, T> {
		private final Queue<T> queue;

		public Simple(Queue<T> queue) {
			this.queue = queue;
		}

		@Nullable
		@Override
		public T poll() {
			return (T)this.queue.poll();
		}

		@Override
		public boolean add(T message) {
			return this.queue.add(message);
		}

		@Override
		public boolean isEmpty() {
			return this.queue.isEmpty();
		}

		@Override
		public int getSize() {
			return this.queue.size();
		}
	}
}
