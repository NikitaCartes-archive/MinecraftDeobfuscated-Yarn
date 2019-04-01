package net.minecraft;

public class class_1954<T> implements Comparable<class_1954<?>> {
	private static long field_9319;
	private final T field_9317;
	public final class_2338 field_9322;
	public final long field_9321;
	public final class_1953 field_9320;
	private final long field_9318;

	public class_1954(class_2338 arg, T object) {
		this(arg, object, 0L, class_1953.field_9314);
	}

	public class_1954(class_2338 arg, T object, long l, class_1953 arg2) {
		this.field_9318 = field_9319++;
		this.field_9322 = arg.method_10062();
		this.field_9317 = object;
		this.field_9321 = l;
		this.field_9320 = arg2;
	}

	public boolean equals(Object object) {
		if (!(object instanceof class_1954)) {
			return false;
		} else {
			class_1954<?> lv = (class_1954<?>)object;
			return this.field_9322.equals(lv.field_9322) && this.field_9317 == lv.field_9317;
		}
	}

	public int hashCode() {
		return this.field_9322.hashCode();
	}

	public int method_8682(class_1954<?> arg) {
		int i = Long.compare(this.field_9321, arg.field_9321);
		if (i != 0) {
			return i;
		} else {
			i = Integer.compare(this.field_9320.ordinal(), arg.field_9320.ordinal());
			return i != 0 ? i : Long.compare(this.field_9318, arg.field_9318);
		}
	}

	public String toString() {
		return this.field_9317 + ": " + this.field_9322 + ", " + this.field_9321 + ", " + this.field_9320 + ", " + this.field_9318;
	}

	public T method_8683() {
		return this.field_9317;
	}
}
