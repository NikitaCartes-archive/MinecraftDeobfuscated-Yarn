package net.minecraft;

public enum class_2771 implements class_3542 {
	field_12679("top"),
	field_12681("bottom"),
	field_12682("double");

	private final String field_12678;

	private class_2771(String string2) {
		this.field_12678 = string2;
	}

	public String toString() {
		return this.field_12678;
	}

	@Override
	public String method_15434() {
		return this.field_12678;
	}
}
