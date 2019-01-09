package net.minecraft;

public enum class_2773 implements class_3542 {
	field_12686("up"),
	field_12689("side"),
	field_12687("none");

	private final String field_12685;

	private class_2773(String string2) {
		this.field_12685 = string2;
	}

	public String toString() {
		return this.method_15434();
	}

	@Override
	public String method_15434() {
		return this.field_12685;
	}
}
