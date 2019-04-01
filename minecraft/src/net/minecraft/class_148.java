package net.minecraft;

public class class_148 extends RuntimeException {
	private final class_128 field_1119;

	public class_148(class_128 arg) {
		this.field_1119 = arg;
	}

	public class_128 method_631() {
		return this.field_1119;
	}

	public Throwable getCause() {
		return this.field_1119.method_564();
	}

	public String getMessage() {
		return this.field_1119.method_561();
	}
}
