package net.minecraft;

public class class_2265 {
	public final int field_10708;
	public final int field_10707;

	public class_2265(int i, int j) {
		this.field_10708 = i;
		this.field_10707 = j;
	}

	public class_2265(class_2338 arg) {
		this.field_10708 = arg.method_10263();
		this.field_10707 = arg.method_10260();
	}

	public long method_20475() {
		return method_20474(this.field_10708, this.field_10707);
	}

	public static long method_20474(int i, int j) {
		return (long)i & 4294967295L | ((long)j & 4294967295L) << 32;
	}

	public String toString() {
		return "[" + this.field_10708 + ", " + this.field_10707 + "]";
	}

	public int hashCode() {
		int i = 1664525 * this.field_10708 + 1013904223;
		int j = 1664525 * (this.field_10707 ^ -559038737) + 1013904223;
		return i ^ j;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_2265)) {
			return false;
		} else {
			class_2265 lv = (class_2265)object;
			return this.field_10708 == lv.field_10708 && this.field_10707 == lv.field_10707;
		}
	}
}
