package net.minecraft;

public enum class_2760 implements class_3542 {
	field_12619("top"),
	field_12617("bottom");

	private final String field_12616;

	private class_2760(String string2) {
		this.field_12616 = string2;
	}

	public String toString() {
		return this.field_12616;
	}

	@Override
	public String method_15434() {
		return this.field_12616;
	}
}
