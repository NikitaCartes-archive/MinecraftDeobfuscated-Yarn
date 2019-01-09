package net.minecraft;

import com.google.common.collect.Queues;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;

public interface class_3847<T, F> {
	@Nullable
	F method_16909();

	boolean method_16910(T object);

	boolean method_16911();

	public static final class class_3848 implements class_3847<class_3847.class_3907, Runnable> {
		private final List<Queue<Runnable>> field_17044;

		public class_3848(int i) {
			this.field_17044 = (List<Queue<Runnable>>)IntStream.range(0, i).mapToObj(ix -> Queues.newConcurrentLinkedQueue()).collect(Collectors.toList());
		}

		@Nullable
		public Runnable method_17346() {
			for (Queue<Runnable> queue : this.field_17044) {
				Runnable runnable = (Runnable)queue.poll();
				if (runnable != null) {
					return runnable;
				}
			}

			return null;
		}

		public boolean method_16913(class_3847.class_3907 arg) {
			int i = arg.method_17347();
			((Queue)this.field_17044.get(i)).add(arg);
			return true;
		}

		@Override
		public boolean method_16911() {
			return this.field_17044.stream().allMatch(Collection::isEmpty);
		}
	}

	public static final class class_3849<T> implements class_3847<T, T> {
		private final Queue<T> field_17045;

		public class_3849(Queue<T> queue) {
			this.field_17045 = queue;
		}

		@Nullable
		@Override
		public T method_16909() {
			return (T)this.field_17045.poll();
		}

		@Override
		public boolean method_16910(T object) {
			return this.field_17045.add(object);
		}

		@Override
		public boolean method_16911() {
			return this.field_17045.isEmpty();
		}
	}

	public static final class class_3907 implements Runnable {
		private final int field_17278;
		private final Runnable field_17279;

		public class_3907(int i, Runnable runnable) {
			this.field_17278 = i;
			this.field_17279 = runnable;
		}

		public void run() {
			this.field_17279.run();
		}

		public int method_17347() {
			return this.field_17278;
		}
	}
}
