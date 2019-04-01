package net.minecraft;

public enum class_2745 implements class_3542 {
	field_12569("single", 0),
	field_12574("left", 2),
	field_12571("right", 1);

	public static final class_2745[] field_12570 = values();
	private final String field_12572;
	private final int field_12568;

	private class_2745(String string2, int j) {
		this.field_12572 = string2;
		this.field_12568 = j;
	}

	@Override
	public String method_15434() {
		return this.field_12572;
	}

	public class_2745 method_11824() {
		return field_12570[this.field_12568];
	}
}
