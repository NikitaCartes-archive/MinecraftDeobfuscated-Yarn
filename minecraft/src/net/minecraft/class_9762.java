package net.minecraft;

import java.util.Locale;
import java.util.function.Consumer;

public class class_9762<T> {
	private final int field_51878;
	private final int field_51879;
	private final int field_51880;
	private final int field_51881;
	private final Object[] field_51882;

	public static <T> class_9762<T> method_60483(int i, int j, int k, class_9762.class_9763<T> arg) {
		int l = i - k;
		int m = j - k;
		int n = 2 * k + 1;
		return new class_9762<>(l, m, n, n, arg);
	}

	private class_9762(int i, int j, int k, int l, class_9762.class_9763<T> arg) {
		this.field_51878 = i;
		this.field_51879 = j;
		this.field_51880 = k;
		this.field_51881 = l;
		this.field_51882 = new Object[this.field_51880 * this.field_51881];

		for (int m = i; m < i + k; m++) {
			for (int n = j; n < j + l; n++) {
				this.field_51882[this.method_60486(m, n)] = arg.get(m, n);
			}
		}
	}

	public void method_60484(Consumer<T> consumer) {
		for (Object object : this.field_51882) {
			consumer.accept(object);
		}
	}

	public T method_60482(int i, int j) {
		if (!this.method_60485(i, j)) {
			throw new IllegalArgumentException("Requested out of range value (" + i + "," + j + ") from " + this);
		} else {
			return (T)this.field_51882[this.method_60486(i, j)];
		}
	}

	public boolean method_60485(int i, int j) {
		int k = i - this.field_51878;
		int l = j - this.field_51879;
		return k >= 0 && k < this.field_51880 && l >= 0 && l < this.field_51881;
	}

	public String toString() {
		return String.format(
			Locale.ROOT, "StaticCache2D[%d, %d, %d, %d]", this.field_51878, this.field_51879, this.field_51878 + this.field_51880, this.field_51879 + this.field_51881
		);
	}

	private int method_60486(int i, int j) {
		int k = i - this.field_51878;
		int l = j - this.field_51879;
		return k * this.field_51881 + l;
	}

	@FunctionalInterface
	public interface class_9763<T> {
		T get(int i, int j);
	}
}
