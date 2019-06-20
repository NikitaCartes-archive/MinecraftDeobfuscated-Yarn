package net.minecraft;

public class class_2940<T> {
	private final int field_13306;
	private final class_2941<T> field_13307;

	public class_2940(int i, class_2941<T> arg) {
		this.field_13306 = i;
		this.field_13307 = arg;
	}

	public int method_12713() {
		return this.field_13306;
	}

	public class_2941<T> method_12712() {
		return this.field_13307;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			class_2940<?> lv = (class_2940<?>)object;
			return this.field_13306 == lv.field_13306;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return this.field_13306;
	}

	public String toString() {
		return "<entity data: " + this.field_13306 + ">";
	}
}
