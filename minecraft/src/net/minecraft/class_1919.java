package net.minecraft;

public class class_1919 {
	private final class_2338 field_9173;
	private final class_2248 field_9172;
	private final int field_9171;
	private final int field_9170;

	public class_1919(class_2338 arg, class_2248 arg2, int i, int j) {
		this.field_9173 = arg;
		this.field_9172 = arg2;
		this.field_9171 = i;
		this.field_9170 = j;
	}

	public class_2338 method_8306() {
		return this.field_9173;
	}

	public class_2248 method_8309() {
		return this.field_9172;
	}

	public int method_8307() {
		return this.field_9171;
	}

	public int method_8308() {
		return this.field_9170;
	}

	public boolean equals(Object object) {
		if (!(object instanceof class_1919)) {
			return false;
		} else {
			class_1919 lv = (class_1919)object;
			return this.field_9173.equals(lv.field_9173) && this.field_9171 == lv.field_9171 && this.field_9170 == lv.field_9170 && this.field_9172 == lv.field_9172;
		}
	}

	public String toString() {
		return "TE(" + this.field_9173 + ")," + this.field_9171 + "," + this.field_9170 + "," + this.field_9172;
	}
}
