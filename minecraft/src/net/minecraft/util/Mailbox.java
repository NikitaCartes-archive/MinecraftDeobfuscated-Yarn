package net.minecraft.util;

import com.google.common.collect.Queues;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;

public interface Mailbox<T, F> {
	@Nullable
	F poll();

	boolean add(T object);

	boolean isEmpty();

	public static final class PrioritizedMessage implements Runnable {
		private final int priority;
		private final Runnable runnable;

		public PrioritizedMessage(int i, Runnable runnable) {
			this.priority = i;
			this.runnable = runnable;
		}

		public void run() {
			this.runnable.run();
		}

		public int getPriority() {
			return this.priority;
		}
	}

	public static final class PrioritizedQueueMailbox implements Mailbox<Mailbox.PrioritizedMessage, Runnable> {
		private final List<Queue<Runnable>> queues;

		public PrioritizedQueueMailbox(int i) {
			this.queues = (List<Queue<Runnable>>)IntStream.range(0, i).mapToObj(ix -> Queues.newConcurrentLinkedQueue()).collect(Collectors.toList());
		}

		@Nullable
		public Runnable method_17346() {
			for (Queue<Runnable> queue : this.queues) {
				Runnable runnable = (Runnable)queue.poll();
				if (runnable != null) {
					return runnable;
				}
			}

			return null;
		}

		public boolean method_16913(Mailbox.PrioritizedMessage prioritizedMessage) {
			int i = prioritizedMessage.getPriority();
			((Queue)this.queues.get(i)).add(prioritizedMessage);
			return true;
		}

		@Override
		public boolean isEmpty() {
			return this.queues.stream().allMatch(Collection::isEmpty);
		}
	}

	public static final class QueueMailbox<T> implements Mailbox<T, T> {
		private final Queue<T> queue;

		public QueueMailbox(Queue<T> queue) {
			this.queue = queue;
		}

		@Nullable
		@Override
		public T poll() {
			return (T)this.queue.poll();
		}

		@Override
		public boolean add(T object) {
			return this.queue.add(object);
		}

		@Override
		public boolean isEmpty() {
			return this.queue.isEmpty();
		}
	}
}
