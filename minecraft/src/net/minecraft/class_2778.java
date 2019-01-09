package net.minecraft;

public enum class_2778 implements class_3542 {
	field_12710("straight"),
	field_12712("inner_left"),
	field_12713("inner_right"),
	field_12708("outer_left"),
	field_12709("outer_right");

	private final String field_12714;

	private class_2778(String string2) {
		this.field_12714 = string2;
	}

	public String toString() {
		return this.field_12714;
	}

	@Override
	public String method_15434() {
		return this.field_12714;
	}
}
