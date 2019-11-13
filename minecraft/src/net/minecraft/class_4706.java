package net.minecraft;

import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class class_4706<T> extends AbstractSet<T> {
	private final Comparator<T> field_21562;
	private T[] field_21563;
	private int field_21564;

	private class_4706(int i, Comparator<T> comparator) {
		this.field_21562 = comparator;
		if (i < 0) {
			throw new IllegalArgumentException("Initial capacity (" + i + ") is negative");
		} else {
			this.field_21563 = (T[])method_23864(new Object[i]);
		}
	}

	public static <T extends Comparable<T>> class_4706<T> method_23859(int i) {
		return new class_4706<>(i, Comparator.naturalOrder());
	}

	private static <T> T[] method_23864(Object[] objects) {
		return (T[])objects;
	}

	private int method_23869(T object) {
		return Arrays.binarySearch(this.field_21563, 0, this.field_21564, object, this.field_21562);
	}

	private static int method_23866(int i) {
		return -i - 1;
	}

	public boolean add(T object) {
		int i = this.method_23869(object);
		if (i >= 0) {
			return false;
		} else {
			int j = method_23866(i);
			this.method_23863(object, j);
			return true;
		}
	}

	private void method_23868(int i) {
		if (i > this.field_21563.length) {
			if (this.field_21563 != ObjectArrays.DEFAULT_EMPTY_ARRAY) {
				i = (int)Math.max(Math.min((long)this.field_21563.length + (long)(this.field_21563.length >> 1), 2147483639L), (long)i);
			} else if (i < 10) {
				i = 10;
			}

			Object[] objects = new Object[i];
			System.arraycopy(this.field_21563, 0, objects, 0, this.field_21564);
			this.field_21563 = (T[])method_23864(objects);
		}
	}

	private void method_23863(T object, int i) {
		this.method_23868(this.field_21564 + 1);
		if (i != this.field_21564) {
			System.arraycopy(this.field_21563, i, this.field_21563, i + 1, this.field_21564 - i);
		}

		this.field_21563[i] = object;
		this.field_21564++;
	}

	private void method_23870(int i) {
		this.field_21564--;
		if (i != this.field_21564) {
			System.arraycopy(this.field_21563, i + 1, this.field_21563, i, this.field_21564 - i);
		}

		this.field_21563[this.field_21564] = null;
	}

	private T method_23871(int i) {
		return this.field_21563[i];
	}

	public T method_23862(T object) {
		int i = this.method_23869(object);
		if (i >= 0) {
			return this.method_23871(i);
		} else {
			this.method_23863(object, method_23866(i));
			return object;
		}
	}

	public boolean remove(Object object) {
		int i = this.method_23869((T)object);
		if (i >= 0) {
			this.method_23870(i);
			return true;
		} else {
			return false;
		}
	}

	public T method_23865() {
		return this.method_23871(0);
	}

	public boolean contains(Object object) {
		int i = this.method_23869((T)object);
		return i >= 0;
	}

	public Iterator<T> iterator() {
		return new class_4706.class_4707();
	}

	public int size() {
		return this.field_21564;
	}

	public Object[] toArray() {
		return (Object[])this.field_21563.clone();
	}

	public <U> U[] toArray(U[] objects) {
		if (objects.length < this.field_21564) {
			return (U[])Arrays.copyOf(this.field_21563, this.field_21564, objects.getClass());
		} else {
			System.arraycopy(this.field_21563, 0, objects, 0, this.field_21564);
			if (objects.length > this.field_21564) {
				objects[this.field_21564] = null;
			}

			return objects;
		}
	}

	public void clear() {
		Arrays.fill(this.field_21563, 0, this.field_21564, null);
		this.field_21564 = 0;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else {
			if (object instanceof class_4706) {
				class_4706<?> lv = (class_4706<?>)object;
				if (this.field_21562.equals(lv.field_21562)) {
					return this.field_21564 == lv.field_21564 && Arrays.equals(this.field_21563, lv.field_21563);
				}
			}

			return super.equals(object);
		}
	}

	class class_4707 implements Iterator<T> {
		private int field_21566;
		private int field_21567 = -1;

		private class_4707() {
		}

		public boolean hasNext() {
			return this.field_21566 < class_4706.this.field_21564;
		}

		public T next() {
			if (this.field_21566 >= class_4706.this.field_21564) {
				throw new NoSuchElementException();
			} else {
				this.field_21567 = this.field_21566++;
				return class_4706.this.field_21563[this.field_21567];
			}
		}

		public void remove() {
			if (this.field_21567 == -1) {
				throw new IllegalStateException();
			} else {
				class_4706.this.method_23870(this.field_21567);
				this.field_21566--;
				this.field_21567 = -1;
			}
		}
	}
}
