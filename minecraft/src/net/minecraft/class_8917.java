package net.minecraft;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Queues;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import java.util.Comparator;
import java.util.Deque;
import java.util.Optional;
import javax.annotation.Nullable;

public final class class_8917<T> extends AbstractIterator<T> {
	private final Int2ObjectMap<Deque<T>> field_46956 = new Int2ObjectOpenHashMap();

	public void method_54726(T object, int i) {
		((Deque)this.field_46956.computeIfAbsent(i, (Int2ObjectFunction)(ix -> Queues.newArrayDeque()))).addLast(object);
	}

	@Nullable
	@Override
	protected T computeNext() {
		Optional<Deque<T>> optional = this.field_46956
			.int2ObjectEntrySet()
			.stream()
			.filter(entry -> !((Deque)entry.getValue()).isEmpty())
			.max(Comparator.comparingInt(java.util.Map.Entry::getKey))
			.map(java.util.Map.Entry::getValue);
		return (T)optional.map(Deque::removeFirst).orElseGet(() -> this.endOfData());
	}
}
