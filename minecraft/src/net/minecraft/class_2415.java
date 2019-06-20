package net.minecraft;

public enum class_2415 {
	field_11302,
	field_11300,
	field_11301;

	public int method_10344(int i, int j) {
		int k = j / 2;
		int l = i > k ? i - j : i;
		switch (this) {
			case field_11301:
				return (j - l) % j;
			case field_11300:
				return (k - l + j) % j;
			default:
				return i;
		}
	}

	public class_2470 method_10345(class_2350 arg) {
		class_2350.class_2351 lv = arg.method_10166();
		return (this != field_11300 || lv != class_2350.class_2351.field_11051) && (this != field_11301 || lv != class_2350.class_2351.field_11048)
			? class_2470.field_11467
			: class_2470.field_11464;
	}

	public class_2350 method_10343(class_2350 arg) {
		if (this == field_11301 && arg.method_10166() == class_2350.class_2351.field_11048) {
			return arg.method_10153();
		} else {
			return this == field_11300 && arg.method_10166() == class_2350.class_2351.field_11051 ? arg.method_10153() : arg;
		}
	}
}
