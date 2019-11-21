package net.minecraft.util.thread;

import com.google.common.collect.Queues;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;

public interface TaskQueue<T, F> {
	@Nullable
	F poll();

	boolean add(T message);

	boolean isEmpty();

	public static final class Prioritized implements TaskQueue<TaskQueue.PrioritizedTask, Runnable> {
		private final List<Queue<Runnable>> queues;

		public Prioritized(int priorityCount) {
			this.queues = (List<Queue<Runnable>>)IntStream.range(0, priorityCount).mapToObj(i -> Queues.newConcurrentLinkedQueue()).collect(Collectors.toList());
		}

		@Nullable
		public Runnable poll() {
			for (Queue<Runnable> queue : this.queues) {
				Runnable runnable = (Runnable)queue.poll();
				if (runnable != null) {
					return runnable;
				}
			}

			return null;
		}

		public boolean add(TaskQueue.PrioritizedTask prioritizedTask) {
			int i = prioritizedTask.getPriority();
			((Queue)this.queues.get(i)).add(prioritizedTask);
			return true;
		}

		@Override
		public boolean isEmpty() {
			return this.queues.stream().allMatch(Collection::isEmpty);
		}
	}

	public static final class PrioritizedTask implements Runnable {
		private final int priority;
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
	}
}
