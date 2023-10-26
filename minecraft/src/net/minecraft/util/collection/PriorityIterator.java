package net.minecraft.util.collection;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Queues;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Comparator;
import java.util.Deque;
import java.util.Optional;
import java.util.Map.Entry;
import javax.annotation.Nullable;

public final class PriorityIterator<T> extends AbstractIterator<T> {
	private final Int2ObjectMap<Deque<T>> entries = new Int2ObjectOpenHashMap<>();

	public void enqueue(T value, int priority) {
		this.entries.computeIfAbsent(priority, (Int2ObjectFunction<? extends Deque<T>>)(p -> Queues.<T>newArrayDeque())).addLast(value);
	}

	@Nullable
	@Override
	protected T computeNext() {
		Optional<Deque<T>> optional = this.entries
			.int2ObjectEntrySet()
			.stream()
			.filter(entry -> !((Deque)entry.getValue()).isEmpty())
			.max(Comparator.comparingInt(Entry::getKey))
			.map(Entry::getValue);
		return (T)optional.map(Deque::removeFirst).orElseGet(() -> this.endOfData());
	}
}
