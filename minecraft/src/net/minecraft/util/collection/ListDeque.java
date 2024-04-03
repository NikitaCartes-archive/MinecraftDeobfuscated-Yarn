package net.minecraft.util.collection;

import java.io.Serializable;
import java.util.Deque;
import java.util.List;
import java.util.RandomAccess;
import javax.annotation.Nullable;

public interface ListDeque<T> extends Serializable, Cloneable, Deque<T>, List<T>, RandomAccess {
	ListDeque<T> reversed();

	T getFirst();

	T getLast();

	void addFirst(T value);

	void addLast(T value);

	T removeFirst();

	T removeLast();

	default boolean offer(T object) {
		return this.offerLast(object);
	}

	default T remove() {
		return this.removeFirst();
	}

	@Nullable
	default T poll() {
		return (T)this.pollFirst();
	}

	default T element() {
		return this.getFirst();
	}

	@Nullable
	default T peek() {
		return (T)this.peekFirst();
	}

	default void push(T object) {
		this.addFirst(object);
	}

	default T pop() {
		return this.removeFirst();
	}
}
