package net.minecraft;

public enum class_3867 implements class_3542 {
	field_17098("floor"),
	field_17099("ceiling"),
	field_17100("single_wall"),
	field_17101("double_wall");

	private final String field_17102;

	private class_3867(String string2) {
		this.field_17102 = string2;
	}

	@Override
	public String method_15434() {
		return this.field_17102;
	}
}
