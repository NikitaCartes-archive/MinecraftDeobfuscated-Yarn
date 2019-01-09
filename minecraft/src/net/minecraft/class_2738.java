package net.minecraft;

public enum class_2738 implements class_3542 {
	field_12475("floor"),
	field_12471("wall"),
	field_12473("ceiling");

	private final String field_12472;

	private class_2738(String string2) {
		this.field_12472 = string2;
	}

	@Override
	public String method_15434() {
		return this.field_12472;
	}
}
