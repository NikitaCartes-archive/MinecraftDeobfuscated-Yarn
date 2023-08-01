package net.minecraft.util.collection;

import com.google.common.annotations.VisibleForTesting;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;

public class ArrayListDeque<T> extends AbstractList<T> implements Serializable, Cloneable, Deque<T>, RandomAccess {
	private static final int MISSING = 1;
	private Object[] array;
	private int startIndex;
	private int size;

	public ArrayListDeque() {
		this(1);
	}

	public ArrayListDeque(int size) {
		this.array = new Object[size];
		this.startIndex = 0;
		this.size = 0;
	}

	public int size() {
		return this.size;
	}

	@VisibleForTesting
	public int getArrayLength() {
		return this.array.length;
	}

	private int wrap(int index) {
		return (index + this.startIndex) % this.array.length;
	}

	public T get(int index) {
		this.checkBounds(index);
		return this.getRaw(this.wrap(index));
	}

	private static void checkBounds(int start, int end) {
		if (start < 0 || start >= end) {
			throw new IndexOutOfBoundsException(start);
		}
	}

	private void checkBounds(int index) {
		checkBounds(index, this.size);
	}

	private T getRaw(int index) {
		return (T)this.array[index];
	}

	public T set(int index, T value) {
		this.checkBounds(index);
		Objects.requireNonNull(value);
		int i = this.wrap(index);
		T object = this.getRaw(i);
		this.array[i] = value;
		return object;
	}

	public void add(int index, T value) {
		checkBounds(index, this.size + 1);
		Objects.requireNonNull(value);
		if (this.size == this.array.length) {
			this.enlarge();
		}

		int i = this.wrap(index);
		if (index == this.size) {
			this.array[i] = value;
		} else if (index == 0) {
			this.startIndex--;
			if (this.startIndex < 0) {
				this.startIndex = this.startIndex + this.array.length;
			}

			this.array[this.wrap(0)] = value;
		} else {
			for (int j = this.size - 1; j >= index; j--) {
				this.array[this.wrap(j + 1)] = this.array[this.wrap(j)];
			}

			this.array[i] = value;
		}

		this.modCount++;
		this.size++;
	}

	private void enlarge() {
		int i = this.array.length + Math.max(this.array.length >> 1, 1);
		Object[] objects = new Object[i];
		this.copyTo(objects, this.size);
		this.startIndex = 0;
		this.array = objects;
	}

	public T remove(int index) {
		this.checkBounds(index);
		int i = this.wrap(index);
		T object = this.getRaw(i);
		if (index == 0) {
			this.array[i] = null;
			this.startIndex++;
		} else if (index == this.size - 1) {
			this.array[i] = null;
		} else {
			for (int j = index + 1; j < this.size; j++) {
				this.array[this.wrap(j - 1)] = this.get(j);
			}

			this.array[this.wrap(this.size - 1)] = null;
		}

		this.modCount++;
		this.size--;
		return object;
	}

	public boolean removeIf(Predicate<? super T> predicate) {
		int i = 0;

		for (int j = 0; j < this.size; j++) {
			T object = this.get(j);
			if (predicate.test(object)) {
				i++;
			} else if (i != 0) {
				this.array[this.wrap(j - i)] = object;
				this.array[this.wrap(j)] = null;
			}
		}

		this.modCount += i;
		this.size -= i;
		return i != 0;
	}

	private void copyTo(Object[] array, int size) {
		for (int i = 0; i < size; i++) {
			array[i] = this.get(i);
		}
	}

	public void replaceAll(UnaryOperator<T> mapper) {
		for (int i = 0; i < this.size; i++) {
			int j = this.wrap(i);
			this.array[j] = Objects.requireNonNull(mapper.apply(this.getRaw(i)));
		}
	}

	public void forEach(Consumer<? super T> consumer) {
		for (int i = 0; i < this.size; i++) {
			consumer.accept(this.get(i));
		}
	}

	public void addFirst(T value) {
		this.add(0, value);
	}

	public void addLast(T value) {
		this.add(this.size, value);
	}

	public boolean offerFirst(T value) {
		this.addFirst(value);
		return true;
	}

	public boolean offerLast(T value) {
		this.addLast(value);
		return true;
	}

	public T removeFirst() {
		if (this.size == 0) {
			throw new NoSuchElementException();
		} else {
			return this.remove(0);
		}
	}

	public T removeLast() {
		if (this.size == 0) {
			throw new NoSuchElementException();
		} else {
			return this.remove(this.size - 1);
		}
	}

	@Nullable
	public T pollFirst() {
		return this.size == 0 ? null : this.removeFirst();
	}

	@Nullable
	public T pollLast() {
		return this.size == 0 ? null : this.removeLast();
	}

	public T getFirst() {
		if (this.size == 0) {
			throw new NoSuchElementException();
		} else {
			return this.get(0);
		}
	}

	public T getLast() {
		if (this.size == 0) {
			throw new NoSuchElementException();
		} else {
			return this.get(this.size - 1);
		}
	}

	@Nullable
	public T peekFirst() {
		return this.size == 0 ? null : this.getFirst();
	}

	@Nullable
	public T peekLast() {
		return this.size == 0 ? null : this.getLast();
	}

	public boolean removeFirstOccurrence(Object value) {
		for (int i = 0; i < this.size; i++) {
			T object = this.get(i);
			if (Objects.equals(value, object)) {
				this.remove(i);
				return true;
			}
		}

		return false;
	}

	public boolean removeLastOccurrence(Object value) {
		for (int i = this.size - 1; i >= 0; i--) {
			T object = this.get(i);
			if (Objects.equals(value, object)) {
				this.remove(i);
				return true;
			}
		}

		return false;
	}

	public boolean offer(T value) {
		return this.offerLast(value);
	}

	public T remove() {
		return this.removeFirst();
	}

	@Nullable
	public T poll() {
		return this.pollFirst();
	}

	public T element() {
		return this.getFirst();
	}

	@Nullable
	public T peek() {
		return this.peekFirst();
	}

	public void push(T value) {
		this.addFirst(value);
	}

	public T pop() {
		return this.removeFirst();
	}

	public Iterator<T> descendingIterator() {
		return new ArrayListDeque.IteratorImpl();
	}

	class IteratorImpl implements Iterator<T> {
		private int currentIndex = ArrayListDeque.this.size() - 1;

		public IteratorImpl() {
		}

		public boolean hasNext() {
			return this.currentIndex >= 0;
		}

		public T next() {
			return ArrayListDeque.this.get(this.currentIndex--);
		}

		public void remove() {
			ArrayListDeque.this.remove(this.currentIndex + 1);
		}
	}
}
