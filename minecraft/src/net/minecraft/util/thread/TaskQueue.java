package net.minecraft.util.thread;

import com.google.common.collect.Queues;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;

public interface TaskQueue<T extends Runnable> {
	@Nullable
	Runnable poll();

	boolean add(T runnable);

	boolean isEmpty();

	int getSize();

	public static final class Prioritized implements TaskQueue<TaskQueue.PrioritizedTask> {
		private final Queue<Runnable>[] queue;
		private final AtomicInteger queueSize = new AtomicInteger();

		public Prioritized(int priorityCount) {
			this.queue = new Queue[priorityCount];

			for (int i = 0; i < priorityCount; i++) {
				this.queue[i] = Queues.<Runnable>newConcurrentLinkedQueue();
			}
		}

		@Nullable
		@Override
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
				throw new IndexOutOfBoundsException(String.format(Locale.ROOT, "Priority %d not supported. Expected range [0-%d]", i, this.queue.length - 1));
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

	public static record PrioritizedTask(int priority, Runnable runnable) implements Runnable {

		public void run() {
			this.runnable.run();
		}
	}

	public static final class Simple implements TaskQueue<Runnable> {
		private final Queue<Runnable> queue;

		public Simple(Queue<Runnable> queue) {
			this.queue = queue;
		}

		@Nullable
		@Override
		public Runnable poll() {
			return (Runnable)this.queue.poll();
		}

		@Override
		public boolean add(Runnable runnable) {
			return this.queue.add(runnable);
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
