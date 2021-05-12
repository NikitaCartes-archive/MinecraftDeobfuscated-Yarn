package net.minecraft.util.collection;

import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;

public class SortedArraySet<T> extends AbstractSet<T> {
	private static final int DEFAULT_CAPACITY = 10;
	private final Comparator<T> comparator;
	T[] elements;
	int size;

	private SortedArraySet(int initialCapacity, Comparator<T> comparator) {
		this.comparator = comparator;
		if (initialCapacity < 0) {
			throw new IllegalArgumentException("Initial capacity (" + initialCapacity + ") is negative");
		} else {
			this.elements = (T[])cast(new Object[initialCapacity]);
		}
	}

	public static <T extends Comparable<T>> SortedArraySet<T> create() {
		return create(10);
	}

	public static <T extends Comparable<T>> SortedArraySet<T> create(int initialCapacity) {
		return new SortedArraySet<>(initialCapacity, Comparator.naturalOrder());
	}

	public static <T> SortedArraySet<T> create(Comparator<T> comparator) {
		return create(comparator, 10);
	}

	public static <T> SortedArraySet<T> create(Comparator<T> comparator, int initialCapacity) {
		return new SortedArraySet<>(initialCapacity, comparator);
	}

	private static <T> T[] cast(Object[] array) {
		return (T[])array;
	}

	private int binarySearch(T object) {
		return Arrays.binarySearch(this.elements, 0, this.size, object, this.comparator);
	}

	private static int insertionPoint(int binarySearchResult) {
		return -binarySearchResult - 1;
	}

	public boolean add(T object) {
		int i = this.binarySearch(object);
		if (i >= 0) {
			return false;
		} else {
			int j = insertionPoint(i);
			this.add(object, j);
			return true;
		}
	}

	private void ensureCapacity(int minCapacity) {
		if (minCapacity > this.elements.length) {
			if (this.elements != ObjectArrays.DEFAULT_EMPTY_ARRAY) {
				minCapacity = (int)Math.max(Math.min((long)this.elements.length + (long)(this.elements.length >> 1), 2147483639L), (long)minCapacity);
			} else if (minCapacity < 10) {
				minCapacity = 10;
			}

			Object[] objects = new Object[minCapacity];
			System.arraycopy(this.elements, 0, objects, 0, this.size);
			this.elements = (T[])cast(objects);
		}
	}

	private void add(T object, int index) {
		this.ensureCapacity(this.size + 1);
		if (index != this.size) {
			System.arraycopy(this.elements, index, this.elements, index + 1, this.size - index);
		}

		this.elements[index] = object;
		this.size++;
	}

	void remove(int index) {
		this.size--;
		if (index != this.size) {
			System.arraycopy(this.elements, index + 1, this.elements, index, this.size - index);
		}

		this.elements[this.size] = null;
	}

	private T get(int index) {
		return this.elements[index];
	}

	public T addAndGet(T object) {
		int i = this.binarySearch(object);
		if (i >= 0) {
			return this.get(i);
		} else {
			this.add(object, insertionPoint(i));
			return object;
		}
	}

	public boolean remove(Object object) {
		int i = this.binarySearch((T)object);
		if (i >= 0) {
			this.remove(i);
			return true;
		} else {
			return false;
		}
	}

	@Nullable
	public T getIfContains(T object) {
		int i = this.binarySearch(object);
		return i >= 0 ? this.get(i) : null;
	}

	public T first() {
		return this.get(0);
	}

	public T last() {
		return this.get(this.size - 1);
	}

	public boolean contains(Object object) {
		int i = this.binarySearch((T)object);
		return i >= 0;
	}

	public Iterator<T> iterator() {
		return new SortedArraySet.SetIterator();
	}

	public int size() {
		return this.size;
	}

	public Object[] toArray() {
		return (Object[])this.elements.clone();
	}

	public <U> U[] toArray(U[] objects) {
		if (objects.length < this.size) {
			return (U[])Arrays.copyOf(this.elements, this.size, objects.getClass());
		} else {
			System.arraycopy(this.elements, 0, objects, 0, this.size);
			if (objects.length > this.size) {
				objects[this.size] = null;
			}

			return objects;
		}
	}

	public void clear() {
		Arrays.fill(this.elements, 0, this.size, null);
		this.size = 0;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			if (o instanceof SortedArraySet<?> sortedArraySet && this.comparator.equals(sortedArraySet.comparator)) {
				return this.size == sortedArraySet.size && Arrays.equals(this.elements, sortedArraySet.elements);
			}

			return super.equals(o);
		}
	}

	class SetIterator implements Iterator<T> {
		private int nextIndex;
		private int lastIndex = -1;

		public boolean hasNext() {
			return this.nextIndex < SortedArraySet.this.size;
		}

		public T next() {
			if (this.nextIndex >= SortedArraySet.this.size) {
				throw new NoSuchElementException();
			} else {
				this.lastIndex = this.nextIndex++;
				return SortedArraySet.this.elements[this.lastIndex];
			}
		}

		public void remove() {
			if (this.lastIndex == -1) {
				throw new IllegalStateException();
			} else {
				SortedArraySet.this.remove(this.lastIndex);
				this.nextIndex--;
				this.lastIndex = -1;
			}
		}
	}
}
