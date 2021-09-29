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
		private final Queue<Runnable>[] field_35032;
		private final AtomicInteger field_35033 = new AtomicInteger();

		public Prioritized(int priorityCount) {
			this.field_35032 = new Queue[priorityCount];

			for (int i = 0; i < priorityCount; i++) {
				this.field_35032[i] = Queues.<Runnable>newConcurrentLinkedQueue();
			}
		}

		@Nullable
		public Runnable poll() {
			for (Queue<Runnable> queue : this.field_35032) {
				Runnable runnable = (Runnable)queue.poll();
				if (runnable != null) {
					this.field_35033.decrementAndGet();
					return runnable;
				}
			}

			return null;
		}

		public boolean add(TaskQueue.PrioritizedTask prioritizedTask) {
			int i = prioritizedTask.priority;
			if (i < this.field_35032.length && i >= 0) {
				this.field_35032[i].add(prioritizedTask);
				this.field_35033.incrementAndGet();
				return true;
			} else {
				throw new IndexOutOfBoundsException("Priority %d not supported. Expected range [0-%d]".formatted(i, this.field_35032.length - 1));
			}
		}

		@Override
		public boolean isEmpty() {
			return this.field_35033.get() == 0;
		}

		@Override
		public int getSize() {
			return this.field_35033.get();
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
