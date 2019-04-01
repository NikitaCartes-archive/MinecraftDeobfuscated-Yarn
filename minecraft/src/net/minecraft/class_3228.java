package net.minecraft;

import java.util.Objects;

final class class_3228<T> implements Comparable<class_3228<?>> {
	private final class_3230<T> field_14023;
	private final int field_14025;
	private final T field_14022;
	private final long field_14024;

	class_3228(class_3230<T> arg, int i, T object, long l) {
		this.field_14023 = arg;
		this.field_14025 = i;
		this.field_14022 = object;
		this.field_14024 = l;
	}

	public int method_14285(class_3228<?> arg) {
		int i = Integer.compare(this.field_14025, arg.field_14025);
		if (i != 0) {
			return i;
		} else {
			int j = Integer.compare(System.identityHashCode(this.field_14023), System.identityHashCode(arg.field_14023));
			return j != 0 ? j : this.field_14023.method_14292().compare(this.field_14022, arg.field_14022);
		}
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_3228)) {
			return false;
		} else {
			class_3228<?> lv = (class_3228<?>)object;
			return this.field_14025 == lv.field_14025 && Objects.equals(this.field_14023, lv.field_14023) && Objects.equals(this.field_14022, lv.field_14022);
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.field_14023, this.field_14025, this.field_14022});
	}

	public String toString() {
		return "Ticket[" + this.field_14023 + " " + this.field_14025 + " (" + this.field_14022 + ")] at " + this.field_14024;
	}

	public class_3230<T> method_14281() {
		return this.field_14023;
	}

	public int method_14283() {
		return this.field_14025;
	}

	public long method_14284() {
		return this.field_14024;
	}
}
