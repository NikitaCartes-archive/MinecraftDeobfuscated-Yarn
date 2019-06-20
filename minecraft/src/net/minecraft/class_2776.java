package net.minecraft;

public enum class_2776 implements class_3542 {
	field_12695("save"),
	field_12697("load"),
	field_12699("corner"),
	field_12696("data");

	private final String field_12698;

	private class_2776(String string2) {
		this.field_12698 = string2;
	}

	@Override
	public String method_15434() {
		return this.field_12698;
	}
}
