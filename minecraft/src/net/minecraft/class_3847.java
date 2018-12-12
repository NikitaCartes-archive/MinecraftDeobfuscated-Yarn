package net.minecraft;

import com.google.common.collect.Queues;
import com.mojang.datafixers.util.Pair;
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

	public static final class class_3848<T> implements class_3847<Pair<Integer, T>, T> {
		private final List<Queue<T>> field_17044;

		public class_3848(int i) {
			this.field_17044 = (List<Queue<T>>)IntStream.range(0, i).mapToObj(ix -> Queues.newConcurrentLinkedQueue()).collect(Collectors.toList());
		}

		@Nullable
		@Override
		public T method_16909() {
			for (Queue<T> queue : this.field_17044) {
				T object = (T)queue.poll();
				if (object != null) {
					return object;
				}
			}

			return null;
		}

		public boolean method_16913(Pair<Integer, T> pair) {
			int i = pair.getFirst();
			((Queue)this.field_17044.get(i)).add(pair.getSecond());
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
}
