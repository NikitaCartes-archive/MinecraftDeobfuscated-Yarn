package net.minecraft;

public enum class_2764 implements class_3542 {
	field_12637("normal"),
	field_12634("sticky");

	private final String field_12635;

	private class_2764(String string2) {
		this.field_12635 = string2;
	}

	public String toString() {
		return this.field_12635;
	}

	@Override
	public String method_15434() {
		return this.field_12635;
	}
}
