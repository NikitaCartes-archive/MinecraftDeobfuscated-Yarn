package net.minecraft;

import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;

public class class_1923 {
	public static final long field_17348 = method_8331(1875016, 1875016);
	public final int field_9181;
	public final int field_9180;

	public class_1923(int i, int j) {
		this.field_9181 = i;
		this.field_9180 = j;
	}

	public class_1923(class_2338 arg) {
		this.field_9181 = arg.method_10263() >> 4;
		this.field_9180 = arg.method_10260() >> 4;
	}

	public class_1923(long l) {
		this.field_9181 = (int)l;
		this.field_9180 = (int)(l >> 32);
	}

	public long method_8324() {
		return method_8331(this.field_9181, this.field_9180);
	}

	public static long method_8331(int i, int j) {
		return (long)i & 4294967295L | ((long)j & 4294967295L) << 32;
	}

	public static int method_8325(long l) {
		return (int)(l & 4294967295L);
	}

	public static int method_8332(long l) {
		return (int)(l >>> 32 & 4294967295L);
	}

	public int hashCode() {
		int i = 1664525 * this.field_9181 + 1013904223;
		int j = 1664525 * (this.field_9180 ^ -559038737) + 1013904223;
		return i ^ j;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_1923)) {
			return false;
		} else {
			class_1923 lv = (class_1923)object;
			return this.field_9181 == lv.field_9181 && this.field_9180 == lv.field_9180;
		}
	}

	public int method_8326() {
		return this.field_9181 << 4;
	}

	public int method_8328() {
		return this.field_9180 << 4;
	}

	public int method_8327() {
		return (this.field_9181 << 4) + 15;
	}

	public int method_8329() {
		return (this.field_9180 << 4) + 15;
	}

	public int method_17885() {
		return this.field_9181 >> 5;
	}

	public int method_17886() {
		return this.field_9180 >> 5;
	}

	public int method_17887() {
		return this.field_9181 & 31;
	}

	public int method_17888() {
		return this.field_9180 & 31;
	}

	public class_2338 method_8330(int i, int j, int k) {
		return new class_2338((this.field_9181 << 4) + i, j, (this.field_9180 << 4) + k);
	}

	public String toString() {
		return "[" + this.field_9181 + ", " + this.field_9180 + "]";
	}

	public class_2338 method_8323() {
		return new class_2338(this.field_9181 << 4, 0, this.field_9180 << 4);
	}

	public static Stream<class_1923> method_19280(class_1923 arg, int i) {
		return method_19281(new class_1923(arg.field_9181 - i, arg.field_9180 - i), new class_1923(arg.field_9181 + i, arg.field_9180 + i));
	}

	public static Stream<class_1923> method_19281(class_1923 arg, class_1923 arg2) {
		int i = Math.abs(arg.field_9181 - arg2.field_9181) + 1;
		int j = Math.abs(arg.field_9180 - arg2.field_9180) + 1;
		final int k = arg.field_9181 < arg2.field_9181 ? 1 : -1;
		final int l = arg.field_9180 < arg2.field_9180 ? 1 : -1;
		return StreamSupport.stream(new AbstractSpliterator<class_1923>((long)(i * j), 64) {
			@Nullable
			private class_1923 field_18684;

			public boolean tryAdvance(Consumer<? super class_1923> consumer) {
				if (this.field_18684 == null) {
					this.field_18684 = arg;
				} else {
					int i = this.field_18684.field_9181;
					int j = this.field_18684.field_9180;
					if (i == arg2.field_9181) {
						if (j == arg2.field_9180) {
							return false;
						}

						this.field_18684 = new class_1923(arg.field_9181, j + l);
					} else {
						this.field_18684 = new class_1923(i + k, j);
					}
				}

				consumer.accept(this.field_18684);
				return true;
			}
		}, false);
	}
}
