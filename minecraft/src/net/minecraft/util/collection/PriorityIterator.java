package net.minecraft.util.collection;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Queues;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import java.util.Deque;
import javax.annotation.Nullable;

/**
 * A queue-like iterator that orders its values by the priority, or the insertion order
 * if the priorities equal.
 */
public final class PriorityIterator<T> extends AbstractIterator<T> {
	private static final int LOWEST_PRIORITY = Integer.MIN_VALUE;
	@Nullable
	private Deque<T> maxPriorityQueue = null;
	private int maxPriority = Integer.MIN_VALUE;
	private final Int2ObjectMap<Deque<T>> queuesByPriority = new Int2ObjectOpenHashMap<>();

	/**
	 * Adds {@code value} with the priority {@code priority}.
	 */
	public void enqueue(T value, int priority) {
		if (priority == this.maxPriority && this.maxPriorityQueue != null) {
			this.maxPriorityQueue.addLast(value);
		} else {
			Deque<T> deque = this.queuesByPriority.computeIfAbsent(priority, (Int2ObjectFunction<? extends Deque<T>>)(p -> Queues.<T>newArrayDeque()));
			deque.addLast(value);
			if (priority >= this.maxPriority) {
				this.maxPriorityQueue = deque;
				this.maxPriority = priority;
			}
		}
	}

	@Nullable
	@Override
	protected T computeNext() {
		if (this.maxPriorityQueue == null) {
			return this.endOfData();
		} else {
			T object = (T)this.maxPriorityQueue.removeFirst();
			if (object == null) {
				return this.endOfData();
			} else {
				if (this.maxPriorityQueue.isEmpty()) {
					this.refreshMaxPriority();
				}

				return object;
			}
		}
	}

	private void refreshMaxPriority() {
		int i = Integer.MIN_VALUE;
		Deque<T> deque = null;

		for (Entry<Deque<T>> entry : Int2ObjectMaps.fastIterable(this.queuesByPriority)) {
			Deque<T> deque2 = (Deque<T>)entry.getValue();
			int j = entry.getIntKey();
			if (j > i && !deque2.isEmpty()) {
				i = j;
				deque = deque2;
				if (j == this.maxPriority - 1) {
					break;
				}
			}
		}

		this.maxPriority = i;
		this.maxPriorityQueue = deque;
	}
}
