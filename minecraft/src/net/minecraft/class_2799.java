package net.minecraft;

import java.io.IOException;

public class class_2799 implements class_2596<class_2792> {
	private class_2799.class_2800 field_12773;

	public class_2799() {
	}

	public class_2799(class_2799.class_2800 arg) {
		this.field_12773 = arg;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12773 = arg.method_10818(class_2799.class_2800.class);
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10817(this.field_12773);
	}

	public void method_12120(class_2792 arg) {
		arg.method_12068(this);
	}

	public class_2799.class_2800 method_12119() {
		return this.field_12773;
	}

	public static enum class_2800 {
		field_12774,
		field_12775;
	}
}
